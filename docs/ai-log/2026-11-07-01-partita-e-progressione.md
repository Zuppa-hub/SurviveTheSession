# 2026-11-07 — Partita, progressione, ricompense e laurea (issue #8)

**Strumento:** Claude Code (Claude Opus 5.5)

**Richiesta:**
Compilare con i fatti forniti da Andrea le sezioni "Modificato / verificato da me" del
registro e aggiungere all'indice la nota sulla redazione e sul flusso di lavoro (commit
diretto su `develop`). Poi proporre e realizzare in TDD la #8 con il minimo necessario:
`ExamSession`, `Game` con avvio degli scontri e applicazione dell'esito, progressione con
soglie e bonus, `RewardPolicy`, laurea fuori dal libretto, notifiche minime, `GameEffects`
collegato davvero. Decisioni di Andrea: Energia ricaricata a ogni esame (motivo nella Wiki);
soglie delle ricompense provvisorie; niente partita di prova in `Main`;
`maxConcentrationBonus` = 1; ultimo esame della sessione come boss, disponibile solo dopo
gli altri.

**Prodotto dall'AI:**
- Registro: sezioni compilate e paragrafo sul flusso di lavoro in `docs/ai-log/README.md`.
- Design: §2.2 (Energia a ogni esame, progressione), §2.3 (esito, ricompense, boss, laurea),
  §2.7 (boss), §3.2 (`engine.game`, `GameAssembly`), §3.4 (SRP con `Graduation`).
- `ExamSession` (ordine e boss, senza stato duplicato), `ProgressionRules` /
  `StandardProgressionRules`, `Reward` / `RewardPolicy` / `StandardRewardPolicy`,
  `Graduation` (voto in 110), `Game` con `availableExams`, `ExamReport` e `GameListener`;
  `BattleListener` con metodi `default` vuoti.
- `app.GameAssembly`: composition root che collega contenuti, scontro, timer reale, effetti e
  ricompense; `Main` la costruisce e chiude lo scheduler.
- Prima del merge, su richiesta: `startExam(esame, listener)` registra il listener della UI
  e avvia lo scontro (niente accoppiamento temporale con `begin()`); design e Wiki aggiornati,
  con i metodi `default` di `BattleListener` spiegati come compromesso sull'ISP.
- Wiki aggiornata. Verificato che togliendo la ricarica dell'Energia il test dedicato
  fallisca. 587 test verdi; `./gradlew run` si avvia.

**Modificato / verificato da me:**
- Supervisione: dettati gli obiettivi della issue (minimo necessario, punti da chiarire nella
  proposta) e letta la proposta prima di approvarla.
- Revisione della proposta con Claude in chat: deciso di ricaricare l'Energia all'inizio di
  ogni esame (per non premiare chi si fa bocciare), tenute provvisorie le soglie delle
  ricompense, tolta la partita di prova da `Main`, aggiunto `maxConcentrationBonus` = 1 e
  introdotto il boss finale disponibile solo dopo gli altri esami (`availableExams()`).
- Revisione della PR con Claude in chat e prompt di correzione prima del merge: eliminato
  l'accoppiamento temporale fra `startExam` e `begin()` (`startExam(esame, listener)`) e fatto
  spiegare nella Wiki i metodi `default` di `BattleListener` come compromesso sull'ISP.
- PR letta prima del merge.
