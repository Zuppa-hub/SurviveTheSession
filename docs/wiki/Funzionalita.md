# Funzionalità

Questa pagina descrive il gioco dal punto di vista del giocatore. Le classi che realizzano ogni
regola sono nella pagina [Classi e interfacce](Classi-e-interfacce); le schermate nella pagina
[Interfaccia grafica](Interfaccia-grafica).

I valori numerici (statistiche, danni, soglie) sono quelli bilanciati nella issue #12 e
verificati da `BalanceTest` (vedi [Classi e interfacce](Classi-e-interfacce)).

## Nuova partita e profili

Dal menu si sceglie **Nuova partita**, si scrive il proprio nome e si sceglie un profilo. Il
profilo decide le statistiche iniziali (`profiles.xml`):

| Profilo | Conoscenza | Energia | Concentrazione | Descrizione nel gioco |
|---|---|---|---|---|
| Secchione | 8 | 7 | 4 | Sa quasi tutto, ma si stanca presto. |
| Pendolare | 3 | 12 | 4 | Studia poco, ma dopo ore di treno regge qualunque esame. |
| Nottambulo | 5 | 9 | 6 | Preparato il giusto, lucidissimo quando serve. |

Lo studente parte dal livello 1, con il libretto vuoto e un **kit iniziale**: 1 Caffè, 1 Appunti
e 1 Energy drink, così prova gli oggetti fin dal primo esame (con il 70% di risposte giuste si
prende 18-21, e gli oggetti come ricompensa arrivano solo dal 24). Una partita caricata tiene
l'inventario salvato, senza ricevere un altro kit. Il nome del salvataggio
si ricava dal nome del giocatore (`SlotNames`): se esiste già una partita con quel nome, il gioco
chiede di sceglierne un altro invece di sovrascriverla.

## Le statistiche

- **Conoscenza**: aumenta il danno di ogni risposta corretta (7 + Conoscenza / 2).
- **Energia**: è la vita. Ogni risposta sbagliata la riduce; a zero l'esame è bocciato. Si
  ricarica del tutto alla fine di **ogni** esame, superato o no, così farsi bocciare non conviene
  mai e la sessione mostra già l'Energia piena per il prossimo esame.
- **Concentrazione**: si spende per le abilità e si recupera di 1 a ogni risposta corretta.
  Passa da un esame all'altro.

## La sessione

La sessione ha cinque esami, nell'ordine di `exams.xml`:

| Esame | Tipo | CFU | Difficoltà | Domande |
|---|---|---|---|---|
| Programmazione (`PROG1`) | scritto | 12 | media | 18 |
| Algoritmi e Strutture Dati (`ASD1`) | scritto | 12 | difficile | 15 |
| Basi di Dati (`BD1`) | orale | 9 | media | 15 |
| Reti di Calcolatori (`RETI1`) | orale | 6 | facile | 15 |
| Metodologie di Programmazione (`MDP1`) | progetto | 12 | difficile | 15 |

La schermata della sessione mostra ogni esame con il suo stato (superato con il voto, da
sostenere, bloccato), le statistiche, il libretto e lo zaino. Gli esami da sostenere si possono
affrontare in qualunque ordine, tranne l'ultimo.

**Boss finale.** L'ultimo esame della sessione, Metodologie di Programmazione, si può sostenere
solo quando tutti gli altri sono superati. Fino ad allora è mostrato come "Boss finale: dopo
tutti gli altri esami".

## L'esame: uno scontro a turni

Ogni esame ha una **resistenza**, calcolata da CFU e difficoltà (CFU × 4 se facile o media, CFU × 5 se difficile). Si combatte
rispondendo a domande vere sulla materia:

- una **risposta corretta** abbassa la resistenza e fa recuperare Concentrazione;
- una **risposta sbagliata** (o il tempo scaduto) toglie Energia: 1 punto se l'esame è facile, 2
  se è medio o difficile, raddoppiati in un progetto;
- nello stesso esame una domanda non viene mai riproposta.

Le azioni del turno sono **Rispondi**, **usa un oggetto**, **usa un'abilità** e **Ritirati**.

### Tipi di domanda

| Tipo | Come si risponde |
|---|---|
| Scelta multipla | un pulsante tra quattro opzioni |
| Vero/Falso | un pulsante tra Vero e Falso |
| Output del codice | si legge uno snippet di codice e si sceglie tra quattro output |
| Completamento | si scrive una risposta breve; maiuscole e spazi non contano |

