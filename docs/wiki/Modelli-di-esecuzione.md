# Modelli di esecuzione

> Bozza: copre il timer degli esami scritti (issue #23). La parte sull'interfaccia grafica si
> completa con le issue #10 e #11.

Nel gioco convivono due modelli di esecuzione: l'interfaccia grafica, **guidata dagli
eventi**, e il **timer** delle domande, che gira su un thread separato. Questa pagina spiega
come comunicano senza che i dati condivisi si corrompano.

## L'interfaccia è event-driven

JavaFX esegue tutto il codice dell'interfaccia su un solo thread, il *JavaFX Application
Thread*. Il thread prende un evento alla volta da una coda (un clic, un tasto, un compito
accodato con `Platform.runLater`) ed esegue il gestore corrispondente. Il programma non ha un
ciclo principale scritto da noi: reagisce agli eventi. Per questo i gestori devono essere
brevi, perché mentre uno è in esecuzione l'interfaccia è ferma.

Lo scontro (`ExamBattle`) vive su questo thread. La UI chiama i suoi metodi (`answer`,
`useItem`, ...) e lo scontro le risponde con le notifiche di `BattleListener` (Observer):
l'engine non conosce JavaFX, conosce solo il listener.

## Il timer gira su un altro thread

Negli scritti ogni domanda ha un tempo limite. Il conto alla rovescia non può stare sul
thread dell'interfaccia: un ciclo con `sleep` la bloccherebbe. `ScheduledQuestionTimer`
usa quindi uno `ScheduledExecutorService` a thread singolo, l'`ExecutorService` della
lezione 16 che sa anche pianificare i compiti:

```java
countdown = scheduler.scheduleAtFixedRate(() -> tick(current), 1, 1, TimeUnit.SECONDS);
```

Rispetto a un `Thread` scritto a mano con `sleep(1000)` in un ciclo:
- il ritmo è fisso e non accumula ritardo per il lavoro fatto a ogni giro;
- il compito si annulla con `ScheduledFuture.cancel`, senza interrompere thread;
- il thread lo gestisce l'executor, e si chiude con `shutdownNow()`.

Lo scheduler lo crea l'applicazione e lo chiude chi lo crea: `Application.stop()` chiamerà
`scheduler.shutdownNow()` (issue #11). Il suo thread è *daemon*, quindi uno scheduler
dimenticato aperto non impedisce alla JVM di terminare.

## Dal thread del timer a quello dell'interfaccia

Il timer non chiama lo scontro dal proprio thread. Ogni notifica (`onTick`, `onExpired`)
passa da un `Executor` ricevuto nel costruttore:
- nell'applicazione è `Platform::runLater`, che accoda la notifica nella coda degli eventi
  di JavaFX;
- nei test è un esecutore diretto oppure una coda gestita dal test.

Così `ExamBattle` viene toccato da **un solo thread**, quello dell'interfaccia (*thread
confinement*). Non ha bisogno di lock e la sua Javadoc dichiara che non è thread-safe: è una
scelta, non una dimenticanza.

## Il dato condiviso: i secondi rimasti

Una cosa però è davvero condivisa: i secondi rimasti. Il thread del timer li decrementa a ogni
tick, mentre il Caffè li aumenta dal thread dell'interfaccia (`addSeconds(15)`).

È la race condition del contatore della lezione 16. `secondsLeft--` sembra un'operazione
sola, ma sono tre passi: leggi, calcola, scrivi. Se i due thread si alternano così, il Caffè
va perso:

| Passo | Thread del timer (`secondsLeft--`) | Thread della UI (`secondsLeft += 15`) | `secondsLeft` |
|---|---|---|---|
| 1 | legge 10 | | 10 |
| 2 | | legge 10 | 10 |
| 3 | | scrive 25 | 25 |
| 4 | scrive 9 | | **9** |

Il giocatore ha consumato il Caffè e ha 9 secondi invece di 24. L'errore non dà eccezioni e
capita di rado, per questo non si scopre provando.

La soluzione della lezione è `synchronized`. Tutti i metodi che leggono o scrivono lo stato
del timer entrano in un blocco `synchronized` sullo stesso lock privato: il tick, `addSeconds`,
`start`, `stop` e il controllo alla consegna delle notifiche. Così i passi di un thread non
si possono più mescolare con quelli dell'altro.

**Perché non `AtomicInteger`.** Basterebbe se il dato condiviso fosse solo il contatore:
`addAndGet` è atomico. Ma lo stato del timer è fatto di quattro campi che devono cambiare
insieme: secondi rimasti, compito pianificato, listener e numero di generazione. Ad esempio
`start` li sostituisce tutti, e il tick a zero decrementa e annulla il compito. Un
`AtomicInteger` renderebbe atomico il singolo numero, non l'invariante su tutti i campi.

## Notifiche fuori dal lock

Il tick calcola cosa notificare tenendo il lock, ma **consegna le notifiche dopo averlo
rilasciato**. Una notifica esegue codice esterno al timer (lo scontro, e attraverso lo
scontro la UI). Chiamare codice esterno tenendo un lock rischia il **deadlock**: quel codice
può aver bisogno di un altro lock. Ad esempio: il thread del timer tiene il lock del timer e,
dentro la notifica, aspetta un lock B della UI; nello stesso momento il thread della UI tiene
B e chiama `addSeconds`, che aspetta il lock del timer. Ognuno aspetta l'altro per sempre.
Tenere il lock solo per leggere e scrivere i campi, e mai mentre si chiama altro codice,
evita il problema.

## Scadenze vecchie

C'è un caso che il lock da solo non risolve:
1. il tempo finisce: il thread del timer accoda `onExpired` nella coda degli eventi;
2. prima che la coda lo esegua, il giocatore risponde: lo scontro chiama `stop()` e poi
   `start()` per la domanda successiva;
3. arriva la notifica `onExpired`, che è della domanda **precedente**: senza controlli
   segnerebbe come sbagliata la domanda nuova.

Il timer ha un **numero di generazione**, incrementato a ogni `start()` e `stop()`. Ogni tick e
ogni notifica ricorda la generazione in cui è nato. Al momento della consegna, sul thread
dell'interfaccia, la notifica controlla se la sua generazione è ancora quella corrente e,
se non lo è, viene scartata. Lo stesso controllo vale per un tick che stava già girando
quando è partito un nuovo conto alla rovescia: non può toccare i secondi di quello nuovo.

Il controllo sta nel timer e non nello scontro. Così vale il contratto di `QuestionTimer`
("dopo `stop()` o un nuovo `start()` nessuna notifica del conto precedente raggiunge il
listener") per qualunque listener, e lo scontro non ha bisogno di un secondo contatore.

## Come si testa senza tempo reale

Tra le proprietà dei test di unità viste nella lezione del Prof. Rossi sul testing c'è la
**ripetibilità**: un test deve dare sempre lo stesso risultato, ogni volta che lo si esegue
e su qualunque macchina. Un test che aspetta secondi veri o che dipende da come il sistema
operativo alterna i thread non è ripetibile: a volte passa, a volte no, e un fallimento non si
riesce a riprodurre. Per questo la regola 8 del design vieta nei test il tempo reale e la
casualità.

I test del timer usano due sostituti:
- **`ManualScheduler`**: estende `ScheduledThreadPoolExecutor` ma registra i compiti invece di
  pianificarli. Il test fa passare un secondo con `tick()`, e può anche eseguire un compito già
  annullato per simulare un tick che stava girando quando è arrivato `stop()`.
- **`QueuedExecutor`**: tiene le notifiche in coda finché il test chiama `runAll()`, come la
  coda di `Platform.runLater`. Così il caso della scadenza vecchia si riproduce passo per
  passo: il tempo scade, il giocatore risponde, la coda viene eseguita, e la scadenza vecchia
  risulta scartata.

Un test con lo scontro vero e il timer vero (con questi due sostituti) verifica lo scenario
completo: una scadenza già in coda quando il giocatore risponde non conta come errore sulla
domanda successiva.

**Niente test con due thread veri.** Un test che lancia molti `addSeconds` mentre il timer
scorre passerebbe o fallirebbe secondo come vengono alternati i thread: un risultato verde
non dimostrerebbe niente, e uno rosso non si potrebbe riprodurre. La correttezza viene dal
progetto (un solo lock per tutto lo stato, lo scontro confinato nel thread
dell'interfaccia); i test verificano la logica in modo deterministico.

Ho verificato che i test sappiano trovare un errore: togliendo per prova il controllo della
generazione alla consegna falliscono i due test sulle scadenze già in coda.
