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
- [ ] _da completare_
