package it.unicam.cs.mpgc.rpg127295.engine.battle;

import static it.unicam.cs.mpgc.rpg127295.engine.TestQuestions.CHOICE_OPTIONS;
import static it.unicam.cs.mpgc.rpg127295.engine.TestQuestions.CORRECT;
import static it.unicam.cs.mpgc.rpg127295.engine.TestQuestions.WRONG;
import static it.unicam.cs.mpgc.rpg127295.engine.TestQuestions.choice;
import static it.unicam.cs.mpgc.rpg127295.engine.TestQuestions.fillIn;
import static it.unicam.cs.mpgc.rpg127295.model.question.Difficulty.EASY;
import static it.unicam.cs.mpgc.rpg127295.model.question.Difficulty.HARD;
import static it.unicam.cs.mpgc.rpg127295.model.question.Difficulty.MEDIUM;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import it.unicam.cs.mpgc.rpg127295.engine.FakeRandom;
import it.unicam.cs.mpgc.rpg127295.engine.TestQuestions;
import it.unicam.cs.mpgc.rpg127295.engine.grading.GradeCalculator;
import it.unicam.cs.mpgc.rpg127295.engine.grading.Performance;
import it.unicam.cs.mpgc.rpg127295.engine.battle.ExamStrategy;
import it.unicam.cs.mpgc.rpg127295.engine.strategy.OralExamStrategy;
import it.unicam.cs.mpgc.rpg127295.engine.strategy.ProjectExamStrategy;
import it.unicam.cs.mpgc.rpg127295.engine.strategy.WrittenExamStrategy;
import it.unicam.cs.mpgc.rpg127295.engine.timer.ManualQuestionTimer;
import it.unicam.cs.mpgc.rpg127295.model.exam.Exam;
import it.unicam.cs.mpgc.rpg127295.model.exam.ExamType;
import it.unicam.cs.mpgc.rpg127295.model.exam.Grade;
import it.unicam.cs.mpgc.rpg127295.model.question.Question;
import it.unicam.cs.mpgc.rpg127295.model.student.Stats;
import it.unicam.cs.mpgc.rpg127295.model.student.Student;
import it.unicam.cs.mpgc.rpg127295.model.student.StudentProfile;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import org.junit.jupiter.api.Test;

class ExamBattleTest {

    private static final GradeCalculator TWENTY_FIVE = performance -> Optional.of(Grade.of(25));
    private static final GradeCalculator INSUFFICIENT = performance -> Optional.empty();
    private static final GradeCalculator NEVER_CALLED = performance -> fail("The grade must not be calculated");

    private final ManualQuestionTimer timer = new ManualQuestionTimer();
    private final RecordingBattleListener listener = new RecordingBattleListener();
    private final Student student = new Student("Andrea",
            new StudentProfile("nerd", "Secchione", "Studia sempre", Stats.initial(5, 6, 4)));
    private List<Question> questions = List.of(choice("Q-1", EASY), choice("Q-2", MEDIUM), fillIn("Q-3", HARD));

    static Exam exam(ExamType type) {
        return new Exam("PROG1", TestQuestions.SUBJECT, type, 12, MEDIUM);
    }

    static Map<ExamType, ExamStrategy> strategies() {
        return Map.of(
                ExamType.WRITTEN, new WrittenExamStrategy(),
                ExamType.ORAL, new OralExamStrategy(),
                ExamType.PROJECT, new ProjectExamStrategy());
    }

    private ExamBattle battle(ExamType type, GradeCalculator calculator) {
        ExamBattleFactory factory = new ExamBattleFactory(strategies(), subject -> questions,
                FixedBattleRules.standard(), calculator, timer, FakeRandom.first());
        ExamBattle battle = factory.create(student, exam(type));
        battle.addListener(listener);
        return battle;
    }

    private ExamBattle begun(ExamType type, GradeCalculator calculator) {
        ExamBattle battle = battle(type, calculator);
        battle.begin();
        return battle;
    }

    private Question presentedQuestion(int index) {
        return listener.presented.get(index).question();
    }

    @Test
    void beforeBeginningTheExamHasFullResistanceAndNoQuestion() {
        BattleState state = battle(ExamType.ORAL, TWENTY_FIVE).state();

        assertAll(
                () -> assertEquals(10, state.resistance()),
                () -> assertEquals(10, state.maxResistance()),
                () -> assertEquals(Optional.empty(), state.currentQuestion()),
                () -> assertEquals(Performance.none(), state.performance()));
    }

