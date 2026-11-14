package it.unicam.cs.mpgc.rpg127295.ui;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.unicam.cs.mpgc.rpg127295.engine.battle.StandardBattleRules;
import it.unicam.cs.mpgc.rpg127295.engine.game.Reward;
import it.unicam.cs.mpgc.rpg127295.engine.grading.GradeCalculator;
import it.unicam.cs.mpgc.rpg127295.model.exam.ExamType;
import it.unicam.cs.mpgc.rpg127295.model.exam.Grade;
import it.unicam.cs.mpgc.rpg127295.model.question.Difficulty;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import org.junit.jupiter.api.Test;

/** The guide asks the rules, so the numbers it shows cannot differ from those of the game. */
class GameGuideTest {

    @Test
    void thePassingShareComesFromTheGradeCalculator() {
        GradeCalculator half = performance -> performance.correctAnswers() * 2 >= performance.answered()
                ? Optional.of(Grade.of(18)) : Optional.empty();

        assertAll(
                () -> assertEquals(60, TestGames.guide().passingPercentage()),
                () -> assertEquals(50, TestGames.guide(new StandardBattleRules(), half).passingPercentage()));
    }

    @Test
    void theLowestGradeWithAnItemComesFromTheRewardPolicy() {
        assertEquals(Optional.of(Grade.of(24)), TestGames.guide().lowestGradeWithAnItem());
    }

    @Test
    void aPolicyWithoutItemsHasNoSuchGrade() {
        GameGuide guide = TestGames.guide();
        GameGuide noItems = new GameGuide(guide.session(), guide.profiles(), guide.starterKit(),
                guide.battleRules(), guide.strategies(), guide.abilities(), guide.items(), guide.progression(),
                (exam, grade) -> Reward.none(), guide.grades());

        assertEquals(Optional.empty(), noItems.lowestGradeWithAnItem());
    }

    @Test
    void theEnergyLostCombinesDifficultyAndExamType() {
        GameGuide guide = TestGames.guide();

        assertAll(
                () -> assertEquals(1, guide.energyLoss(Difficulty.EASY, ExamType.WRITTEN)),
                () -> assertEquals(2, guide.energyLoss(Difficulty.HARD, ExamType.ORAL)),
                () -> assertEquals(4, guide.energyLoss(Difficulty.HARD, ExamType.PROJECT)));
    }

    @Test
    void theTimerComesFromTheStrategy() {
        GameGuide guide = TestGames.guide();

        assertAll(
                () -> assertEquals(OptionalInt.of(30), guide.secondsPerQuestion(ExamType.WRITTEN)),
                () -> assertEquals(OptionalInt.empty(), guide.secondsPerQuestion(ExamType.ORAL)));
    }

    @Test
    void needsAStrategyForEveryTypeOfExam() {
        GameGuide guide = TestGames.guide();

        assertThrows(IllegalArgumentException.class, () -> new GameGuide(guide.session(), guide.profiles(),
                guide.starterKit(), guide.battleRules(), Map.of(), guide.abilities(), guide.items(),
                guide.progression(), guide.rewards(), guide.grades()));
    }

    @Test
    void rejectsMissingParts() {
        GameGuide guide = TestGames.guide();

        assertThrows(NullPointerException.class, () -> new GameGuide(null, guide.profiles(), guide.starterKit(),
                guide.battleRules(), guide.strategies(), guide.abilities(), guide.items(), guide.progression(),
                guide.rewards(), guide.grades()));
    }
}
