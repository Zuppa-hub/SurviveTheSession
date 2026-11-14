# Strutture dati, equals e Stream

Ogni collezione del progetto è stata scelta per un motivo: l'ordine conta? servono doppioni? si
cerca per chiave? Questa pagina riporta le scelte, l'identità degli oggetti (da cui dipendono
`HashSet` e `HashMap`) e l'uso degli Stream.

## Liste: quando conta l'ordine

| Dove | Perché una `List` |
|---|---|
| `ExamSession.exams()` | l'ordine della sessione conta: l'ultimo esame è il boss (`exams.getLast()`) |
| `QuestionPool` (domande della materia) | la scelta avviene per indice, `candidates.get(random.nextInt(candidates.size()))` |
| `ChoiceQuestion.options()` | ordine di visualizzazione; `PresentedQuestion` lo mescola su una copia |
| listener di `ExamBattle` e `Game` (`ArrayList`) | l'ordine di registrazione conta: `Game` ascolta lo scontro prima della UI, `AutoSave` salva prima che la UI mostri l'esito |
| `BattleEffects.abilities()` | le abilità compaiono nella schermata nell'ordine in cui sono definite |

## `HashSet`: niente doppioni

| Dove | A cosa serve |
|---|---|
| `QuestionPool.asked` | le domande già poste nello scontro, per non ripeterle |
| `XmlQuestionRepository` (`seen`) | un id di domanda usato in due file diversi ferma l'avvio |
| `ExamSession`, `AbstractChoiceQuestion`, `BattleEffects`, `PresentedQuestion` | controllano che non ci siano esami, opzioni o nomi di abilità ripetuti |
| `FillInParser` → `FillInQuestion` (risposte accettate, un `Set<String>`) | l'ordine non conta, conta solo se la risposta c'è |

Il controllo dei duplicati tra file sfrutta il valore restituito da `add`:

```java
if (!seen.add(question)) {
    throw new ContentException("Invalid content in " + location + ": id already used in another subject");
}
```

E per sapere se una lista ha doppioni basta confrontare le dimensioni (in `ExamSession`):

```java
if (new HashSet<>(this.exams).size() != this.exams.size()) { ... }
```

Funziona solo perché `Question` ed `Exam` hanno `equals` e `hashCode` coerenti con la loro
identità (sotto).

## Mappe: quale e perché

| Mappa | Dove | Perché questa |
|---|---|---|
| `HashMap<Item, Integer>` | `Inventory` | quantità per oggetto; `merge(item, quantity, Integer::sum)` somma senza `if`; l'ordine non conta |
| `HashMap<String, QuestionParser>` | `QuestionParsers.byElement` | elemento XML → parser; `put` restituisce il parser precedente, così un elemento dichiarato due volte si scopre subito |
| `Map<Subject, List<Question>>` | `XmlQuestionRepository` | domande per materia; `Subject` è un record, quindi è una chiave valida |
| `LinkedHashMap<Exam, Grade>` | `Transcript` | è una `HashMap` (ricerca veloce per esame) che conserva l'**ordine di registrazione** del libretto |
| `LinkedHashMap<String, T>` | `CatalogIndex` | ricerca per id in tempo costante invece di scorrere una lista, e `all()` resta nell'ordine del file |
| `LinkedHashMap` | `SaveData` | `Map.copyOf` non garantisce l'ordine, quindi la copia immutabile è `Collections.unmodifiableMap(new LinkedHashMap<>(map))`: il libretto nel file resta in ordine |
| `TreeMap<String, Integer>` | `SaveMapper` (inventario) | ordinata per id: lo stesso inventario si scrive sempre nello stesso modo |
| `EnumMap<ExamType, ExamStrategy>` | `GameAssembly`, `ExamBattleFactory` | tipo d'esame → strategia, al posto di uno `switch` |
| `EnumMap<Difficulty, Integer>` | `StandardBattleRules` | resistenza e perdita di Energia per difficoltà |
| `EnumMap` | `ExamRow`, `OutcomeSummary` | etichette in italiano per tipo d'esame e per esito |

**Perché `EnumMap`.** Le chiavi sono le costanti di un enum: internamente è un array indicizzato
dall'ordinale, più compatto e veloce di una `HashMap`, e itera nell'ordine dell'enum.
`ExamBattleFactory` la usa insieme a un `EnumSet` per controllare all'avvio che ogni tipo abbia la
sua strategia:

```java
Set<ExamType> missing = EnumSet.allOf(ExamType.class);
missing.removeAll(strategies.keySet());
if (!missing.isEmpty()) {
    throw new IllegalArgumentException("No strategy for exam types " + missing);
}
```

**Perché una mappa nei cataloghi.** All'inizio i cataloghi cercavano un oggetto per id scorrendo
la lista con uno stream, cioè in tempo lineare, come visto a lezione. `CatalogIndex` li indicizza
una volta al caricamento.

## Identità: `equals` e `hashCode`

`HashSet` e `HashMap` funzionano solo se `equals` e `hashCode` sono coerenti: due oggetti uguali
devono avere lo stesso hash. Come visto nella lezione 13, prima di scriverli bisogna chiedersi
**che cosa rende un oggetto sé stesso**.

