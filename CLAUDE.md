# CLAUDE.md

Istruzioni per l'assistente AI che lavora su questo repository.

## A inizio sessione
**Leggere sempre `docs/design.md`**: è il documento di design ed è la fonte di verità.
Ogni sessione lo rispetta. Se una scelta va cambiata, si propone e si aggiorna prima
il documento, poi il codice.

## Contesto
Progetto d'esame di Metodologie di Programmazione (UNICAM): gioco di ruolo
"Sopravvivi alla Sessione" in Java 25, build con Gradle (Kotlin DSL), interfaccia JavaFX.
Package base: `it.unicam.cs.mpgc.rpg127295`.

- Build: `./gradlew build`
- Test: `./gradlew test`
- Esecuzione: `./gradlew run`

## Regole di sviluppo (da `docs/design.md`, sezione 4)
1. Rispettare il documento di design; se serve cambiarlo, proporre la modifica prima
   di scrivere codice.
2. Codice e nomi in inglese; Javadoc in inglese; Wiki, README e messaggi di commit
   in italiano.
3. Validazione nei costruttori, fail fast, messaggi chiari: `Objects.requireNonNull`
   (con messaggio) per i riferimenti nulli, `IllegalArgumentException` per i valori non
   validi; i controlli ricorrenti stanno in `model.Preconditions`. Nessun `null`
   restituito: usare `Optional`.
4. Collezioni esposte come copie immutabili (`List.copyOf`, `Collections.unmodifiableMap`).
5. Niente Singleton, niente stato statico mutabile, niente Lombok (il codice deve
   mostrare che equals, hashCode, getter e costruttori sono stati scritti e capiti).
6. Metodi piccoli; catene di stream lunghe spezzate in metodi privati con nome.
7. Niente codice commentato, niente commenti ovvi.
8. Ogni classe di model ed engine ha test JUnit. I test non dipendono da tempo reale,
   file reali o casualità.
9. Un branch `feature/<nome>` per ogni issue; commit piccoli e descrittivi; pull request
   su `develop` con descrizione; merge dopo che Andrea ha letto il codice.
10. A ogni sessione aggiornare `docs/ai-log.md` (data, cosa è stato chiesto, cosa è
    stato prodotto). La parte "verificato da me" la compila Andrea.

## Convenzione dei commit
Prefisso minuscolo seguito da testo in italiano:
- `build:` configurazione Gradle e dipendenze
- `test:` test (in TDD: commit dei test che falliscono, prima dell'implementazione)
- `feat:` nuova funzionalità o implementazione che fa passare i test
- `fix:` correzione di un errore, anche nei contenuti (domande, profili, esami)
- `refactor:` modifica della struttura senza cambiare il comportamento
- `docs:` documentazione (design, README, Wiki, registro AI)

Esempio: `feat: Grade con validazione 18-31 e lode`

## Architettura (sintesi, dettagli in `docs/design.md` sezione 3)
- `model` – entità e stato del gioco; non importa `engine`, `persistence` né `ui`.
- `engine` – regole e flusso della partita; dipende solo da `model` e da interfacce
  (mai JavaFX, Gson o file). Notifica la UI tramite listener definiti nell'engine.
- `persistence` – contenuti XML (sola lettura) e salvataggi JSON.
- `ui` – controller JavaFX sottili, nessuna regola di gioco.
- `app` – composition root: unico punto in cui si creano e collegano le
  implementazioni concrete.

## Modo di lavorare
- **Wiki aggiornata a ogni issue chiusa**: quando si chiude una issue si aggiorna la pagina
  della Wiki corrispondente in `docs/wiki/` (indice in `docs/wiki/Home.md`).
- **Spiegare prima di procedere**: prima di passare al passo successivo, spiegare allo
  studente cosa è stato fatto e perché, in modo che il codice sia compreso e non solo
  generato.
