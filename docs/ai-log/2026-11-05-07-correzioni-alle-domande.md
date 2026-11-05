# 2026-11-05 — Correzioni dopo la revisione delle domande

**Strumento:** Claude Code (Claude Opus 5.5)

**Richiesta:**
Applicare la revisione delle domande di Programmazione (nuovo indizio per PROG-01, risposta
"overload" accettata in PROG-18, PROG-16 EASY); aggiungere il prefisso `fix:` alla convenzione
dei commit in `CLAUDE.md`; ripristinare nel registro la voce della PR #20, persa nella
risoluzione del conflitto della PR #21.

**Prodotto dall'AI:**
- `fix:` le tre modifiche a `questions/prog.xml`; `ShippedContentTest` verde.
- `docs:` prefisso `fix:` in `CLAUDE.md`.
- La voce della PR #20 mancava solo sul branch `docs/readme` (su `develop` c'è ancora):
  recuperata dal commit `e507c0f` e rimessa al suo posto con un commit sulla PR #21.

**Modificato / verificato da me:**
- [x] revisione delle 18 domande di Programmazione
- [ ] _da completare_