    @Test
    void beginPresentsTheFirstQuestionWithShuffledOptions() {
        ExamBattle battle = begun(ExamType.ORAL, TWENTY_FIVE);

        PresentedQuestion first = listener.presented.getFirst();
        assertAll(
                () -> assertEquals(choice("Q-1", EASY), first.question()),
                () -> assertNotEquals(CHOICE_OPTIONS, first.options()),
                () -> assertEquals(Optional.of(first), battle.state().currentQuestion()),
                () -> assertEquals(OptionalInt.empty(), listener.seconds.getFirst()));
    }

    @Test
    void aWrittenExamStartsTheTimerForEachQuestion() {
        begun(ExamType.WRITTEN, TWENTY_FIVE);

        assertAll(
                () -> assertTrue(timer.isRunning()),
                () -> assertEquals(30, timer.secondsLeft()),
                () -> assertEquals(OptionalInt.of(30), listener.seconds.getFirst()));
    }

    @Test
    void oralAndProjectExamsHaveNoTimer() {
        begun(ExamType.ORAL, TWENTY_FIVE);
        boolean runningInOral = timer.isRunning();
        begun(ExamType.PROJECT, TWENTY_FIVE);

        assertAll(
                () -> assertFalse(runningInOral),
                () -> assertFalse(timer.isRunning()));
    }

    @Test
    void aCorrectAnswerLowersTheResistanceAndRecoversConcentration() {
        student.updateStats(stats -> stats.spendConcentration(2));
        ExamBattle battle = begun(ExamType.ORAL, TWENTY_FIVE);

        battle.answer(CORRECT);

        BattleState state = battle.state();
        assertAll(
                () -> assertEquals(5, state.resistance()),
                () -> assertEquals(3, student.stats().concentration()),
                () -> assertEquals(new Performance(1, 0, 0), state.performance()),
                () -> assertEquals(List.of(true), listener.evaluations),
                () -> assertEquals(choice("Q-2", MEDIUM), presentedQuestion(1)));
    }

    @Test
    void aWrongAnswerCostsEnergy() {
        ExamBattle battle = begun(ExamType.ORAL, TWENTY_FIVE);

        battle.answer(WRONG);

        assertAll(
                () -> assertEquals(4, student.stats().energy()),
                () -> assertEquals(10, battle.state().resistance()),
                () -> assertEquals(new Performance(0, 1, 0), battle.state().performance()),
                () -> assertEquals(List.of(false), listener.evaluations));
    }

    @Test
    void inAProjectExamMistakesCostDoubleEnergy() {
        begun(ExamType.PROJECT, TWENTY_FIVE).answer(WRONG);

        assertEquals(2, student.stats().energy());
    }

    @Test
    void theLastAnswerIsInTheState() {
        ExamBattle battle = begun(ExamType.ORAL, TWENTY_FIVE);

        battle.answer(WRONG);

        assertEquals(Optional.of(new AnswerResult(choice("Q-1", EASY), false)), battle.state().lastAnswer());
    }

    @Test
    void theOralExamPressesOnAfterAMistake() {
        questions = List.of(choice("Q-1", EASY), choice("Q-4", EASY), choice("Q-2", MEDIUM));

        begun(ExamType.ORAL, TWENTY_FIVE).answer(WRONG);

        assertEquals(MEDIUM, presentedQuestion(1).difficulty());
    }

    @Test
    void defeatingTheExamEndsItWithTheGradeOfTheCalculator() {
        ExamBattle battle = begun(ExamType.ORAL, TWENTY_FIVE);

        battle.answer(CORRECT);
        battle.answer(CORRECT);

        BattleOutcome expected = BattleOutcome.passed(Grade.of(25), new Performance(2, 0, 0));
        assertAll(
                () -> assertEquals(Optional.of(expected), battle.outcome()),
                () -> assertEquals(List.of(expected), listener.outcomes),
                () -> assertTrue(battle.isOver()),
                () -> assertEquals(Optional.empty(), battle.state().currentQuestion()),
                () -> assertEquals(2, listener.presented.size()));
    }

    @Test
    void defeatingTheExamWithAnInsufficientPerformanceIsAFailure() {
        ExamBattle battle = begun(ExamType.ORAL, INSUFFICIENT);

        battle.answer(CORRECT);
        battle.answer(CORRECT);

        assertEquals(Optional.of(BattleOutcome.failed(new Performance(2, 0, 0))), battle.outcome());
    }

