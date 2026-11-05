# Classi e interfacce

> Bozza: copre il **model** (issue #2, #3, #4) e il calcolo del voto nell'engine (issue #6).
> Le altre sezioni su engine, persistence e UI si aggiungono quando si chiudono le issue
> corrispondenti.

Il model contiene le entità e lo stato del gioco e **non dipende da nessun altro
package**: niente engine, niente persistenza, niente UI. Ogni classe valida i propri dati
nel costruttore (fail fast): `Objects.requireNonNull` per i riferimenti nulli,
`IllegalArgumentException` per i valori non validi. I controlli ricorrenti (testo non
vuoto, numero positivo o non negativo) stanno in un solo punto, `model.Preconditions`.

## Panoramica

| Package | Classi |
|---|---|
| `model.exam` | `Grade`, `Exam`, `ExamType` |
| `model.question` | `Question`, `ChoiceQuestion`, `QuestionDetails`, `AbstractQuestion`, `AbstractChoiceQuestion`, `MultipleChoiceQuestion`, `TrueFalseQuestion`, `CodeOutputQuestion`, `FillInQuestion`, `Subject`, `Difficulty` |
| `model.student` | `Student`, `StudentProfile`, `Stats`, `Level` |
| `model.item` | `Item`, `Inventory` |
| `model.transcript` | `Transcript`, `TranscriptEntry` |
| `engine.grading` | `Performance`, `GradeCalculator`, `PerformanceGradeCalculator` |

## Esami e voti

### `Grade`: una classe, non un record
Un voto va da 18 a 30, e 31 rappresenta il 30 e lode. Il costruttore è privato e l'unico
modo di creare un voto è `Grade.of(int)`: la validazione dell'intervallo sta quindi in un
solo punto. Un record avrebbe sempre un costruttore pubblico, cioè un secondo modo di
creare voti che bisognerebbe validare di nuovo.

`Grade` è un value object: due voti sono uguali se hanno lo stesso valore. Offre
`label()` per il testo mostrato al giocatore ("28", "30 e lode"), `valueForAverage()` (la
lode vale 30 nella media) ed è `Comparable`.

### `Exam` ed `ExamType`
`Exam` ha codice, materia, tipo, CFU e difficoltà. La sua identità è il **codice**: due
esami con lo stesso codice sono lo stesso esame, per questo `equals` e `hashCode` usano
solo quello e `Exam` può fare da chiave nel libretto. Non è un record perché un record
confronterebbe tutti i campi.

La resistenza dell'esame non è un campo di `Exam`: è una regola di bilanciamento e la
calcolerà l'engine da CFU e difficoltà.

`ExamType` (scritto, orale, progetto) è un enum **senza comportamento**. Ogni tipo verrà
associato alla sua strategia con una `EnumMap<ExamType, ExamStrategy>` costruita
nell'avvio dell'applicazione: l'engine non conterrà `switch` o `if` sul tipo, e un nuovo
tipo d'esame sarà una nuova classe più una voce nella mappa (OCP).

## Domande

```
Question
├── AbstractQuestion               equals/hashCode final sull'id
│   ├── FillInQuestion
│   └── AbstractChoiceQuestion     implementa ChoiceQuestion; isCorrect final
│       ├── MultipleChoiceQuestion (4 opzioni)
│       ├── TrueFalseQuestion      (Vero/Falso)
│       └── CodeOutputQuestion     (4 output + code())
QuestionDetails                    id, materia, difficoltà, testo, indizio
```

### Il contratto di `Question`
La Javadoc di `Question` dichiara le aspettative valide per **ogni** implementazione:
le domande sono immutabili, `isCorrect` non lancia mai eccezioni (con risposte sbagliate,
vuote o nulle restituisce `false`) e due domande sono uguali **se e solo se** hanno lo
stesso id, qualunque sia il loro tipo concreto. In `AbstractQuestion` `equals` e
`hashCode` sono `final`, così nessuna sottoclasse può rompere questa regola.

La risposta è il **testo** dell'opzione, non l'indice: non dipende dall'ordine in cui le
opzioni vengono mostrate né da quelle nascoste dal 50/50.

### `QuestionDetails`: un parameter object
I cinque dati comuni a tutte le domande viaggiano sempre insieme. Senza raggrupparli,
`CodeOutputQuestion` avrebbe otto parametri nel costruttore. Con il record
`QuestionDetails` i costruttori ne hanno da due a quattro, e la validazione dei dati
comuni sta in un solo punto. Le fabbriche `withHint` e `withoutHint` evitano di passare
`Optional` come parametro.

### Domande a scelta e 50/50
`AbstractChoiceQuestion` valida le opzioni (almeno due, non vuote, distinte, con quella
corretta tra loro) e implementa `isCorrect` come `final`: tutte le domande a scelta
controllano la risposta allo stesso modo.

Gli Appunti eliminano due opzioni sbagliate, quindi si possono usare solo se
`wrongOptions().size() >= 3`. Il Vero/Falso è comunque una `ChoiceQuestion` (così la UI
mostra tutte le domande a scelta allo stesso modo), ma con una sola opzione sbagliata la
regola lo esclude da sola, senza controllare il tipo concreto.

### `CodeOutputQuestion` e LSP
`CodeOutputQuestion` è sorella di `MultipleChoiceQuestion`, non figlia: entrambe
estendono `AbstractChoiceQuestion`. Rispetta il contratto delle domande a scelta senza
cambiarlo: il testo della domanda resta in `prompt()` e lo snippet è un dato **in più**,
esposto da `code()`.

### Test di contratto
Il principio di sostituzione di Liskov è verificato dai test, non solo dichiarato.
`QuestionContractTest` e `ChoiceQuestionContractTest` sono classi di test astratte con le
asserzioni del contratto; il test di ogni tipo concreto le estende e fornisce le proprie
domande. Le stesse asserzioni girano così su tutte le implementazioni, compresa la regola
"uguali se e solo se stesso id" verificata anche tra tipi diversi.

### `FillInQuestion`
La risposta viene normalizzata prima del confronto: spazi iniziali e finali tolti, spazi
interni ridotti a uno, lettere minuscole (con `Locale.ROOT`, indipendente dalla lingua del
sistema). Si possono accettare più risposte equivalenti, ad esempio "LIFO" e "Last In First
Out".

### `Subject` e `Difficulty`
`Subject` è un record e non un enum: le materie arrivano dai file di contenuto, così se ne
può aggiungere una senza modificare il codice. `Difficulty` è un enum con `harder()`, che
servirà all'orale in cui il professore "incalza".

## Studente

### `Stats` e `Level`: valori immutabili
`Stats` (conoscenza, energia, concentrazione con i loro massimi) e `Level` (livello ed
esperienza) sono record immutabili: ogni operazione restituisce un nuovo valore. I limiti
sono garantiti dal costruttore: l'energia non scende sotto zero e non supera il massimo,
spendere più concentrazione di quella disponibile è un errore (`IllegalStateException`).

Le regole di progressione sono divise tra model ed engine. Il model sa **come** cambia lo
stato in modo valido (`Level.levelUp(cost)` controlla solo che l'esperienza basti,
`Stats.improve(...)` applica i bonus); l'engine deciderà **quando** e **di quanto** (soglie
di esperienza, bonus a ogni livello, esperienza data da un esame).

