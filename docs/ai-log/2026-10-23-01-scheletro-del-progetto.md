# 2026-10-23 — Scheletro del progetto

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
