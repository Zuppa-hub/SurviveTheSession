# Architettura e responsabilità

Il codice è diviso in cinque package sotto `it.unicam.cs.mpgc.rpg127295`, uno per
responsabilità. La regola che li tiene insieme è una sola: **le dipendenze vanno in una
direzione, verso il dominio**, e non ci sono cicli.

## I package

| Package | Responsabilità | Può usare |
|---|---|---|
| `model` | entità e stato del gioco: voti, esami, domande, studente, oggetti, libretto | solo il JDK |
| `engine` | regole e flusso: scontro, strategie, voto, effetti, timer, partita; le interfacce dei repository | `model` |
| `persistence` | contenuti XML (sola lettura) e salvataggi JSON | `engine`, `model`, DOM/XSD/XPath del JDK, Gson |
| `ui` | schermate JavaFX: input e output verso il giocatore | `engine`, `model`, JavaFX |
| `app` | composition root: crea e collega le implementazioni concrete | tutti |

```
            app
         /   |   \
       ui  persistence
         \   |
          engine
            |
          model
```

Le frecce scendono soltanto, e lo confermano gli `import` del codice:

- `model` importa solo classi di `model`;
- `engine` importa solo `engine` e `model`: niente JavaFX, niente Gson, niente `java.nio.file`;
- `persistence` importa `engine` e `model` (più Gson, DOM e `java.nio.file`), mai `ui` né `app`;
- `ui` importa `engine`, `model` e JavaFX, mai `persistence` né `app`;
- solo `app` importa tutto.

## Dentro l'engine

L'engine ha sottopackage con la stessa regola:

| Sottopackage | Contenuto | Dipende da |
|---|---|---|
| `engine.battle` | `ExamBattle`, `ExamBattleFactory`, le interfacce `ExamStrategy`, `BattleEffect`, `BattleRules`, `BattleListener` | `grading`, `timer`, `repository` |
| `engine.strategy` | le tre strategie d'esame | `battle` |
| `engine.effect` | i cinque effetti di oggetti e abilità | `battle` |
| `engine.grading` | `GradeCalculator`, `Performance` | nessuno |
| `engine.timer` | `QuestionTimer`, `ScheduledQuestionTimer` | nessuno |
| `engine.game` | `Game`, progressione, ricompense, laurea | `battle`, `repository` |
| `engine.repository` | cataloghi, `QuestionRepository`, `SaveGameRepository`, `SaveData`, `SaveMapper` | nessuno |

`engine.strategy` ed `engine.effect` contengono **solo implementazioni**: le interfacce che
implementano stanno in `engine.battle`, accanto a `ExamBattle` che le usa. All'inizio
`ExamStrategy` era in `engine.strategy` e i due package dipendevano l'uno dall'altro; spostarla ha
tolto il ciclo (dettagli in [Classi e interfacce](Classi-e-interfacce)).

## Perché non ci sono cicli: le interfacce stanno con chi le usa

Il punto delicato è sempre lo stesso: un package di livello alto deve **usare** qualcosa che sta
in un package di livello basso, senza dipenderne. Ad esempio l'engine ha bisogno delle domande,
ma non deve sapere che arrivano da un file XML.

La soluzione è il principio di inversione delle dipendenze (DIP): l'interfaccia la definisce chi
la **usa**, e chi la **implementa** dipende da lei.

| Interfaccia | Definita in (chi la usa) | Implementata in |
|---|---|---|
| `QuestionRepository`, `ExamCatalog`, `ProfileCatalog`, `ItemCatalog` | `engine.repository` | `persistence.content` (`XmlQuestionRepository`, `XmlExamCatalog`, ...) |
| `SaveGameRepository` | `engine.repository` | `persistence.save.JsonSaveGameRepository` |
| `ExamStrategy`, `BattleEffect` | `engine.battle` | `engine.strategy`, `engine.effect` |
| `BattleListener` | `engine.battle` | `ui.ExamController`; `Game` con una classe anonima |
| `GameListener` | `engine.game` | `app.AutoSave`; la UI con una lambda in `ViewNavigator.openGame` |
| `GameService` | `ui` | `app.GameAssembly` |

