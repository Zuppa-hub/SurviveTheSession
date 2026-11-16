# Estendibilità

Guide passo passo per i cinque casi previsti dal design (§6). Ogni passo è stato controllato sul
codice: per ognuno è indicato **quale file cambia** e, dove serve, quale codice va toccato. Dove
un'estensione non è "solo una classe nuova" lo si dice chiaramente.

| Estensione | Codice Java del gioco da modificare | Nuove classi |
|---|---|---|
| nuova materia | nessuno (solo XML) | nessuna |
| nuovo tipo di domanda a scelta | una riga in `GameContent` | domanda, parser |
| nuova categoria di domanda | `QuestionVisitor` e i suoi visitor, probabilmente la schermata d'esame | domanda, parser |
| nuovo tipo d'esame | `ExamType`, `GameAssembly.strategies()`, `Labels` | strategia |
| nuovo oggetto | una voce in `GameEffects` | un effetto, se nessuno di quelli esistenti va bene |
| nuovo formato di salvataggio | una riga in `GameApplication` | il repository |

## 1. Nuova materia, senza codice

Una materia è un dato (`Subject` è un record letto dai contenuti, non un enum), quindi si aggiunge
solo con l'XML in `src/main/resources/content/`.

1. **`exams.xml`: la materia.** Aggiungere un elemento `subject` con un id fatto di lettere, numeri,
   `-` e `_`:
   ```xml
   <subject id="SO">
       <name>Sistemi Operativi</name>
   </subject>
   ```
2. **`exams.xml`: l'esame.** Aggiungere un `exam` che la riferisce. `type` è `WRITTEN`, `ORAL` o
   `PROJECT`, `difficulty` è `EASY`, `MEDIUM` o `HARD`:
   ```xml
   <exam code="SO1" subject="SO" type="WRITTEN" credits="9" difficulty="MEDIUM"/>
   ```
   **L'ordine conta**: la sessione segue l'ordine del file e l'ultimo esame è il boss finale. Per
   lasciare Metodologie di Programmazione come boss, il nuovo esame va prima di `MDP1`.
