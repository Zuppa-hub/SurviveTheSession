# Principi SOLID

Per ogni principio: cosa dice, dove si vede nel codice e quale alternativa è stata scartata, con
il motivo. I nomi e i frammenti di codice sono quelli veri del progetto.

## S: Single Responsibility Principle

*Una classe ha un solo motivo per cambiare.*

### Il libretto non sa quando ci si laurea
`Transcript` registra gli esami superati, impedisce i doppioni e calcola CFU e media ponderata.
La regola "tutti gli esami della sessione superati" e il voto su 110 stanno in `Graduation`:

```java
public boolean canGraduate(ExamSession session, Transcript transcript) {
    return session.exams().stream().allMatch(transcript::hasPassed);
}
```

**Scartato:** un metodo `puoLaurearsi()` nel libretto, l'esempio di violazione visto a lezione con
la classe `Libretto`. Il libretto cambierebbe per due motivi: come si registrano i voti e come
cambiano le regole di laurea.

### Dati e comportamento degli oggetti separati
`Item` (model) è solo un dato: id, nome, descrizione. Cosa fa durante un esame è un
`BattleEffect` (engine), associato all'oggetto per id in `app.GameEffects`.

**Scartato:** il comportamento dentro `Item`. L'effetto dipende dallo stato dello scontro, quindi
il model dipenderebbe dall'engine.

### La partita non si salva da sola
`Game` applica l'esito di un esame e avvisa i `GameListener`. Il salvataggio è `AutoSave`, un
listener registrato da `GameAssembly`. Allo stesso modo le classi di supporto della UI
(`ExamRow`, `OutcomeSummary`, `GraduationSummary`, ...) tolgono ai controller tutto quello che non
è collegamento con JavaFX.

**Scartato:** `Game` che chiama `SaveGameRepository` alla fine di ogni esame. Cambierebbe sia per
le regole di gioco sia per il modo di salvare.

## O: Open/Closed Principle

*Aperto alle estensioni, chiuso alle modifiche.*

### Tipi d'esame senza `switch`
`ExamType` è un enum senza comportamento. Ogni tipo ha una `ExamStrategy`, e l'associazione è una
`EnumMap<ExamType, ExamStrategy>` costruita in `app`. `ExamBattleFactory` è l'unico punto che la
consulta:

```java
return new ExamBattle(student, exam, strategies.get(exam.type()), pool, services);
```

Lo scontro chiede alla strategia la prossima domanda (`nextQuestion`), il timer
(`secondsPerQuestion`) e il peso degli errori (`mistakeMultiplier`), senza sapere di che tipo è
l'esame.

**Scartato:** uno `switch (exam.type())` nell'engine. Ogni nuovo tipo obbligherebbe a cercare e
modificare tutti gli `switch`. Con la mappa basta una classe e una voce (vedi
[Estendibilità](Estendibilita); l'enum va comunque esteso).

### Nuovi tipi di domanda con un parser annotato
`XmlQuestionRepository` sceglie il parser dal nome dell'elemento XML, in una
`Map<String, QuestionParser>`. La mappa la costruisce `QuestionParsers.byElement` leggendo
`@QuestionElement` su ogni parser (lezione 03):

```java
@QuestionElement("fillIn")
public final class FillInParser implements QuestionParser { ... }
```

**Scartato:** uno `switch` sul nome dell'elemento nel repository, escluso già nella richiesta della
#5. Poi è stata sostituita anche la prima versione della mappa, scritta a mano in `GameContent`
(`Map.of("multipleChoice", new MultipleChoiceParser(), ...)`): il nome dell'elemento stava lontano
dalla classe che lo gestisce.

### Oggetti e abilità come dati
Un nuovo oggetto con un effetto esistente è una voce nella mappa `EFFECTS_BY_ITEM_ID` di
`GameEffects`; una nuova abilità è un record:

```java
new Ability("Ripasso lampo", 2, SHOW_HINT)
```

Riassunto e Ripasso lampo usano lo stesso `HintEffect`.

## L: Liskov Substitution Principle

*Un sottotipo si può usare ovunque si usa il tipo, senza sorprese.* Come visto nella lezione 10, il
contratto di un'interfaccia sono le **aspettative** di chi la usa: per questo nel progetto ogni
interfaccia importante le scrive in Javadoc e un test di contratto le verifica su ogni
implementazione (vedi [Testing](Testing)).

### Il contratto di `Question`
La Javadoc di `Question` dice: implementazioni immutabili, `isCorrect` non lancia mai eccezioni
(anche con risposte vuote o `null`), due domande sono uguali se e solo se hanno lo stesso id,
qualunque sia il tipo concreto. In `AbstractQuestion` `equals` e `hashCode` sono `final`, così una
sottoclasse non può rompere la regola:

```java
@Override
public final boolean equals(Object other) {
    return other instanceof Question question && id().equals(question.id());
}
```

`CodeOutputQuestion` è sorella di `MultipleChoiceQuestion`, non figlia: entrambe estendono
`AbstractChoiceQuestion`. Il testo resta in `prompt()` e lo snippet è un dato in più (`code()`,
`codeSnippet()`): chi usa una domanda a scelta non trova un comportamento diverso.

### Il contratto di `GradeCalculator`
Il contratto fissa proprietà, non una formula: voto valido oppure `Optional.empty()`, nessuna
risposta corretta → insufficiente, prestazione perfetta → sufficiente, una risposta corretta in più
non peggiora mai il voto, un indizio in più non lo migliora mai.

**Scartato:** un contratto con soglia e formula di `PerformanceGradeCalculator`. Coinciderebbe con
un'unica implementazione e l'interfaccia non servirebbe più. È il ragionamento del
`ValutatoreMinimale` della lezione 10: anche "18 a chiunque superi la soglia" rispetta il
contratto e può sostituire l'implementazione principale.

### Il Vero/Falso è una domanda a scelta vera
`TrueFalseQuestion` è una `ChoiceQuestion` con due opzioni. Gli Appunti richiedono almeno tre
opzioni sbagliate visibili (`FiftyFiftyEffect`): il Vero/Falso, che ne ha una, è escluso dalla
regola stessa, non da un controllo sul tipo.

**Scartato:** `if (question instanceof TrueFalseQuestion)` negli Appunti. Il sottotipo
richiederebbe un trattamento speciale, cioè non sarebbe sostituibile.

## I: Interface Segregation Principle

*Nessun client deve dipendere da metodi che non usa.*

### `ChoiceQuestion` separata da `Question`
Solo le domande a scelta hanno opzioni, quindi `options()` e `wrongOptions()` stanno in
`ChoiceQuestion`. `FillInQuestion` implementa `Question` e non deve inventarsi una lista vuota di
opzioni.

### `BattleControls`: cinque operazioni, non tutto lo scontro
Un effetto riceve `BattleControls` (`addExtraTime`, `removeTwoWrongOptions`, `showHint`,
`restoreEnergy`, `doubleNextCorrectAnswer`), non `ExamBattle`. La implementa una classe interna
privata di `ExamBattle`, passata all'effetto solo dopo le verifiche. È anche incapsulamento
(lezione 07): la modifica non è vietata, è controllata.

**Scartato:** passare `ExamBattle` agli effetti. Quei metodi dovrebbero diventare pubblici e
chiunque, anche la UI, potrebbe dare tempo extra senza consumare un Caffè.

### Interfacce piccole per la UI e per il timer
- `GameService` ha i quattro metodi che servono alla UI per iniziare o riprendere una partita
  (`profiles`, `savedGames`, `newGame`, `loadGame`), non tutto `GameAssembly`.
- `TimerListener` (`onTick`, `onExpired`) è separato da `BattleListener`: il timer non sa niente
  di domande e risposte.
- `GameListener` ha un solo metodo ed è `@FunctionalInterface`: la UI lo registra con una lambda,
  `game.addListener(report -> showOutcome(game, report))`.

**Un compromesso dichiarato.** `BattleListener` ha cinque notifiche con un'implementazione
`default` vuota, così `Game` scrive solo `onBattleEnded`. Listener separati sarebbero più fedeli
all'ISP, ma con due soli client (la schermata d'esame, che usa quasi tutto, e la partita)
moltiplicherebbero le registrazioni senza un vantaggio concreto. Il motivo è spiegato in
[Classi e interfacce](Classi-e-interfacce).

