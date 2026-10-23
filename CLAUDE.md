# CLAUDE.md

Istruzioni per l'assistente AI che lavora su questo repository.

## Contesto
Progetto d'esame di Metodologie di Programmazione (UNICAM): gioco di ruolo in Java 25,
build con Gradle (Kotlin DSL). Package base: `it.unicam.cs.mpgc.rpg127295`.

- Build: `./gradlew build`
- Esecuzione: `./gradlew run`

## Regole

1. **SOLID e Clean Code.** Ogni classe ha una sola responsabilità; dipendere da
   astrazioni, non da implementazioni concrete; metodi brevi, nomi espressivi,
   niente codice duplicato o morto.
2. **Nomi in inglese** per tutto il codice (classi, metodi, variabili, package).
   Testi mostrati al giocatore e documentazione possono essere in italiano.
3. **Package coerenti con la responsabilità.** Ogni classe va nel package giusto:
   - `model` – entità e stato del gioco, nessun I/O
   - `engine` – regole e flusso della partita
   - `persistence` – salvataggio/caricamento
   - `ui` – interazione con il giocatore
   - `app` – entry point e assemblaggio dei componenti
4. **Il motore non dipende dalla UI.** `engine` e `model` non devono mai importare
   nulla da `ui`. La comunicazione verso la UI avviene tramite interfacce definite
   lato engine e implementate in `ui`.
5. **Spiegare prima di procedere.** Prima di passare al passo successivo, spiegare
   sempre allo studente cosa è stato fatto e perché, in modo che il codice sia
   compreso e non solo generato.
6. **Aggiornare `docs/ai-log.md` ad ogni sessione** con: data, cosa è stato chiesto,
   cosa è stato prodotto, e uno spazio per ciò che lo studente ha modificato o verificato.
