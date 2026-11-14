# Clean Code

Le regole di Clean Code del progetto sono nel design (§3.4 e §4). Questa pagina mostra come sono
applicate, con esempi dal codice.

## Nomi

- **Le classi dicono cosa sono, i metodi cosa fanno.** `ExamBattle.retire()`,
  `Transcript.hasPassed(exam)`, `Game.availableExams()`, `Stats.isExhausted()`,
  `QuestionPool.drawClosestTo(difficulty)`.
- **I metodi privati raccontano il flusso.** La valutazione di una risposta in `ExamBattle` si
  legge come una frase:

  ```java
  if (correct) {
      rewardCorrectAnswer();
  } else {
      punishMistake();
  }
  ...
  continueOrEnd();
  ```

- **Niente numeri magici.** I valori hanno un nome: `SECONDS_PER_QUESTION = 30` in
  `WrittenExamStrategy`, `MISTAKE_MULTIPLIER = 2` in `ProjectExamStrategy`,
  `EXPERIENCE_PER_LEVEL = 100` in `StandardProgressionRules`, `GRADUATION_SCALE = 110` in
  `Graduation`, `WRONG_OPTIONS_REMOVED = 2` negli Appunti.
- **Nomi in inglese nel codice, testi in italiano solo per il giocatore** (regola 2): le
  etichette come "Scritto" o "30 e lode" stanno in `ExamRow` e in `Grade.label()`.

## Metodi piccoli

Ogni metodo fa una cosa. `Game.conclude` applica l'esito di un esame delegando a metodi con un
nome:

```java
private void conclude(Exam exam, BattleOutcome outcome) {
    currentBattle = Optional.empty();
    Reward reward = outcome.grade()
            .map(grade -> pass(exam, grade))
            .orElse(Reward.none());
    int levelsGained = gainExperience(reward.experience());
    ExamReport report = new ExamReport(exam, outcome, reward, levelsGained);
    listeners.forEach(listener -> listener.onExamConcluded(report));
}
```

`pass` registra il voto e consegna gli oggetti, `gainExperience` fa salire di livello,
`costOfNextLevel` legge la soglia. Allo stesso modo `Game.startExam` controlla le condizioni in
`requireStartable` invece di tenere tre `if` in testa al metodo.

Le catene di Stream lunghe sono spezzate in metodi privati con nome (regola 6): esempi nella pagina
[Strutture dati, equals e Stream](Strutture-dati-equals-e-Stream).

## Niente duplicazione (DRY)

| Duplicazione evitata | Dove sta il punto unico |
|---|---|
| controlli "testo non vuoto", "positivo", "non negativo" | `model.Preconditions` |
| validazione dell'intervallo dei voti | `Grade.of(int)`, unico modo di creare un voto (costruttore privato) |
| verifiche prima di usare un oggetto o un'abilità, per `canUse` e per `use` | un solo metodo privato, `ExamBattle.refusalOf`, che restituisce il motivo del rifiuto |
| "mostra l'indizio" per Riassunto e Ripasso lampo | un solo `HintEffect` |
| ricerca per chiave nei tre cataloghi XML | `CatalogIndex<T>`, usato per composizione |
| tipi XML ripetuti negli schemi | `common.xsd`, incluso con `xs:include` |
| validazione dei nomi degli slot | `SaveGameRepository.requireValidSlot`, statico nell'interfaccia: vale per ogni implementazione |

`refusalOf` è l'esempio più chiaro. Senza di lui `canUse(item)`, `canUse(ability)`,
`useItem` e `useAbility` ripeterebbero le stesse quattro verifiche. Con il record privato `Attempt`
oggetti e abilità passano dallo stesso percorso:

```java
public boolean canUse(Item item) {
    return refusalOf(attemptToUse(item)).isEmpty();
}

private void use(Attempt attempt) {
    refusalOf(attempt).ifPresent(refusal -> {
        throw refusal;
    });
    ...
}
```

## Data clumping: parameter object

Quando gli stessi dati viaggiano sempre insieme, diventano un oggetto.

- **`QuestionDetails`** raccoglie id, materia, difficoltà, testo e indizio, comuni a tutte le
  domande. Senza, il costruttore di `CodeOutputQuestion` avrebbe otto parametri; con il record ne
  ha quattro, e la validazione dei dati comuni sta in un punto. Le fabbriche `withHint` e
  `withoutHint` evitano di passare un `Optional` come argomento.
- **`BattleServices`** raccoglie regole, calcolatore del voto, timer, generatore casuale ed
  effetti, uguali per tutti gli scontri. `ExamBattleFactory` ha tre parametri e il costruttore di
  `ExamBattle` cinque.
- **`SaveData.StatsData`** tiene insieme le cinque statistiche nel file di salvataggio.
- **`ExamReport`** consegna in un'unica notifica esame, esito, ricompensa e livelli guadagnati.

Il design esclude anche le **liste parallele**, cioè dati collegati tenuti in liste diverse e
allineati per posizione. Una domanda a scelta, ad esempio, tiene in `AbstractChoiceQuestion` le
opzioni e quella corretta, e `wrongOptions()` si ricava da loro invece di essere una seconda lista
da tenere allineata.

## Gestione degli errori

