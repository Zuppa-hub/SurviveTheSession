package it.unicam.cs.mpgc.rpg127295.ui;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleRules;
import it.unicam.cs.mpgc.rpg127295.engine.grading.PerformanceGradeCalculator;
import it.unicam.cs.mpgc.rpg127295.model.exam.Exam;
import it.unicam.cs.mpgc.rpg127295.model.question.Difficulty;
import it.unicam.cs.mpgc.rpg127295.model.student.Stats;
import java.util.List;
import org.junit.jupiter.api.Test;

class HowToPlayTest {

    /** Unusual values, so that the test sees whether the text reads them from the rules. */
    private static final BattleRules ODD_RULES = new BattleRules() {
        @Override
        public int resistanceOf(Exam exam) {
            return 10;
        }

        @Override
        public int damageOf(Stats stats) {
            return 41;
        }

        @Override
        public int baseEnergyLoss(Difficulty difficulty) {
            return 7 + difficulty.ordinal();
        }

        @Override
        public int concentrationPerCorrectAnswer() {
            return 43;
        }

        @Override
        public int extraSeconds() {
            return 97;
        }

        @Override
        public int energyRestored() {
            return 53;
        }

        @Override
        public int boostedDamageMultiplier() {
            return 59;
        }
    };

    private static String allText(List<HowToPlay.Page> pages) {
        return String.join("\n", pages.stream().map(page -> page.title() + "\n" + page.text()).toList());
    }

    @Test
    void explainsTheGameInOrder() {
        assertEquals(List.of("Obiettivo", "Le statistiche", "I tipi d'esame", "Oggetti e abilità",
                        "Il voto", "Ricompense e livelli", "Boss finale, laurea e salvataggi"),
                HowToPlay.pages(TestGames.guide()).stream().map(HowToPlay.Page::title).toList());
    }

    @Test
    void theNumbersOfTheBattleComeFromTheRules() {
        String text = allText(HowToPlay.pages(TestGames.guide(ODD_RULES, new PerformanceGradeCalculator())));

        assertAll(
                () -> assertTrue(text.contains("41 punti"), "damage"),
                () -> assertTrue(text.contains("+43"), "concentration per correct answer"),
                () -> assertTrue(text.contains("97 secondi"), "extra time"),
                () -> assertTrue(text.contains("53 punti di Energia"), "energy restored"),
                () -> assertTrue(text.contains("×59"), "boosted damage"),
                () -> assertTrue(text.contains("Facile 7, Media 8, Difficile 9"), "energy lost"));
    }

    @Test
    void theOtherNumbersComeFromTheirRules() {
        String text = allText(HowToPlay.pages(TestGames.guide()));

        assertAll(
                () -> assertTrue(text.contains("tutti i 2 esami"), "exams of the session"),
                () -> assertTrue(text.contains("30 secondi"), "timer of the written exam"),
                () -> assertTrue(text.contains("×2"), "mistakes in a project"),
                () -> assertTrue(text.contains("almeno il 60%"), "passing share"),
                () -> assertTrue(text.contains("da 24 in su"), "first grade with an item"),
                () -> assertTrue(text.contains("50 punti di esperienza"), "first level"),
                () -> assertTrue(text.contains("+3 Energia massima"), "level bonus"),
                () -> assertTrue(text.contains("Ripasso lampo (2 Concentrazione)"), "ability cost"));
    }

    @Test
    void namesTheContentOfTheGame() {
        String text = allText(HowToPlay.pages(TestGames.guide()));

        assertAll(
                () -> assertTrue(text.contains("Parti con: Appunti, Caffè."), "starter kit"),
                () -> assertTrue(text.contains("Secchione con Conoscenza 8"), "damage example"),
                () -> assertTrue(text.contains("L'ultimo esame, " + TestGames.PROJECT.subject().name()), "boss"),
                () -> assertTrue(text.contains("Caffè: " + TestGames.COFFEE.description()), "item"));
    }
}
