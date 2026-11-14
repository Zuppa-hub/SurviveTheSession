# 2026-11-14 — Screenshot della GUI e chiusura della v0.1

**Strumento:** Claude Code (Claude Opus 5.5)

**Richiesta:**
Creare il tag `v0.1` sul merge della release, cancellare su GitHub i branch già uniti e
attivare la cancellazione automatica dopo il merge, aggiornare `main` e `develop` locali;
completare la #11 con gli screenshot di menu, sessione ed esame e con l'elenco di cosa provare
a mano.

**Prodotto dall'AI:**
- Tag annotato `v0.1` sul merge commit della release (`9d44b37`), pubblicato su GitHub.
- Cancellati 16 branch remoti già uniti (verificato prima che fossero contenuti in `main`;
  `feature/ui-session` aveva in più solo il merge della #36, con lo stesso contenuto di
  `main`); attivata la cancellazione automatica dei branch dopo il merge.
- `docs/images/menu.png`, `sessione.png`, `esame.png`, generate con un programma fuori dal
  repository che apre le schermate con i contenuti veri e ne salva l'istantanea
  (`Scene.snapshot`); inserite nel README e nella Wiki Interfaccia grafica.
- PR con la guida alla revisione e l'elenco delle prove a mano di tutta la GUI.

**Modificato / verificato da me:**
- Ho unito la #36 e la #35 prima della release: la #36 è finita su `feature/ui-session` e
  ho unito la #38, che ha riportato la #11 su `develop`, prima della release.
- Ho chiesto tag, pulizia dei branch e screenshot, e ho provato la GUI con `./gradlew run`.
- Ho letto la PR prima del merge.
