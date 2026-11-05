# 2026-11-04 — Backlog GitHub e configurazione della build (issue #1)

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
