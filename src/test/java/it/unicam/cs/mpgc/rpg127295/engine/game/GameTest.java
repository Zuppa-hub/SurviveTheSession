package it.unicam.cs.mpgc.rpg127295.engine.game;

import static it.unicam.cs.mpgc.rpg127295.engine.TestQuestions.CORRECT;
import static it.unicam.cs.mpgc.rpg127295.engine.TestQuestions.choice;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unicam.cs.mpgc.rpg127295.engine.FakeRandom;
import it.unicam.cs.mpgc.rpg127295.engine.TestQuestions;
import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleEffects;
import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleOutcome;
import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleResult;
import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleServices;
import it.unicam.cs.mpgc.rpg127295.engine.battle.ExamBattle;
import it.unicam.cs.mpgc.rpg127295.engine.battle.ExamBattleFactory;
import it.unicam.cs.mpgc.rpg127295.engine.battle.ExamStrategy;
import it.unicam.cs.mpgc.rpg127295.engine.battle.StandardBattleRules;
import it.unicam.cs.mpgc.rpg127295.engine.grading.Performance;
import it.unicam.cs.mpgc.rpg127295.engine.strategy.OralExamStrategy;
import it.unicam.cs.mpgc.rpg127295.engine.strategy.ProjectExamStrategy;
import it.unicam.cs.mpgc.rpg127295.engine.strategy.WrittenExamStrategy;
import it.unicam.cs.mpgc.rpg127295.engine.timer.ManualQuestionTimer;
import it.unicam.cs.mpgc.rpg127295.model.exam.Exam;
import it.unicam.cs.mpgc.rpg127295.model.exam.ExamSession;
import it.unicam.cs.mpgc.rpg127295.model.exam.ExamType;
import it.unicam.cs.mpgc.rpg127295.model.exam.Grade;
import it.unicam.cs.mpgc.rpg127295.model.item.Item;
import it.unicam.cs.mpgc.rpg127295.model.question.Difficulty;
import it.unicam.cs.mpgc.rpg127295.model.student.Level;
import it.unicam.cs.mpgc.rpg127295.model.student.Stats;
import it.unicam.cs.mpgc.rpg127295.model.student.Student;
import it.unicam.cs.mpgc.rpg127295.model.student.StudentProfile;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * Every exam has 1 credit and is easy, so with the standard rules one correct answer defeats
 * it; the grade is decided by {@link #grade}, the reward by {@link #reward}.
 */
class GameTest {

    private static final Item COFFEE = new Item("coffee", "Caffè", "Più tempo");
    private static final Exam FIRST = exam("PROG1");
    private static final Exam SECOND = exam("ASD");
    private static final Exam BOSS = exam("MDP");

    private Optional<Grade> grade = Optional.of(Grade.of(27));
    private Reward reward = new Reward(150, Map.of(COFFEE, 1));
    private final List<ExamReport> reports = new ArrayList<>();
    private final Student student = new Student("Andrea",
            new StudentProfile("nerd", "Secchione", "Studia sempre", Stats.initial(5, 6, 4)));

    private static Exam exam(String code) {
        return new Exam(code, TestQuestions.SUBJECT, ExamType.ORAL, 1, Difficulty.EASY);
    }

    private Game game(Exam... exams) {
        Map<ExamType, ExamStrategy> strategies = Map.of(
                ExamType.WRITTEN, new WrittenExamStrategy(),
                ExamType.ORAL, new OralExamStrategy(),
                ExamType.PROJECT, new ProjectExamStrategy());
        BattleServices services = new BattleServices(new StandardBattleRules(), performance -> grade,
                new ManualQuestionTimer(), FakeRandom.first(), BattleEffects.none());
        ExamBattleFactory battles = new ExamBattleFactory(strategies,
                subject -> List.of(choice("Q-1"), choice("Q-2")), services);
        Game game = new Game(student, new ExamSession(List.of(exams)), battles,
                new StandardProgressionRules(), (exam, passedWith) -> reward);
        game.addListener(reports::add);
        return game;
    }

    private static void pass(Game game, Exam exam) {
        ExamBattle battle = game.startExam(exam);
        battle.begin();
        battle.answer(CORRECT);
    }

    @Test
    void everyExamStartsWithFullEnergy() {
        Game game = game(FIRST);
        student.updateStats(stats -> stats.loseEnergy(5));

        game.startExam(FIRST);

        assertEquals(6, student.stats().energy());
    }

    @Test
    void aPassedExamGoesInTheTranscriptWithItsReward() {
        Game game = game(FIRST, SECOND);

        pass(game, FIRST);

        assertAll(
                () -> assertEquals(Optional.of(Grade.of(27)), student.transcript().gradeOf(FIRST)),
                () -> assertEquals(1, student.inventory().quantityOf(COFFEE)),
                () -> assertEquals(List.of(SECOND), game.remainingExams()),
                () -> assertEquals(Optional.empty(), game.currentBattle()));
    }

    @Test
    void experienceRaisesTheLevelAndImprovesTheStats() {
        Game game = game(FIRST);

        pass(game, FIRST);

        assertAll(
                () -> assertEquals(new Level(2, 50), student.level()),
                () -> assertEquals(6, student.stats().knowledge()),
                () -> assertEquals(8, student.stats().maxEnergy()),
                () -> assertEquals(5, student.stats().maxConcentration()),
                () -> assertEquals(1, reports.getFirst().levelsGained()));
    }

    @Test
    void oneExamCanRaiseMoreThanOneLevel() {
        reward = new Reward(350, Map.of());
        Game game = game(FIRST);

        pass(game, FIRST);

        assertAll(
                () -> assertEquals(new Level(3, 50), student.level()),
                () -> assertEquals(2, reports.getFirst().levelsGained()),
                () -> assertEquals(7, student.stats().knowledge()));
    }

    @Test
    void aFailedExamStaysInTheSessionWithoutReward() {
        grade = Optional.empty();
        Game game = game(FIRST);

        pass(game, FIRST);

        ExamReport report = reports.getFirst();
        assertAll(
                () -> assertEquals(BattleResult.FAILED, report.outcome().result()),
                () -> assertEquals(Reward.none(), report.reward()),
                () -> assertEquals(0, report.levelsGained()),
                () -> assertEquals(List.of(FIRST), game.remainingExams()),
                () -> assertFalse(student.transcript().hasPassed(FIRST)),
                () -> assertEquals(Level.initial(), student.level()),
                () -> assertTrue(student.inventory().isEmpty()));
    }

    @Test
    void aRetiredExamCanBeTakenAgain() {
        Game game = game(FIRST);
        ExamBattle battle = game.startExam(FIRST);
        battle.begin();

        battle.retire();
        game.startExam(FIRST);

        assertEquals(BattleOutcome.retired(Performance.none()), reports.getFirst().outcome());
    }

    @Test
    void theBossIsAvailableOnlyAfterAllTheOtherExams() {
        Game game = game(FIRST, SECOND, BOSS);
        List<Exam> atTheStart = game.availableExams();
        boolean bossRefused = throwsIllegalState(() -> game.startExam(BOSS));

        pass(game, FIRST);
        pass(game, SECOND);

        assertAll(
                () -> assertEquals(List.of(FIRST, SECOND), atTheStart),
                () -> assertTrue(bossRefused),
                () -> assertEquals(List.of(BOSS), game.availableExams()));
    }

    @Test
    void inASessionOfOneExamTheBossIsAvailable() {
        assertEquals(List.of(FIRST), game(FIRST).availableExams());
    }

    @Test
    void graduatesWhenEveryExamIsPassed() {
        Game game = game(FIRST, BOSS);
        pass(game, FIRST);
        boolean graduatedHalfway = game.canGraduate();
        Optional<Integer> gradeHalfway = game.graduationGrade();

        pass(game, BOSS);

        assertAll(
                () -> assertFalse(graduatedHalfway),
                () -> assertEquals(Optional.empty(), gradeHalfway),
                () -> assertTrue(game.isSessionComplete()),
                () -> assertTrue(game.canGraduate()),
                () -> assertEquals(Optional.of(99), game.graduationGrade()));
    }

    @Test
    void listenersAreNotifiedAfterTheOutcomeIsApplied() {
        Game game = game(FIRST);
        List<Boolean> passedWhenNotified = new ArrayList<>();
        game.addListener(report -> passedWhenNotified.add(student.transcript().hasPassed(FIRST)));

        pass(game, FIRST);

        assertAll(
                () -> assertEquals(List.of(true), passedWhenNotified),
                () -> assertEquals(new ExamReport(FIRST, BattleOutcome.passed(Grade.of(27), new Performance(1, 0, 0)),
                        reward, 1), reports.getFirst()));
    }

    @Test
    void rejectsExamsThatCannotBeTaken() {
        Game game = game(FIRST, SECOND);
        Game busy = game(FIRST, SECOND);
        busy.startExam(FIRST);
        pass(game, FIRST);

        assertAll(
                () -> assertThrows(IllegalStateException.class, () -> game.startExam(FIRST)),
                () -> assertThrows(IllegalArgumentException.class, () -> game.startExam(exam("RETI"))),
                () -> assertThrows(IllegalStateException.class, () -> busy.startExam(SECOND)),
                () -> assertThrows(NullPointerException.class, () -> game.startExam(null)),
                () -> assertThrows(NullPointerException.class, () -> game.addListener(null)));
    }

    private static boolean throwsIllegalState(Runnable action) {
        try {
            action.run();
            return false;
        } catch (IllegalStateException expected) {
            return true;
        }
    }
}
