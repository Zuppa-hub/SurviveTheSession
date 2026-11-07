package it.unicam.cs.mpgc.rpg127295.engine.game;

import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleListener;
import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleOutcome;
import it.unicam.cs.mpgc.rpg127295.engine.battle.ExamBattle;
import it.unicam.cs.mpgc.rpg127295.engine.battle.ExamBattleFactory;
import it.unicam.cs.mpgc.rpg127295.model.exam.Exam;
import it.unicam.cs.mpgc.rpg127295.model.exam.ExamSession;
import it.unicam.cs.mpgc.rpg127295.model.exam.Grade;
import it.unicam.cs.mpgc.rpg127295.model.student.Stats;
import it.unicam.cs.mpgc.rpg127295.model.student.Student;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A game: one student facing a session of exams.
 * <ul>
 *   <li>{@link #startExam} restores the student's energy, creates the battle, registers the
 *       caller's listener and begins it, all in one call: a battle created but not begun cannot
 *       exist, because no order of calls is left to the caller. Every exam starts rested, so
 *       failing never pays off as a way to recover.</li>
 *   <li>When the battle ends the game applies the outcome: a passed exam goes in the
 *       transcript and gives its reward (items, experience, maybe new levels); a failed or
 *       abandoned one stays in the session.</li>
 *   <li>The last exam of the session is the final boss, available only when all the others
 *       are passed.</li>
 * </ul>
 * One battle at a time. No {@code equals}: a game has no stable identity.
 */
public final class Game {

    private final Student student;
    private final ExamSession session;
    private final ExamBattleFactory battles;
    private final ProgressionRules progression;
    private final RewardPolicy rewards;
    private final Graduation graduation = new Graduation();
    private final List<GameListener> listeners = new ArrayList<>();
    private Optional<ExamBattle> currentBattle = Optional.empty();

    public Game(Student student, ExamSession session, ExamBattleFactory battles,
                ProgressionRules progression, RewardPolicy rewards) {
        this.student = Objects.requireNonNull(student, "Student must not be null");
        this.session = Objects.requireNonNull(session, "Session must not be null");
        this.battles = Objects.requireNonNull(battles, "Battle factory must not be null");
        this.progression = Objects.requireNonNull(progression, "Progression rules must not be null");
        this.rewards = Objects.requireNonNull(rewards, "Reward policy must not be null");
    }

    public void addListener(GameListener listener) {
        listeners.add(Objects.requireNonNull(listener, "Listener must not be null"));
    }

    /**
     * Restores the student's energy, creates the battle of the exam and begins it, with the
     * listener of the caller already registered: it receives the first question. The game
     * listens first, so the outcome is applied before the caller hears of the end.
     *
     * @param listener the caller's listener, such as the exam screen
     * @return the battle in progress, or already over if it ended as soon as it began
     * @throws IllegalArgumentException if the exam is not in the session
     * @throws IllegalStateException    if another battle is in progress, or the exam is passed
     *                                  or not available yet (the final boss)
     */
    public ExamBattle startExam(Exam exam, BattleListener listener) {
        Objects.requireNonNull(exam, "Exam must not be null");
        Objects.requireNonNull(listener, "Listener must not be null");
        requireStartable(exam);
        student.updateStats(Stats::restoreFullEnergy);
        ExamBattle battle = battles.create(student, exam);
        battle.addListener(new BattleListener() {
            @Override
            public void onBattleEnded(BattleOutcome outcome) {
                conclude(exam, outcome);
            }
        });
        battle.addListener(listener);
        currentBattle = Optional.of(battle);
        battle.begin();
        return battle;
    }

    public Student student() {
        return student;
    }

    public ExamSession session() {
        return session;
    }

    /** @return the battle started and not ended yet, if any */
    public Optional<ExamBattle> currentBattle() {
        return currentBattle;
    }

    /** @return the exams not passed yet, in session order */
    public List<Exam> remainingExams() {
        return session.exams().stream()
                .filter(exam -> !student.transcript().hasPassed(exam))
                .toList();
    }

    /** @return the exams that can be taken now: the remaining ones, the boss only if it is the last */
    public List<Exam> availableExams() {
        List<Exam> remaining = remainingExams();
        if (remaining.size() == 1) {
            return remaining;
        }
        return remaining.stream()
                .filter(exam -> !exam.equals(session.boss()))
                .toList();
    }

    public boolean isSessionComplete() {
        return remainingExams().isEmpty();
    }

    public boolean canGraduate() {
        return graduation.canGraduate(session, student.transcript());
    }

    /** @return the final grade on 110, or empty until the student can graduate */
    public Optional<Integer> graduationGrade() {
        return canGraduate() ? Optional.of(graduation.finalGrade(student.transcript())) : Optional.empty();
    }

    private void requireStartable(Exam exam) {
        if (!session.contains(exam)) {
            throw new IllegalArgumentException("Exam " + exam.code() + " is not in the session");
        }
        if (currentBattle.isPresent()) {
            throw new IllegalStateException("Another exam is in progress");
        }
        if (!availableExams().contains(exam)) {
            throw new IllegalStateException("Exam " + exam.code() + " is passed or not available yet");
        }
    }

    private void conclude(Exam exam, BattleOutcome outcome) {
        currentBattle = Optional.empty();
        Reward reward = outcome.grade()
                .map(grade -> pass(exam, grade))
                .orElse(Reward.none());
        int levelsGained = gainExperience(reward.experience());
        ExamReport report = new ExamReport(exam, outcome, reward, levelsGained);
        listeners.forEach(listener -> listener.onExamConcluded(report));
    }

    private Reward pass(Exam exam, Grade grade) {
        student.transcript().record(exam, grade);
        Reward reward = rewards.rewardFor(exam, grade);
        reward.items().forEach(student.inventory()::add);
        return reward;
    }

    /**
     * Adds the experience and levels up as long as it is enough.
     *
     * @return the levels gained
     */
    private int gainExperience(int experience) {
        student.updateLevel(level -> level.addExperience(experience));
        int levelsGained = 0;
        while (student.level().experience() >= costOfNextLevel()) {
            int cost = costOfNextLevel();
            student.updateLevel(level -> level.levelUp(cost));
            student.updateStats(stats -> stats.improve(progression.knowledgeBonus(),
                    progression.maxEnergyBonus(), progression.maxConcentrationBonus()));
            levelsGained++;
        }
        return levelsGained;
    }

    private int costOfNextLevel() {
        return progression.experienceToLevelUp(student.level().number());
    }
}