Le opzioni sono mescolate a ogni presentazione.

### Tipi di esame

| Tipo | Timer | Errori | Particolarità |
|---|---|---|---|
| Scritto | 30 secondi per domanda | ×1 | tempo scaduto = risposta sbagliata |
| Orale | no | ×1 | dopo un errore il professore incalza: la domanda successiva è più difficile |
| Progetto | no | ×2 | domande di ogni tipo, ogni errore costa il doppio |

Negli scritti il timer scende davvero, un secondo alla volta, e sotto i 5 secondi diventa
rosso. Se il tempo scade, il gioco lo dice esplicitamente invece di mostrare solo "sbagliata".

### Oggetti

Gli oggetti si ottengono come ricompensa e si usano solo mentre una domanda attende risposta.
Un oggetto che in quel momento non avrebbe effetto non si può usare e non viene consumato.

| Oggetto | Effetto | Quando si può usare |
|---|---|---|
| Caffè | +15 secondi alla domanda corrente | solo con il timer |
| Appunti | elimina due opzioni sbagliate | se sono visibili almeno tre opzioni sbagliate |
| Riassunto | mostra l'indizio | se la domanda ha un indizio non ancora mostrato |
| Energy drink | +4 Energia, senza superare il massimo | se l'Energia non è al massimo |

### Abilità

| Abilità | Costo | Effetto |
|---|---|---|
| Ripasso lampo | 2 Concentrazione | mostra l'indizio, come il Riassunto |
| Colpo di genio | 3 Concentrazione | la prossima risposta corretta fa danno doppio |

Un indizio pesa sul voto una sola volta per domanda, da qualunque parte arrivi.

## L'esito

La prova finisce in quattro modi:

| Come finisce | Risultato |
|---|---|
| la resistenza arriva a zero | decide il voto |
| le domande finiscono | decide il voto |
| l'Energia arriva a zero | bocciatura, senza voto |
| il giocatore si ritira | nessun voto, l'esame resta da sostenere |

**Il voto** dipende dalla prestazione: servono almeno il 60% di risposte corrette; dal 60% al
100% il voto sale da 18 a 30; ogni indizio usato toglie un punto, senza scendere sotto il 18; la
lode arriva solo con tutte le risposte corrette e nessun indizio. Ad esempio 8 risposte corrette
su 10 danno 24, con due indizi 22.

Un esame bocciato o abbandonato resta nella sessione e si può ritentare.

## Ricompense e livelli

Un esame superato entra nel libretto e dà:
- **esperienza**: CFU × (voto − 15), con la lode che vale 30;
- **oggetti**: nessuno con 18-23, uno con 24-27, due con 28-30, tre con la lode, scelti a caso
  tra quelli del catalogo.

Salire dal livello n al successivo costa 50 × n esperienza. Ogni livello dà +1 Conoscenza, +3
Energia massima e +1 Concentrazione massima. Un esame con molta esperienza può far salire di più
livelli. La schermata dell'esito mostra voto, risposte, ricompense e livelli guadagnati.

## Laurea

Quando tutti gli esami della sessione sono nel libretto, dall'esito si passa alla **laurea**: il
libretto completo, i CFU, la media ponderata sui CFU e il voto di laurea su 110 (media × 110 / 30,
arrotondato: media 30 → 110, media 18 → 66). Punti bonus e lode di laurea dipendono dai
regolamenti dei singoli corsi e non sono previsti.

## Salvataggio e caricamento

- La partita si **salva da sola** appena creata e dopo ogni esame concluso (superato, bocciato o abbandonato), in
  `~/.sopravvivi-alla-sessione/saves/<nome>.json`.
- Dal menu si vede l'elenco delle partite salvate e se ne carica una; la partita caricata
  continua a salvarsi nello stesso file.
- Si salva lo studente (statistiche, livello, oggetti, libretto), non l'esame in corso: chi
  chiude il gioco a metà di un esame riparte dalla sessione, con quell'esame ancora da sostenere.
- Se un salvataggio fallisce (ad esempio cartella non scrivibile) compare un avviso e la partita
  continua. Un file rovinato non si carica e il menu mostra il motivo.

I dettagli tecnici sono nella pagina [Persistenza](Persistenza).

## Cosa non c'è, per scelta

Mappe da esplorare, negozi, dialoghi e grafica elaborata sono fuori scope (design, §1): il gioco è
il contesto in cui mostrare la progettazione, non il fine.