## D: Dependency Inversion Principle

*I moduli di alto livello dipendono da astrazioni; le astrazioni stanno con chi le usa.*

### Repository e cataloghi nell'engine
`ExamCatalog`, `QuestionRepository`, `SaveGameRepository` e gli altri sono definiti in
`engine.repository` e implementati in `persistence`. La dipendenza va da `persistence` verso
`engine`.

**Scartato:** le interfacce in `persistence`, accanto alle implementazioni. L'engine dovrebbe
importare `persistence` per usarle, e il dominio dipenderebbe dal modo in cui si leggono i file.

### Lo scontro dipende solo da interfacce
`ExamBattle` conosce `ExamStrategy`, `BattleRules`, `GradeCalculator`, `QuestionTimer`,
`BattleEffect` e `RandomGenerator`. Le implementazioni arrivano in `BattleServices`, costruito in
`app`. Nei test le stesse interfacce ricevono regole fisse, un calcolatore scritto come lambda
(`performance -> Optional.of(Grade.of(28))`), `ManualQuestionTimer` e `FakeRandom`.

### Il timer non conosce JavaFX
`ScheduledQuestionTimer` riceve uno `ScheduledExecutorService` e un `Executor` per le notifiche.
In `app` l'esecutore è `Platform::runLater`; nei test un esecutore diretto o una coda. Chiamare
`Platform.runLater` dentro il timer legherebbe l'engine a JavaFX e renderebbe il timer
impossibile da testare senza la piattaforma grafica.

### Stessa regola dentro l'engine e per la UI
`ExamStrategy` e `BattleEffect` stanno in `engine.battle`, con chi le usa; le implementazioni in
`engine.strategy` ed `engine.effect`. `GameService` sta in `ui` e la implementa `app`. Così non c'è
nessun ciclo tra package (vedi [Architettura e responsabilità](Architettura-e-responsabilita)).

## Riepilogo

| Principio | Esempi principali | Alternativa scartata |
|---|---|---|
| SRP | `Transcript` / `Graduation`, `Item` / `BattleEffect`, `AutoSave` | `puoLaurearsi()` nel libretto |
| OCP | `EnumMap<ExamType, ExamStrategy>`, `@QuestionElement`, `GameEffects` | `switch` sul tipo d'esame o di domanda |
| LSP | contratti di `Question`, `GradeCalculator`, `ExamStrategy` con test di contratto | `instanceof` sul Vero/Falso |
| ISP | `ChoiceQuestion`, `BattleControls`, `GameService`, `TimerListener` | effetti che ricevono `ExamBattle` |
| DIP | `engine.repository`, `BattleServices`, `Executor` del timer | interfacce in `persistence` |
