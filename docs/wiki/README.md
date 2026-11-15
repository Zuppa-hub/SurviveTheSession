# Sorgenti della Wiki

Questa cartella contiene le pagine della Wiki del progetto. Stanno nel repository per essere
scritte, riviste e versionate insieme al codice, nella PR di ogni issue; la Wiki di GitHub ne è
una copia. Questo file non è una pagina della Wiki e non va copiato.

## Nomi e collegamenti
- Ogni file `Nome-della-pagina.md` diventa la pagina "Nome della pagina"; `Home.md` è la pagina
  iniziale.
- I collegamenti tra pagine usano il nome senza estensione, come vuole la Wiki di GitHub:
  `[Testing](Testing)`. Per questo, nella vista del repository, non portano alla pagina.
- Le immagini sono in `docs/images` e le pagine le collegano con `../images/<file>.png`; nella
  Wiki il percorso va sostituito con l'indirizzo del file su `main` (passo 3).

## Copiare le pagine nella Wiki di GitHub
1. **Solo la prima volta:** su GitHub, nella scheda *Wiki* del repository, creare una pagina
   qualsiasi (ad esempio "Home") e salvarla. Finché la Wiki non ha una pagina, il suo
   repository `SurviveTheSession.wiki.git` non esiste e non si può clonare.
2. Clonare la Wiki accanto al progetto:
   ```bash
   git clone https://github.com/Zuppa-hub/SurviveTheSession.wiki.git ../SurviveTheSession.wiki
   ```
3. Dalla cartella del progetto, aggiornato su `main`, copiare le pagine (tranne questo file) e
   correggere il percorso delle immagini:
   ```bash
   rsync -a --delete --exclude README.md --exclude .git docs/wiki/ ../SurviveTheSession.wiki/
   sed -i.bak 's#](\.\./images/#](https://raw.githubusercontent.com/Zuppa-hub/SurviveTheSession/main/docs/images/#g' \
       ../SurviveTheSession.wiki/*.md && rm ../SurviveTheSession.wiki/*.bak
   ```
   `--delete` toglie dalla Wiki le pagine che non esistono più in `docs/wiki`.
4. Controllare e pubblicare:
   ```bash
   cd ../SurviveTheSession.wiki
   git status
   git add -A
   git commit -m "docs: Wiki allineata a docs/wiki"
   git push
   ```

Le pagine si modificano sempre qui, mai direttamente sulla Wiki: alla copia successiva le
modifiche fatte su GitHub andrebbero perse.
