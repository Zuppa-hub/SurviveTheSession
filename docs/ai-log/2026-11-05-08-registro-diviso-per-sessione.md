# 2026-11-05 — Registro AI diviso in un file per sessione

**Strumento:** Claude Code (Claude Opus 5.5)

**Richiesta:**
Dividere `docs/ai-log.md` in un file per sessione (`docs/ai-log/AAAA-MM-GG-NN-titolo.md`)
con `docs/ai-log/README.md` come indice; aggiornare `CLAUDE.md` e la regola 10 del design.

**Prodotto dall'AI:**
- Un file per ciascuna delle sessioni precedenti, testo invariato (verificato con un diff
  rispetto al file originale); il titolo diventa `# AAAA-MM-GG — titolo`, perché l'ordine
  nel giorno è nel numero `NN` del nome del file.
- Nel merge delle PR #21, #24 e #25 le voci 5, 6 e 7 si erano mescolate (titoli uno sotto
  l'altro, testi sovrapposti, metà della voce della #6 persa): ricostruite dalle versioni
  intatte nella storia di Git (`f440c3b`, `3e01eb3`, `34da6de`).
- `docs/ai-log/README.md` con l'indice; regola 10 aggiornata in `CLAUDE.md` e in
  `docs/design.md` §4; link aggiornato nel README del progetto.

**Modificato / verificato da me:**
- [ ] _da completare_
