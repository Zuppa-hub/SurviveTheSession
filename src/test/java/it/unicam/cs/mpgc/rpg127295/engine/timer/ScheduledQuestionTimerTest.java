package it.unicam.cs.mpgc.rpg127295.engine.timer;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.Executor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class ScheduledQuestionTimerTest {

    private static final Executor DIRECT = Runnable::run;

    private final ManualScheduler scheduler = new ManualScheduler();
    private final RecordingTimerListener listener = new RecordingTimerListener();

    @AfterEach
    void shutDownScheduler() {
        scheduler.shutdownNow();
    }

    private ScheduledQuestionTimer timer(Executor notifier) {
        return new ScheduledQuestionTimer(scheduler, notifier);
    }

    @Test
    void ticksEverySecond() {
        timer(DIRECT).start(3, listener);

        assertEquals(List.of(1000L, 1000L), scheduler.lastDelayAndPeriod());
    }

    @Test
    void countsDownAndExpiresOnce() {
        timer(DIRECT).start(3, listener);

        scheduler.tick(5);

        assertAll(
                () -> assertEquals(List.of(2, 1, 0), listener.ticks),
                () -> assertEquals(1, listener.expiries),
                () -> assertEquals(0, scheduler.activeTasks()));
    }

    @Test
    void addSecondsExtendsTheCountdown() {
        ScheduledQuestionTimer timer = timer(DIRECT);
        timer.start(2, listener);
        scheduler.tick();

        timer.addSeconds(15);
        scheduler.tick();

        assertEquals(List.of(1, 15), listener.ticks);
    }

    @Test
    void addSecondsWithoutACountdownIsIgnored() {
        ScheduledQuestionTimer timer = timer(DIRECT);
        timer.start(1, listener);
        scheduler.tick();

        assertDoesNotThrow(() -> timer.addSeconds(5));
        scheduler.tick();
        assertEquals(List.of(0), listener.ticks);
    }

    @Test
    void stopCancelsTheCountdown() {
        ScheduledQuestionTimer timer = timer(DIRECT);
        timer.start(3, listener);

        timer.stop();
        scheduler.tick();

        assertAll(
                () -> assertEquals(0, scheduler.activeTasks()),
                () -> assertTrue(listener.ticks.isEmpty()),
                () -> assertDoesNotThrow(timer::stop));
    }

    @Test
    void startReplacesTheCountdownInProgress() {
        ScheduledQuestionTimer timer = timer(DIRECT);
        RecordingTimerListener next = new RecordingTimerListener();
        timer.start(3, listener);
        scheduler.tick();

        timer.start(5, next);
        scheduler.tick();

        assertAll(
                () -> assertEquals(List.of(2), listener.ticks),
                () -> assertEquals(List.of(4), next.ticks),
                () -> assertEquals(1, scheduler.activeTasks()));
    }

    @Test
    void anOldTickDoesNotTouchTheNewCountdown() {
        ScheduledQuestionTimer timer = timer(DIRECT);
        RecordingTimerListener next = new RecordingTimerListener();
        timer.start(3, listener);
        timer.start(5, next);

        scheduler.runTask(0);
        scheduler.tick();

        assertAll(
                () -> assertTrue(listener.ticks.isEmpty()),
                () -> assertEquals(List.of(4), next.ticks));
    }

    @Test
    void notificationsAreDeliveredThroughTheNotifier() {
        QueuedExecutor notifier = new QueuedExecutor();
        timer(notifier).start(2, listener);
        scheduler.tick();

        boolean deliveredBeforeTheQueueRuns = !listener.ticks.isEmpty();
        notifier.runAll();

        assertAll(
                () -> assertFalse(deliveredBeforeTheQueueRuns),
                () -> assertEquals(List.of(1), listener.ticks));
    }

    @Test
    void aQueuedExpiryIsDroppedAfterStop() {
        QueuedExecutor notifier = new QueuedExecutor();
        ScheduledQuestionTimer timer = timer(notifier);
        timer.start(1, listener);
        scheduler.tick();

        timer.stop();
        notifier.runAll();

        assertAll(
                () -> assertTrue(listener.ticks.isEmpty()),
                () -> assertEquals(0, listener.expiries));
    }

    @Test
    void aQueuedExpiryIsDroppedAfterANewStart() {
        QueuedExecutor notifier = new QueuedExecutor();
        ScheduledQuestionTimer timer = timer(notifier);
        RecordingTimerListener next = new RecordingTimerListener();
        timer.start(1, listener);
        scheduler.tick();

        timer.start(30, next);
        notifier.runAll();

        assertAll(
                () -> assertEquals(0, listener.expiries),
                () -> assertTrue(next.ticks.isEmpty()),
                () -> assertEquals(0, next.expiries));
    }

    @Test
    void rejectsInvalidArguments() {
        ScheduledQuestionTimer timer = timer(DIRECT);

        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> timer.start(0, listener)),
                () -> assertThrows(NullPointerException.class, () -> timer.start(1, null)),
                () -> assertThrows(IllegalArgumentException.class, () -> timer.addSeconds(0)),
                () -> assertThrows(NullPointerException.class, () -> new ScheduledQuestionTimer(null, DIRECT)),
                () -> assertThrows(NullPointerException.class, () -> new ScheduledQuestionTimer(scheduler, null)));
    }
}
