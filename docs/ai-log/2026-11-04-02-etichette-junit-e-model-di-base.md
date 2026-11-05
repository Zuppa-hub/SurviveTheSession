# 2026-11-04 — Etichette, JUnit 6 e model di base (issue #2)

**Strumento:** Claude Code (Claude Opus 5.5)

**Richiesta:**
Aggiornare `develop`; trovare l'origine del warning di Gradle sulle funzionalità
deprecate; aggiungere le etichette `build`, `refactor`, `content`; portare JUnit alla
versione 6 tramite BOM. Poi issue #2: proporre le classi (campi, metodi, validazione) e
aspettare l'ok; dopo l'ok, TDD con un commit di test rossi e uno di implementazione per
ogni classe; cancellare `BuildSetupTest`; aprire la PR.

**Prodotto dall'AI:**
- Warning `Task.extensions at execution time` (solo su `./gradlew run`): origine
  `org.openjfx.gradle.JavaFXPlugin.java:95`, cioè il plugin JavaFX. Lasciato com'è.
- Etichette `build` (#1), `refactor` (#13), `content` (#12); issue #1 chiusa a mano con
  rimando alla PR #15; issue #2 e #8 aggiornate (ExamSession spostata nella #8).
- Su `develop`: JUnit BOM 6.1.3; `docs/design.md` (regola EnumMap tipo → strategia in
  §3.4, resistenza calcolata dall'engine in §2.3); convenzione dei commit in `CLAUDE.md`.
- Proposta delle classi approvata con modifiche: niente `resistance` in `Exam`,
  `Grade.label()` per la UI, massimo della concentrazione aumentabile con `improve`,
  lode = 30 nella media.
- Branch `feature/model-core`, dieci commit TDD (test rosso → implementazione):
  `Grade`, `Difficulty`, `Subject`, `Stats`, `Exam` (+ `ExamType`). 68 test verdi.

**Modificato / verificato da me:**
- [ ] _da completare_