3. **Le domande.** Creare `questions/so.xml` (l'id della materia in minuscolo), con la radice
   `<questions subject="SO">` e le domande negli elementi `multipleChoice`, `trueFalse`,
   `codeOutput` e `fillIn`, come in `questions/prog.xml`. Gli id delle domande devono essere unici
   in **tutti** i file, non solo in questo.
4. **Avviare il gioco.** All'avvio `exams.xml` e `so.xml` sono validati con gli XSD: una materia
   inesistente, un id ripetuto, un'opzione doppia o una domanda a scelta senza tre opzioni
   sbagliate fermano il gioco con una `ContentException` che indica file e id.
5. **Aggiornare `ShippedContentTest`.** Non serve codice del gioco, ma questo test controlla i
   contenuti distribuiti e va adeguato: `theSessionHasFiveExamsWithTheProjectAsFinalBoss` confronta
   l'elenco esatto dei codici e dei tipi degli esami. Gli altri test impongono le regole dei
   contenuti del progetto: da 15 a 20 domande per esame, tutti e quattro i tipi di domanda, tutte e
   tre le difficoltà (servono all'orale che incalza).

I salvataggi esistenti restano validi: riferiscono gli esami per codice, e il nuovo esame risulta
semplicemente da sostenere. Al contrario, **togliere o rinominare** un esame rende illeggibili i
salvataggi che lo contengono (`SaveGameException`: "Unknown exam in saved game").

## 2. Nuovo tipo di domanda

Il caso più semplice è un nuovo tipo **dentro una categoria esistente**, ad esempio un'altra
domanda a scelta. Una **categoria nuova** richiede più lavoro (sotto).

### Una nuova domanda a scelta
1. **La classe nel model** (`model.question`): estendere `AbstractChoiceQuestion`, che valida le
   opzioni e implementa `isCorrect`, `options`, `wrongOptions` e `accept` (tutti `final`).
   `MultipleChoiceQuestion` è un esempio di poche righe: il costruttore controlla solo il numero
   di opzioni con `requireOptionCount`. Se la domanda mostra del codice, ridefinire
   `codeSnippet()` come fa `CodeOutputQuestion`.
2. **Il test**: estendere `ChoiceQuestionContractTest` e implementare i metodi che forniscono le
   istanze. Il contratto di `Question` e `ChoiceQuestion` viene verificato senza scriverlo di nuovo.
3. **Il parser** (`persistence.content`): una classe che implementa `QuestionParser` con
   l'annotation dell'elemento XML:
   ```java
   @QuestionElement("orderedChoice")
   public final class OrderedChoiceParser implements QuestionParser { ... }
   ```
   I dati comuni (id, difficoltà, testo, indizio) arrivano già letti in `QuestionDetails`; per
   `correct` e `wrong` c'è `ChoiceOptions.read(element, reader)`.
4. **Registrare il parser** in `GameContent.questionParsers()`, aggiungendolo all'elenco passato a
   `QuestionParsers.byElement`. È l'unica riga di codice esistente da toccare: l'elemento si legge
   dall'annotation, e un parser senza annotation o due parser per lo stesso elemento fermano
   l'avvio.
5. **Lo schema** `questions.xsd`: un `complexType` che estende `question` e un `xs:element` nella
   `xs:choice` di `questions`, con un `xs:unique` sulle opzioni se servono distinte.

Non cambiano `XmlQuestionRepository`, l'engine e la UI. Per l'engine è una `ChoiceQuestion`:
`PresentedQuestion` ne mescola le opzioni e la schermata mostra i pulsanti. Gli Appunti funzionano
da soli: si usano solo se sono visibili almeno tre opzioni sbagliate.

Un'attenzione: `ShippedContentTest.notesCanBeUsedOnEveryFourOptionQuestion` richiede almeno tre
opzioni sbagliate a ogni domanda a scelta tranne il Vero/Falso. Se il nuovo tipo ne ha meno, il
test va adeguato.

### Una nuova categoria
Se la risposta non è "scegli un'opzione" né "scrivi un testo" (ad esempio mettere in ordine degli
elementi), servono in più:
1. un metodo in `QuestionVisitor` (ad esempio `visitOrdering`). Il compilatore segnala ogni visitor
   da completare: `PresentedQuestion` (l'unico nel codice del gioco) e, nei test, `CategoryVisitor`
   e il visitor di `QuestionContractTest`;
2. l'`accept` della nuova classe che chiama il nuovo metodo;
3. un modo di rispondere nella schermata d'esame. Oggi `ExamController` decide tra pulsanti e campo
   di testo guardando se `PresentedQuestion.options()` è vuota: una risposta di altra forma richiede
   un controllo nuovo in `exam.fxml` e nel controller.

È il compromesso del Visitor descritto in [Classi e interfacce](Classi-e-interfacce): una categoria
nuova tocca più punti, ma il compilatore li indica tutti.

## 3. Nuovo tipo d'esame

Un tipo d'esame è un comportamento (Strategy): prossima domanda, timer, peso degli errori.

1. **L'enum**: aggiungere la costante a `ExamType` (ad esempio `LAB`). `ExamType` non ha
   comportamento, quindi la modifica è solo questa.
2. **La strategia** in `engine.strategy`: una classe che implementa `ExamStrategy` (`nextQuestion`,
   `secondsPerQuestion`, `mistakeMultiplier`). Per scegliere le domande ha già `QuestionPool`
   (`drawAny`, `drawClosestTo`) e lo stato dello scontro (`BattleState.lastAnswer()`).
3. **Il test**: estendere `ExamStrategyContractTest` (nessuna domanda ripetuta, risultato vuoto solo
   a domande finite, secondi positivi, moltiplicatore almeno 1) e aggiungere i casi propri.
4. **Collegarla** in `GameAssembly.strategies()`:
   ```java
   strategies.put(ExamType.LAB, new LabExamStrategy());
   ```
   Se si dimentica, `ExamBattleFactory` ferma l'avvio con "No strategy for exam types [LAB]".
5. **Lo schema**: aggiungere `LAB` all'enumerazione `examType` di `exams.xsd`, altrimenti un esame
   con `type="LAB"` non supera la validazione. `XmlExamCatalog` usa `ExamType.valueOf`, quindi non
   cambia.
6. **L'etichetta nella UI**: aggiungere il nome in italiano alla mappa `EXAM_TYPES` di `Labels`.
   Qui **non c'è un controllo all'avvio**: senza la voce, `Labels.of(ExamType)` lancia
   `IllegalStateException` ("No label for LAB") quando la sessione o l'esame mostrano il tipo.

Lo scontro (`ExamBattle`) non cambia: non sa di che tipo è l'esame.

## 4. Nuovo oggetto

Un oggetto è un dato (`Item` in `items.xml`) più un comportamento (`BattleEffect`) associato per id.

1. **`items.xml`**: un nuovo `item` con id, nome e descrizione (nome e descrizione sono quelli che
   la schermata d'esame mostra al giocatore).
2. **L'effetto.** Se ne basta uno esistente (`ExtraTimeEffect`, `FiftyFiftyEffect`, `HintEffect`,
   `EnergyRestoreEffect`, `DoubleDamageEffect`), si passa al punto 3. Altrimenti una nuova classe in
   `engine.effect` che implementa `BattleEffect`:
   - `canBeUsedIn(BattleState)`: quando ha senso usarlo (se restituisce `false` l'oggetto non viene
     consumato);
   - `applyTo(BattleControls)`: cosa fa, usando solo le operazioni di `BattleControls`.
   I numeri (secondi, punti) vanno in `BattleRules`, non nell'effetto.
3. **Associarlo** in `GameEffects`, nella mappa `EFFECTS_BY_ITEM_ID`:
   ```java
   "coffee", new ExtraTimeEffect(),
   ```
   Se un oggetto di `items.xml` non ha un effetto, l'avvio si ferma con "No effect for item '...'
   in items.xml".
4. **Aggiornare `ShippedContentTest.containsTheFourItems`**, che controlla l'elenco esatto degli id.

Non cambiano `ExamBattle`, le ricompense (`StandardRewardPolicy` sceglie tra tutti gli oggetti del
catalogo) e la UI (`BattleAction` mostra ogni oggetto posseduto e lo abilita con
`ExamBattle.canUse`).

**Quando non basta.** Se l'effetto deve fare qualcosa che nessuna operazione di `BattleControls`
permette (ad esempio saltare la domanda), serve un metodo nuovo in `BattleControls`, la sua
implementazione nella classe interna `Controls` di `ExamBattle` e in `RecordingControls` dei test.
È voluto: `BattleControls` è la lista di ciò che un effetto può fare, e allungarla è una decisione
da prendere in modo esplicito (incapsulamento, lezione 07).

**Una nuova abilità** è ancora più semplice: un record `Ability` (nome, costo in Concentrazione,
effetto) nella lista `ABILITIES` di `GameEffects`.

## 5. Nuovo formato di salvataggio

Esempio: salvare su un database (`JpaSaveGameRepository` con H2) invece che in file JSON.

1. **Il repository** in `persistence` (ad esempio `persistence.save`): una classe che implementa
   `SaveGameRepository` e rispetta il suo contratto:
   - `save(slot, data)` sostituisce quello che c'era;
   - `load(slot)` restituisce `Optional.empty()` se lo slot non esiste;
   - `listSlots()` restituisce gli slot in ordine alfabetico;
   - ogni nome di slot passa da `SaveGameRepository.requireValidSlot`;
   - ogni errore di lettura o scrittura diventa una `SaveGameException`.

   Il repository riceve e restituisce `SaveData`, un DTO con soli tipi del JDK: la conversione da e
   verso lo studente resta in `SaveMapper` e non va riscritta.
2. **La dipendenza**: aggiungere la libreria (driver, JPA) in `build.gradle.kts`.
3. **Collegarlo** in `GameApplication.start`, dove oggi c'è
   `new JsonSaveGameRepository(SAVES_DIRECTORY)`. `GameAssembly` riceve il repository nel
   costruttore come interfaccia e non cambia.
4. **I test.** Non esiste un test di contratto astratto per `SaveGameRepository`: oggi c'è una sola
   implementazione vera. Il modello da seguire è `JsonSaveGameRepositoryTest` (salvataggio e
   caricamento, ordine del libretto, testo non ASCII, slot mancante, dati illeggibili, nomi non
   validi). Con una seconda implementazione converrebbe spostare i casi comuni in un test di
   contratto, come per le domande.

Non cambiano engine e UI: `Game` non sa di essere salvata, `AutoSave` usa l'interfaccia e la UI usa
`GameService`. La versione del formato (`formatVersion`) e la scrittura atomica sono dettagli del
file JSON (vedi [Persistenza](Persistenza)): un database avrebbe i propri meccanismi.
