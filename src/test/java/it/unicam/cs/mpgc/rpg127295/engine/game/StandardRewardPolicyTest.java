package it.unicam.cs.mpgc.rpg127295.engine.game;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.unicam.cs.mpgc.rpg127295.engine.FakeRandom;
import it.unicam.cs.mpgc.rpg127295.engine.TestQuestions;
import it.unicam.cs.mpgc.rpg127295.engine.repository.ItemCatalog;
import it.unicam.cs.mpgc.rpg127295.model.exam.Exam;
import it.unicam.cs.mpgc.rpg127295.model.exam.ExamType;
import it.unicam.cs.mpgc.rpg127295.model.exam.Grade;
import it.unicam.cs.mpgc.rpg127295.model.item.Item;
import it.unicam.cs.mpgc.rpg127295.model.question.Difficulty;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class StandardRewardPolicyTest {

    static final Item COFFEE = new Item("coffee", "Caffè", "Più tempo");
    static final Item NOTES = new Item("notes", "Appunti", "50/50");
    static final ItemCatalog CATALOG = TestCatalogs.items(COFFEE, NOTES);

    private static final Exam TWELVE_CREDITS =
            new Exam("PROG1", TestQuestions.SUBJECT, ExamType.WRITTEN, 12, Difficulty.MEDIUM);

    private final RewardPolicy policy = new StandardRewardPolicy(CATALOG, FakeRandom.first());

    @ParameterizedTest(name = "{0} -> {1} experience")
    @CsvSource({
        "18,  36",
        "25, 120",
        "30, 180",
        "31, 180"
    })
    void experienceIsCreditsTimesGradeMinusFifteen(int grade, int expected) {
        assertEquals(expected, policy.rewardFor(TWELVE_CREDITS, Grade.of(grade)).experience());
    }

    @ParameterizedTest(name = "{0} -> {1} items")
    @CsvSource({
        "18, 0",
        "23, 0",
        "24, 1",
        "27, 1",
        "28, 2",
        "30, 2",
        "31, 3"
    })
    void betterGradesGiveMoreItems(int grade, int expected) {
        int items = policy.rewardFor(TWELVE_CREDITS, Grade.of(grade)).items().values().stream()
                .mapToInt(Integer::intValue)
                .sum();

        assertEquals(expected, items);
    }

    @Test
    void itemsAreChosenWithTheInjectedGenerator() {
        RewardPolicy lastItem = new StandardRewardPolicy(CATALOG, FakeRandom.last());

        assertAll(
                () -> assertEquals(Map.of(COFFEE, 2), policy.rewardFor(TWELVE_CREDITS, Grade.of(28)).items()),
                () -> assertEquals(Map.of(NOTES, 2), lastItem.rewardFor(TWELVE_CREDITS, Grade.of(28)).items()));
    }

    @Test
    void anEmptyCatalogGivesNoItems() {
        RewardPolicy noItems = new StandardRewardPolicy(TestCatalogs.items(), FakeRandom.first());

        assertEquals(Map.of(), noItems.rewardFor(TWELVE_CREDITS, Grade.of(31)).items());
    }

    @Test
    void rejectsNullArguments() {
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> new StandardRewardPolicy(null, FakeRandom.first())),
                () -> assertThrows(NullPointerException.class, () -> new StandardRewardPolicy(CATALOG, null)),
                () -> assertThrows(NullPointerException.class, () -> policy.rewardFor(null, Grade.of(18))),
                () -> assertThrows(NullPointerException.class, () -> policy.rewardFor(TWELVE_CREDITS, null)));
    }
}
