# Uso dell'AI

Il progetto è stato sviluppato con due strumenti di AI di Anthropic, con ruoli diversi. Questa
pagina spiega chi ha fatto cosa, gli errori trovati e cosa ho imparato. Il dettaglio sessione
per sessione è nel [registro](https://github.com/Zuppa-hub/SurviveTheSession/blob/main/docs/ai-log/README.md)
in `docs/ai-log/`.

## Strumenti e ruoli

| Chi | Cosa ha fatto |
|---|---|
| **Claude in chat** | Analisi delle slide e appunti delle lezioni; idee per il gioco; stesura del documento di design; piano e calendario; revisione delle proposte e delle pull request; revisione delle domande; scrittura dei prompt per Claude Code. |
| **Claude Code** | Implementazione di codice e test (in TDD), contenuti XML, Wiki, registro AI, screenshot; lavoro in parallelo con sub-agent e git worktree. |
| **Io** | Studio delle slide e delle videolezioni registrate; direzione del progetto: decisioni, test a mano, revisione delle PR con Claude in chat, merge e rilasci. |

In pratica il lavoro è stato fatto due volte: Claude Code produceva, Claude in chat rivedeva, io
decidevo. Il mio compito principale è stato dare la direzione e supervisionare.

## Come abbiamo lavorato
- **Prima parte: progettazione e proposte approvate prima del codice.** Il design (tema,
  regole, architettura, regole di sviluppo) è nato in chat; per ogni issue Claude Code
  proponeva una soluzione, la discutevo con Claude in chat e approvavo o chiedevo modifiche.
- **Seconda parte: implementazione diretta.** Con design e regole ormai stabili, e per
  rispettare i tempi di consegna, Claude Code ha deciso e implementato le soluzioni nel
  rispetto del design, scrivendo in ogni PR una "Guida alla revisione". Ogni PR è stata
  rivista da me con Claude in chat.
- Il registro AI ha un file per sessione. Le sezioni "Modificato / verificato da me" e i testi
  di questa dichiarazione sono stati redatti con l'AI a partire dalle mie decisioni, poi letti
  e approvati da me.

## Cosa ho deciso io
Le scelte di architettura le abbiamo discusse insieme: spesso la proposta veniva dall'AI e io
sceglievo. Alcune decisioni sono state mie:
- il tema "Sopravvivi alla Sessione", scelto tra più idee proposte;
- l'approccio: SOLID e Clean Code come criteri, test prima del codice, poche funzionalità curate;
- tenere cinque esami quando, per accorciare i tempi, ne erano stati proposti tre;
- togliere JPA, che era facoltativo, per avere tempo per la revisione;
- tenere sempre un passaggio di revisione, anche quando si doveva andare veloci;
- dopo i miei test a mano: kit iniziale di oggetti, difficoltà visibile, Energia ricaricata a
  fine esame, messaggi solo in italiano;
- la schermata "Come si gioca" e la coerenza grafica di tutte le schermate.

## Errori dell'AI trovati e corretti
| Dove | Errore | Come è stato trovato |
|---|---|---|
| Progettazione | `Item` dipendeva dall'engine; la regola di laurea stava nel libretto; le interfacce dei repository stavano in `persistence`; un ciclo tra i package dello scontro e delle strategie; uno `switch` sul tipo d'esame | revisione delle proposte con Claude in chat |
| Domande | un indizio rivelava la risposta; un distrattore era sbagliato; una domanda aveva la difficoltà sbagliata | revisione delle domande |
| Gioco | messaggio d'errore in inglese; Energia che restava a 1/9 tra un esame e l'altro; domande con codice tagliate fuori dallo schermo; zaino sempre vuoto con voti bassi; difficoltà delle domande non visibile | i miei test a mano sulla v0.1 |
| Bilanciamento | con i valori iniziali un buon giocatore poteva essere bocciato e uno scarso poteva passare | test di simulazione chiesto nella #12 |
| Processo | una riga del registro persa risolvendo un conflitto | recuperata dalla storia di Git; da allora un file per sessione |

Anche Claude in chat ha sbagliato: in un caso ha dato per mergiata una PR che non lo era, e
l'ho corretto controllando su GitHub.

## Cosa ho verificato io
- `./gradlew build` verde su ogni PR prima del merge e su un clone pulito del repository;
- partite complete giocate a mano sulle versioni rilasciate, con bocciatura, ritiro, oggetti,
  boss, salvataggio, file di salvataggio corrotto e laurea;
- lettura delle PR e delle pagine della Wiki con Claude in chat;
- revisione delle domande di ogni materia.

## Cosa ho imparato
- A usare meglio gli strumenti di AI: dividere il lavoro tra chi produce e chi rivede, scrivere
  prompt precisi, chiedere sempre il motivo di una scelta e non solo il codice.
- I principi del corso applicati a un progetto vero: SOLID, Clean Code, TDD, contratti tra
  interfacce e implementazioni, scelta delle strutture dati, concorrenza, XML e persistenza.
- Che con l'AI si può produrre codice di qualità molto più in fretta, ma solo se qualcuno
  controlla: quasi tutti gli errori della tabella sopra sarebbero arrivati alla consegna senza
  revisione.

## Cosa cambierei
- **Progettare la grafica prima.** Le schermate sono state uniformate alla fine; delle bozze
  prima di scrivere la GUI avrebbero evitato un passaggio.
- **Provare il gioco a mano dopo ogni issue**, non solo ai rilasci: i problemi della v0.1 si
  potevano vedere prima.