### `Student` e `StudentProfile`
`StudentProfile` ha le statistiche iniziali, che devono essere "piene"; la sua identità è
l'id. `Student` è creato a partire da un profilo e sostituisce statistiche e livello con
`updateStats` e `updateLevel`, che ricevono una funzione (`UnaryOperator`, interfaccia
funzionale) da passare come lambda:

```java
student.updateStats(stats -> stats.loseEnergy(3));
```

`Student` espone direttamente `Inventory` e `Transcript`, che proteggono da soli le
proprie regole. Non ha `equals`: non ha un'identità stabile (il nome non è unico).

## Oggetti

### `Item` senza comportamento
`Item` è solo un dato: id, nome e descrizione, con identità sull'id. Cosa fa un oggetto
durante un esame (ad esempio il Caffè che aggiunge 15 secondi) dipende dallo stato dello
scontro, che è nell'engine. Se `Item` contenesse quel comportamento, il model dipenderebbe
dall'engine. Per questo il comportamento starà in un'interfaccia dell'engine,
`ItemEffect`, e l'associazione `Item → ItemEffect` sarà una mappa costruita all'avvio.

### `Inventory`
Associa a ogni oggetto la sua quantità (`Map<Item, Integer>`). Invariante: le quantità
sono sempre positive, e un oggetto che arriva a zero viene tolto. `items()` restituisce una
copia immutabile.

## Libretto

