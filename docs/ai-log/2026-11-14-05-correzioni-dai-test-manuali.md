# 2026-11-14 — Correzioni dai test manuali della v0.1 (issue #43)

**Strumento:** Claude Code (Claude Opus 5.5)

**Richiesta:**
Correggere i problemi trovati giocando la v0.1: kit iniziale di oggetti, difficoltà della
domanda nella schermata d'esame, Energia che sembrava non ricaricata dopo un esame, "Tempo
scaduto" che sembrava riferito alla domanda successiva, dialoghi con il messaggio inglese
dell'eccezione, quarta opzione fuori schermo nelle domande con codice.

**Prodotto dall'AI:**
- Issue #43 con l'elenco dei punti.
- **Kit iniziale** (TDD): `StarterKit` nell'engine (oggetti e quantità, `giveTo(inventory)`);
  gli oggetti (1 Caffè, 1 Appunti, 1 Energy drink) li sceglie `GameAssembly` per id, come
  `GameEffects` per gli effetti, e un id mancante ferma l'avvio. Il kit si dà solo a una
  partita nuova: il formato dei salvataggi non cambia e una partita caricata non riceve un
  secondo kit (test dedicato).
- **Energia** (TDD): ricaricata alla fine di ogni esame, dopo i livelli e prima di avvisare i
  listener, quindi prima del salvataggio automatico e della schermata della sessione. Scelta
  invece di "spiegare" il comportamento, perché un 1/9 mostrato per un'Energia che tornerà
  piena sembra un errore anche se spiegato. La ricarica all'inizio resta per i salvataggi della
  v0.1.
- **Difficoltà e messaggio** (TDD): `Labels` raccoglie le etichette italiane di tipo d'esame e
  difficoltà (sostituisce `ExamRow.typeLabelOf`); il messaggio dopo una risposta inizia con
  "Domanda precedente:" e sta in alto nella scheda, accanto alla difficoltà.
- **Dialoghi** (TDD): `ErrorMessages` con i testi italiani di caricamento, salvataggio
  mancante, salvataggio fallito e contenuti non validi; `Alerts` scrive l'eccezione nel log
  (`System.Logger`) e non mostra più `getMessage()`.
- **Layout**: snippet di codice a sinistra e opzioni a destra, spaziature un po' ridotte.
  Un programma fuori dal repository ha aperto la schermata d'esame per tutte le 78 domande a
  960 × 640 e misurato se il contenuto supera lo spazio: prima 8 domande con codice uscivano
  dalla finestra (fino a 202 px), dopo nessuna (margine minimo 8 px).
- Design §2.2, §2.3, §2.6, §3.8 e Wiki (Funzionalità, Classi e interfacce, Interfaccia
  grafica).

**Modificato / verificato da me:**
- Ho fatto io i test manuali sulla v0.1 e ho scritto l'elenco dei problemi da correggere.
- Ho chiesto che il kit rispettasse DIP e non rompesse i salvataggi esistenti, e ho lasciato a
  Claude la scelta tra ricaricare l'Energia a fine esame o spiegare il comportamento.
- Ho chiesto a Claude di fare il merge con la build verde; la revisione della PR la faccio in
  chat.
