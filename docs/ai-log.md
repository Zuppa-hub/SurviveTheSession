# Registro uso AI

Registro delle sessioni di lavoro con strumenti di AI. Per ogni sessione:
data, richiesta fatta all'AI, cosa ha prodotto, cosa ho modificato o verificato io.

---

## 2026-10-23 — Scheletro del progetto

**Strumento:** Claude Code (Claude Opus 5.5)

**Richiesta:**
Creare solo lo scheletro del progetto, senza logica di gioco: progetto Gradle
(Kotlin DSL) con wrapper, toolchain Java 25 e plugin `application`; package base
`it.unicam.cs.mpgc.rpg127295` con sottopackage `model`, `engine`, `persistence`,
`ui`, `app` (ognuno con `package-info.java`); `Main` che stampa "RPG avviato";
`.gitignore`; `README.md` dal template del corso; `CLAUDE.md` con le regole di
sviluppo; questo registro; inizializzazione git con branch `main` e `develop`.

**Prodotto dall'AI:**
- `settings.gradle.kts`, `build.gradle.kts`, Gradle Wrapper (`gradlew`, `gradlew.bat`, `gradle/wrapper/`)
- `package-info.java` per i cinque sottopackage e la classe `app.Main`
- `.gitignore`, `README.md`, `CLAUDE.md`, `docs/ai-log.md`
- repository git con commit iniziale su `main` e branch `develop`

**Modificato / verificato da me:**
- [ ] _da completare_

---

## 2026-11-04 — Backlog GitHub e configurazione della build (issue #1)

**Strumento:** Claude Code (Claude Opus 5.5)

**Richiesta:**
Leggere `docs/design.md` e rispettarlo; verificare il collegamento a GitHub e che
`main` e `develop` fossero pushati; aggiornare `CLAUDE.md` con la lettura del design
a inizio sessione e le regole della sezione 4; creare le 14 issue della sezione 5 e
le etichette per area; lavorare sulla issue #1 nel branch `feature/build-setup`
(JavaFX 25, Gson 2.13.2, JUnit Jupiter, test con `./gradlew test`) e aprire la PR su
`develop`.

**Prodotto dall'AI:**
- Verifica remote: al primo controllo `develop` non era su GitHub, l'AI si è fermata;
  dopo il push di `develop`, ha proseguito.
