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
import it.unicam.cs.mpgc.rpg127295.engine.repository.SaveGameException;
import it.unicam.cs.mpgc.rpg127295.engine.repository.SaveGameRepository;
import it.unicam.cs.mpgc.rpg127295.engine.repository.SaveMapper;
import it.unicam.cs.mpgc.rpg127295.engine.strategy.OralExamStrategy;
import it.unicam.cs.mpgc.rpg127295.engine.strategy.ProjectExamStrategy;
import it.unicam.cs.mpgc.rpg127295.engine.strategy.WrittenExamStrategy;
import it.unicam.cs.mpgc.rpg127295.engine.timer.ScheduledQuestionTimer;
import it.unicam.cs.mpgc.rpg127295.model.exam.ExamSession;
import it.unicam.cs.mpgc.rpg127295.model.exam.ExamType;
import it.unicam.cs.mpgc.rpg127295.model.student.Student;
import it.unicam.cs.mpgc.rpg127295.model.student.StudentProfile;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
    private final SaveGameRepository saves;
    private final SaveMapper mapper;

    /**
     * @param scheduler  runs the countdown of timed questions; owned by the caller, who shuts it down
     * @param uiExecutor delivers the timer notifications on the user interface thread
     * @param random     shuffles options and chooses questions and rewards
     * @param saves      where games are saved after every exam
     * @throws it.unicam.cs.mpgc.rpg127295.persistence.content.ContentException if an item has no effect
     */
    public GameAssembly(GameContent content, ScheduledExecutorService scheduler, Executor uiExecutor,
                        RandomGenerator random, SaveGameRepository saves) {
        this.content = Objects.requireNonNull(content, "Content must not be null");
        Objects.requireNonNull(scheduler, "Scheduler must not be null");
        Objects.requireNonNull(uiExecutor, "UI executor must not be null");
        Objects.requireNonNull(random, "Random generator must not be null");
        BattleServices services = new BattleServices(new StandardBattleRules(), new PerformanceGradeCalculator(),
                new ScheduledQuestionTimer(scheduler, uiExecutor), random, GameEffects.of(content.items()));
        this.battles = new ExamBattleFactory(strategies(), content.questions(), services);
        this.session = new ExamSession(content.exams().all());
        this.rewards = new StandardRewardPolicy(content.items(), random);
        this.saves = Objects.requireNonNull(saves, "Save repository must not be null");
        this.mapper = new SaveMapper(content.profiles(), content.items(), content.exams());
    }

    /**
     * Creates a game saved in the given slot after every exam.
     *
     * @param profileId one of the profiles of the content
     * @param slot      letters, digits, - and _ only
     * @throws IllegalArgumentException if no profile has that id or the slot name is invalid
     */
    public Game newGame(String name, String profileId, String slot) {
        SaveGameRepository.requireValidSlot(slot);
        StudentProfile profile = content.profiles().findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown profile: " + profileId));
        return gameOf(new Student(name, profile), slot);
    }

    /**
     * @return the game saved in the slot, which keeps being saved there; empty if never saved
     * @throws SaveGameException if the saved game is unreadable or does not match the content
     */
    public Optional<Game> loadGame(String slot) {
        return saves.load(slot).map(data -> gameOf(mapper.toStudent(data), slot));
    }

    /** @return the slots with a saved game, in alphabetical order */
    public List<String> savedGames() {
        return saves.listSlots();
    }

    private Game gameOf(Student student, String slot) {
        Game game = new Game(student, session, battles, new StandardProgressionRules(), rewards);
        game.addListener(new AutoSave(saves, mapper, slot, student));
        return game;
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
