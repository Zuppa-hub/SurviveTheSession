# 2026-11-05 — Timer concorrente (issue #23, 7c)

**Strumento:** Claude Code (Claude Opus 5.5)

**Richiesta:**
Proporre e poi realizzare in TDD `ScheduledQuestionTimer` rispettando i requisiti della issue
(notifiche tramite `Executor`, scadenze superate ignorate, tempo residuo protetto), con test
senza tempo reale e la pagina Wiki "Modelli di esecuzione" (lezione 16, GUI event-driven,
notifiche fuori dal lock, ripetibilità dei test). Decisioni di Andrea: controllo delle
scadenze vecchie solo nel timer con il numero di generazione; requisito di `shutdownNow()` in
`Application.stop()` annotato nella #11. Creare la issue per l'annotation dei parser.

**Prodotto dall'AI:**
- Issue #29 "Annotation personalizzata per i parser delle domande" (persistence), inserita
  nel piano del design dopo la #9; requisito sulla chiusura dello scheduler nella #11.
- Design §3.6 riscritto: costruttore con scheduler ed `Executor`, `synchronized` motivato,
  numero di generazione, notifiche fuori dal lock, test doubles.
- `ScheduledQuestionTimer`: `scheduleAtFixedRate` ogni secondo, stato sotto un lock privato,
  generazione incrementata da `start`/`stop`, consegna tramite l'`Executor` con controllo della
  generazione, scheduler non posseduto.
- Test: `ManualScheduler` (registra i compiti, il test fa scattare i secondi),
  `QueuedExecutor` (coda come `Platform.runLater`), test del timer e test end-to-end con
  `ExamBattle` (passato subito: la protezione è nel timer). Verificato che togliendo il
  controllo della generazione falliscano i due test sulle scadenze in coda. 534 test verdi.
- Wiki: nuova pagina "Modelli di esecuzione", Home e Classi e interfacce aggiornate.

**Modificato / verificato da me:**
- [ ] _da completare_
