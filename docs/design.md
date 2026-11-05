# Sopravvivi alla Sessione: documento di design

Progetto d'esame di Metodologie di Programmazione (UNICAM, A.A. 2025/26). Autore: Andrea Cazzato, matricola 127295.

Questo documento è la fonte di verità per la progettazione. Ogni sessione di sviluppo lo rispetta. Se una scelta va cambiata, si aggiorna prima questo documento, poi il codice.

---

## 1. Obiettivo e criterio guida

Dalle slide del corso: "Il gioco NON è il fine, è il contesto. Il valore del progetto è in come lo progettate e lo sviluppate. Il vostro obiettivo è farci capire che avete capito TUTTI i concetti visti a lezione."

Quindi:
- **Poche funzionalità, fatte bene.** Ogni classe deve avere un motivo per esistere e, possibilmente, mostrare un concetto del corso.
- **SOLID e Clean Code sono requisiti**, non decorazioni: il progetto viene valutato su questi.
- **Niente funzionalità da "videogioco commerciale"**: mappe esplorabili, negozi, dialoghi, grafica elaborata sono fuori scope.

## 2. Il gioco in breve

Lo studente deve superare una **sessione d'esami**. Ogni esame è uno scontro a turni in cui il "combattimento" consiste nel rispondere a **domande vere sulla materia**. Le statistiche dello studente e gli oggetti influenzano lo scontro. Gli esami superati entrano nel **libretto** con voto e CFU. Quando tutti gli esami della sessione sono superati, lo studente "si laurea" e vede il riepilogo (media ponderata, CFU, voto di laurea indicativo).

### 2.1 Profilo dello studente (scelto a inizio partita)
Il profilo determina le statistiche iniziali. Tre profili, definiti in un file di dati:
| Profilo | Conoscenza | Energia | Concentrazione |
|---|---|---|---|
| Secchione | alta | bassa | media |
| Pendolare | bassa | alta | media |
| Nottambulo | media | media | alta |

### 2.2 Statistiche
- **Conoscenza**: aumenta i "punti" tolti all'esame con una risposta corretta e la probabilità di ricevere un indizio gratuito.
- **Energia** (vita): diminuisce a ogni risposta sbagliata o tempo scaduto. A zero lo studente viene bocciato in quell'esame.
- **Concentrazione** (risorsa): si spende per le abilità; si recupera di 1 a ogni risposta corretta.
- **Livello ed esperienza**: superare un esame dà esperienza; salire di livello aumenta Conoscenza ed Energia massima. È la progressione RPG.

