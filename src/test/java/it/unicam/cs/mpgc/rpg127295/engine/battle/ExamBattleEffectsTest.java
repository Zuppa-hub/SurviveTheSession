package it.unicam.cs.mpgc.rpg127295.engine.battle;

import static it.unicam.cs.mpgc.rpg127295.engine.TestQuestions.CORRECT;
import static it.unicam.cs.mpgc.rpg127295.engine.TestQuestions.WRONG;
import static it.unicam.cs.mpgc.rpg127295.engine.TestQuestions.choiceWithHint;
import static it.unicam.cs.mpgc.rpg127295.engine.TestQuestions.fillIn;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unicam.cs.mpgc.rpg127295.engine.FakeRandom;
import it.unicam.cs.mpgc.rpg127295.engine.effect.DoubleDamageEffect;
import it.unicam.cs.mpgc.rpg127295.engine.effect.EnergyRestoreEffect;
import it.unicam.cs.mpgc.rpg127295.engine.effect.ExtraTimeEffect;
import it.unicam.cs.mpgc.rpg127295.engine.effect.FiftyFiftyEffect;
import it.unicam.cs.mpgc.rpg127295.engine.effect.HintEffect;
import it.unicam.cs.mpgc.rpg127295.engine.grading.GradeCalculator;
import it.unicam.cs.mpgc.rpg127295.engine.timer.ManualQuestionTimer;
import it.unicam.cs.mpgc.rpg127295.model.exam.ExamType;
import it.unicam.cs.mpgc.rpg127295.model.exam.Grade;
import it.unicam.cs.mpgc.rpg127295.model.item.Item;
import it.unicam.cs.mpgc.rpg127295.model.question.Difficulty;
import it.unicam.cs.mpgc.rpg127295.model.question.Question;
import it.unicam.cs.mpgc.rpg127295.model.student.Stats;
import it.unicam.cs.mpgc.rpg127295.model.student.Student;
import it.unicam.cs.mpgc.rpg127295.model.student.StudentProfile;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ExamBattleEffectsTest {

    private static final GradeCalculator TWENTY_FIVE = performance -> Optional.of(Grade.of(25));

    private static final Item COFFEE = new Item("coffee", "Caffè", "Più tempo");
    private static final Item NOTES = new Item("notes", "Appunti", "50/50");
    private static final Item SUMMARY = new Item("summary", "Riassunto", "Indizio");
    private static final Item ENERGY_DRINK = new Item("energy-drink", "Energy drink", "Energia");
    private static final Item USELESS = new Item("pen", "Penna", "Nessun effetto");
    private static final Ability QUICK_REVIEW = new Ability("Ripasso lampo", 2, new HintEffect());
    private static final Ability STROKE_OF_GENIUS = new Ability("Colpo di genio", 3, new DoubleDamageEffect());

    private static final BattleEffects EFFECTS = new BattleEffects(
            Map.of(COFFEE, new ExtraTimeEffect(), NOTES, new FiftyFiftyEffect(), SUMMARY, new HintEffect(),
                    ENERGY_DRINK, new EnergyRestoreEffect()),
            List.of(QUICK_REVIEW, STROKE_OF_GENIUS));

    private final ManualQuestionTimer timer = new ManualQuestionTimer();
    private final RecordingBattleListener listener = new RecordingBattleListener();
    private final Student student = new Student("Andrea",
            new StudentProfile("nerd", "Secchione", "Studia sempre", Stats.initial(5, 6, 4)));
    private List<Question> questions = List.of(choiceWithHint("Q-1"), choiceWithHint("Q-2"), choiceWithHint("Q-3"));

    private ExamBattle begun(ExamType type) {
        BattleServices services = new BattleServices(FixedBattleRules.standard(), TWENTY_FIVE, timer,
                FakeRandom.first(), EFFECTS);
        ExamBattle battle = new ExamBattleFactory(ExamBattleTest.strategies(), subject -> questions, services)
                .create(student, ExamBattleTest.exam(type));
        battle.addListener(listener);
        battle.begin();
        return battle;
    }

    private PresentedQuestion current(ExamBattle battle) {
        return battle.state().currentQuestion().orElseThrow();
    }

    private void give(Item item, int quantity) {
        student.inventory().add(item, quantity);
    }

    @Test
    void coffeeAddsTimeToATimedQuestionAndIsConsumed() {
        give(COFFEE, 1);
        ExamBattle battle = begun(ExamType.WRITTEN);
        timer.advance(10);

        battle.useItem(COFFEE);

        assertAll(
                () -> assertEquals(35, timer.secondsLeft()),
                () -> assertFalse(student.inventory().contains(COFFEE)),
                () -> assertEquals(1, listener.effects.size()));
    }

    @Test
    void coffeeCannotBeUsedWithoutATimer() {
        give(COFFEE, 1);
        ExamBattle battle = begun(ExamType.ORAL);

        assertAll(
                () -> assertFalse(battle.canUse(COFFEE)),
                () -> assertThrows(IllegalStateException.class, () -> battle.useItem(COFFEE)),
                () -> assertTrue(student.inventory().contains(COFFEE)),
                () -> assertTrue(listener.effects.isEmpty()));
    }

    @Test
    void notesRemoveTwoWrongOptionsOncePerQuestion() {
        give(NOTES, 2);
        ExamBattle battle = begun(ExamType.ORAL);

        battle.useItem(NOTES);

        PresentedQuestion halved = current(battle);
        assertAll(
                () -> assertEquals(2, halved.options().size()),
                () -> assertTrue(halved.options().contains(CORRECT)),
                () -> assertFalse(battle.canUse(NOTES)),
                () -> assertEquals(1, student.inventory().quantityOf(NOTES)));
    }

    @Test
    void notesCannotBeUsedOnAFillInQuestion() {
        give(NOTES, 1);
        questions = List.of(fillIn("Q-1", Difficulty.EASY));

        assertFalse(begun(ExamType.ORAL).canUse(NOTES));
    }

    @Test
    void theHintCountsOnceEvenFromTwoSources() {
        give(SUMMARY, 1);
        ExamBattle battle = begun(ExamType.ORAL);

        battle.useItem(SUMMARY);

        assertAll(
                () -> assertTrue(current(battle).hintVisible()),
                () -> assertEquals(1, battle.state().performance().hintsUsed()),
                () -> assertFalse(battle.canUse(QUICK_REVIEW)),
                () -> assertThrows(IllegalStateException.class, () -> battle.useAbility(QUICK_REVIEW)),
                () -> assertEquals(4, student.stats().concentration()),
                () -> assertEquals(1, battle.state().performance().hintsUsed()));
    }

    @Test
    void theHintIsHiddenAgainWithTheNextQuestion() {
        give(SUMMARY, 1);
        ExamBattle battle = begun(ExamType.ORAL);
        battle.useItem(SUMMARY);

        battle.answer(WRONG);

        assertAll(
                () -> assertFalse(current(battle).hintVisible()),
                () -> assertTrue(battle.canUse(QUICK_REVIEW)));
    }

    @Test
    void quickReviewShowsTheHintAndSpendsConcentration() {
        ExamBattle battle = begun(ExamType.ORAL);

        battle.useAbility(QUICK_REVIEW);

        assertAll(
                () -> assertTrue(current(battle).hintVisible()),
                () -> assertEquals(2, student.stats().concentration()),
                () -> assertEquals(1, battle.state().performance().hintsUsed()));
    }

    @Test
    void anAbilityNeedsEnoughConcentration() {
        student.updateStats(stats -> stats.spendConcentration(2));
        ExamBattle battle = begun(ExamType.ORAL);

        assertAll(
                () -> assertFalse(battle.canUse(STROKE_OF_GENIUS)),
                () -> assertThrows(IllegalStateException.class, () -> battle.useAbility(STROKE_OF_GENIUS)),
                () -> assertEquals(2, student.stats().concentration()));
    }

    @Test
    void theEnergyDrinkRestoresEnergyUpToTheMaximum() {
        give(ENERGY_DRINK, 2);
        ExamBattle battle = begun(ExamType.ORAL);
        boolean usableAtFullEnergy = battle.canUse(ENERGY_DRINK);
        battle.answer(WRONG);

        battle.useItem(ENERGY_DRINK);

        assertAll(
                () -> assertFalse(usableAtFullEnergy),
                () -> assertEquals(6, student.stats().energy()),
                () -> assertEquals(1, student.inventory().quantityOf(ENERGY_DRINK)));
    }

    @Test
    void strokeOfGeniusDoublesTheNextCorrectAnswerEvenAfterAMistake() {
        ExamBattle battle = begun(ExamType.ORAL);

        battle.useAbility(STROKE_OF_GENIUS);
        boolean doubledBeforeAnswering = battle.state().nextCorrectAnswerDoubled();
        battle.answer(WRONG);
        boolean doubledAfterAMistake = battle.state().nextCorrectAnswerDoubled();
        battle.answer(CORRECT);

        assertAll(
                () -> assertTrue(doubledBeforeAnswering),
                () -> assertTrue(doubledAfterAMistake),
                () -> assertEquals(0, battle.state().resistance()),
                () -> assertFalse(battle.state().nextCorrectAnswerDoubled()),
                () -> assertEquals(2, student.stats().concentration()));
    }

    @Test
    void strokeOfGeniusDoesNotStack() {
        ExamBattle battle = begun(ExamType.ORAL);
        battle.useAbility(STROKE_OF_GENIUS);
        student.updateStats(stats -> stats.recoverConcentration(3));

        assertThrows(IllegalStateException.class, () -> battle.useAbility(STROKE_OF_GENIUS));
    }

    @Test
    void anItemMustBeOwned() {
        ExamBattle battle = begun(ExamType.WRITTEN);

        assertAll(
                () -> assertFalse(battle.canUse(COFFEE)),
                () -> assertThrows(IllegalStateException.class, () -> battle.useItem(COFFEE)));
    }

    @Test
    void itemsWithoutEffectAndUnknownAbilitiesAreRejected() {
        give(USELESS, 1);
        Ability unknown = new Ability("Telepatia", 1, new HintEffect());
        ExamBattle battle = begun(ExamType.ORAL);

        assertAll(
                () -> assertFalse(battle.canUse(USELESS)),
                () -> assertThrows(IllegalArgumentException.class, () -> battle.useItem(USELESS)),
                () -> assertFalse(battle.canUse(unknown)),
                () -> assertThrows(IllegalArgumentException.class, () -> battle.useAbility(unknown)),
                () -> assertTrue(student.inventory().contains(USELESS)));
    }

    @Test
    void nothingCanBeUsedWithoutAQuestionWaitingForAnAnswer() {
        give(ENERGY_DRINK, 1);
        student.updateStats(stats -> stats.loseEnergy(1));
        ExamBattle battle = begun(ExamType.ORAL);
        battle.retire();

        assertAll(
                () -> assertFalse(battle.canUse(ENERGY_DRINK)),
                () -> assertThrows(IllegalStateException.class, () -> battle.useItem(ENERGY_DRINK)),
                () -> assertThrows(IllegalStateException.class, () -> battle.useAbility(QUICK_REVIEW)));
    }

    @Test
    void listenersReceiveTheStateAfterTheEffect() {
        ExamBattle battle = begun(ExamType.ORAL);

        battle.useAbility(STROKE_OF_GENIUS);

        assertEquals(List.of(battle.state()), listener.effects);
    }

    @Test
    void exposesTheAbilitiesOfTheBattle() {
        assertEquals(List.of(QUICK_REVIEW, STROKE_OF_GENIUS), begun(ExamType.ORAL).abilities());
    }

    @Test
    void rejectsNullArguments() {
        ExamBattle battle = begun(ExamType.ORAL);

        assertAll(
                () -> assertThrows(NullPointerException.class, () -> battle.useItem(null)),
                () -> assertThrows(NullPointerException.class, () -> battle.useAbility(null)),
                () -> assertThrows(NullPointerException.class, () -> battle.canUse((Item) null)),
                () -> assertThrows(NullPointerException.class, () -> battle.canUse((Ability) null)));
    }
}
