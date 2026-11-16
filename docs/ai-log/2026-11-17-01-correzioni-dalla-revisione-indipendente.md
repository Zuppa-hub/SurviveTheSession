# 2026-11-17: Correzioni dalla revisione indipendente

**Strumento:** Claude Code (Claude Fable 5.2)

**Richiesta:**
Applicare le correzioni decise dopo la revisione indipendente del 16/11, in TDD, su un branch
`fix/revisione-indipendente` con una sola PR verso `develop`: coerenza della dichiarazione
sull'AI, timer (Caffè all'ultimo istante), cartella dei salvataggi illeggibile, partita nuova
salvata subito, pulizie nel codice, allineamento di design e Wiki, voci del registro. Prima,
controllare la PR #52 (pagina "Uso dell'AI") e farne il merge se era fatta bene.

**Prodotto dall'AI:**
- Merge della PR #52 dopo averla riletta: coerente con README e registro.
- Test rossi prima del codice (commit `test:`), poi le correzioni:
  - `ScheduledQuestionTimer`: la scadenza si decide alla consegna della notifica, sotto lock;
    secondi aggiunti con la scadenza già in coda tengono vivo il conto. Tre test deterministici
    con `ManualScheduler` e `QueuedExecutor`.
  - `ViewNavigator.savedGames()`: se l'elenco dei salvataggi non si può leggere, avviso in
    italiano (`Alerts.savesUnreadable`) e il gioco prosegue.
  - `GameAssembly.newGame` salva la partita appena creata; `Graduation` iniettata in `Game`.
  - `SaveMapper` senza `catch` di `NullPointerException`, con controlli espliciti;
    `XmlDocumentLoader` valida il costruttore; `Difficulty.distanceFrom` al posto di
    `ordinal()` in `QuestionPool`.
  - Tolti `SaveGameRepository.delete` (mai usato) e `standardInput` da `build.gradle.kts`.
- Documentazione: design allineato (`BattleListener`, `BattleRules`, ISP, `HashSet`, branch,
  salvataggio alla creazione, scadenza alla consegna); Wiki corretta dove non corrispondeva al
  codice (Estendibilità, Principi SOLID, Architettura, Persistenza con un salvataggio vero,
  Home) e nuova sezione "Il Caffè all'ultimo istante" in Modelli di esecuzione; scelte lasciate
  motivate in Classi e interfacce e Interfaccia grafica.
- `./gradlew clean build` verde, 727 test.

**Modificato / verificato da me:**
- Il prompt l'ho preparato con Claude in chat a partire dal rapporto della revisione; prima di
  passarlo a Claude Code ho tolto la modifica alla riga sul merge delle PR, perché quella riga
  è vera.
- Ho deciso io cosa correggere e cosa lasciare: delle osservazioni minori ho scelto di
  motivare nella Wiki `Student` che espone inventario e libretto, i testi italiani nel dominio,
  gli id degli oggetti come stringhe e il tempo scaduto dedotto nella UI, invece di cambiare il
  codice a due giorni dalla consegna.
- Ho autorizzato il merge della PR #52 solo dopo il controllo della pagina.
- Questa PR non è mergiata: la leggo io, con la "Guida alla revisione", e provo a mano i casi
  elencati lì prima del merge.
