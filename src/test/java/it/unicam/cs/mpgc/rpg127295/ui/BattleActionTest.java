package it.unicam.cs.mpgc.rpg127295.ui;

import static it.unicam.cs.mpgc.rpg127295.ui.TestGames.COFFEE;
import static it.unicam.cs.mpgc.rpg127295.ui.TestGames.NOTES;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.unicam.cs.mpgc.rpg127295.engine.TestQuestions;
import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleListener;
import it.unicam.cs.mpgc.rpg127295.engine.battle.ExamBattle;
import it.unicam.cs.mpgc.rpg127295.engine.game.Game;
import it.unicam.cs.mpgc.rpg127295.model.exam.Exam;
import it.unicam.cs.mpgc.rpg127295.model.exam.ExamType;
import it.unicam.cs.mpgc.rpg127295.model.item.Inventory;
import it.unicam.cs.mpgc.rpg127295.model.question.Difficulty;
import java.util.List;
import org.junit.jupiter.api.Test;

class BattleActionTest {

    private static final Exam WRITTEN = new Exam("PROG1", TestQuestions.SUBJECT, ExamType.WRITTEN, 6, Difficulty.EASY);
    private static final Exam ORAL = new Exam("BD1", TestQuestions.SUBJECT, ExamType.ORAL, 6, Difficulty.EASY);

    private static Game gameWithItems(Exam exam) {
        Game game = TestGames.game(exam);
        game.student().inventory().add(COFFEE, 2);
        game.student().inventory().add(NOTES, 1);
        return game;
    }

    private static ExamBattle start(Game game, Exam exam) {
        return game.startExam(exam, new BattleListener() {
        });
    }

    @Test
    void listsTheOwnedItemsByNameThenTheAbilities() {
        Game game = gameWithItems(WRITTEN);
        List<BattleAction> actions = BattleAction.of(start(game, WRITTEN), game.student().inventory());

        assertEquals(List.of("Appunti × 1", "Caffè × 2", "Ripasso lampo · 2 Concentrazione",
                        "Colpo di genio · 3 Concentrazione"),
                actions.stream().map(BattleAction::label).toList());
    }

    @Test
    void describesItemsWithTheirDescriptionAndAbilitiesWithTheirCost() {
        Game game = gameWithItems(WRITTEN);
        List<BattleAction> actions = BattleAction.of(start(game, WRITTEN), game.student().inventory());

        assertAll(
                () -> assertEquals("Elimina due opzioni sbagliate", actions.get(0).description()),
                () -> assertEquals("Costa 2 di Concentrazione", actions.get(2).description()));
    }

    @Test
    void anActionIsEnabledOnlyIfTheBattleAllowsIt() {
        Game game = gameWithItems(WRITTEN);
        List<BattleAction> actions = BattleAction.of(start(game, WRITTEN), game.student().inventory());

        assertEquals(List.of(true, true, false, true), actions.stream().map(BattleAction::enabled).toList());
    }

    @Test
    void extraTimeIsDisabledInAnExamWithoutTimer() {
        Game game = gameWithItems(ORAL);
        List<BattleAction> actions = BattleAction.of(start(game, ORAL), game.student().inventory());

        assertFalse(actions.get(1).enabled());
    }

    @Test
    void usingAnItemConsumesOneUnit() {
        Game game = gameWithItems(WRITTEN);
        Inventory inventory = game.student().inventory();

        BattleAction.of(start(game, WRITTEN), inventory).get(1).use().run();

        assertEquals(1, inventory.quantityOf(COFFEE));
    }

    @Test
    void rejectsMissingArguments() {
        Game game = gameWithItems(WRITTEN);
        ExamBattle battle = start(game, WRITTEN);

        assertAll(
                () -> assertThrows(NullPointerException.class, () -> BattleAction.of(null, new Inventory())),
                () -> assertThrows(NullPointerException.class, () -> BattleAction.of(battle, null)));
    }
}
