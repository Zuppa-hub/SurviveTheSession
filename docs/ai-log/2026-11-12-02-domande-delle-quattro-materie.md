# 2026-11-12 — Domande delle quattro materie mancanti

**Strumento:** Claude Code (Claude Opus 5.5), in un git worktree separato
(`../surviveTheSession-content`, branch `content/exams`) per non interferire con l'altra
sessione nella cartella principale.

**Richiesta:**
Scrivere in italiano 15 domande originali per ciascuno dei quattro esami mancanti (ASD
scritto, BD orale, RETI orale, MDP progetto e boss finale, ultimo in `exams.xml`), mescolando
i quattro tipi come in `prog.xml`; aggiungere materie ed esami; verificare con Java 25 ogni
snippet di "output del codice"; nessuna risposta ambigua, nessun distrattore corretto,
indizi che non regalino la risposta; PR con l'elenco completo delle domande, senza merge.

**Prodotto dall'AI:**
- `questions/asd.xml`, `bd.xml`, `reti.xml`, `mdp.xml`: 15 domande ciascuno, tutti e quattro
  i tipi e tutte e tre le difficoltà (servono all'orale che incalza).
- `exams.xml`: cinque esami in ordine, MDP1 (progetto) ultimo come boss finale.
- Ogni snippet (i 13 nuovi e i 4 di Programmazione) estratto dagli XML ed eseguito con Java
  25: l'output coincide con la risposta corretta. Tolto un distrattore ambiguo
  (`git checkout nome`, che può creare il branch da quello remoto).
- `ShippedContentTest`: sessione di cinque esami con il progetto come boss, tutti i tipi e
  tutte le difficoltà in ogni esame, regola degli Appunti su tutte le domande a quattro
  opzioni. 589 test verdi; `./gradlew run` carica 5 esami.

**Modificato / verificato da me:**
- Revisionate le 60 domande dei quattro nuovi esami insieme a Claude in chat; corretti un
  distrattore ambiguo (MDP-02), una domanda fuori programma (MDP-13) e un indizio che rivelava
  la risposta (RETI-15).