### Fail fast, nei costruttori
Ogni classe del dominio valida i propri dati nel costruttore (regola 3). Un oggetto non valido non
può esistere, quindi chi lo usa non deve ricontrollarlo.

| Problema | Eccezione | Esempio |
|---|---|---|
| riferimento nullo | `NullPointerException` con messaggio, da `Objects.requireNonNull` | `Objects.requireNonNull(exam, "Exam must not be null")` |
| valore non valido | `IllegalArgumentException` | `Grade.of(17)`, un esame con 0 CFU, un nome di slot con `/` |
| chiamata nel momento sbagliato | `IllegalStateException` | rispondere prima di `begin()`, registrare due volte lo stesso esame, avviare il boss troppo presto |

I messaggi dicono cosa è sbagliato e con quale valore: "Grade must be between 18 and 31 (30 with
honors): 17", "Exam PROG1 is passed or not available yet".

I controlli ricorrenti stanno in `model.Preconditions`. Ogni metodo restituisce il valore
controllato, così si usa direttamente nell'assegnamento:

```java
this.code = requireNonBlank(code, "Exam code");
this.credits = requirePositive(credits, "Exam credits");
```

### Eccezioni dedicate ai confini
Dove i dati arrivano da fuori il programma ci sono due eccezioni dedicate, entrambe non
controllate:

- **`ContentException`** (contenuti XML): non c'è niente da recuperare all'avvio, quindi la
  composition root la cattura una volta sola e ferma il gioco con il messaggio. Il metodo
  `ContentException.building` trasforma un `IllegalArgumentException` del dominio in un errore di
  contenuto con file e id: "Invalid content in questions/prog.xml, question PROG-01: ...".
- **`SaveGameException`** (salvataggi): definita in `engine.repository` accanto all'interfaccia.
  La UI la distingue da un errore di programmazione: il menu mostra il motivo, `AutoSave` la passa
  a un callback e la partita continua.

### `Optional` invece di `null`
Nessun metodo pubblico restituisce `null` (regola 3). Quando un valore può mancare il tipo lo dice:
`Transcript.gradeOf(exam)`, `ExamCatalog.findByCode(code)`, `Game.currentBattle()`,
`Game.graduationGrade()`, `Question.hint()`, `ExamStrategy.nextQuestion(...)`. Per i numeri si usano
`OptionalInt` (`secondsPerQuestion()`, vuoto = nessun timer) e `OptionalDouble`
(`weightedAverage()`, vuoto senza esami). Anche dentro `ExamBattle` la domanda corrente è un
`Optional<PresentedQuestion>`, non un campo che può valere `null`.

## Niente accoppiamento temporale

Un ordine di chiamate che il codice non impone è uno stato non valido possibile. All'inizio
`Game.startExam` restituiva uno scontro e la UI doveva ricordarsi di registrare il listener e
chiamare `begin()`: uno scontro creato e mai iniziato avrebbe lasciato la partita "occupata". Ora
`startExam(exam, listener)` fa tutto in un'unica chiamata, nell'ordine giusto:

```java
student.updateStats(Stats::restoreFullEnergy);
ExamBattle battle = battles.create(student, exam);
battle.addListener(new BattleListener() { ... conclude(exam, outcome) ... });
battle.addListener(listener);
currentBattle = Optional.of(battle);
battle.begin();
```

Per lo stesso motivo `GameAssembly` costruisce tutto nel costruttore (niente metodo `init()` da
ricordare) e i controller ricevono le dipendenze nel costruttore (niente setter da chiamare prima di
mostrare la schermata).

## KISS e YAGNI: cosa non è stato costruito

Alcune cose mancano per scelta, perché non risolvevano un problema reale:

| Non c'è | Perché |
|---|---|
| un'interfaccia per `Graduation` | c'è una sola regola di laurea; un'interfaccia non risolverebbe nulla |
| listener dello scontro divisi in più interfacce | due soli client: è il compromesso spiegato in [Principi SOLID](Principi-SOLID) |
| un framework di iniezione delle dipendenze, `fx:controller` negli FXML | `ViewNavigator` crea i controller con il costruttore: le dipendenze si leggono lì |
| il filtro per tipo di domanda nell'esame Progetto | richiederebbe un controllo sul tipo concreto nell'engine |
| gli esami rimanenti nel salvataggio | si ricavano da sessione e libretto; salvarli sarebbe stato duplicato |
| il salvataggio dell'esame in corso | si salva dopo ogni esame concluso, ed è abbastanza per il gioco |
| un salvataggio su database | è solo descritto come estensione in [Estendibilità](Estendibilita) |
| punti bonus e lode di laurea | dipendono dai regolamenti dei corsi |
| librerie per l'XML | DOM, XSD e XPath del JDK bastano; l'unica dipendenza esterna è Gson |
| test con thread veri | non sarebbero ripetibili (vedi [Modelli di esecuzione](Modelli-di-esecuzione)) |

## Commenti

Niente codice commentato e niente commenti ovvi (regola 7). La Javadoc spiega il contratto e il
**perché** delle scelte, non il come. Ad esempio in `ExamSession`: "The session does not know which
exams are passed: that is recorded once, in the transcript, and the game combines the two. Keeping a
second list here would duplicate that state."