### `Transcript`: una sola responsabilità
Il libretto registra gli esami superati, impedisce di registrare due volte lo stesso esame
e calcola CFU totali e media ponderata sui CFU (con gli Stream; la lode vale 30). Usa una
`LinkedHashMap<Exam, Grade>`, che è una `HashMap` ma conserva l'ordine di registrazione.

Il libretto **non** sa se lo studente può laurearsi né calcola il voto di laurea: sono
regole di gioco e staranno in `Game`. È la violazione di SRP vista a lezione con il metodo
`puoLaurearsi()` nella classe `Libretto`: una classe che registra i voti cambierebbe anche
quando cambiano le regole di laurea.

Un voto insufficiente non si può registrare, ma non serve un controllo: `Grade` non può
esistere sotto il 18, quindi lo stato non valido è impedito dal tipo stesso.

## Calcolo del voto (`engine.grading`)

Il voto è una regola di gioco, quindi sta nell'engine e non in `Grade`: `Grade` sa solo
quali valori sono validi, non come si ottengono.

### `Performance`
Record con le risposte corrette, quelle sbagliate (compresi i tempi scaduti) e gli
indizi usati, tutti non negativi. Offre `answered()` (risposte date) e `isFlawless()`:
almeno una risposta, nessun errore, nessun indizio. Zero risposte non è una prestazione
perfetta.

### Il contratto di `GradeCalculator`
`GradeCalculator` è un'interfaccia funzionale con un solo metodo,
`Optional<Grade> calculate(Performance)`. La Javadoc dichiara le regole valide per
**ogni** implementazione:

- una prestazione sufficiente dà un `Grade`, una insufficiente `Optional.empty()`; mai
  `null`, mai eccezioni. Un voto fuori dall'intervallo è impossibile per costruzione,
  perché `Grade` non può esistere fuori da 18-31;
- nessuna risposta corretta → insufficiente;
- prestazione perfetta (nessun errore, nessun indizio) → sufficiente;
- con le stesse risposte date e gli stessi indizi, una risposta corretta in più non
  peggiora mai il risultato;
- con le stesse risposte corrette e sbagliate, un indizio in più non migliora mai il
  risultato.

Nelle ultime due regole una bocciatura conta meno di qualunque voto.

Il contratto **non** fissa la soglia, la formula o la penalità degli indizi. Sono scelte
di bilanciamento, e un contratto che le imponesse coinciderebbe con un'unica
implementazione: l'interfaccia non servirebbe più. Fissa invece le proprietà che ogni
valutazione sensata deve avere. È lo stesso ragionamento del `ValutatoreMinimale` della
lezione 10: anche una politica molto diversa, come "18 a chiunque superi la soglia",
rispetta il contratto e può sostituire l'implementazione principale senza che chi la usa
se ne accorga (LSP).

### `PerformanceGradeCalculator`
- **Soglia** al 60% di risposte corrette.
- **Scala lineare**: dal 60% al 100% il voto sale da 18 a 30, arrotondando per difetto.
- **Indizi**: −1 punto ciascuno, senza scendere sotto il 18. Un indizio quindi non
  trasforma mai un esame superato in una bocciatura.
- **Lode** solo se `isFlawless()`.

Il calcolo usa **solo interi**. La soglia si controlla senza divisioni
(`corrette × 5 ≥ date × 3`) e il voto base è:

```
18 + 12 · (5c − 3a) / (2a)        c = risposte corrette, a = risposte date
```

La divisione intera arrotonda già per difetto. Con i `double`, una quota esattamente
sulla soglia (3 su 5) potrebbe risultare 0,5999… per un errore di rappresentazione e dare
una bocciatura ingiusta. Alcuni esempi: 3/5 → 18, 2/3 → 20, 8/10 → 24, 8/10 con due
indizi → 22, 6/6 senza indizi → 30 e lode, 6/6 con un indizio → 29.

### Test di contratto
Come per le domande, `GradeCalculatorContractTest` è una classe di test astratta con le
regole del contratto, verificate su una griglia di 484 prestazioni (0-10 corrette × 0-10
sbagliate × 0-3 indizi). `PerformanceGradeCalculatorTest` la estende e aggiunge i casi
specifici della formula (soglia esatta, arrotondamento, lode, penalità, minimo di 18).
Ogni futura implementazione di `GradeCalculator` dovrà estendere lo stesso test.

Ho verificato che il test di contratto sappia trovare un errore: trasformando per prova la
penalità degli indizi in un bonus, la regola "un indizio in più non migliora mai il voto"
fallisce.
