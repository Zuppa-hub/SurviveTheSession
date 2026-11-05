# 2026-11-05 — Refactor della ricerca nei cataloghi

**Strumento:** Claude Code (Claude Opus 5.5)

**Richiesta:**
I tre cataloghi cercavano per id scorrendo una lista con uno stream (ricerca O(n), anti-esempio
della lezione 14): indicizzarli in una `LinkedHashMap` al caricamento ed eliminare la
duplicazione, proponendo prima la soluzione.

**Prodotto dall'AI:**
- Proposta approvata: classe generica package-private `CatalogIndex<T>` usata per
  composizione (scartata una classe base astratta: le interfacce hanno metodi con nomi
  diversi, `findById` e `findByCode`); chiave estratta con un method reference.
- `test:` `CatalogIndexTest`; `refactor:` i tre cataloghi delegano a `CatalogIndex`.
  I test esistenti dei cataloghi, invariati, restano verdi.

**Modificato / verificato da me:**
- [ ] _da completare_
