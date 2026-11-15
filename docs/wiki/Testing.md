# Testing

Il progetto ha **720 test JUnit** (JUnit 6, al momento di questa pagina), tutti verdi con
`./gradlew clean build`. Ogni classe di `model` ed `engine` ha i suoi test (regola 8 del design);
hanno test anche `persistence`, la composition root e le classi di supporto della UI.

## Come si eseguono

```bash
./gradlew test                                   # tutti i test
./gradlew test --tests '*GradeTest'              # una sola classe
./gradlew clean build                            # compilazione, test e JAR da zero
```

Gradle stampa ogni test con il suo esito (`testLogging` in `build.gradle.kts`) e scrive il report
HTML in `build/reports/tests/test/index.html`. Java 25 non va installato a mano: lo scarica la
toolchain (vedi [Strumenti e metodologie](Strumenti-e-metodologie)).

## TDD: prima il test rosso

Model ed engine sono stati scritti in TDD: per ogni classe un commit `test:` con i test che
falliscono, poi un commit `feat:` con l'implementazione che li fa passare. La sequenza è visibile
nella storia di Git:

```bash
git log --oneline --reverse develop | grep -E ' (test|feat):'
```

```
4e23ae0 test: Grade (validazione 18-31, lode, label, uguaglianza, ordinamento)
76c5c8e feat: Grade con validazione 18-31, lode e label per la UI
...
ee581cd test: Transcript (registrazione senza doppioni, ordine, CFU, media ponderata)
e21d0dc feat: Transcript con media ponderata sui CFU e senza regole di laurea
```

Su `develop` ci sono 59 commit `test:` e 58 commit `feat:`. I refactoring (`refactor:`, ad esempio
`CatalogIndex`) sono avvenuti con i test esistenti verdi prima e dopo: è il terzo passo del ciclo
rosso → verde → refactoring.

## Test di contratto (LSP)

Un'interfaccia promette delle aspettative a chi la usa (lezione 10). Un **test di contratto** le
verifica su **ogni** implementazione: è una classe di test astratta con le asserzioni del
contratto, e il test di ogni classe concreta la estende fornendo i propri oggetti.

| Test di contratto | Contratto verificato | Esteso da |
|---|---|---|
| `QuestionContractTest` | dati esposti, risposta corretta accettata, risposte sbagliate, vuote o `null` rifiutate senza eccezioni, uguali se e solo se stesso id (anche con dati o tipi diversi), `accept` passa al visitor la domanda stessa | `FillInQuestionTest` e, tramite il successivo, le domande a scelta |
| `ChoiceQuestionContractTest` (estende il precedente) | almeno due opzioni, quella corretta tra le opzioni, opzioni sbagliate = tutte tranne la corretta, confronto esatto, opzioni non modificabili, visitata come scelta | `MultipleChoiceQuestionTest`, `TrueFalseQuestionTest`, `CodeOutputQuestionTest` |
| `GradeCalculatorContractTest` | le quattro regole del contratto su una griglia di 484 prestazioni (0-10 corrette × 0-10 sbagliate × 0-3 indizi) | `PerformanceGradeCalculatorTest` |
| `ExamStrategyContractTest` | nessuna domanda ripetuta, risultato vuoto solo a domande finite, secondi positivi, moltiplicatore almeno 1 | `WrittenExamStrategyTest`, `OralExamStrategyTest`, `ProjectExamStrategyTest` |

Il metodo astratto è il punto in cui ogni sottoclasse fornisce la propria istanza:

```java
abstract class QuestionContractTest {

    /** @return a question with the given id, difficulty MEDIUM, subject PROGRAMMING and {@link #HINT} */
    abstract Question createQuestion(String id, String prompt);
    ...
}
```

Una nuova implementazione deve estendere lo stesso test: se rompe il contratto, la build fallisce.

## Test deterministici

Tra le proprietà dei test di unità viste nella lezione del Prof. Rossi sul testing c'è la
**ripetibilità**: un test dà sempre lo stesso risultato, su qualunque macchina. Per questo la regola
8 vieta nei test il tempo reale, i file reali e la casualità. Il codice lo permette perché tempo,
caso e file arrivano dal costruttore come interfacce (DIP, vedi [Principi SOLID](Principi-SOLID)).

