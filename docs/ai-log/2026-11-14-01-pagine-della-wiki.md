# 2026-11-14 — Pagine della Wiki

**Strumento:** Claude Code (Claude Opus 5.5), sub-agente in un worktree separato

**Richiesta:**
Scrivere in italiano, in un worktree separato sul branch `docs/wiki`, le pagine della Wiki ancora
"previste" nella Home, tranne "Uso dell'AI": Funzionalità, Architettura e responsabilità, Principi
SOLID, Clean Code, Strutture dati equals e Stream, Testing, Estendibilità, Strumenti e metodologie.
Ogni esempio preso dal codice vero, collegamenti solo alle lezioni già citate nel progetto (03, 07,
10, 13, 16 e la lezione del Prof. Rossi sul testing), guide di estendibilità verificate sul codice;
PR su `develop` senza merge.

**Prodotto dall'AI:**
- `Funzionalita.md`: profili, sessione e boss finale, tipi di domanda e d'esame, timer, oggetti e
  abilità, esito e voto, ricompense e livelli, laurea su 110, salvataggio automatico e caricamento.
- `Architettura-e-responsabilita.md`: package e dipendenze ammesse (verificate sugli `import`),
  sottopackage dell'engine, tabella interfaccia → dove è definita → chi la implementa, composition
  root (`Main`, `GameApplication`, `GameContent`, `GameEffects`, `GameAssembly`, `AutoSave`).
- `Principi-SOLID.md`: i cinque principi con esempi dal codice e le alternative scartate.
- `Clean-Code.md`: nomi, metodi piccoli, DRY, parameter object, errori (`Preconditions`,
  `Optional`, `ContentException`, `SaveGameException`), accoppiamento temporale, cosa non è stato
  costruito.
- `Strutture-dati-equals-e-Stream.md`: motivo di ogni `List`, `HashSet` e mappa (`HashMap`,
  `LinkedHashMap`, `TreeMap`, `EnumMap`), identità per id e codice, classi senza `equals`, copie
  immutabili, catene di Stream spezzate.
- `Testing.md`: TDD nella storia di Git, i quattro test di contratto, sostituti deterministici,
  verifiche dei test con errori introdotti per prova (dal registro), come eseguire i test.
- `Estendibilita.md`: cinque guide con i file da toccare; indicati i punti in cui serve codice
  (`ExamType`, `ExamRow`, `GameEffects`, `GameContent`, `GameApplication`, `ShippedContentTest`).
- `Strumenti-e-metodologie.md`: Gradle (wrapper, toolchain, Kotlin DSL, task), Git (branch, commit,
  PR con merge commit, conflitti negli indici, tag `v0.1`), Issues ed etichette, Scrum leggero.
- Stato delle otto pagine aggiornato in `Home.md`. `./gradlew clean build` verde, 667 test.

**Modificato / verificato da me:**
- Ho chiesto di scrivere con un sub-agente in un worktree separato le pagine previste della Wiki,
  con esempi dal codice vero e collegamenti alle lezioni, senza la pagina Uso dell'AI.
- Ho letto la PR prima del merge.
