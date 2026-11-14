# 2026-11-05 — Effetti di oggetti e abilità (issue #22, 7b)

**Strumento:** Claude Code (Claude Opus 5.5)

**Richiesta:**
Proporre e poi realizzare in TDD la #22 con il minimo necessario: un'unica interfaccia
`BattleEffect` per oggetti e abilità (le abilità come dati), mappa oggetto → effetto in `app`,
`useItem`/`useAbility`, regole di Caffè, Appunti, indizio, Energy drink e Colpo di genio,
notifiche minime. Decisioni di Andrea: `BattleControls` come interfaccia ristretta (spiegata
come incapsulamento, lezione 07); `canUse` e `use` con un unico metodo privato di verifica;
mappa id → effetto in `app` subito, con test di fail fast su `items.xml`.

**Prodotto dall'AI:**
- Design: §2.6 (regole d'uso), §3.2-§3.4 (`BattleEffect`, `BattleControls`, `Ability`,
  `engine.effect` al posto di `engine.item` ed `engine.ability`).
- `PresentedQuestion` con sottoinsieme di opzioni, `withoutWrongOptions`, `withHintShown`;
  `Performance.withHint`; `BattleRules` con secondi extra, energia recuperata e moltiplicatore.
- `BattleEffect`, `BattleControls`, `Ability`, `BattleEffects` e i cinque effetti;
  `BattleState` con `timed` e `nextCorrectAnswerDoubled`.
- `ExamBattle`: `useItem`, `useAbility`, `canUse` (un solo `refusalOf`), classe interna
  `Controls`, notifica `onEffectApplied`; `BattleServices` pubblico con gli effetti, factory
  con tre parametri.
- `app.GameEffects`: id → effetto e abilità; un oggetto senza effetto ferma l'avvio.
- Wiki aggiornata. Verificato che togliendo l'azzeramento del Colpo di genio il test fallisca.
  521 test verdi.

**Modificato / verificato da me:**
- Deciso un unico effetto per Riassunto e Ripasso lampo.
- Richiesto un solo metodo di verifica condiviso (DRY).
- Approvata la mappa oggetto → effetto con fail fast.
