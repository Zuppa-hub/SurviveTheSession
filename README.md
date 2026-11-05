# 📌 Sopravvivi alla Sessione

Gioco di ruolo in Java sviluppato come progetto d'esame di **Metodologie di
Programmazione** (UNICAM). Lo studente deve superare una sessione d'esami: ogni esame è
uno scontro a turni in cui si "combatte" rispondendo a domande vere sulla materia, con
statistiche, oggetti e abilità che influenzano lo scontro. Gli esami superati entrano nel
libretto con voto e CFU; superata tutta la sessione, lo studente si laurea.

---

## 🚀 Come eseguire il progetto

### Prerequisiti
- Java 25
- Gradle **non** va installato: il progetto include il Gradle Wrapper (`./gradlew`)

> Se Java 25 non è installato, Gradle lo scarica automaticamente grazie alla toolchain
> configurata in `build.gradle.kts`. Per avviare il wrapper serve comunque un JDK 17 o
> superiore.

### Istruzioni

```bash
git clone https://github.com/Zuppa-hub/SurviveTheSession.git
cd SurviveTheSession
```

### Build del progetto
```bash
./gradlew build
```

### Esecuzione
```bash
./gradlew run
```

### Test
```bash
./gradlew test
```

---

## 🗂️ Struttura del progetto

Package base: `it.unicam.cs.mpgc.rpg127295`

| Package       | Responsabilità                                                              |
|---------------|-----------------------------------------------------------------------------|
| `model`       | Entità e stato del gioco; non dipende da nessun altro package               |
| `engine`      | Regole e flusso della partita; definisce le interfacce che usa (es. cataloghi) |
| `persistence` | Implementazioni: lettura dei contenuti XML e salvataggi JSON                |
| `ui`          | Interfaccia JavaFX: input e output verso il giocatore                       |
| `app`         | Punto di ingresso (`Main`) e assemblaggio dei componenti                    |

Il documento di progettazione è [`docs/design.md`](docs/design.md); la documentazione
estesa è nella Wiki ([`docs/wiki/Home.md`](docs/wiki/Home.md)).

---

## 🤖 Uso di strumenti di AI

Il progetto è sviluppato con il supporto di **Claude Code** (Anthropic), usato per
proporre soluzioni, scrivere codice e test e redigere documentazione. La progettazione e
le decisioni sono mie: ogni proposta viene discussa e approvata prima di scrivere codice,
e ogni pull request viene letta e verificata da me prima del merge.

Il dettaglio è nella pagina "Uso dell'AI" della Wiki e, sessione per sessione, in
[`docs/ai-log/`](docs/ai-log/README.md).