- Su `develop`: commit di `docs/design.md` (prima non tracciato) e di `CLAUDE.md`
  aggiornato (lettura del design, regole §4, sintesi dell'architettura).
- Etichette GitHub `model`, `engine`, `persistence`, `ui`, `docs`; issue #1–#14 con
  titolo, descrizione e branch previsto. La #1 (build) è senza etichetta: non rientra
  in nessuna delle cinque aree.
- Branch `feature/build-setup`, tre commit: plugin JavaFX 0.1.0 con JavaFX 25
  (`controls`, `fxml`); Gson 2.13.2; log dei test e `BuildSetupTest` (round-trip di un
  record con Gson).
- Pull request #15 su `develop` (`Closes #1`).

**Modificato / verificato da me:**
- Verificato ma non modificato il commit e pronto per il merge della PR #15. 
---

## 2026-11-04 (seconda parte) — Etichette, JUnit 6 e model di base (issue #2)

**Strumento:** Claude Code (Claude Opus 5.5)

**Richiesta:**
Aggiornare `develop`; trovare l'origine del warning di Gradle sulle funzionalità
deprecate; aggiungere le etichette `build`, `refactor`, `content`; portare JUnit alla
versione 6 tramite BOM. Poi issue #2: proporre le classi (campi, metodi, validazione) e
aspettare l'ok; dopo l'ok, TDD con un commit di test rossi e uno di implementazione per
ogni classe; cancellare `BuildSetupTest`; aprire la PR.

**Prodotto dall'AI:**
- Warning `Task.extensions at execution time` (solo su `./gradlew run`): origine
  `org.openjfx.gradle.JavaFXPlugin.java:95`, cioè il plugin JavaFX. Lasciato com'è.
- Etichette `build` (#1), `refactor` (#13), `content` (#12); issue #1 chiusa a mano con
  rimando alla PR #15; issue #2 e #8 aggiornate (ExamSession spostata nella #8).
- Su `develop`: JUnit BOM 6.1.3; `docs/design.md` (regola EnumMap tipo → strategia in
  §3.4, resistenza calcolata dall'engine in §2.3); convenzione dei commit in `CLAUDE.md`.
- Proposta delle classi approvata con modifiche: niente `resistance` in `Exam`,
  `Grade.label()` per la UI, massimo della concentrazione aumentabile con `improve`,
  lode = 30 nella media.
- Branch `feature/model-core`, dieci commit TDD (test rosso → implementazione):
  `Grade`, `Difficulty`, `Subject`, `Stats`, `Exam` (+ `ExamType`). 68 test verdi.

**Modificato / verificato da me:**
- [ ] _da completare_

---

## 2026-11-05 — Gerarchia delle domande (issue #3)

**Strumento:** Claude Code (Claude Opus 5.5)

**Richiesta:**
Fissare la convenzione delle eccezioni nella regola 3 del design; introdurre una classe di
controlli condivisa come primo commit della #3; proporre la gerarchia delle domande
(forma della risposta, Vero/Falso e 50/50, normalizzazione del Completamento, LSP di
`CodeOutputQuestion`) e aspettare l'ok; poi TDD con un commit di test e uno di
implementazione per classe, PR su `develop`.

**Prodotto dall'AI:**
- Su `develop`: regola 3 (`Objects.requireNonNull` per i null, `IllegalArgumentException`
  per i valori non validi) in `docs/design.md` e `CLAUDE.md`.
- `refactor:` `model.Preconditions` (`requireNonBlank`, `requirePositive`,
  `requireNonNegative`) con test, usata in `Subject`, `Exam` e `Stats`.
- `refactor:` di `StatsTest` (`@CsvSource`, `assertAll`, rinomina di un test): era
  richiesto ma mancava, fatto prima dei commit `test:` della #3.
- `docs:` in §3.3 regola del 50/50 (`wrongOptions().size() >= 3`) e uguaglianza per id
  come contratto di `Question`.
- Gerarchia: `Question`, `ChoiceQuestion`, `QuestionDetails` (parameter object),
  `AbstractQuestion` (equals/hashCode `final` su id), `AbstractChoiceQuestion`
  (validazione delle opzioni e `isCorrect` `final`), `MultipleChoiceQuestion`,
  `TrueFalseQuestion`, `CodeOutputQuestion`, `FillInQuestion`.
- Test di contratto astratti `QuestionContractTest` e `ChoiceQuestionContractTest`,
  ereditati dai test di ogni tipo concreto (LSP verificato dai test). 194 test verdi.

**Modificato / verificato da me:**
- [ ] _da completare_

---

## 2026-11-05 (seconda parte) — Studente, oggetti e libretto (issue #4)

**Strumento:** Claude Code (Claude Opus 5.5)

**Richiesta:**
Correggere il design: `Item` nel model solo dati, comportamento in `ItemEffect`
nell'engine (§3.1); `Transcript` senza regola né voto di laurea (SRP, esempio
`puoLaurearsi()` della lezione). Proporre le classi della #4 e la divisione delle regole
di progressione tra model ed engine; dopo l'ok, TDD e PR.

**Prodotto dall'AI:**
- `docs:` design §3.2, §3.3, §3.4 aggiornati (`Item` dato, `ItemEffect` +
  `CoffeeEffect`/`NotesEffect`/`SummaryEffect`/`EnergyDrinkEffect` nell'engine, mappa
  `Item → ItemEffect` in `app`, laurea in `Game`); `items.xml`, `items.xsd`,
  `ItemCatalog` tra i contenuti. Issue #5 aggiornata con `items.xml`.
- TDD (coppie `test:` → `feat:`): `Item`, `Inventory`, `TranscriptEntry`,
  `Transcript`, `Level`, `StudentProfile`, `Student`. 260 test verdi.
- Progressione: nel model solo stato e invarianti (`Level.addExperience`,
  `Level.levelUp(cost)`, `Stats.improve`); soglie, bonus ed esperienza per esame
  nell'engine (#8).

**Modificato / verificato da me:**
- [ ] _da completare_

---

## 2026-11-05 (terza parte) — Contenuti XML (issue #5)

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
- [ ] revisione delle 18 domande una per una
- [ ] _da completare_

---

## 2026-11-05 (quarta parte) — Refactor della ricerca nei cataloghi

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

---

## 2026-11-05 (sesta parte) — Calcolo del voto (issue #6) e divisione della #7
## 2026-11-05 (quinta parte) — README e Wiki

**Strumento:** Claude Code (Claude Opus 5.5)

**Richiesta:**
Proporre `Performance` e `PerformanceGradeCalculator` e aspettare l'ok; rendere esplicito
il contratto di `GradeCalculator` in Javadoc e verificarlo con un test di contratto
astratto; dividere la #7 in tre issue (scontro e strategie, oggetti e abilità, timer).

**Prodotto dall'AI:**
- Issue #7 aggiornata (7a) e create #22 (7b) e #23 (7c); tabella di §5 del design aggiornata.
- `Performance` (risposte corrette, sbagliate, indizi usati; `answered()`, `isFlawless()`).
- `GradeCalculator` (`@FunctionalInterface`) con il contratto in Javadoc: voto valido o
  `Optional.empty()`, mai null, nessuna corretta → insufficiente, perfetta → sufficiente,
  monotonia su risposte corrette e su indizi (regola aggiunta su richiesta).
- `PerformanceGradeCalculator`: soglia 60%, da 18 a 30 in modo lineare arrotondando per
  difetto, −1 per indizio senza scendere sotto 18, lode solo se perfetta; solo interi.
- `GradeCalculatorContractTest` astratto su una griglia di 484 prestazioni, esteso da
  `PerformanceGradeCalculatorTest`. Verificato che fallisca rendendo la penalità un bonus.
  351 test verdi.
Riscrivere il README dal template del corso senza segnaposto; creare `docs/wiki/` con
`Home.md` (descrizione e indice delle pagine di design §6) e una bozza di
`Classi-e-interfacce.md` per il model; aggiungere a `CLAUDE.md` la regola di aggiornare la
Wiki a ogni issue chiusa.

**Prodotto dall'AI:**
- `README.md`: descrizione, prerequisiti, comandi `build`/`run`/`test`, struttura dei
  package, sezione breve sull'uso dell'AI.
- `docs/wiki/Home.md` con avvio rapido e indice (stato di ogni pagina);
  `docs/wiki/Classi-e-interfacce.md` con le scelte del model motivate.
- Regola sulla Wiki in `CLAUDE.md`.

**Modificato / verificato da me:**
- [ ] _da completare_
