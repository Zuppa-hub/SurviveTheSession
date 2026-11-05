# 2026-11-05 — Scontro e strategie d'esame (issue #7, 7a)

**Strumento:** Claude Code (Claude Opus 5.5)

**Richiesta:**
Aggiungere alla Wiki la sezione su `engine.grading` (commit diretto su `develop`); scrivere in
`CLAUDE.md` che la Wiki si aggiorna nella PR della issue. Proporre e poi realizzare in TDD la
#7a: `ExamBattle`, stato ed esito, listener, strategie scritto/orale/progetto dalla
`EnumMap`, provenienza delle domande, regole di resistenza e danno, opzioni mescolate con il
`RandomGenerator` senza modificare le domande, nessuna domanda ripetuta, orale che incalza,
solo l'interfaccia del timer. Decisioni di Andrea: Visitor nel model; a domande finite decide
il `GradeCalculator`; il Progetto non filtra per tipo di domanda.

**Prodotto dall'AI:**
- Wiki: `engine.grading` su `develop`; in questa PR le sezioni su `QuestionVisitor` (tre punti
  senza `instanceof` e il compromesso) e sullo scontro.
- Design: §2.3 (fine della prova), §2.4 (Progetto), §3.2-§3.4 (nuove classi, firme di
  `ExamStrategy`, `BattleRules`, Visitor).
- `QuestionVisitor` e `Question.accept` (final nelle domande a scelta); verificato nei test
  di contratto delle domande.
- `QuestionPool` (HashSet delle domande poste, difficoltà più vicina), `PresentedQuestion`
  (opzioni mescolate), `BattleRules`/`StandardBattleRules` (valori provvisori, EnumMap),
  `BattleState`, `AnswerResult`, `BattleOutcome` (voto se e solo se superato), strategie con
  `ExamStrategyContractTest`, `QuestionTimer`/`TimerListener`, `ExamBattle` ed
  `ExamBattleFactory`; `Performance.none/withCorrectAnswer/withWrongAnswer`.
- Refactor richiesto prima del merge: `ExamStrategy` spostata in `engine.battle` per
  eliminare il ciclo con `engine.strategy` (DIP); design e Wiki aggiornati. Requisiti
  annotati nella issue #23 (Executor per le notifiche, scadenze superate, tempo protetto).
- Nei test: `ManualQuestionTimer`, `FakeRandom`, regole fisse, calcolatori come lambda.
  Verificato che togliendo `harder()` dall'orale falliscano tre test. 463 test verdi.

**Modificato / verificato da me:**
- [ ] _da completare_
