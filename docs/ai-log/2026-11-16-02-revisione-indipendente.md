# 2026-11-16: Revisione indipendente del progetto

**Strumento:** Claude Code (Claude Fable 5.2), in sola lettura

**Richiesta:**
Rivedere tutto il progetto come farebbero i professori, senza modificare file: correttezza
(casi limite, concorrenza del timer, chiusura dell'app, salvataggi), SOLID, Clean Code e regole
del design §4, corrispondenza tra Wiki e codice, coerenza tra README, registro AI e pagina
"Uso dell'AI", riproducibilità da un clone pulito. Rapporto in tre livelli (critico,
importante, minore) e le dieci domande più probabili a un colloquio.

**Prodotto dall'AI:**
- `./gradlew clean build` verde su `develop` (720 test) partendo da un JDK 21: la toolchain
  fornisce Java 25, quindi i requisiti del README bastano.
- Nessun problema critico nel codice; nessun import vietato tra i package, nessun ciclo
  nell'engine.
- Problemi trovati: Wiki non ancora pubblicata e pagina "Uso dell'AI" ancora in una PR aperta;
  un Caffè usato nell'istante in cui il tempo scade veniva consumato senza effetto; un'eccezione
  non gestita se la cartella dei salvataggi non si può leggere; la guida "nuovo tipo d'esame"
  della Wiki citava una mappa e un metodo che non esistono; alcuni punti del design e della Wiki
  rimasti indietro rispetto al codice; una serie di osservazioni minori (codice morto,
  validazioni mancanti, scelte da motivare).
- Le dieci domande d'esame più probabili, con il punto del codice a cui si riferiscono.

**Modificato / verificato da me:**
- Ho chiesto io questa revisione, in sola lettura, per avere un controllo indipendente prima
  della consegna.
- Ho letto il rapporto e l'ho discusso con Claude in chat per decidere cosa
  correggere nel codice, cosa correggere nella documentazione e quali scelte lasciare
  motivandole nella Wiki.
- Ho corretto un punto del rapporto: proponeva di riscrivere la riga della Wiki sul merge delle
  PR dopo la mia lettura, che invece descrive come ho lavorato e resta com'è.
- Da questa discussione è nato il prompt della sessione di correzione
  ([2026-11-17](2026-11-17-01-correzioni-dalla-revisione-indipendente.md)).