### 2.3 Lo scontro (esame)
- L'esame ha una **resistenza** (punti esame). Ogni risposta corretta la riduce di un danno che cresce con la Conoscenza. La resistenza non è un dato di `Exam`: è una regola di bilanciamento e la calcola l'engine da CFU e difficoltà.
- Ogni risposta sbagliata (o tempo scaduto) toglie Energia in base alla difficoltà dell'esame.
- Nello stesso esame una domanda non viene mai riproposta.
- La prova termina quando la resistenza arriva a zero **oppure** quando le domande finiscono. In entrambi i casi decide il **voto** (18-30, 31 = 30 e lode), calcolato dalla prestazione (percentuale di risposte corrette, eventuali penalità): con un voto l'esame è superato, senza voto è una bocciatura.
- Energia a zero: bocciatura, senza calcolare il voto.
- Azioni del turno: **Rispondi**, **Usa oggetto**, **Usa abilità**, **Ritirati** (l'esame resta da sostenere, nessun voto).
- Bocciatura o ritiro: l'esame resta nella sessione e si può ritentare; prima di ritentare lo studente recupera Energia.

### 2.4 Tipi di esame (strategie)
| Tipo | Comportamento |
|---|---|
| Scritto | domande a tempo (timer per domanda); tempo scaduto = risposta sbagliata |
| Orale | nessun timer; dopo un errore il "professore incalza": la domanda successiva è di difficoltà maggiore |
| Progetto | domande di ogni tipo, nessun timer, danno doppio sugli errori (non filtra per tipo di domanda: servirebbe un controllo sul tipo concreto nell'engine) |

### 2.5 Tipi di domanda
| Tipo | Risposta |
|---|---|
| Scelta multipla | una opzione tra 4 |
| Vero/Falso | vero o falso |
| Output del codice | uno snippet di codice e 4 possibili output (variante della scelta multipla con codice) |
| Completamento | testo libero breve, confrontato in modo normalizzato (maiuscole, spazi) |

### 2.6 Oggetti e abilità
| Oggetto | Effetto | Usabile su |
|---|---|---|
| Caffè | +15 secondi alla domanda corrente | solo se la domanda corrente ha il timer |
| Appunti | elimina due opzioni sbagliate (50/50) | solo se restano almeno 3 opzioni sbagliate visibili (quindi una volta per domanda, mai sul Vero/Falso né sul completamento) |
| Riassunto | mostra l'indizio della domanda | domande con indizio non ancora mostrato |
| Energy drink | recupera 4 punti di Energia, senza superare il massimo | solo se l'Energia non è al massimo |

| Abilità | Costo Concentrazione | Effetto |
|---|---|---|
| Ripasso lampo | 2 | mostra l'indizio (stesso effetto del Riassunto) |
| Colpo di genio | 3 | la prossima risposta corretta vale doppio danno; non si accumula, un errore non lo annulla, finisce con lo scontro |

Un indizio conta come penalità nel voto una sola volta per domanda, da qualunque fonte arrivi. Oggetti e abilità si usano solo mentre una domanda attende risposta; un oggetto o un'abilità non applicabile non viene consumato.

Oggetti ottenuti come ricompensa a fine esame (in base al voto).

### 2.7 Materie e contenuti
Cinque materie reali del corso di Informatica, ognuna con un file XML di domande (15-20 domande ciascuna, scritte originali e **verificate a mano da Andrea**):
1. Programmazione (scritto)
2. Algoritmi e Strutture Dati (scritto)
3. Basi di Dati (orale)
4. Reti di Calcolatori (orale)
5. Metodologie di Programmazione (progetto, boss finale): domande su SOLID, Clean Code, Git, collezioni, equals/hashCode, Stream API, modelli di esecuzione.

---

## 3. Architettura

### 3.1 Principi architetturali
- **Il dominio non dipende da niente**: `model` non importa `engine`, `persistence` né `ui`.
- **L'engine dipende solo da model e da interfacce** (mai da JavaFX, mai da Gson, mai da file).
- **Le interfacce stanno dalla parte di chi le usa (DIP)**: repository e cataloghi sono definiti nell'engine (`engine.repository`); `persistence` contiene solo le implementazioni e dipende da `engine` e `model`, mai il contrario. Vale anche dentro l'engine: `ExamStrategy` sta in `engine.battle` con `ExamBattle`, e `engine.strategy` contiene solo le implementazioni. Nessun ciclo fra package.
- **La UI chiama l'engine, mai il contrario**: l'engine notifica la UI tramite listener (Observer) definiti nell'engine.
- **Composition root**: tutte le implementazioni concrete vengono create e collegate in un solo punto (`app`). Nessun Singleton. Dipendenze passate nel costruttore.
- **Nessuna dipendenza da casualità o tempo reale nel dominio**: `RandomGenerator` e il timer vengono iniettati, così i test sono deterministici.

### 3.2 Package (base `it.unicam.cs.mpgc.rpg127295`)
```
app            composition root, avvio JavaFX
model
  student      Student, Stats, StudentProfile, Level
  exam         Exam, ExamType, ExamSession, Grade
  question     Question, ChoiceQuestion, MultipleChoiceQuestion, TrueFalseQuestion,
               CodeOutputQuestion, FillInQuestion, QuestionVisitor, Difficulty, Subject
  item         Item (solo dati: id, nome, descrizione), Inventory
  transcript   Transcript (libretto), TranscriptEntry
engine
  battle       ExamBattle, ExamBattleFactory, BattleServices, BattleState, BattleOutcome,
               BattleResult, BattleListener, QuestionPool, PresentedQuestion, AnswerResult,
               BattleRules, StandardBattleRules, ExamStrategy (interfaccia, con chi la usa),
               BattleEffect, BattleControls, BattleEffects, Ability (record: nome, costo, effetto)
  strategy     WrittenExamStrategy, OralExamStrategy, ProjectExamStrategy (solo implementazioni:
               strategy dipende da battle, mai il contrario)
  grading      GradeCalculator, PerformanceGradeCalculator, Performance
  effect       ExtraTimeEffect, FiftyFiftyEffect, HintEffect, EnergyRestoreEffect, DoubleDamageEffect
               (solo implementazioni di BattleEffect: effect dipende da battle, mai il contrario)
  timer        QuestionTimer, TimerListener, ScheduledQuestionTimer
  game         Game (partita: studente + sessione + progressione), RewardPolicy
  repository   QuestionRepository, ExamCatalog, ProfileCatalog, ItemCatalog, SaveGameRepository,
               SaveData (DTO del contratto di salvataggio), SaveMapper
persistence
  content      implementazioni XML dei cataloghi e del repository delle domande (DOM, XSD, XPath)
  save         JsonSaveGameRepository (Gson)
ui             controller JavaFX, ViewNavigator, adattatori dei listener
```
Risorse:
```
src/main/resources/
  content/exams.xml, content/profiles.xml, content/items.xml
  content/questions/<materia>.xml
  content/schema/common.xsd (tipi condivisi, xs:include), questions.xsd, exams.xsd, profiles.xsd, items.xsd
  ui/*.fxml, ui/styles.css
```

### 3.3 Interfacce principali (contratti)
Ogni interfaccia ha Javadoc che rende esplicite le **aspettative del tipo** (LSP, lezione 10).

```java
/** Una domanda d'esame. Implementazioni immutabili.
 *  Contratto: due domande sono uguali se e solo se hanno lo stesso id. */
public interface Question {
    String id();                  // univoco nel contenuto, identità della domanda
    Subject subject();
    Difficulty difficulty();
    String prompt();
    Optional<String> hint();
    /** true se la risposta è corretta; non lancia eccezioni per risposte sbagliate o vuote. */
    boolean isCorrect(String answer);
    /** Visitor: chiama esattamente un metodo del visitor, passando sé stessa. */
    <R> R accept(QuestionVisitor<R> visitor);
}

/** Una categoria di domanda = un metodo. Evita instanceof nell'engine (opzioni da mescolare),
 *  nella UI (pulsanti o campo di testo) e nell'effetto Appunti.
 *  Un nuovo tipo in una categoria esistente non cambia il visitor; una categoria nuova sì. */
public interface QuestionVisitor<R> {
    R visitChoice(ChoiceQuestion question);
    R visitFillIn(FillInQuestion question);
}

/** Domanda con opzioni tra cui scegliere (ISP: solo queste hanno opzioni).
 *  Il 50/50 (Appunti) si applica solo se wrongOptions().size() >= 3, così dopo aver
 *  eliminato due opzioni sbagliate ne resta almeno una: il Vero/Falso è escluso. */
public interface ChoiceQuestion extends Question {
    List<String> options();       // lista immutabile, almeno 2 opzioni
    List<String> wrongOptions();  // sottoinsieme di options()
}

/** Comportamento di un tipo d'esame (OCP: nuovo tipo = nuova classe).
 *  Le strategie arrivano all'engine in una EnumMap<ExamType, ExamStrategy> costruita in app. */
public interface ExamStrategy {
    Optional<Question> nextQuestion(BattleState state, QuestionPool pool); // vuoto solo se il pool è vuoto
    OptionalInt secondsPerQuestion();   // vuoto = nessun timer
    int mistakeMultiplier();            // >= 1; moltiplica la perdita di Energia di BattleRules
}
// Scritto: domanda qualsiasi, 30 s, x1. Orale: dopo un errore difficoltà harder() della domanda
// sbagliata (se finite, la più vicina), nessun timer, x1. Progetto: domanda qualsiasi, nessun timer, x2.
// QuestionPool: domande della materia per uno scontro; HashSet delle domande già poste,
// drawAny() e drawClosestTo(Difficulty) con il RandomGenerator iniettato.

/** Regole numeriche dello scontro, in un solo punto (valori provvisori, bilanciamento in #12). */
public interface BattleRules {
    int resistanceOf(Exam exam);            // CFU x fattore della difficoltà
    int damageOf(Stats stats);              // danno base + bonus della Conoscenza
    int baseEnergyLoss(Difficulty difficulty);
    int concentrationPerCorrectAnswer();
}

/** Calcola il voto da una prestazione.
 *  Contratto: restituisce sempre un Grade valido (18-30 o lode) per una prestazione sufficiente;
 *  per una prestazione insufficiente restituisce Optional.empty(). Mai voti fuori range. */
@FunctionalInterface
public interface GradeCalculator {
    Optional<Grade> calculate(Performance performance);
}
// Contratto completo nella Javadoc di GradeCalculator, verificato da GradeCalculatorContractTest:
// nessuna risposta corretta -> insufficiente; prestazione perfetta -> sufficiente;
// una risposta corretta in più non peggiora mai il voto; un indizio in più non lo migliora mai.
// Performance = risposte corrette, risposte sbagliate (tempi scaduti compresi), indizi usati.

/** model.item: un oggetto dell'inventario, solo dati (il model non dipende dall'engine, §3.1).
 *  Identità = id. */
public final class Item {
    public String id();
    public String name();
    public String description();
}

/** engine.battle: l'effetto di un oggetto o di un'abilità durante un esame.
 *  Oggetti: Map<Item, BattleEffect> costruita in app. Abilità: dati, record Ability(nome,
 *  costo in Concentrazione, BattleEffect). Così "mostra indizio" (Riassunto e Ripasso lampo)
 *  si scrive una volta sola (HintEffect). */
public interface BattleEffect {
    boolean canBeUsedIn(BattleState state);
    void applyTo(BattleControls controls);
}

/** Interfaccia ristretta con cui un effetto modifica lo scontro (incapsulamento: la modifica
 *  non è vietata, è controllata). La implementa una classe interna privata di ExamBattle;
 *  ExamBattle espone solo useItem/useAbility, che verificano prima di applicare. */
public interface BattleControls {
    void addExtraTime();              // BattleRules.extraSeconds()
    void removeTwoWrongOptions();     // dalla PresentedQuestion corrente, scelte con il RandomGenerator
    void showHint();                  // una volta per domanda: Performance.withHint()
    void restoreEnergy();             // BattleRules.energyRestored()
    void doubleNextCorrectAnswer();   // BattleRules.boostedDamageMultiplier()
}

// engine.repository: interfacce definite dall'engine (che le usa) e implementate in persistence (DIP)
public interface QuestionRepository { List<Question> findBySubject(Subject subject); }
public interface ExamCatalog    { List<Exam> all(); Optional<Exam> findByCode(String code); }
public interface ProfileCatalog { List<StudentProfile> all(); Optional<StudentProfile> findById(String id); }
public interface ItemCatalog    { List<Item> all(); Optional<Item> findById(String id); }
public interface SaveGameRepository {
    void save(String slot, SaveData data);
    Optional<SaveData> load(String slot);
    List<String> listSlots();
    void delete(String slot);
}

/** Notifiche dall'engine alla UI (Observer, modello event-driven). */
public interface BattleListener {
    void onQuestionPresented(PresentedQuestion question, OptionalInt seconds); // opzioni già mescolate
    void onAnswerEvaluated(boolean correct, BattleState state);
    void onTimeTick(int secondsLeft);
    void onBattleEnded(BattleOutcome outcome);
}
```

### 3.4 Dove si vede ogni concetto del corso
| Concetto | Dove |
|---|---|
| Incapsulamento, validazione nei costruttori | Stats, Grade, Exam, Student (fail fast, `IllegalArgumentException`) |
| Ereditarietà, classi astratte | `AbstractQuestion` (campi comuni di tutte le domande), `AbstractChoiceQuestion` |
| Interfacce, polimorfismo | Question, ExamStrategy, BattleEffect, GradeCalculator, BattleRules; Visitor (`QuestionVisitor`) al posto di `instanceof` sul tipo di domanda |
| SRP | Transcript registra gli esami superati (senza doppioni) e calcola media ponderata sui CFU e CFU totali, e nient'altro: **non** contiene la regola di laurea né il voto di laurea (è la violazione di SRP vista a lezione con `puoLaurearsi()` in `Libretto`), che stanno in Game (#8); la formattazione dei voti sta in Grade; la stampa sta nella UI; dati e comportamento degli oggetti sono separati (Item nel model, BattleEffect nell'engine) |
| OCP | nuovo tipo di esame, domanda, oggetto, abilità = nuova classe, nessuna modifica all'engine; nell'engine nessuno `switch`/`if` su `ExamType`: l'associazione tipo → strategia è una `EnumMap<ExamType, ExamStrategy>` costruita in `app` e passata all'engine; allo stesso modo un nuovo oggetto = un `BattleEffect` (nuovo o esistente) + una voce nella mappa id → effetto costruita in `app`; una nuova abilità = un record `Ability` con un effetto |
| LSP | contratti espliciti in Javadoc; metodi di validazione condivisi (es. `Grade.of(int)` unico punto di creazione) |
| ISP | ChoiceQuestion separata da Question; listener piccoli e separati |
| DIP | ExamBattle dipende da ExamStrategy, GradeCalculator, QuestionTimer, BattleEffect; Game dipende da SaveGameRepository; le interfacce di repository e cataloghi stanno in `engine.repository` (dalla parte di chi le usa) e `persistence` le implementa: la dipendenza va da `persistence` verso `engine`; tutto collegato in `app` |
| Clean Code | nomi significativi, metodi piccoli, niente codice commentato, DRY, niente liste parallele (data clumping) |
| Package | struttura per responsabilità (3.2) |
| List | domande della sessione, ordine degli esami |
| HashSet + equals/hashCode | esami superati; domande già poste nello scontro (`QuestionPool`, per non ripeterle); identità per `id` |
| HashMap | libretto `Map<Exam, Grade>`; inventario `Map<Item, Integer>` (quantità); effetti `Map<Item, BattleEffect>` costruita in `app` |
| Stream API | media ponderata, CFU totali, filtri domande per difficoltà, statistiche; catene lunghe spezzate in metodi con nome |
| Modelli di esecuzione | GUI event-driven; timer della domanda su thread separato (ScheduledExecutorService) con passaggio al thread JavaFX via `Platform.runLater`; stato condiviso protetto |
| Gradle | build, run, test, dipendenze, plugin JavaFX |
| Annotation | `@Override`, `@FunctionalInterface` (listener e timer), `@Test`; da completare con le slide del Prof. Rossi |
| Persistenza | contenuti XML (sola lettura, in resources) + salvataggi JSON (Gson, cartella dell'utente) |
| XML, XML Schema, XPath, parser | domande ed esami in XML, validati con XSD all'avvio, interrogati con XPath |
| JavaFX | FXML + CSS + controller sottili |
| Git e GitHub | Issues come backlog, branch `feature/*`, pull request su `develop`, commit piccoli |
| Testing | JUnit su model, engine, persistence; test deterministici con random e timer finti |

### 3.5 Identità degli oggetti (equals/hashCode)
- `Exam`: identità = `code` (es. "PROG1"). Usato come chiave nel libretto.
- `Question`: identità = `id`.
- `Item`: identità = `id` (due Caffè sono lo stesso oggetto per l'inventario).
- `StudentProfile`: identità = `id`.
- `Grade`, `Stats`: value object, uguaglianza su tutti i campi (record dove ha senso).
- Classi senza identità chiara e stabile (ExamBattle, Game): **niente equals** (lezione 13).

### 3.6 Concorrenza (timer degli scritti)
- `QuestionTimer` (interfaccia) con `start(int seconds, TimerListener listener)`, `addSeconds(int)`, `stop()`. Contratto: dopo `stop()` o un nuovo `start()` nessuna notifica del conto alla rovescia precedente raggiunge un listener.
- `ScheduledQuestionTimer(ScheduledExecutorService scheduler, Executor notifier)`: `scheduleAtFixedRate` ogni secondo su uno scheduler a thread singolo (daemon) creato in `app`. Il timer non possiede lo scheduler: lo chiude chi lo crea, con `shutdownNow()` in `Application.stop()` (#11).
- **Notifiche tramite l'`Executor`**: in `app` `Platform::runLater`, nei test un esecutore diretto o in coda. Così `ExamBattle` viene modificato sempre dal solo thread della UI e resta non thread-safe per scelta (thread confinement). Le notifiche si consegnano **fuori dal lock**: chiamare codice esterno tenendo un lock rischia il deadlock.
- **Stato condiviso protetto con `synchronized`**: tempo residuo, `ScheduledFuture`, listener e numero di generazione cambiano insieme (tick sul thread del timer, `addSeconds` del Caffè e `start`/`stop` sul thread della UI). `AtomicInteger` renderebbe atomico solo il contatore, non l'invariante su più campi. Nella Wiki si spiega il perché con la race condition del contatore della lezione 16.
- **Scadenze vecchie ignorate**: ogni `start()` e `stop()` incrementa un numero di generazione; ogni tick e ogni notifica ricorda la generazione in cui è nato e, alla consegna, viene scartato se non è più quella corrente. Così una scadenza già in coda che arriva dopo la risposta non conta sulla domanda successiva.
- Nei test niente tempo reale: `ManualQuestionTimer` per lo scontro; per `ScheduledQuestionTimer` uno scheduler manuale (registra il compito senza pianificarlo, il test fa scattare i secondi) e un esecutore in coda che simula la coda di `Platform.runLater`.

### 3.7 Persistenza
- **Contenuti (sola lettura)**: XML in `resources/content`, validati con XSD all'avvio. Un errore nei contenuti blocca l'avvio con messaggio chiaro (fail fast). I file si leggono con `getResourceAsStream`, gli schemi dal loro URL (`getResource`) così gli `xs:include` si risolvono anche dentro il JAR. Ogni errore diventa una `ContentException` (file, riga, colonna o id). Le domande si costruiscono con una `Map<String, QuestionParser>` (nome dell'elemento → parser) costruita in `app`: nessuno `switch` sul tipo di domanda. XPath serve per leggere i campi relativi a un elemento; i filtri sulle domande si fanno con gli Stream nell'engine.
- **Salvataggi**: JSON con Gson in `~/.sopravvivi-alla-sessione/saves/<slot>.json`. Si salvano DTO semplici (`SaveData`: id profilo, statistiche, livello, esperienza, inventario come id → quantità, libretto come codice esame → voto, esami rimanenti), non gli oggetti del dominio: il `SaveMapper` (in `engine.repository`, perché usa solo model e interfacce dei cataloghi) ricostruisce il dominio usando i cataloghi. `SaveData` fa parte del contratto di `SaveGameRepository`, quindi sta con l'interfaccia in `engine.repository` e contiene solo tipi del JDK. Così il formato di salvataggio non dipende dalle classi interne e `persistence` contiene solo la scrittura JSON.
- Salvataggio automatico dopo ogni esame concluso; caricamento dal menu.
- Estensione possibile (documentata nella Wiki): `JpaSaveGameRepository` con H2, senza toccare engine e UI.

### 3.8 Interfaccia grafica
Schermate (FXML + controller):
1. **Menu**: nuova partita, carica, esci.
2. **Nuova partita**: nome e profilo.
3. **Sessione**: elenco esami (superati, da sostenere), statistiche, inventario, libretto, pulsante "Sostieni esame".
4. **Esame**: domanda, opzioni o campo di testo, timer, barre di Energia, Concentrazione e resistenza dell'esame, pulsanti oggetti e abilità.
5. **Esito**: voto o bocciatura, ricompense, livello.
6. **Laurea**: riepilogo finale con media ponderata e CFU.

I controller non contengono regole di gioco: traducono eventi in chiamate all'engine e aggiornano la vista. Stile con un unico CSS, grafica semplice e pulita.

---

## 4. Regole di sviluppo (valgono anche per Claude Code)
1. Rispettare questo documento; se serve cambiarlo, proporre la modifica prima di scrivere codice.
2. Codice e nomi in inglese; Javadoc in inglese; Wiki, README e messaggi di commit in italiano.
3. Validazione nei costruttori, fail fast, messaggi chiari: `Objects.requireNonNull` (con messaggio) per i riferimenti nulli, `IllegalArgumentException` per i valori non validi; i controlli ricorrenti stanno in `model.Preconditions`. Nessun `null` restituito: usare `Optional`.
4. Collezioni esposte come copie immutabili (`List.copyOf`, `Collections.unmodifiableMap`).
5. Niente Singleton, niente stato statico mutabile, niente Lombok (il codice deve mostrare che equals, hashCode, getter e costruttori sono stati scritti e capiti).
6. Metodi piccoli; catene di stream lunghe spezzate in metodi privati con nome.
7. Niente codice commentato, niente commenti ovvi.
8. Ogni classe di model ed engine ha test JUnit. I test non dipendono da tempo reale, file reali o casualità.
9. Un branch `feature/<nome>` per ogni issue; commit piccoli e descrittivi; pull request su `develop` con descrizione; merge dopo che Andrea ha letto il codice.
10. A ogni sessione aggiungere un file in `docs/ai-log/` chiamato `AAAA-MM-GG-NN-titolo.md` (`NN` = numero della sessione nel giorno: i nomi si ordinano da soli per data) con cosa è stato chiesto e cosa è stato prodotto, e una riga nell'indice `docs/ai-log/README.md`. Un file per sessione evita i conflitti tra pull request aperte insieme. La parte "verificato da me" la compila Andrea.

## 5. Piano di lavoro (issue)
| # | Issue | Branch |
|---|---|---|
| 1 | Configurazione Gradle completa: JavaFX, Gson, JUnit, test runner | `feature/build-setup` |
| 2 | Model: Grade, Stats, Exam, Subject, Difficulty con test | `feature/model-core` |
| 3 | Model: domande (gerarchia Question) con test | `feature/model-questions` |
| 4 | Model: Item, Inventory, Transcript, Student, StudentProfile con test | `feature/model-student` |
| 5 | Persistence: XML + XSD + XPath per profili, esami e domande; prima materia | `feature/xml-content` |
| 6 | Engine: GradeCalculator e Performance con test | `feature/grading` |
| 7 (7a) | Engine: ExamBattle e strategie d'esame (con timer finto nei test) | `feature/battle` |
| 22 (7b) | Engine: effetti degli oggetti e abilità (BattleEffect) | `feature/item-effects` |
| 23 (7c) | Engine: timer concorrente (ScheduledQuestionTimer) | `feature/question-timer` |
| 8 | Engine: Game, progressione, ricompense | `feature/game` |
| 9 | Persistence: salvataggi JSON con Gson, SaveMapper, test | `feature/save-json` |
| 29 | Persistence: annotation `@QuestionElement` per i parser delle domande (lezione 03) | `feature/question-element` |
| 10 | UI: menu, nuova partita, sessione | `feature/ui-session` |
| 11 | UI: schermata esame, timer reale, esito, laurea | `feature/ui-battle` |
| 12 | Contenuti: le cinque materie complete, bilanciamento | `feature/content` |
| 13 | Revisione SOLID e Clean Code, refactoring | `refactor/review` |
| 14 | README, Wiki, dichiarazione AI | `docs/wiki` |

## 6. Wiki prevista
Home (descrizione, indice, avvio rapido) · Funzionalità · Architettura e responsabilità · Classi e interfacce · Principi SOLID (tutti e cinque, con esempi dal codice) · Clean Code · Strutture dati, equals e Stream · Modelli di esecuzione (timer, race condition evitata) · Persistenza (XML + JSON) · Interfaccia grafica · Testing · Estendibilità (guide passo passo: nuova materia senza codice, nuovo tipo di domanda, nuovo tipo di esame, nuovo oggetto, nuovo tipo di salvataggio) · Strumenti e metodologie (Gradle, Git, Issues, workflow Scrum leggero) · Uso dell'AI (per strumento, con esempi e intervento personale).
