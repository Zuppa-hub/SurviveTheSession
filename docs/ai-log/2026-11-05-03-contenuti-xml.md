# 2026-11-05 — Contenuti XML (issue #5)

**Strumento:** Claude Code (Claude Opus 5.5)

**Richiesta:**
Spostare le interfacce di repository e cataloghi nell'engine (DIP); proporre la lettura
dei contenuti con sole API del JDK (DOM, XSD, XPath), XSD che impongano davvero le regole,
fail fast con eccezione dedicata, niente `switch` sul tipo di domanda, XPath dove serve;
dopo l'ok, TDD e una materia (Programmazione) con 15-20 domande in un commit separato.

**Prodotto dall'AI:**
- `docs:` interfacce in `engine.repository` (anche `SaveData` e `SaveMapper`), design
  §3.1-3.4 e §3.7 aggiornati.
- `XmlDocumentLoader` (validazione durante il parsing, `ErrorHandler` che lancia,
  DOCTYPE disattivato), `ContentException`, `XmlReader` (XPath con espressioni compilate
  una volta), `XmlItemCatalog`, `XmlProfileCatalog`, `XmlExamCatalog`,
  `XmlQuestionRepository` con `QuestionParser` registrati per nome di elemento.
- XSD con `common.xsd` incluso: 1 `correct` + 3 `wrong`, `xs:unique` sulle opzioni,
  enumerazioni, `xs:key`/`xs:keyref`, testi non vuoti. Verificato con i test, con
  `./gradlew run` e avviando dal JAR (`installDist`).
- `app.GameContent` e `Main` che carica i contenuti all'avvio e si ferma con un messaggio
  se non sono validi.
- 18 domande di Programmazione in un commit a parte; output degli snippet verificati
  eseguendo il codice. 322 test verdi.

**Modificato / verificato da me:**
- Fatte spostare le interfacce dei repository nell'engine (DIP).
- Chiesto `common.xsd` con `xs:include` invece di schemi duplicati.
- Riviste le 18 domande di Programmazione una per una: corretto l'indizio della PROG-01,
  aggiunta la risposta "overload" alla PROG-18, PROG-16 portata a EASY.
