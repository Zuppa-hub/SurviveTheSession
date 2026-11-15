# 2026-11-15 — README finale e Wiki (issue #14)

**Strumento:** Claude Code (Claude Opus 5.5)

**Richiesta:**
Preparare la #14 in un branch separato, mentre la #13 aspetta la revisione, senza la pagina
"Uso dell'AI": README finale, Home della Wiki con tutte le pagine collegate e istruzioni per
copiare `docs/wiki` nella Wiki di GitHub.

**Prodotto dall'AI:**
- README: cos'è, requisiti, compilazione ed esecuzione, come si gioca in breve con gli
  screenshot, struttura, documentazione con i link alla Wiki di GitHub, sezione breve sull'AI
  con il link alla pagina "Uso dell'AI". La sezione sull'AI non dice più che ogni proposta
  veniva approvata prima del codice, che dal 12/11 non era più vero.
- Home della Wiki: tutte le tredici pagine collegate, senza la colonna dello stato, e i link a
  design, registro AI e README su `main`.
- Persistenza: la parte sui contenuti XML, prima riassunta, ora completa (file e schemi,
  `xs:key`/`xs:keyref`, XPath, cataloghi con `CatalogIndex`, errori, test). Classi e
  interfacce: elenco aggiornato delle classi della UI.
- `docs/wiki/README.md` con la procedura di copia nella Wiki di GitHub, provata su una
  cartella di prova: `rsync` che esclude il README e `.git`, poi `sed` che sostituisce
  `../images/` con l'indirizzo delle immagini su `main`. La Wiki di GitHub è abilitata ma non
  ha ancora pagine, quindi va creata la prima dal sito.

**Modificato / verificato da me:**
- Ho deciso che la pagina "Uso dell'AI" la scriviamo io e Claude in chat, fuori da questa PR.
- Rivedo la PR in chat con Claude prima del merge.
