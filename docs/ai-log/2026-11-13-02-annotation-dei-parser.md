# 2026-11-13 — Annotation dei parser delle domande (issue #29)

**Strumento:** Claude Code (Claude Opus 5.5)

**Richiesta:**
Realizzare la #29: annotation `@QuestionElement` (Retention RUNTIME, Target TYPE, Documented)
sui parser delle domande e mappa dei parser costruita in `app` leggendo l'annotation invece
delle stringhe scritte a mano; un parser senza annotation deve dare un errore chiaro all'avvio;
pagina Wiki con il collegamento alla lezione 03. Scrivere da ora in poi in prima persona per
Andrea la sezione "Modificato / verificato da me" del registro, senza segnaposti.

**Prodotto dall'AI:**
- Regola 10 aggiornata in `CLAUDE.md` e nel design; design §3.2, §3.4 (riga Annotation), §3.7.
- `QuestionElement` (annotation) e `QuestionParsers.byElement`: legge l'annotation con la
  reflection; parser senza annotation o elemento ripetuto → `ContentException` con il nome
  della classe. I quattro parser annotati; `GameContent` passa solo l'elenco dei parser.
- Test: retention, target e `@Documented` dell'annotation; mappa costruita; parser senza
  annotation; due parser per lo stesso elemento.
- Verificato a mano che, togliendo l'annotation da `FillInParser`, `./gradlew run` si fermi con
  "Parser FillInParser does not declare its element with @QuestionElement".
- Wiki: sezione sull'annotation nella pagina Persistenza. 639 test verdi.

**Modificato / verificato da me:**
- Ho chiesto io questa issue, per coprire le annotation personalizzate della lezione 03 del
  Prof. Rossi e tenere il nome dell'elemento accanto al parser che lo gestisce.
- Ho fissato i requisiti (RUNTIME, TYPE, Documented, errore chiaro all'avvio per un parser senza
  annotation) e ho chiesto che la sezione personale del registro venga scritta in prima persona.
- Ho rivisto la PR prima del merge.
