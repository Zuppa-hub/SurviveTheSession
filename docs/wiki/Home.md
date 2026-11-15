# Sopravvivi alla Sessione

Gioco di ruolo in Java 25 sviluppato come progetto d'esame di **Metodologie di
Programmazione** (UNICAM, A.A. 2025/26). Autore: Andrea Cazzato, matricola 127295.

Lo studente deve superare una **sessione d'esami**. Ogni esame è uno scontro a turni in cui
il "combattimento" consiste nel rispondere a domande vere sulla materia: le risposte
corrette riducono la resistenza dell'esame, quelle sbagliate tolgono energia allo studente.
Statistiche, oggetti e abilità influenzano lo scontro. Gli esami superati entrano nel
**libretto** con voto e CFU; quando la sessione è finita lo studente si laurea e vede il
riepilogo con media ponderata e CFU.

Il gioco è il contesto, non il fine: il progetto vuole mostrare come è stato progettato e
sviluppato, con SOLID, Clean Code e i concetti visti a lezione.

## Avvio rapido

```bash
git clone https://github.com/Zuppa-hub/SurviveTheSession.git
cd SurviveTheSession
./gradlew run      # avvia il gioco
./gradlew test     # esegue i test
```

Serve Java 25; Gradle non va installato perché il progetto include il wrapper.

## Indice

| Pagina | Contenuto |
|---|---|
| [Funzionalità](Funzionalita) | cosa si può fare nel gioco, dal punto di vista del giocatore |
| [Architettura e responsabilità](Architettura-e-responsabilita) | package, dipendenze, composition root |
| [Classi e interfacce](Classi-e-interfacce) | le classi principali e le scelte motivate |
| [Principi SOLID](Principi-SOLID) | tutti e cinque, con esempi dal codice |
| [Clean Code](Clean-Code) | nomi, metodi piccoli, niente duplicazione, revisione finale |
| [Strutture dati, equals e Stream](Strutture-dati-equals-e-Stream) | List, HashSet, HashMap, identità, Stream API |
| [Modelli di esecuzione](Modelli-di-esecuzione) | GUI event-driven, timer su thread separato, race condition evitata |
| [Persistenza](Persistenza) | contenuti XML con XSD e XPath, annotation dei parser, salvataggi JSON |
| [Interfaccia grafica](Interfaccia-grafica) | JavaFX, FXML, CSS, Come si gioca, controller sottili |
| [Testing](Testing) | JUnit, TDD, test di contratto, test deterministici |
| [Estendibilità](Estendibilita) | nuova materia, domanda, esame, oggetto, salvataggio: guide passo passo |
| [Strumenti e metodologie](Strumenti-e-metodologie) | Gradle, Git, Issues, workflow Scrum leggero |
| [Uso dell'AI](Uso-dell-AI) | strumenti usati, con esempi e intervento personale |

## Documenti nel repository
- [Documento di design](https://github.com/Zuppa-hub/SurviveTheSession/blob/main/docs/design.md): la fonte
  di verità delle scelte, da cui sono nate queste pagine.
- [Registro dell'uso dell'AI](https://github.com/Zuppa-hub/SurviveTheSession/blob/main/docs/ai-log/README.md):
  una voce per ogni sessione di lavoro con Claude Code.
- [README](https://github.com/Zuppa-hub/SurviveTheSession/blob/main/README.md): requisiti, compilazione,
  esecuzione.
