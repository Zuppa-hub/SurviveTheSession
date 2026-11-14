# 2026-11-14 — Bilanciamento (issue #12)

**Strumento:** Claude Code (Claude Opus 5.5)

**Richiesta:**
Rivedere i valori provvisori (resistenze, danni, energia, ricompense, soglie di livello) con un
test deterministico che simula un giocatore: con il 70% di risposte giuste ogni profilo deve
poter superare ogni esame, con il 40% deve venire bocciato.

**Prodotto dall'AI:**
- `BalanceTest` (in TDD, prima rosso): contenuti, regole, ricompense e progressione veri
  tramite `GameAssembly`, domande sostituite da domande con risposta nota (stessi id, numero e
  difficoltà), timer manuale e seme fisso. Al 70% il giocatore sbaglia per primo, al 40%
  indovina per primo. Controlla ogni esame come primo della sessione, la sessione intera fino
  alla laurea e la bocciatura al 40% in ogni esame, boss compreso.
- Con i vecchi valori il test falliva per due motivi opposti: esami lunghi che esaurivano
  l'Energia (Secchione in Programmazione, tutti in ASD) ed esami corti superati al 40% (Reti
  con 2 risposte giuste su 3).
- Valori cercati prima con una simulazione delle stesse regole su migliaia di combinazioni,
  scegliendo quella che cambia meno e mantiene diversi i profili: resistenza 4/4/5 per CFU,
  danno 7 + Conoscenza/2, Energia persa 1/2/2, livello a 50 × livello con +3 Energia massima,
  Secchione con Energia 7. Ricompense invariate.
- Controllo a mano che il test trovi gli errori: tre valori riportati per prova a quelli
  vecchi o vicini fanno fallire il test.
- Test delle regole aggiornati ai nuovi valori; design §2.2 e §2.8, Wiki Classi e interfacce
  (sezione sul bilanciamento, con il limite del margine al 40%). 676 test verdi.

**Modificato / verificato da me:**
- Ho fissato il criterio del bilanciamento (70% supera ogni esame con ogni profilo, 40%
  bocciato) e chiesto che fosse un test deterministico.
- Ho letto la PR prima del merge e ho giocato una sessione con i nuovi valori.
