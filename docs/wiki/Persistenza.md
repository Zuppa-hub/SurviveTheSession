# Persistenza

> Bozza: copre i salvataggi JSON (issue #9) e l'annotation dei parser (issue #29). La parte
> sui contenuti XML (issue #5) è riassunta.

Il gioco legge e scrive due tipi di dati, con esigenze diverse:

| | Contenuti | Salvataggi |
|---|---|---|
| Cosa | esami, domande, profili, oggetti | la partita di un giocatore |
| Formato | XML, validato con XML Schema | JSON, con Gson |
| Dove | dentro il JAR (`src/main/resources/content`) | `~/.sopravvivi-alla-sessione/saves/<slot>.json` |
| Accesso | sola lettura, all'avvio | lettura e scrittura durante il gioco |
| Se è rotto | il gioco non parte (fail fast) | `SaveGameException`, la partita non si carica |

In entrambi i casi l'engine non conosce il formato: usa interfacce definite in
`engine.repository` (`ExamCatalog`, `QuestionRepository`, `SaveGameRepository`, ...) e
`persistence` contiene solo le implementazioni. La dipendenza va da `persistence` verso
`engine`, mai il contrario (DIP).

## Contenuti XML (in breve)
I file sono letti con `getResourceAsStream`, validati con XSD (tipi condivisi in `common.xsd`
con `xs:include`) e interrogati con XPath. Ogni errore diventa una `ContentException` con file,
riga e colonna, e il gioco si ferma all'avvio con un messaggio chiaro.

## Annotation personalizzata `@QuestionElement` (lezione 03)

Ogni tipo di domanda ha nell'XML il suo elemento (`<multipleChoice>`, `<trueFalse>`,
`<codeOutput>`, `<fillIn>`) e un parser che lo trasforma in una domanda. Il repository delle
domande usa una mappa elemento → parser, così un nuovo tipo di domanda non richiede di
modificare il repository (OCP).

All'inizio la mappa era scritta a mano in `GameContent`:

```java
Map.of("multipleChoice", new MultipleChoiceParser(),
       "trueFalse", new TrueFalseParser(), ...)
```

Il nome dell'elemento stava lontano dalla classe che lo gestisce: aggiungendo un parser bisognava
ricordarsi di aggiungere anche la stringa giusta in un altro file. Ora ogni parser lo dichiara
da sé con un'annotation personalizzata, uno degli argomenti della lezione 03 del Prof. Rossi:

```java
@QuestionElement("fillIn")
public final class FillInParser implements QuestionParser { ... }
```

L'annotation è definita così:

```java
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface QuestionElement {
    String value();
}
```

- **`@Retention(RUNTIME)`**: l'annotation resta nel bytecode e si può leggere mentre il
  programma gira. Con `SOURCE` (come `@Override`) sparirebbe dopo la compilazione, con
  `CLASS` resterebbe nel file `.class` ma non sarebbe leggibile con la reflection.
- **`@Target(TYPE)`**: si mette solo su una classe, perché descrive un parser intero; il
  compilatore la rifiuta su un metodo o un campo.
- **`@Documented`**: compare nella Javadoc di ogni parser, così chi legge la documentazione vede
  quale elemento gestisce.
- **`value()`**: con un solo attributo chiamato `value` si può scrivere
  `@QuestionElement("fillIn")` senza il nome dell'attributo.

`QuestionParsers.byElement(parsers)` costruisce la mappa leggendo l'annotation con la reflection
(`parser.getClass().getAnnotation(QuestionElement.class)`). `GameContent` passa solo l'elenco dei
parser:

```java
QuestionParsers.byElement(List.of(
        new MultipleChoiceParser(), new TrueFalseParser(), new CodeOutputParser(), new FillInParser()));
```

**Fail fast.** Un parser senza annotation, o due parser che dichiarano lo stesso elemento,
fermano l'avvio con una `ContentException` che nomina la classe: ad esempio "Parser
FillInParser does not declare its element with @QuestionElement". I test lo verificano con un
parser senza annotation e con un secondo parser per `fillIn`, e verificano anche che
l'annotation abbia davvero `RUNTIME`, `TYPE` e `@Documented`.

## Salvataggi JSON

### Si salvano dati, non oggetti del dominio
`SaveData` è un DTO: un record con soli tipi del JDK (stringhe, numeri, mappe).

| Campo | Contenuto |
|---|---|
| `studentName`, `profileId` | nome dello studente e id del profilo scelto |
| `stats` | Conoscenza, Energia e Concentrazione con i loro massimi |
| `level`, `experience` | livello ed esperienza nel livello |
| `inventory` | id dell'oggetto → quantità |
| `transcript` | codice dell'esame → voto (31 = lode), nell'ordine di registrazione |

Perché non salvare direttamente `Student` con Gson:
- il formato del file non deve cambiare ogni volta che cambia una classe del dominio;
- gli oggetti del dominio hanno invarianti (energia tra 0 e il massimo, voti tra 18 e 31) che
  devono essere ricontrollati quando si carica un file scritto da fuori;
- profili, oggetti ed esami sono contenuti: il salvataggio li **riferisce** per id, non li
  copia.

Gli esami rimanenti non si salvano: si ricavano dalla sessione e dal libretto, e salvarli
sarebbe uno stato duplicato che può andare fuori sincrono.

`SaveMapper` (in `engine.repository`, perché usa solo il model e le interfacce dei cataloghi)
converte `Student` ↔ `SaveData`. Nel verso del caricamento risolve gli id con i cataloghi e
ricostruisce lo studente con i costruttori del dominio, che validano ogni valore. Un profilo,
un oggetto o un esame che non esistono più, o un valore non valido (un voto di 17, un'energia
negativa), diventano una `SaveGameException`.

### Il file
```json
{
  "formatVersion": 1,
  "game": {
    "studentName": "Andrea",
    "profileId": "nerd",
    "stats": { "knowledge": 6, "energy": 4, "maxEnergy": 8, "concentration": 3, "maxConcentration": 5 },
    "level": 2,
    "experience": 50,
    "inventory": { "coffee": 2 },
    "transcript": { "RETI1": 31, "PROG1": 27 }
  }
}
```

- **Versione del formato.** `formatVersion` permette di riconoscere un file scritto da una
  versione diversa del gioco: oggi vale 1, e un file con un'altra versione viene rifiutato con
  un messaggio invece di essere letto male. Se un giorno il formato cambierà, si aumenterà il
  numero e si potrà convertire il vecchio formato.
- **UTF-8 e stream bufferizzati.** File letti e scritti con `Files.newBufferedReader` e
  `Files.newBufferedWriter` in UTF-8: un nome con accenti si salva e si rilegge uguale su
  qualunque sistema, senza dipendere dalla codifica predefinita.

### Scrittura atomica
Un salvataggio non scrive direttamente su `<slot>.json`. Scrive su un file temporaneo nella
stessa cartella e poi lo **sposta** al posto di quello vecchio (`Files.move` con
`ATOMIC_MOVE`). Se il gioco si chiude a metà scrittura, resta il salvataggio precedente
intero, mai un file troncato a metà. La cartella è la stessa perché lo spostamento atomico
funziona solo all'interno dello stesso file system. Se il file system non supporta lo
spostamento atomico (`AtomicMoveNotSupportedException`), si ripiega su `Files.move` con
`REPLACE_EXISTING`: meno sicuro, ma sempre meglio che scrivere direttamente sul file finale.

Il ripiego dipende dal file system, e i test non possono scegliere su quale girano. Per questo
lo spostamento passa da una piccola interfaccia package-private (`FileMover`, normalmente
`Files::move`): il test ne passa una che rifiuta `ATOMIC_MOVE` e verifica che il secondo
tentativo usi `REPLACE_EXISTING` e che il salvataggio venga sostituito.

### Nomi degli slot
Il nome di uno slot diventa il nome di un file, quindi è validato: solo lettere, numeri, `-` e
`_`. Così un nome come `../altro` non può far scrivere fuori dalla cartella dei salvataggi. La
regola è nel contratto di `SaveGameRepository` (metodo statico `requireValidSlot`), quindi
vale per qualunque implementazione.

### Errori
| Situazione | Risultato |
|---|---|
| lo slot non è mai stato salvato | `Optional.empty()` |
| il file non è JSON, manca la versione o manca un campo | `SaveGameException` |
| versione del formato sconosciuta | `SaveGameException` con la versione trovata |
| profilo, oggetto o esame inesistente; valore non valido | `SaveGameException` |
| nome dello slot non valido | `IllegalArgumentException` |

`SaveGameException` è un'eccezione dedicata, definita nell'engine accanto all'interfaccia:
chi usa il repository (la UI) può distinguere "salvataggio illeggibile" da un errore di
programmazione.

### Salvataggio automatico
La partita viene salvata dopo ogni esame concluso. `Game` però **non sa** di essere salvata:
il salvataggio è `AutoSave`, un `GameListener` che l'applicazione (`GameAssembly`) registra
sulla partita. Quando arriva `onExamConcluded`, `AutoSave` converte lo studente con il
`SaveMapper` e lo salva nello slot. È lo stesso meccanismo (Observer) con cui la UI riceve
l'esito dell'esame: `Game` non dipende né dalla persistenza né dalla UI.

`GameAssembly` offre `newGame(nome, profilo, slot)`, `loadGame(slot)` e `savedGames()`; una
partita caricata continua a salvarsi nello stesso slot.

**Un salvataggio fallito non ferma la partita.** Se il disco è pieno o la cartella non è
scrivibile, `AutoSave` cattura la `SaveGameException` e la passa a un callback
(`Consumer<SaveGameException>`) ricevuto da `GameAssembly`. La partita prosegue e gli altri
listener ricevono comunque l'esito dell'esame. Nella GUI il callback è `Alerts::saveFailed`,
che mostra un avviso senza bloccare il gioco (vedi [Interfaccia grafica](Interfaccia-grafica)). Senza questa scelta
l'eccezione risalirebbe dalla notifica di fine esame fino alla UI e interromperebbe il gioco
per un problema che non riguarda le regole.

### Test
`JsonSaveGameRepositoryTest` usa una cartella temporanea di JUnit (`@TempDir`), creata e
cancellata per ogni test: nessun test tocca la cartella vera dei salvataggi. Verifica
salvataggio e caricamento, ordine del libretto e testo non ASCII, la versione nel file,
la sostituzione senza file temporanei rimasti, i file corrotti, la versione sconosciuta e i
nomi degli slot non validi. Il test di `GameAssembly` usa un repository in memoria per
verificare il salvataggio automatico e il caricamento con i contenuti veri.

## Estensione possibile
Per salvare su un database (ad esempio `JpaSaveGameRepository` con H2) basta una nuova
implementazione di `SaveGameRepository` collegata in `GameAssembly`: engine e UI non cambiano.
