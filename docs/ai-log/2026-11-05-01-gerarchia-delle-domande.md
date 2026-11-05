# 2026-11-05 — Gerarchia delle domande (issue #3)

**Strumento:** Claude Code (Claude Opus 5.5)

**Richiesta:**
Fissare la convenzione delle eccezioni nella regola 3 del design; introdurre una classe di
controlli condivisa come primo commit della #3; proporre la gerarchia delle domande
(forma della risposta, Vero/Falso e 50/50, normalizzazione del Completamento, LSP di
`CodeOutputQuestion`) e aspettare l'ok; poi TDD con un commit di test e uno di
implementazione per classe, PR su `develop`.

**Prodotto dall'AI:**
- Su `develop`: regola 3 (`Objects.requireNonNull` per i null, `IllegalArgumentException`
  per i valori non validi) in `docs/design.md` e `CLAUDE.md`.
- `refactor:` `model.Preconditions` (`requireNonBlank`, `requirePositive`,
  `requireNonNegative`) con test, usata in `Subject`, `Exam` e `Stats`.
- `refactor:` di `StatsTest` (`@CsvSource`, `assertAll`, rinomina di un test): era
  richiesto ma mancava, fatto prima dei commit `test:` della #3.
- `docs:` in §3.3 regola del 50/50 (`wrongOptions().size() >= 3`) e uguaglianza per id
  come contratto di `Question`.
- Gerarchia: `Question`, `ChoiceQuestion`, `QuestionDetails` (parameter object),
  `AbstractQuestion` (equals/hashCode `final` su id), `AbstractChoiceQuestion`
  (validazione delle opzioni e `isCorrect` `final`), `MultipleChoiceQuestion`,
  `TrueFalseQuestion`, `CodeOutputQuestion`, `FillInQuestion`.
- Test di contratto astratti `QuestionContractTest` e `ChoiceQuestionContractTest`,
  ereditati dai test di ogni tipo concreto (LSP verificato dai test). 194 test verdi.

**Modificato / verificato da me:**
- [ ] _da completare_