| Dipendenza vera | Sostituto nei test | Cosa fa |
|---|---|---|
| `RandomGenerator.getDefault()` | `FakeRandom.first()` / `FakeRandom.last()` | ogni scelta limitata restituisce sempre il primo o l'ultimo indice |
| `ScheduledQuestionTimer` (per lo scontro) | `ManualQuestionTimer` | il tempo passa solo quando il test chiama `advance(secondi)` |
| `ScheduledExecutorService` (per il timer) | `ManualScheduler` | registra i compiti invece di pianificarli; il test fa scattare i secondi con `tick()` |
| `Platform::runLater` | `QueuedExecutor` oppure `Runnable::run` | la coda si svuota quando il test chiama `runAll()`; l'esecutore diretto consegna subito |
| `StandardBattleRules` | `FixedBattleRules` | numeri facili da seguire: resistenza 10, danno 5, Energia persa 2 |
| `PerformanceGradeCalculator` | una lambda, `performance -> Optional.of(Grade.of(28))` | `GradeCalculator` è un'interfaccia funzionale |
| cartella dei salvataggi | `@TempDir` di JUnit | una cartella temporanea per ogni test, cancellata alla fine |
| `Files::move` | un `FileMover` che rifiuta `ATOMIC_MOVE` | prova il ripiego senza un file system apposito |
| `JsonSaveGameRepository` | `InMemorySaveGameRepository`, `FailingSaveGameRepository` | salvataggi in memoria; un disco "pieno" |
| cataloghi XML | `TestCatalogs` | cataloghi di prova costruiti da una lista |

Con questi sostituti si scrivono anche i casi più difficili, passo per passo. Ad esempio la
scadenza vecchia del timer: il tempo scade, il giocatore risponde, solo dopo il test svuota la coda
del `QueuedExecutor`, e la scadenza vecchia risulta scartata (dettagli in
[Modelli di esecuzione](Modelli-di-esecuzione)).

Per osservare le notifiche ci sono registratori: `RecordingBattleListener`,
`RecordingTimerListener`, `RecordingControls` (registra le chiamate degli effetti).

**File di prova, non file dell'utente.** I test dei contenuti leggono XML di prova dal class path
(`src/test/resources/content-test/`), uno per ogni errore da riconoscere: un'opzione ripetuta, un
id usato in due materie, un tipo d'esame sconosciuto. Sono versionati con il codice e non cambiano
tra un'esecuzione e l'altra. `ShippedContentTest` carica invece i contenuti veri del gioco: se un
file distribuito rompe una regola (ad esempio un esame con meno di 15 domande, o senza domande di
una difficoltà), la build fallisce.

**Niente test con thread veri**: passerebbero o fallirebbero secondo come il sistema alterna i
thread. Il motivo è spiegato in [Modelli di esecuzione](Modelli-di-esecuzione).

## Verifiche a mano dei test (mutazioni)

Un test verde dice poco se passerebbe anche con il codice sbagliato. Per i punti più delicati,
durante lo sviluppo è stato introdotto **per prova** un errore nel codice, si è controllato che i
test fallissero e la modifica è stata annullata. Sono verifiche fatte a mano nelle sessioni di
lavoro con Claude Code, non con uno strumento di mutation testing.

| Errore introdotto | Risultato |
|---|---|
| la penalità degli indizi trasformata in un bonus | fallisce la regola del contratto "un indizio in più non migliora mai il voto" |
| tolto `harder()` dall'orale | falliscono tre test, due della strategia e uno dello scontro |
| tolto l'azzeramento del Colpo di genio dopo la risposta corretta | fallisce il test dedicato |
| tolto il controllo della generazione alla consegna delle notifiche del timer | falliscono i due test sulle scadenze già in coda |
| tolta la ricarica dell'Energia da `startExam` | fallisce il test dedicato |
| tolta la registrazione di `AutoSave`, oppure rilanciata l'eccezione invece di passarla al callback | falliscono i test dedicati |
| tolta l'annotation da `FillInParser` | `./gradlew run` si ferma con "Parser FillInParser does not declare its element with @QuestionElement" |

Sono tutte registrate nel registro dell'AI (`docs/ai-log/`), e le prime cinque anche nelle pagine
[Classi e interfacce](Classi-e-interfacce) e [Modelli di esecuzione](Modelli-di-esecuzione).
L'ultima è una verifica dell'avvio, non di un test.

## Stile dei test

- **Un comportamento per test, con il nome che lo dice**: `everyExamStartsWithFullEnergy`,
  `theSessionHasFiveExamsWithTheProjectAsFinalBoss`.
- **Test parametrici** per i casi simili: `@ValueSource`, `@CsvSource`, `@EnumSource`,
  `@MethodSource`, `@NullSource`. Ad esempio `SaveGameRepositoryTest` prova con `@ValueSource` i
  nomi di slot validi e quelli da rifiutare (`"../andrea"`, `"a b"`, `"partita.json"`, ...).
- **`assertAll`** quando un test controlla più proprietà dello stesso oggetto: si vedono tutte le
  asserzioni fallite, non solo la prima.
- **Oggetti di prova condivisi** in piccole classi di supporto (`TestQuestions`, `TestGames`,
  `States`) invece di ripetere la costruzione in ogni test.

## Cosa non è testato automaticamente

I controller JavaFX non hanno test automatici: richiederebbero una libreria apposita e un display.
La loro logica è nelle classi di supporto testate (`ExamRow`, `BattleAction`, `OutcomeSummary`,
...), e il resto è stato provato a mano giocando (vedi [Interfaccia grafica](Interfaccia-grafica)).
