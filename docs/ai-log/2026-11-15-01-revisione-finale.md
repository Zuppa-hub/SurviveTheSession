# 2026-11-15 — Revisione finale del codice (issue #13)

**Strumento:** Claude Code (Claude Opus 5.5)

**Richiesta:**
Tornare alla regola del CLAUDE.md (niente merge da parte di Claude Code). Togliere "prima del
merge" dall'ultima frase del flusso di lavoro nell'indice del registro (PR #49). Rivedere
tutto il codice rispetto al §4 del design, a SOLID e a Clean Code, correggendo solo ciò che lo
migliora davvero, con una tabella dei problemi trovati nella PR.

**Prodotto dall'AI:**
- PR #49: una sola frase cambiata in `docs/ai-log/README.md`.
- Controlli fatti con piccoli script, fuori dal repository: righe per classe e per metodo,
  metodi pubblici mai usati fuori dai test, metodi delle interfacce senza Javadoc, classi di
  model ed engine senza test, import tra package, numeri letterali, stringhe ripetute nella UI,
  `switch`, `instanceof` e `null`.
- Corretto: tolti `Game.isSessionComplete` e `BattleState.isExamDefeated` (codice morto) e un
  import inutile in `ExamBattle`; `StatTexts` (TDD) per i testi ripetuti in sessione, esame e
  laurea, con il 110 preso da `Graduation`; `Placeholders` al posto di un metodo statico di
  `MenuController` usato dalla sessione; il messaggio dopo una risposta toglie lo stile di ogni
  `AnswerFeedback`; Javadoc dei contratti di cinque interfacce; test di `BattleServices` e
  `SaveGameException`.
- Lasciato, con il motivo nella PR: `ExamController` in un solo controller, `ExamBattle` lungo
  ma con metodi brevi, il costruttore di `GameAssembly` (composition root), i campi `null`
  interni di `ScheduledQuestionTimer`, i numeri nelle tabelle delle regole.
- Wiki Clean Code (sezione sulla revisione, valore di `EXPERIENCE_PER_LEVEL` aggiornato),
  Testing (720 test) e Interfaccia grafica.

**Modificato / verificato da me:**
- Ho rivisto in chat le PR #42-#48 dopo il merge e ho chiesto di tornare alla regola: le PR le
  mergio io.
- Ho indicato cosa controllare nella revisione e chiesto di non fare refactoring estetici né
  introdurre pattern nuovi.
- Rivedo la PR in chat con Claude prima del merge.
