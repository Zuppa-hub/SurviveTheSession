# 2026-11-13 — GUI: esame, esito e laurea (issue #11)

**Strumento:** Claude Code (Claude Opus 5.5)

**Richiesta:**
Iniziare subito la #11 (schermata d'esame con timer reale, esito, laurea) in un branch che parte
da quello della PR #35, ancora in revisione; dopo il merge della #35 fare il rebase su
`develop`.

**Prodotto dall'AI:**
- `Question.codeSnippet()` (vuoto di default, ridefinito da `CodeOutputQuestion`), in TDD: lo
  snippet si mostra senza `instanceof`.
- Classi di supporto senza JavaFX, in TDD: `BattleAction` (oggetti e abilità come pulsanti,
  abilitati con `ExamBattle.canUse`), `AnswerFeedback` (giusta, sbagliata, tempo scaduto),
  `OutcomeSummary` (testi dell'esito da `ExamReport`, `EnumMap` per i titoli),
  `GraduationSummary` (libretto, media, CFU, voto su 110). `TestGames` condiviso dai test della
  UI; `ExamRowTest` lo usa.
- `ExamController` (`BattleListener` passato a `Game.startExam`, scontro letto da
  `Game.currentBattle()`), `OutcomeController`, `GraduationController`; `exam.fxml`,
  `outcome.fxml`, `graduation.fxml`, stili nuovi in `styles.css`.
- `ViewNavigator.openGame` registra una volta per partita il `GameListener` che mostra l'esito;
  `showExam` carica la vista e poi avvia l'esame. Tolto il segnaposto della #10.
- Controllo fuori dal repository con contenuti e timer veri: timer da 30 s a 28 s, risposte da
  pulsanti e campo di testo, snippet visibile, esito, salvataggio, ritiro, laurea; nessuna
  eccezione.
- Design §3.3 e §3.8; Wiki: Interfaccia grafica, Classi e interfacce, Modelli di esecuzione.
  662 test verdi.

**Modificato / verificato da me:**
- Ho chiesto di iniziare la #11 subito, partendo dal branch della #10, invece di aspettare il
  merge della PR #35.
- Ho rivisto la PR prima del merge e ho giocato una sessione con `./gradlew run`.
