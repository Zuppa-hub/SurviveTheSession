# 📌 Sopravvivi alla Sessione

*(nome provvisorio)*

Gioco di ruolo testuale in Java sviluppato come progetto d'esame di
**Metodologie di Programmazione** (UNICAM). Il progetto è attualmente nella fase
iniziale: è presente solo lo scheletro dell'applicazione, senza logica di gioco.

---

## 🚀 Come eseguire il progetto

### Prerequisiti
- Java 25 (LTS)
- Gradle (non è necessario installarlo: si usa il Gradle Wrapper incluso, `./gradlew`)

> Se Java 25 non è installato, Gradle lo scarica automaticamente grazie alla
> toolchain configurata in `build.gradle.kts`. Per avviare il wrapper serve comunque
> un JDK 17 o superiore.

### Istruzioni

```bash
git clone <url-del-repository>
cd <nome-cartella>
```

### Build del progetto
```bash
./gradlew build
```

### Esecuzione
```bash
./gradlew run
```

---

## 🗂️ Struttura del progetto

Package base: `it.unicam.cs.mpgc.rpg127295`

| Package       | Responsabilità                                                        |
|---------------|-----------------------------------------------------------------------|
| `model`       | Modello di dominio: entità e stato del gioco                          |
| `engine`      | Motore di gioco: regole e flusso della partita (non dipende dalla UI) |
| `persistence` | Salvataggio e caricamento dei dati                                    |
| `ui`          | Interfaccia utente: input e output verso il giocatore                 |
| `app`         | Punto di ingresso (`Main`) e assemblaggio dei componenti              |

---

## 🤖 Uso di strumenti di AI

> ⚠️ **Sezione da completare.**

<!--
Indicare se e come sono stati utilizzati strumenti di AI, per quali attività e con
quale livello di intervento personale. Il registro dettagliato delle sessioni si
trova in docs/ai-log.md (ed eventualmente nella Wiki del repository).
-->
