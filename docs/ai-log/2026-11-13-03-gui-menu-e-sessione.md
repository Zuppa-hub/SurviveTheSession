# 2026-11-13 — GUI: menu, nuova partita e sessione (issue #10)

**Strumento:** Claude Code (Claude Opus 5.5)

**Richiesta:**
Mentre la PR della #29 è in revisione, realizzare la #10 secondo il design §3.8: FXML, CSS e
controller sottili, `Platform::runLater` come esecutore del timer, `scheduler.shutdownNow()`
in `Application.stop()`, avviso visibile quando un salvataggio automatico fallisce; grafica
semplice e pulita.

**Prodotto dall'AI:**
- Design §3.2 e §3.8: `GameApplication`, `GameService`, `ViewNavigator`, classi di supporto.
- `ui.GameService` implementata da `GameAssembly` (con `profiles()`); `ExamRow` e `SlotNames`
  con test JUnit.
- `ViewNavigator`, `MenuController`, `NewGameController`, `SessionController`, `Alerts`;
  `menu.fxml`, `new-game.fxml`, `session.fxml`, `styles.css`.
- `app.GameApplication` (contenuti, `GameAssembly` con `Platform::runLater` e `Alerts::saveFailed`,
  `shutdownNow` in `stop()`); `Main` lancia l'applicazione.
- Controllo fuori dal repository: le tre schermate caricate con i contenuti veri senza errori.
  Wiki: pagina Interfaccia grafica. 645 test verdi.

**Modificato / verificato da me:**
- Ho chiesto di iniziare la #10 senza aspettare la revisione della #29 e ho fissato i requisiti
  (FXML, CSS, controller sottili, `Platform::runLater`, `shutdownNow` in `stop()`, avviso per il
  salvataggio fallito, grafica semplice).
- Ho rivisto la PR prima del merge, provando le schermate con `./gradlew run`.
