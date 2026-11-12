package it.unicam.cs.mpgc.rpg127295.app;

import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleServices;
import it.unicam.cs.mpgc.rpg127295.engine.battle.ExamBattleFactory;
import it.unicam.cs.mpgc.rpg127295.engine.battle.ExamStrategy;
import it.unicam.cs.mpgc.rpg127295.engine.battle.StandardBattleRules;
import it.unicam.cs.mpgc.rpg127295.engine.game.Game;
import it.unicam.cs.mpgc.rpg127295.engine.game.RewardPolicy;
import it.unicam.cs.mpgc.rpg127295.engine.game.StandardProgressionRules;
import it.unicam.cs.mpgc.rpg127295.engine.game.StandardRewardPolicy;
import it.unicam.cs.mpgc.rpg127295.engine.grading.PerformanceGradeCalculator;
import it.unicam.cs.mpgc.rpg127295.engine.strategy.OralExamStrategy;
import it.unicam.cs.mpgc.rpg127295.engine.strategy.ProjectExamStrategy;
import it.unicam.cs.mpgc.rpg127295.engine.strategy.WrittenExamStrategy;
import it.unicam.cs.mpgc.rpg127295.engine.timer.ScheduledQuestionTimer;
import it.unicam.cs.mpgc.rpg127295.model.exam.ExamSession;
import it.unicam.cs.mpgc.rpg127295.model.exam.ExamType;
import it.unicam.cs.mpgc.rpg127295.model.student.Student;
import it.unicam.cs.mpgc.rpg127295.model.student.StudentProfile;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.random.RandomGenerator;

/**
 * The composition root of the engine: the only place where the concrete rules, strategies,
 * effects and timer are chosen and connected. Everything is built in the constructor, so a
 * missing strategy or an item without effect stops the application at startup (fail fast).
 */
public final class GameAssembly {

    private final GameContent content;
    private final ExamBattleFactory battles;
    private final ExamSession session;
    private final RewardPolicy rewards;

    /**
     * @param scheduler  runs the countdown of timed questions; owned by the caller, who shuts it down
     * @param uiExecutor delivers the timer notifications on the user interface thread
     * @param random     shuffles options and chooses questions and rewards
     * @throws it.unicam.cs.mpgc.rpg127295.persistence.content.ContentException if an item has no effect
     */
    public GameAssembly(GameContent content, ScheduledExecutorService scheduler, Executor uiExecutor,
                        RandomGenerator random) {
        this.content = Objects.requireNonNull(content, "Content must not be null");
        Objects.requireNonNull(scheduler, "Scheduler must not be null");
        Objects.requireNonNull(uiExecutor, "UI executor must not be null");
        Objects.requireNonNull(random, "Random generator must not be null");
        BattleServices services = new BattleServices(new StandardBattleRules(), new PerformanceGradeCalculator(),
                new ScheduledQuestionTimer(scheduler, uiExecutor), random, GameEffects.of(content.items()));
        this.battles = new ExamBattleFactory(strategies(), content.questions(), services);
        this.session = new ExamSession(content.exams().all());
        this.rewards = new StandardRewardPolicy(content.items(), random);
    }

    /**
     * @param profileId one of the profiles of the content
     * @throws IllegalArgumentException if no profile has that id
     */
    public Game newGame(String name, String profileId) {
        StudentProfile profile = content.profiles().findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown profile: " + profileId));
        return new Game(new Student(name, profile), session, battles, new StandardProgressionRules(), rewards);
    }

    /** @return a scheduler with a single daemon thread, so it never keeps the application alive */
    public static ScheduledExecutorService newTimerScheduler() {
        return Executors.newSingleThreadScheduledExecutor(task -> {
            Thread thread = new Thread(task, "question-timer");
            thread.setDaemon(true);
            return thread;
        });
    }

    private static Map<ExamType, ExamStrategy> strategies() {
        Map<ExamType, ExamStrategy> strategies = new EnumMap<>(ExamType.class);
        strategies.put(ExamType.WRITTEN, new WrittenExamStrategy());
        strategies.put(ExamType.ORAL, new OralExamStrategy());
        strategies.put(ExamType.PROJECT, new ProjectExamStrategy());
        return strategies;
    }
}