| Classe | Identità | Motivo |
|---|---|---|
| `Question` | `id` | due domande con lo stesso id sono la stessa domanda, qualunque sia il tipo concreto |
| `Exam` | `code` | è la chiave del libretto; due esami con lo stesso codice sono lo stesso esame |
| `Item` | `id` | due Caffè sono lo stesso oggetto per l'inventario |
| `StudentProfile` | `id` | è come il salvataggio lo riferisce |
| `Grade`, `Stats`, `Level`, `Subject` | tutti i campi | value object: contano solo i valori |

Le entità confrontano solo l'identificatore, e `hashCode` usa lo stesso campo:

```java
@Override
public boolean equals(Object other) {
    return other instanceof Exam exam && code.equals(exam.code);
}

@Override
public int hashCode() {
    return code.hashCode();
}
```

- **`Exam` non è un record**: un record confronterebbe tutti i campi, e due esami con lo stesso
  codice ma CFU diversi risulterebbero diversi.
- **In `AbstractQuestion` `equals` e `hashCode` sono `final`** e accettano qualunque `Question`:
  nessuna sottoclasse può cambiare la regola, che vale anche tra tipi diversi (contratto di
  `Question`, vedi [Principi SOLID](Principi-SOLID)).
- **`Grade` non è un record** per un altro motivo: il costruttore è privato e `Grade.of(int)` è
  l'unico modo di crearne uno. `equals` confronta il valore.
- **`Stats` e `Level` sono record**: immutabili, uguali se tutti i campi sono uguali.

### Classi senza `equals`
`ExamBattle`, `Game` e `Student` non ridefiniscono `equals`: non hanno un'identità chiara e stabile
(il nome di uno studente non è unico, uno scontro è un processo che cambia). Usano l'identità di
`Object`, che è quella giusta: uno scontro è uguale solo a sé stesso. Scrivere un `equals` su campi
che cambiano romperebbe anche le collezioni hash, perché l'hash cambierebbe dopo l'inserimento. La
Javadoc lo dichiara: "No `equals`: a battle has no stable identity."

## Copie immutabili

Le collezioni esposte sono copie immutabili (regola 4): chi le riceve non può cambiare lo stato
interno di un'altra classe.

- `List.copyOf` nei costruttori: `ExamSession`, `QuestionPool`, `PresentedQuestion`,
  `BattleEffects`. La copia protegge anche da chi modifica la lista originale dopo averla passata.
- `Map.copyOf` per le mappe senza ordine: `Inventory.items()`, `Reward`, `BattleEffects`,
  `QuestionParsers.byElement`.
- `Collections.unmodifiableMap` dove l'ordine conta e la mappa è già una copia: `CatalogIndex`,
  `SaveData`.
- `Stream.toList()` restituisce già una lista non modificabile: `Transcript.entries()`,
  `Game.remainingExams()`.

## Stream

Gli Stream servono dove si filtra, si trasforma o si aggrega: media ponderata e CFU nel libretto,
esami rimasti, domande disponibili, materie distinte degli esami, mappa oggetto → effetto.

### Catene lunghe spezzate in metodi con nome
Una catena lunga è difficile da leggere, quindi ogni passaggio con un significato diventa un metodo
privato (regola 6). `QuestionPool` sceglie la domanda di difficoltà più vicina a quella richiesta:

```java
private List<Question> closestTo(Difficulty target) {
    List<Question> available = available();
    return available.stream()
            .map(Question::difficulty)
            .min(byDistanceFrom(target))
            .map(closest -> withDifficulty(available, closest))
            .orElse(List.of());
}
```

`available()` (domande non ancora poste), `byDistanceFrom(target)` (il `Comparator`: distanza, poi
la più difficile a parità) e `withDifficulty(...)` (il filtro) hanno ciascuno il proprio nome.

Nel libretto la media ponderata usa due metodi:

```java
public OptionalDouble weightedAverage() {
    int credits = totalCredits();
    if (credits == 0) {
        return OptionalDouble.empty();
    }
    return OptionalDouble.of((double) weightedGradeSum() / credits);
}

private int weightedGradeSum() {
    return grades.entrySet().stream()
            .mapToInt(entry -> entry.getKey().credits() * entry.getValue().valueForAverage())
            .sum();
}
```

Altri esempi: `GameContent.subjectsOf` (materie distinte con `distinct()`),
`XmlExamCatalog.subjectsById` (`Collectors.toUnmodifiableMap`), `GameEffects.of`
(`Collectors.toMap(Function.identity(), GameEffects::effectOf)`).

### Method reference e interfacce funzionali
Dove una lambda si limita a chiamare un metodo si usa il method reference: `Exam::credits`,
`transcript::hasPassed`, `Stats::restoreFullEnergy`, `PresentedQuestion::withHintShown`. Lo
studente si aggiorna con un `UnaryOperator<Stats>`:

```java
student.updateStats(stats -> stats.loseEnergy(energyLoss));
```

### Quando non si usa uno Stream
Dove servono un ciclo con uno stato o un'uscita anticipata si usa un ciclo normale: il passaggio di
più livelli in `Game.gainExperience` (`while`), il controllo dei duplicati in `CatalogIndex.of` e in
`QuestionParsers.byElement` (`for`, perché al primo doppione si lancia l'eccezione con il nome della
chiave).