Così:
- **l'engine non conosce la persistenza**: usa `ExamCatalog`, e `XmlExamCatalog` dipende
  dall'engine, non il contrario;
- **l'engine non conosce la UI**: la notifica con `BattleListener` e `GameListener` (Observer). La
  UI chiama l'engine; l'engine "chiama" la UI solo attraverso interfacce sue;
- **la UI non conosce `app`**: per creare e caricare partite usa `GameService`, definita in `ui`.
  Se la UI chiamasse direttamente `GameAssembly`, `ui` dipenderebbe da `app` e `app` da `ui`: un
  ciclo.

## La composition root (`app`)

Tutte le implementazioni concrete si scelgono e si collegano in **un solo punto**. Altrove le
dipendenze arrivano dal costruttore, come interfacce. Niente Singleton e niente stato statico
mutabile (regola 5 del design).

| Classe | Cosa fa |
|---|---|
| `Main` | lancia `GameApplication`; una classe separata permette di avviare il gioco dal class path |
| `GameApplication` | avvio e chiusura di JavaFX: carica i contenuti, crea lo scheduler del timer, `JsonSaveGameRepository` e `GameAssembly` con `Platform::runLater`; in `stop()` chiude lo scheduler |
| `GameContent` | sceglie le implementazioni XML dei cataloghi e i parser delle domande (letti da `@QuestionElement`) |
| `GameEffects` | associa ogni oggetto di `items.xml` al suo `BattleEffect` per id, e definisce le abilità |
| `GameAssembly` | collega regole, calcolatore del voto, timer, strategie, effetti, ricompense e salvataggi; implementa `ui.GameService` |
| `AutoSave` | il `GameListener` che salva dopo ogni esame |

Il cuore di `GameAssembly` è il costruttore: è qui, e solo qui, che compaiono le classi concrete
dell'engine.

```java
BattleServices services = new BattleServices(new StandardBattleRules(), new PerformanceGradeCalculator(),
        new ScheduledQuestionTimer(scheduler, uiExecutor), random, GameEffects.of(content.items()));
this.battles = new ExamBattleFactory(strategies(), content.questions(), services);
```

Le strategie arrivano in una `EnumMap` costruita in `app`:

```java
Map<ExamType, ExamStrategy> strategies = new EnumMap<>(ExamType.class);
strategies.put(ExamType.WRITTEN, new WrittenExamStrategy());
strategies.put(ExamType.ORAL, new OralExamStrategy());
strategies.put(ExamType.PROJECT, new ProjectExamStrategy());
```

**Fail fast all'avvio.** Tutto si costruisce subito, nel costruttore. Una strategia mancante
(`ExamBattleFactory` controlla che ogni `ExamType` ne abbia una) o un oggetto senza effetto
(`GameEffects`) fermano il gioco prima che compaia il menu, non a metà partita. Contenuti XML non
validi diventano una `ContentException`, che `GameApplication.start` mostra in una finestra
d'errore prima di chiudere.

**Le dipendenze esterne entrano da qui.** Il tempo e il caso non sono mai creati dentro il
dominio: `GameApplication` passa `RandomGenerator.getDefault()`, lo scheduler e
`Platform::runLater`. I test passano al loro posto `FakeRandom`, `ManualScheduler` ed esecutori
diretti (vedi [Testing](Testing)).

## Chi possiede cosa

- **Lo scheduler del timer** lo crea `GameApplication` e lo chiude `GameApplication.stop()`:
  chi crea una risorsa la chiude. `ScheduledQuestionTimer` lo usa ma non lo possiede.
- **Lo stato della partita** è in `Game` e `Student`; la UI lo legge (`Game.availableExams()`,
  `Game.currentBattle()`) e non lo duplica.
- **Il salvataggio** non è responsabilità di `Game`: è `AutoSave`, registrato da `GameAssembly`.

## Pagine collegate

- [Principi SOLID](Principi-SOLID): DIP e gli altri principi, con esempi.
- [Modelli di esecuzione](Modelli-di-esecuzione): il thread del timer e quello dell'interfaccia.
- [Persistenza](Persistenza) e [Interfaccia grafica](Interfaccia-grafica): i due package esterni.
- [Estendibilità](Estendibilita): cosa cambia, package per package, per aggiungere qualcosa.
