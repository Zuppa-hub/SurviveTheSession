# 2026-11-05 — Studente, oggetti e libretto (issue #4)

**Strumento:** Claude Code (Claude Opus 5.5)

**Richiesta:**
Correggere il design: `Item` nel model solo dati, comportamento in `ItemEffect`
nell'engine (§3.1); `Transcript` senza regola né voto di laurea (SRP, esempio
`puoLaurearsi()` della lezione). Proporre le classi della #4 e la divisione delle regole
di progressione tra model ed engine; dopo l'ok, TDD e PR.

**Prodotto dall'AI:**
- `docs:` design §3.2, §3.3, §3.4 aggiornati (`Item` dato, `ItemEffect` +
  `CoffeeEffect`/`NotesEffect`/`SummaryEffect`/`EnergyDrinkEffect` nell'engine, mappa
  `Item → ItemEffect` in `app`, laurea in `Game`); `items.xml`, `items.xsd`,
  `ItemCatalog` tra i contenuti. Issue #5 aggiornata con `items.xml`.
- TDD (coppie `test:` → `feat:`): `Item`, `Inventory`, `TranscriptEntry`,
  `Transcript`, `Level`, `StudentProfile`, `Student`. 260 test verdi.
- Progressione: nel model solo stato e invarianti (`Level.addExperience`,
  `Level.levelUp(cost)`, `Stats.improve`); soglie, bonus ed esperienza per esame
  nell'engine (#8).

**Modificato / verificato da me:**
- [ ] _da completare_
