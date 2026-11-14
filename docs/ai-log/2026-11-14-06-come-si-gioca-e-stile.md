# 2026-11-14 — Come si gioca e coerenza grafica (issue #45)

**Strumento:** Claude Code (Claude Opus 5.5)

**Richiesta:**
Una schermata "Come si gioca" (la prima volta prima della scelta del profilo, poi da menu e
sessione) senza numeri delle regole scritti a mano, e uno stile coerente in tutte le schermate
e nei dialoghi, controllato con screenshot prima e dopo.

**Prodotto dall'AI:**
- Issue #45.
- `Grade.all()` (TDD): tutti i voti in ordine, per interrogare le regole senza riscrivere
  l'intervallo 18-31.
- `GameGuide` (TDD): record con le stesse istanze di regole e contenuti usate dalle partite,
  fornito da `GameService.guide()` e costruito da `GameAssembly`. Ricava la percentuale per
  superare un esame interrogando il `GradeCalculator` e il primo voto con oggetti interrogando
  la `RewardPolicy`.
- `HowToPlay` (TDD): sette pagine. Il test usa regole con valori insoliti (97 secondi, ×59) e
  controlla che compaiano nel testo, quindi un numero scritto a mano farebbe fallire il test.
  La descrizione del Caffè in `items.xml` non ripete più i 15 secondi.
- `InventoryRow` ed `EmptyStates` (TDD): righe dello zaino con la descrizione per il tooltip e
  testi degli stati vuoti (zaino, partite salvate), con il voto letto dalle regole.
- `HowToPlayController` e `how-to-play.fxml`; `ViewNavigator.showHowToPlay(azione, etichetta)`
  e la prima apertura automatica quando non ci sono partite salvate.
- `styles.css` riscritto: colori come variabili, spaziature in classi di layout (tolte dagli
  FXML), varianti `primary`/`secondary`/`link`/`option`, liste senza strisce, dialoghi con lo
  stile dell'app (`Alerts.dialog`), tooltip. Tutti gli FXML con la stessa struttura: colonna
  centrata per menu, nuova partita, come si gioca, esito e laurea; intestazione a sinistra per
  sessione ed esame.
- Screenshot prima e dopo di ogni schermata e dei dialoghi, generati da un programma fuori dal
  repository. Dal controllo è emerso un difetto, corretto: le righe dispari degli esami
  superati non erano verdi (lo stile `:odd` prevaleva sul colore). La misura della schermata
  d'esame su tutte le 78 domande resta senza scroll (margine minimo 11 px).
- Design §3.8 e Wiki Interfaccia grafica.

**Modificato / verificato da me:**
- Ho chiesto che il testo di Come si gioca non contenesse numeri scritti a mano, per non farlo
  divergere dal bilanciamento.
- Ho indicato gli elementi grafici da uniformare (pulsanti, dialoghi, stati vuoti, tooltip,
  righe a strisce) e chiesto gli screenshot prima e dopo.
- Ho chiesto a Claude di fare il merge con la build verde; la revisione della PR la faccio in
  chat.
