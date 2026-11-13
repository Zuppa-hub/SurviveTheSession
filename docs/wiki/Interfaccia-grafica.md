# Interfaccia grafica

> Bozza: copre menu, nuova partita e sessione (issue #10). Schermata d'esame, esito e laurea
> arrivano con la issue #11.

L'interfaccia è in JavaFX: ogni schermata è un file FXML (la struttura), un controller (le
reazioni agli eventi) e un unico foglio di stile, `styles.css` (l'aspetto). La grafica è
volutamente semplice: sfondo chiaro, schede bianche, un solo colore d'accento.

## Le schermate

| Schermata | File | Cosa fa |
|---|---|---|
| Menu | `menu.fxml`, `MenuController` | nuova partita, elenco delle partite salvate e caricamento, uscita |
| Nuova partita | `new-game.fxml`, `NewGameController` | nome del giocatore e scelta del profilo, con descrizione e statistiche iniziali |
| Sessione | `session.fxml`, `SessionController` | esami della sessione con il loro stato, statistiche, libretto, zaino, "Sostieni esame" |

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

Così le parti con una logica sono verificate dai test, e i controller restano codice di
collegamento.

## Chi crea i controller
Gli FXML non hanno `fx:controller`. È `ViewNavigator` a creare ogni controller passandogli nel
costruttore quello che usa (il navigatore, la partita o il servizio delle partite) e a
consegnarlo a `FXMLLoader` con `setController`. Niente stato statico, niente framework di
iniezione: le dipendenze di un controller si leggono nel suo costruttore. `ViewNavigator`
carica anche il foglio di stile una volta sola, sulla scena.

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
- le classi con logica (`ExamRow`, `SlotNames`, `GameAssembly` come `GameService`) hanno test
  JUnit;
- un controllo eseguito a mano, fuori dal repository, ha caricato le tre schermate con i
  contenuti veri (FXML, campi `@FXML` dei controller e CSS) senza errori.