    @Test
    void whenTheQuestionsRunOutTheCalculatorDecides() {
        questions = List.of(choice("Q-1", EASY));
        ExamBattle passed = begun(ExamType.ORAL, TWENTY_FIVE);
        passed.answer(CORRECT);
        ExamBattle failed = begun(ExamType.ORAL, INSUFFICIENT);
        failed.answer(CORRECT);

        assertAll(
                () -> assertEquals(5, passed.state().resistance()),
                () -> assertEquals(Optional.of(BattleOutcome.passed(Grade.of(25), new Performance(1, 0, 0))),
                        passed.outcome()),
                () -> assertEquals(Optional.of(BattleOutcome.failed(new Performance(1, 0, 0))), failed.outcome()));
    }

    @Test
    void runningOutOfEnergyIsAFailureWithoutCalculatingTheGrade() {
        ExamBattle battle = begun(ExamType.ORAL, NEVER_CALLED);

        battle.answer(WRONG);
        battle.answer(WRONG);
        battle.answer(WRONG);

        assertAll(
                () -> assertTrue(student.stats().isExhausted()),
                () -> assertEquals(Optional.of(BattleOutcome.failed(new Performance(0, 3, 0))), battle.outcome()));
    }

    @Test
    void anExhaustedStudentFailsAtOnce() {
        student.updateStats(stats -> stats.loseEnergy(6));

        ExamBattle battle = begun(ExamType.ORAL, NEVER_CALLED);

        assertAll(
                () -> assertEquals(Optional.of(BattleOutcome.failed(Performance.none())), battle.outcome()),
                () -> assertTrue(listener.presented.isEmpty()));
    }

    @Test
    void anExpiredTimeCountsAsAWrongAnswer() {
        ExamBattle battle = begun(ExamType.WRITTEN, TWENTY_FIVE);

        timer.advance(30);

        assertAll(
                () -> assertEquals(30, listener.ticks.size()),
                () -> assertEquals(0, listener.ticks.getLast()),
                () -> assertEquals(List.of(false), listener.evaluations),
                () -> assertEquals(4, student.stats().energy()),
                () -> assertEquals(new Performance(0, 1, 0), battle.state().performance()),
                () -> assertEquals(2, listener.presented.size()),
                () -> assertEquals(30, timer.secondsLeft()));
    }

    @Test
    void eachQuestionHasItsOwnTime() {
        ExamBattle battle = begun(ExamType.WRITTEN, TWENTY_FIVE);
        timer.advance(10);

        battle.answer(CORRECT);

        assertEquals(30, timer.secondsLeft());
    }

    @Test
    void theTimerStopsWhenTheBattleEnds() {
        ExamBattle battle = begun(ExamType.WRITTEN, TWENTY_FIVE);

        battle.answer(CORRECT);
        battle.answer(CORRECT);

        assertFalse(timer.isRunning());
    }

    @Test
    void retiringEndsTheBattleWithoutAGrade() {
        ExamBattle battle = begun(ExamType.WRITTEN, TWENTY_FIVE);
        battle.answer(CORRECT);

        battle.retire();

        assertAll(
                () -> assertEquals(Optional.of(BattleOutcome.retired(new Performance(1, 0, 0))), battle.outcome()),
                () -> assertEquals(1, listener.outcomes.size()),
                () -> assertFalse(timer.isRunning()));
    }

    @Test
    void rejectsActionsOutOfOrder() {
        ExamBattle notBegun = battle(ExamType.ORAL, TWENTY_FIVE);
        ExamBattle retired = begun(ExamType.ORAL, TWENTY_FIVE);
        retired.retire();

        assertAll(
                () -> assertThrows(IllegalStateException.class, () -> notBegun.answer(CORRECT)),
                () -> assertThrows(IllegalStateException.class, notBegun::retire),
                () -> assertThrows(IllegalStateException.class, retired::begin),
                () -> assertThrows(IllegalStateException.class, () -> retired.answer(CORRECT)),
                () -> assertThrows(IllegalStateException.class, retired::retire));
    }

    @Test
    void rejectsNullArguments() {
        ExamBattle battle = begun(ExamType.ORAL, TWENTY_FIVE);

        assertAll(
                () -> assertThrows(NullPointerException.class, () -> battle.answer(null)),
                () -> assertThrows(NullPointerException.class, () -> battle.addListener(null)));
    }
}
