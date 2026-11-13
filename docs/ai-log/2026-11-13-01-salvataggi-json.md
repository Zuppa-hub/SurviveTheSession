# 2026-11-13 — Salvataggi JSON (issue #9)

**Strumento:** Claude Code (Claude Opus 5.5)

**Richiesta:**
Scrivere in `CLAUDE.md` il flusso di lavoro dal 12/11 (soluzione decisa e implementata senza
approvazione preventiva, "Guida alla revisione" in ogni PR, niente merge) e aggiornare il
paragrafo del registro; poi realizzare la #9: `SaveData` e `SaveMapper` in
`engine.repository`, `JsonSaveGameRepository` con Gson, cartella iniettata, stream
bufferizzati UTF-8, versione del formato, slot validati, scrittura atomica, file mancante →
vuoto e file corrotto o versione ignota → eccezione dedicata, salvataggio automatico come
`GameListener` in `app`.

**Prodotto dall'AI:**
- `CLAUDE.md`: sezione "Flusso dal 12/11"; paragrafo sul flusso aggiornato nell'indice del
  registro.
- Design §3.2, §3.3, §3.4 (DIP), §3.7: versione del formato, slot validati, scrittura
  atomica, errori, `AutoSave` in `app`; gli esami rimanenti non si salvano (si ricavano).
- `SaveData` con `StatsData`, `SaveGameException`, `SaveGameRepository.requireValidSlot`,
  `SaveMapper` (risolve gli id con i cataloghi, valida con i costruttori del dominio).
- `JsonSaveGameRepository`: file `{formatVersion, game}`, scrittura su file temporaneo e
  `ATOMIC_MOVE`, lettura che rifiuta file corrotti e versioni sconosciute.
- `AutoSave` e `GameAssembly.newGame(nome, profilo, slot)`, `loadGame`, `savedGames`; `Main`
  con la cartella `~/.sopravvivi-alla-sessione/saves`.
- Wiki: pagina Persistenza. Verificato che togliendo la registrazione di `AutoSave` il test
  dedicato fallisca. 632 test verdi.

**Modificato / verificato da me:**
- [ ] _da completare_
