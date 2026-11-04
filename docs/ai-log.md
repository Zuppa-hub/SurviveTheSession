# Registro uso AI

Registro delle sessioni di lavoro con strumenti di AI. Per ogni sessione:
data, richiesta fatta all'AI, cosa ha prodotto, cosa ho modificato o verificato io.

---

## 2026-10-23 — Scheletro del progetto

**Strumento:** Claude Code (Claude Opus 5.5)

**Richiesta:**
Creare solo lo scheletro del progetto, senza logica di gioco: progetto Gradle
(Kotlin DSL) con wrapper, toolchain Java 25 e plugin `application`; package base
`it.unicam.cs.mpgc.rpg127295` con sottopackage `model`, `engine`, `persistence`,
`ui`, `app` (ognuno con `package-info.java`); `Main` che stampa "RPG avviato";
`.gitignore`; `README.md` dal template del corso; `CLAUDE.md` con le regole di
sviluppo; questo registro; inizializzazione git con branch `main` e `develop`.

**Prodotto dall'AI:**
- `settings.gradle.kts`, `build.gradle.kts`, Gradle Wrapper (`gradlew`, `gradlew.bat`, `gradle/wrapper/`)
- `package-info.java` per i cinque sottopackage e la classe `app.Main`
- `.gitignore`, `README.md`, `CLAUDE.md`, `docs/ai-log.md`
- repository git con commit iniziale su `main` e branch `develop`

**Modificato / verificato da me:**
- [ ] _da completare_

---

## 2026-11-04 — Backlog GitHub e configurazione della build (issue #1)

**Strumento:** Claude Code (Claude Opus 5.5)

**Richiesta:**
Leggere `docs/design.md` e rispettarlo; verificare il collegamento a GitHub e che
`main` e `develop` fossero pushati; aggiornare `CLAUDE.md` con la lettura del design
a inizio sessione e le regole della sezione 4; creare le 14 issue della sezione 5 e
le etichette per area; lavorare sulla issue #1 nel branch `feature/build-setup`
(JavaFX 25, Gson 2.13.2, JUnit Jupiter, test con `./gradlew test`) e aprire la PR su
`develop`.

**Prodotto dall'AI:**
- Verifica remote: al primo controllo `develop` non era su GitHub, l'AI si è fermata;
  dopo il push di `develop`, ha proseguito.
- Su `develop`: commit di `docs/design.md` (prima non tracciato) e di `CLAUDE.md`
  aggiornato (lettura del design, regole §4, sintesi dell'architettura).
- Etichette GitHub `model`, `engine`, `persistence`, `ui`, `docs`; issue #1–#14 con
  titolo, descrizione e branch previsto. La #1 (build) è senza etichetta: non rientra
  in nessuna delle cinque aree.
- Branch `feature/build-setup`, tre commit: plugin JavaFX 0.1.0 con JavaFX 25
  (`controls`, `fxml`); Gson 2.13.2; log dei test e `BuildSetupTest` (round-trip di un
  record con Gson).
- Pull request #15 su `develop` (`Closes #1`).

**Modificato / verificato da me:**
- Verificato ma non modificato il commit e pronto per il merge della PR #15. 
---

## 2026-11-04 (seconda parte) — Etichette, JUnit 6 e model di base (issue #2)

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
