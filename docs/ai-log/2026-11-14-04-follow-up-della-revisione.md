# 2026-11-14 — Follow-up della revisione di #39, #40 e #41

**Strumento:** Claude Code (Claude Opus 5.5)

**Richiesta:**
Fare il merge di #40 e #41 in develop (#39 l'avevo già fatto io), risolvendo i conflitti
nell'indice dei log; poi una PR con le correzioni trovate durante la scrittura della Wiki:
`ExamRow.typeLabelOf` senza `null`, testi superati della Wiki, incoerenze tra design e codice,
numeri di Funzionalità aggiornati al bilanciamento, lezione 14 per `CatalogIndex`.

**Prodotto dall'AI:**
- Merge di #40 e #41: in entrambi il conflitto era solo nell'indice dei log, risolto tenendo
  tutte le righe in ordine di sessione; build verde prima di ogni merge. Rimosso il worktree
  della Wiki e i branch locali già mergiati.
- `ExamRow.typeLabelOf`: per un tipo senza etichetta restituiva `null` (regola 3). Ora lancia
  `IllegalStateException` con il nome del tipo. Test: ogni `ExamType` ha un'etichetta non
  vuota (`@EnumSource`) e `null` è rifiutato. Il test non poteva essere rosso, perché oggi
  tutti i tipi hanno un'etichetta: protegge dal giorno in cui se ne aggiunge uno.
- Wiki: `BattleEffect` al posto di `ItemEffect` e la mappa costruita in `GameEffects`; laurea
  in `Graduation` e non "in Game"; il repository dei salvataggi creato in
  `GameApplication.start`; Funzionalità con i valori della #12 (Secchione Energia 7, danno
  7 + Conoscenza/2, resistenza 4/4/5 per CFU, Energia persa 1/2/2 raddoppiata nel progetto,
  livello a 50 × n con +3 Energia massima) senza la nota "provvisori"; lezione 14 citata per
  la ricerca per chiave di `CatalogIndex`.
- Design §3.4: il libretto è una `LinkedHashMap`. Javadoc di `BattleListener`: i metodi
  `default` sono un compromesso sull'ISP, come scritto nella Wiki.

**Modificato / verificato da me:**
- Ho rivisto in chat #39, #40 e #41 e ho chiesto il merge in quest'ordine.
- Ho confermato che la lezione 14 è quella su `HashSet` e `HashMap`.
- Ho letto la PR di follow-up prima del merge.
