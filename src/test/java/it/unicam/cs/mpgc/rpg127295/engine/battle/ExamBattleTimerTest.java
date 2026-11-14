package it.unicam.cs.mpgc.rpg127295.engine.battle;

import static it.unicam.cs.mpgc.rpg127295.engine.TestQuestions.CORRECT;
import static it.unicam.cs.mpgc.rpg127295.engine.TestQuestions.choice;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import it.unicam.cs.mpgc.rpg127295.engine.FakeRandom;
import it.unicam.cs.mpgc.rpg127295.engine.grading.GradeCalculator;
import it.unicam.cs.mpgc.rpg127295.engine.grading.Performance;
import it.unicam.cs.mpgc.rpg127295.engine.timer.ManualScheduler;
import it.unicam.cs.mpgc.rpg127295.engine.timer.QueuedExecutor;
import it.unicam.cs.mpgc.rpg127295.engine.timer.ScheduledQuestionTimer;
import it.unicam.cs.mpgc.rpg127295.model.exam.ExamType;
import it.unicam.cs.mpgc.rpg127295.model.exam.Grade;
import it.unicam.cs.mpgc.rpg127295.model.student.Stats;
import it.unicam.cs.mpgc.rpg127295.model.student.Student;
import it.unicam.cs.mpgc.rpg127295.model.student.StudentProfile;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * A written exam with the real timer: the scheduler is moved by hand and the notifications wait
 * in a queue, as in the event queue of the user interface.
 */
class ExamBattleTimerTest {

    private static final GradeCalculator TWENTY_FIVE = performance -> Optional.of(Grade.of(25));

    private final ManualScheduler scheduler = new ManualScheduler();
    private final QueuedExecutor uiQueue = new QueuedExecutor();
    private final RecordingBattleListener listener = new RecordingBattleListener();
    private final Student student = new Student("Andrea",
            new StudentProfile("nerd", "Secchione", "Studia sempre", Stats.initial(5, 6, 4)));
    private ExamBattle battle;

    @BeforeEach
    void beginAWrittenExam() {
        BattleServices services = new BattleServices(FixedBattleRules.standard(), TWENTY_FIVE,
                new ScheduledQuestionTimer(scheduler, uiQueue), FakeRandom.first(), BattleEffects.none());
        battle = new ExamBattleFactory(ExamBattleTest.strategies(), subject -> List.of(choice("Q-1"), choice("Q-2")),
                services).create(student, ExamBattleTest.exam(ExamType.WRITTEN));
        battle.addListener(listener);
        battle.begin();
    }

    @AfterEach
    void shutDownScheduler() {
        scheduler.shutdownNow();
    }

    @Test
    void anExpiredTimeDeliveredByTheQueueIsAMistake() {
        scheduler.tick(30);
        uiQueue.runAll();

        assertAll(
                () -> assertEquals(List.of(false), listener.evaluations),
                () -> assertEquals(new Performance(0, 1, 0), battle.state().performance()));
    }

    @Test
    void anExpiryAlreadyQueuedWhenThePlayerAnswersIsIgnored() {
        scheduler.tick(30);

        battle.answer(CORRECT);
        uiQueue.runAll();

        assertAll(
                () -> assertEquals(List.of(true), listener.evaluations),
                () -> assertEquals(new Performance(1, 0, 0), battle.state().performance()),
                () -> assertEquals(6, student.stats().energy()),
                () -> assertEquals(choice("Q-2"), battle.state().currentQuestion().orElseThrow().question()),
                () -> assertEquals(List.of(), listener.ticks));
    }
}
