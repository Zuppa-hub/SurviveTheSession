# Interfaccia grafica

L'interfaccia è in JavaFX: ogni schermata è un file FXML (la struttura), un controller (le
reazioni agli eventi) e un unico foglio di stile, `styles.css` (l'aspetto). La grafica è
volutamente semplice: sfondo chiaro, schede bianche, un solo colore d'accento.

## Le schermate

| Schermata | File | Cosa fa |
|---|---|---|
| Menu | `menu.fxml`, `MenuController` | nuova partita, elenco delle partite salvate e caricamento, uscita |
| Nuova partita | `new-game.fxml`, `NewGameController` | nome del giocatore e scelta del profilo, con descrizione e statistiche iniziali |
| Sessione | `session.fxml`, `SessionController` | esami della sessione con il loro stato, statistiche, libretto, zaino, "Sostieni esame" |
| Esame | `exam.fxml`, `ExamController` | domanda (con lo snippet di codice se c'è), opzioni o campo di testo, timer, barre di resistenza, Energia e Concentrazione, oggetti e abilità, "Ritirati" |
| Esito | `outcome.fxml`, `OutcomeController` | voto o bocciatura, risposte, ricompense e livelli; poi sessione o laurea |
| Laurea | `graduation.fxml`, `GraduationController` | libretto, media ponderata, CFU e voto di laurea su 110 |

## Controller sottili
I controller non contengono regole di gioco: traducono un clic in una chiamata all'engine e
mostrano il risultato. Ad esempio la schermata della sessione non decide quali esami si
possono sostenere: lo chiede a `Game.availableExams()`.

Quello che serve a mostrare i dati, ma non dipende da JavaFX, sta in classi separate testate con
JUnit:
- **`ExamRow`**: lo stato di ogni esame (superato con il voto, da sostenere, oppure bloccato
  perché è il boss finale) e le etichette in italiano. Il tipo d'esame si traduce con una
  `EnumMap`, come nel resto del progetto.
- **`SlotNames`**: il nome del salvataggio ricavato dal nome del giocatore ("Àndrea Cazzàto" →
  `andrea-cazzato`), sempre valido per la regola degli slot della #9.
- **`BattleAction`**: ogni oggetto posseduto (in ordine di nome) e ogni abilità come pulsante,
  con etichetta, descrizione e stato. Se si può usare lo decide lo scontro con
  `ExamBattle.canUse`: la schermata non sa che il Caffè serve solo con il timer o che gli
  Appunti vogliono almeno tre opzioni sbagliate.
- **`AnswerFeedback`**: il messaggio dopo una risposta (giusta, sbagliata, tempo scaduto).
- **`OutcomeSummary`**: titolo, voto e righe dell'esito, ricavati dall'`ExamReport`; i titoli
  per `BattleResult` stanno in una `EnumMap`.
- **`GraduationSummary`**: righe del libretto, media, CFU e voto di laurea, tutti calcolati
  dall'engine (`Game.graduationGrade()`, `Transcript.weightedAverage()`).

Così le parti con una logica sono verificate dai test, e i controller restano codice di
collegamento.

## Chi crea i controller
Gli FXML non hanno `fx:controller`. È `ViewNavigator` a creare ogni controller passandogli nel
costruttore quello che usa (il navigatore, la partita o il servizio delle partite) e a
consegnarlo a `FXMLLoader` con `setController`. Niente stato statico, niente framework di
iniezione: le dipendenze di un controller si leggono nel suo costruttore. `ViewNavigator`
carica anche il foglio di stile una volta sola, sulla scena.

## La schermata d'esame
`ExamController` implementa `BattleListener` ed è il listener passato a
`Game.startExam(esame, listener)`. Il navigatore prima carica la vista e poi avvia l'esame,
perché la prima domanda arriva subito, dentro `startExam`.

- **Lo scontro si legge dalla partita.** Il controller non tiene un campo con l'`ExamBattle`:
  lo chiede a `Game.currentBattle()`, che lo contiene già quando arriva la prima domanda e
  torna vuoto a esame finito. Così non c'è un campo che resta `null` fino al ritorno di
  `startExam`.
- **Ogni notifica aggiorna la vista.** Nuova domanda ed effetto applicato ridisegnano barre,
  domanda e pulsanti dallo stato dello scontro (`BattleState`); il tick aggiorna solo il
  timer, che sotto i 5 secondi diventa rosso.
- **Categoria della domanda senza `instanceof`.** Una domanda a scelta ha delle opzioni
  (`PresentedQuestion.options()`), una da completare no: pulsanti nel primo caso, campo di
  testo nel secondo. Dopo gli Appunti le opzioni sono due di meno, e la vista le ridisegna.
- **Lo snippet di codice.** `Question.codeSnippet()` è vuoto di default e
  `CodeOutputQuestion` lo ridefinisce. La schermata lo mostra se c'è, senza chiedere il tipo
  concreto della domanda (stesso principio del `QuestionVisitor`).
- **Tempo scaduto.** Per lo scontro il tempo scaduto è una risposta sbagliata, ed è giusto
  così per il voto. Al giocatore però va detto: il controller alza un flag mentre passa la
  risposta allo scontro, quindi una risposta sbagliata arrivata senza il flag è una scadenza.
- **Il timer è reale.** Le notifiche arrivano dal thread del timer attraverso
  `Platform::runLater`, quindi il controller e lo scontro vengono toccati solo dal thread di
  JavaFX (vedi [Modelli di esecuzione](Modelli-di-esecuzione)).

## Esito e laurea
Chi mostra l'esito è un `GameListener`: `ViewNavigator.openGame` lo registra una volta per
partita, quando la partita viene creata o caricata. A fine esame `Game` applica l'esito
(libretto, ricompense, livelli), avvisa i listener in ordine di registrazione (prima
`AutoSave`, che salva, poi la UI) e la UI mostra la schermata dell'esito con l'`ExamReport`.
La schermata d'esame non fa niente alla fine: non deve sapere che dopo c'è l'esito.

Dall'esito si torna alla sessione, oppure, se la sessione è completa (`Game.canGraduate()`),
si va alla laurea.

## La UI dipende dall'engine, non da `app`
Per creare o caricare una partita la UI usa l'interfaccia **`GameService`** (profili, partite
salvate, nuova partita, caricamento), definita nel package `ui` dove viene usata. La
implementa `GameAssembly`, la composition root in `app`. Le dipendenze vanno quindi
`app → ui → engine → model`, senza cicli: è lo stesso principio (DIP) usato per i repository.

## Avvio e chiusura: `GameApplication`
`Main` lancia `GameApplication` (sottoclasse di `Application`). Una classe di avvio separata,
che non estende `Application`, permette di avviare il gioco dal class path senza
configurazioni dei moduli.

- **`start`** carica i contenuti, crea lo scheduler del timer e `GameAssembly`, e mostra il
  menu. Se i contenuti non sono validi compare una finestra d'errore con il messaggio della
  `ContentException` e il gioco si chiude (fail fast, come prima in console).
- **`Platform::runLater`** è l'esecutore delle notifiche del timer: ogni secondo e ogni
  scadenza arrivano sul thread dell'interfaccia, così lo scontro viene toccato da un solo
  thread (vedi [Modelli di esecuzione](Modelli-di-esecuzione)).
- **`stop`** chiama `scheduler.shutdownNow()`: lo scheduler l'ha creato l'applicazione, quindi
  è l'applicazione a chiuderlo.

## Salvataggio fallito: un avviso, non un errore
Se un salvataggio automatico fallisce (disco pieno, cartella non scrivibile), il callback della
#9 mostra un avviso con `Alert.show()`, che non blocca: il giocatore legge il messaggio e la
partita continua.

## Come è stato verificato
I controller non hanno test automatici: un test dell'interfaccia grafica richiederebbe una
libreria apposita e un display. Sono verificati in due modi:
- le classi con logica (`ExamRow`, `SlotNames`, `BattleAction`, `AnswerFeedback`,
  `OutcomeSummary`, `GraduationSummary`, `GameAssembly` come `GameService`) hanno test JUnit;
  i test della UI condividono una partita di prova (`TestGames`) con timer manuale;
- un programma eseguito a mano, fuori dal repository, ha giocato con i contenuti veri e il
  timer vero: ha controllato che il timer scendesse (30 s → 28 s dopo due secondi e mezzo),
  risposto alle domande dai pulsanti e dal campo di testo, visto lo snippet di codice,
  l'esito e il salvataggio automatico, il ritiro da un esame e la schermata di laurea, senza
  eccezioni.
