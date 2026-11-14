# Strumenti e metodologie

Come è costruito il progetto (Gradle), come è stata gestita la storia del codice (Git e GitHub) e
con quale flusso di lavoro (uno Scrum leggero, per un solo sviluppatore). L'uso degli strumenti di
AI ha una pagina a parte, [Uso dell'AI](Uso-dell-AI).

## Gradle

### Wrapper e toolchain
Il progetto include il **Gradle Wrapper** (`gradlew`, `gradlew.bat`, `gradle/wrapper/`): chi clona il
repository non installa Gradle, e tutti usano la stessa versione (9.8.2, fissata in
`gradle-wrapper.properties`).

Anche Java è fissato, con una **toolchain**:

```kotlin
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}
```

Se Java 25 non c'è, lo scarica Gradle grazie al plugin `foojay-resolver-convention` dichiarato in
`settings.gradle.kts`. Per avviare il wrapper basta un JDK 17 o superiore.

### Kotlin DSL
La build è scritta in Kotlin (`build.gradle.kts`, `settings.gradle.kts`) invece che in Groovy: il
file è tipizzato e l'editor segnala gli errori mentre si scrive. Il file è breve:

| Blocco | Contenuto |
|---|---|
| `plugins` | `application` (task `run`, classe principale) e `org.openjfx.javafxplugin` 0.1.0 |
| `javafx` | JavaFX 25, moduli `javafx.controls` e `javafx.fxml` |
| `dependencies` | Gson 2.13.2; JUnit 6 tramite il BOM `junit-bom` 6.1.3 |
| `application` | `mainClass = "it.unicam.cs.mpgc.rpg127295.app.Main"` |
| `tasks.test` | `useJUnitPlatform()` e la stampa di ogni test con il suo esito |

Il **BOM** di JUnit fissa in un solo punto la versione di tutti i moduli di JUnit, così
`junit-jupiter` e `junit-platform-launcher` non vanno allineati a mano. XML, XSD e XPath non hanno
dipendenze: sono API del JDK.

### Task usati

| Comando | Cosa fa |
|---|---|
| `./gradlew build` | compila, esegue i test, crea il JAR |
| `./gradlew clean build` | lo stesso, partendo da zero: è il controllo richiesto prima di ogni PR |
| `./gradlew test` | solo i test (report in `build/reports/tests/test/index.html`) |
| `./gradlew run` | avvia il gioco |
| `./gradlew installDist` | crea una distribuzione avviabile; è servito a verificare che i contenuti e gli XSD si leggano anche dal JAR |

Un avviso di deprecazione di Gradle compare solo con `./gradlew run`: è stato rintracciato nel plugin
JavaFX (`JavaFXPlugin.java:95`) e lasciato com'è, perché non dipende dal progetto.

## Git

### Branch
| Branch | Uso |
|---|---|
| `main` | solo le release, con il tag (`v0.1`) |
| `develop` | integrazione: ogni PR entra qui |
| `feature/<nome>` | una issue di codice, ad esempio `feature/battle` (#7), `feature/save-json` (#9) |
| `fix/`, `refactor/`, `docs/`, `content/` | correzioni, refactoring, documentazione e contenuti: `fix/prog-questions`, `refactor/catalog-lookup`, `docs/readme`, `content/exams` |

Il nome del branch è scritto nel piano del design (§5) accanto a ogni issue.

### Commit
Commit piccoli, ognuno con un prefisso minuscolo e un testo in italiano (convenzione in
`CLAUDE.md`): `build:`, `test:`, `feat:`, `fix:`, `refactor:`, `docs:`. In TDD il commit `test:` con
i test rossi precede il commit `feat:` che li fa passare (vedi [Testing](Testing)):

```
fa51a5c test: Exam (validazione, identità sul codice, uso come chiave)
004068f feat: Exam con identità sul codice ed ExamType senza comportamento
```

### Pull request
- **Una PR per issue**, da `feature/<nome>` verso `develop`, con una descrizione: cosa è stato
  fatto e, dal 12/11, una sezione finale **"Guida alla revisione"** con le decisioni principali, i
  file da leggere per primi e cosa provare a mano.
- **Merge solo dopo la lettura di Andrea.** Il merge lo fa lui, dopo aver letto il codice.
- **Merge commit, non squash.** Nella storia restano i singoli commit (e quindi le coppie
  `test:` → `feat:`) e il commit "Merge pull request #N" che segna dove la PR è entrata.
- **PR in catena.** Quando una issue dipende da una PR non ancora accettata, il nuovo branch parte
  da quello della PR aperta: la PR #36 (#11) aveva come base `feature/ui-session` della PR #35
  (#10), ed è arrivata su `develop` con la PR #38.
- **Branch cancellati dopo il merge.** Nel repository GitHub è attiva la cancellazione automatica
  del branch remoto al merge della PR.

### Conflitti
Lavorando su più PR in parallelo, i conflitti nascono quasi sempre nelle tabelle indice
(`docs/ai-log/README.md`, `docs/wiki/Home.md`), dove due branch aggiungono una riga nello stesso
punto. Si risolvono facendo il merge di `develop` nel branch della PR e **tenendo entrambe le
righe**. Ad esempio il commit `eca262b` ("Merge di develop in feature/ui-session: risolti gli indici
di registro AI e Wiki (righe di #29 e #10)") tiene sia la riga della #29 sia quella della #10.

La regola è nata da un errore. Nella risoluzione del conflitto della PR #21 una voce del
registro è andata persa ed è stata recuperata dalla storia di Git; nei merge delle PR #21, #24 e #25
alcune voci si erano mescolate e sono state ricostruite dalle versioni intatte nei commit
precedenti. Per questo il registro è stato diviso in **un file per sessione**: due PR aggiungono due
file diversi, e il conflitto resta solo sulla riga dell'indice.

### Release e tag
Quando `develop` contiene un insieme di funzionalità completo, una PR da `develop` verso `main`
(PR #37, "Release v0.1") lo porta sul branch principale, e il commit di merge riceve un **tag
annotato**, `v0.1`, con il messaggio "Release v0.1: model, contenuti XML, engine (scontro, effetti,
timer, partita), salvataggi JSON, GUI JavaFX".

La PR di release chiude anche le issue (`Closes #2`, ...): GitHub chiude una issue solo quando la
modifica arriva sul branch principale, non quando entra in `develop`. Le release successive (v0.2)
seguiranno lo stesso percorso.

## GitHub Issues come backlog

Le 14 issue iniziali sono state create dal piano di lavoro del design (§5), ognuna con titolo,
descrizione e branch previsto. Le **etichette** dicono l'area:

| Etichetta | Area |
|---|---|
| `model`, `engine`, `persistence`, `ui` | i package del codice |
| `build` | configurazione Gradle e dipendenze |
| `content` | materie, domande, bilanciamento |
| `refactor` | revisione e refactoring |
| `docs` | README, Wiki, documentazione |

Il backlog è cambiato durante il lavoro, come deve:
- la #7 era troppo grande ed è stata divisa in tre: scontro e strategie (7a), effetti di oggetti e
  abilità (#22, 7b), timer concorrente (#23, 7c);
- la #29 (annotation per i parser delle domande) è stata aggiunta dopo, per coprire le annotation
  personalizzate della lezione 03.

Il design viene aggiornato prima del codice quando una issue cambia una scelta (regola 1): ad
esempio la divisione della #7 è entrata nella tabella del §5.

## Uno Scrum leggero

Il progetto ha un solo sviluppatore, quindi di Scrum sono state prese le idee utili, non le
cerimonie pensate per un gruppo:

| Idea di Scrum | Nel progetto |
|---|---|
| product backlog | le issue di GitHub, in ordine di dipendenza nel piano del design |
| backlog refinement | issue divise (#7) o aggiunte (#29) quando serviva |
| incremento | ogni PR lascia `develop` funzionante: `./gradlew clean build` verde |
| definition of done | test verdi, design aggiornato se la scelta cambia, pagina della Wiki aggiornata nella PR, una voce nel registro AI |
| revisione | Andrea legge ogni PR, con la "Guida alla revisione", prima del merge |
| release | `develop` → `main` con un tag (`v0.1`) |

La Wiki si aggiorna **nella PR della issue**, con un commit `docs:` prima di quello del registro:
così documentazione e codice entrano in `develop` insieme e non restano indietro.
