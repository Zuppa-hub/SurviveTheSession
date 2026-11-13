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

Le pagine seguono il piano del documento di design (`docs/design.md`, §6). Ognuna viene
scritta o aggiornata quando si chiude la issue corrispondente.

| Pagina | Contenuto | Stato |
|---|---|---|
| [Funzionalità](Funzionalita) | cosa si può fare nel gioco | prevista |
| [Architettura e responsabilità](Architettura-e-responsabilita) | package, dipendenze, composition root | prevista |
| [Classi e interfacce](Classi-e-interfacce) | le classi principali e le scelte motivate | **bozza (model, voto, scontro, effetti, partita)** |
| [Principi SOLID](Principi-SOLID) | tutti e cinque, con esempi dal codice | prevista |
| [Clean Code](Clean-Code) | nomi, metodi piccoli, niente duplicazione | prevista |
| [Strutture dati, equals e Stream](Strutture-dati-equals-e-Stream) | List, HashSet, HashMap, identità, Stream API | prevista |
| [Modelli di esecuzione](Modelli-di-esecuzione) | GUI event-driven, timer su thread separato, race condition evitata | **bozza (timer)** |
| [Persistenza](Persistenza) | contenuti XML con XSD e XPath, salvataggi JSON | **bozza (salvataggi, annotation dei parser)** |
| [Interfaccia grafica](Interfaccia-grafica) | JavaFX, FXML, CSS, controller sottili | **bozza (menu, nuova partita, sessione)** |
| [Testing](Testing) | JUnit, test di contratto, test deterministici | prevista |
| [Estendibilità](Estendibilita) | nuova materia, domanda, esame, oggetto, salvataggio: guide passo passo | prevista |
| [Strumenti e metodologie](Strumenti-e-metodologie) | Gradle, Git, Issues, workflow Scrum leggero | prevista |
| [Uso dell'AI](Uso-dell-AI) | per strumento, con esempi e intervento personale | prevista |
