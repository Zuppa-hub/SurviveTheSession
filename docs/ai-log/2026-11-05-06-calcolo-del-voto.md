# 2026-11-05 — Calcolo del voto (issue #6) e divisione della #7

**Strumento:** Claude Code (Claude Opus 5.5)

**Richiesta:**
Proporre `Performance` e `PerformanceGradeCalculator` e aspettare l'ok; rendere esplicito
il contratto di `GradeCalculator` in Javadoc e verificarlo con un test di contratto
astratto; dividere la #7 in tre issue (scontro e strategie, oggetti e abilità, timer).

**Prodotto dall'AI:**
- Issue #7 aggiornata (7a) e create #22 (7b) e #23 (7c); tabella di §5 del design aggiornata.
- `Performance` (risposte corrette, sbagliate, indizi usati; `answered()`, `isFlawless()`).
- `GradeCalculator` (`@FunctionalInterface`) con il contratto in Javadoc: voto valido o
  `Optional.empty()`, mai null, nessuna corretta → insufficiente, perfetta → sufficiente,
  monotonia su risposte corrette e su indizi (regola aggiunta su richiesta).
- `PerformanceGradeCalculator`: soglia 60%, da 18 a 30 in modo lineare arrotondando per
  difetto, −1 per indizio senza scendere sotto 18, lode solo se perfetta; solo interi.
- `GradeCalculatorContractTest` astratto su una griglia di 484 prestazioni, esteso da
  `PerformanceGradeCalculatorTest`. Verificato che fallisca rendendo la penalità un bonus.
  351 test verdi.

**Modificato / verificato da me:**
- [ ] _da completare_
