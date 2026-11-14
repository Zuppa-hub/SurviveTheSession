package it.unicam.cs.mpgc.rpg127295.engine.battle;

import it.unicam.cs.mpgc.rpg127295.engine.grading.Performance;
import it.unicam.cs.mpgc.rpg127295.engine.battle.ExamStrategy;
import it.unicam.cs.mpgc.rpg127295.engine.timer.TimerListener;
import it.unicam.cs.mpgc.rpg127295.model.exam.Exam;
import it.unicam.cs.mpgc.rpg127295.model.item.Item;
import it.unicam.cs.mpgc.rpg127295.model.question.Question;
import it.unicam.cs.mpgc.rpg127295.model.student.Student;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;

/**
 * A turn-based battle against an exam.
 * <ul>
 *   <li>A correct answer lowers the resistance of the exam and recovers concentration; a
 *       wrong answer, or an expired time, costs energy.</li>
 *   <li>The battle ends when the resistance reaches zero or the questions run out: the
 *       {@link it.unicam.cs.mpgc.rpg127295.engine.grading.GradeCalculator} decides between
 *       passed and failed.</li>
 *   <li>With no energy left the exam is failed, without a grade; {@link #retire()} ends it
 *       without a grade too.</li>
 *   <li>While a question waits for an answer, items and abilities apply their
 *       {@link BattleEffect}, which changes the battle only through {@link BattleControls}.</li>
 * </ul>
 * Not thread-safe: every call, timer notifications included, must come from the same thread.
 * No {@code equals}: a battle has no stable identity.
 */
public final class ExamBattle {

    private final Student student;
    private final Exam exam;
    private final ExamStrategy strategy;
    private final QuestionPool pool;
    private final BattleServices services;
    private final List<BattleListener> listeners = new ArrayList<>();
    private final TimerListener clock = new Clock();
    private final BattleControls controls = new Controls();
    private final int maxResistance;

    private int resistance;
    private Performance performance = Performance.none();
    private Optional<PresentedQuestion> currentQuestion = Optional.empty();
    private Optional<AnswerResult> lastAnswer = Optional.empty();
    private Optional<BattleOutcome> outcome = Optional.empty();
    private boolean timed;
    private boolean nextCorrectAnswerDoubled;
    private boolean begun;

    ExamBattle(Student student, Exam exam, ExamStrategy strategy, QuestionPool pool, BattleServices services) {
        this.student = Objects.requireNonNull(student, "Student must not be null");
        this.exam = Objects.requireNonNull(exam, "Exam must not be null");
        this.strategy = Objects.requireNonNull(strategy, "Strategy must not be null");
        this.pool = Objects.requireNonNull(pool, "Question pool must not be null");
        this.services = Objects.requireNonNull(services, "Battle services must not be null");
        this.maxResistance = services.rules().resistanceOf(exam);
        this.resistance = maxResistance;
    }

    public void addListener(BattleListener listener) {
        listeners.add(Objects.requireNonNull(listener, "Listener must not be null"));
    }

    /**
     * Presents the first question; an exhausted student fails at once.
     *
     * @throws IllegalStateException if the battle has already begun
     */
    public void begin() {
        if (begun) {
            throw new IllegalStateException("The battle has already begun");
        }
        begun = true;
        if (student.stats().isExhausted()) {
            end(BattleOutcome.failed(performance));
        } else {
            presentNextQuestion();
        }
    }

    /**
     * Answers the current question.
     *
     * @throws IllegalStateException if no question is waiting for an answer
     */
    public void answer(String answer) {
        Objects.requireNonNull(answer, "Answer must not be null");
        Question question = requireCurrentQuestion();
        services.timer().stop();
        evaluate(question, question.isCorrect(answer));
    }

    /**
     * Leaves the exam without a grade.
     *
     * @throws IllegalStateException if the battle has not begun or is over
     */
    public void retire() {
        requireCurrentQuestion();
        end(BattleOutcome.retired(performance));
    }

    /** @return the abilities the student can use in this battle */
    public List<Ability> abilities() {
        return services.effects().abilities();
    }

    /** @return true if {@link #useItem(Item)} would succeed now */
    public boolean canUse(Item item) {
        return refusalOf(attemptToUse(item)).isEmpty();
    }

    /** @return true if {@link #useAbility(Ability)} would succeed now */
    public boolean canUse(Ability ability) {
        return refusalOf(attemptToUse(ability)).isEmpty();
    }

    /**
     * Uses one unit of an owned item and applies its effect.
     *
     * @throws IllegalArgumentException if the item has no effect
     * @throws IllegalStateException    if no question is waiting for an answer, the item is not
     *                                  owned or its effect cannot be used now
     */
    public void useItem(Item item) {
        use(attemptToUse(item));
    }

    /**
     * Spends the concentration of an ability and applies its effect.
     *
     * @throws IllegalArgumentException if the ability is not available in this battle
     * @throws IllegalStateException    if no question is waiting for an answer, concentration is
     *                                  not enough or the effect cannot be used now
     */
    public void useAbility(Ability ability) {
        use(attemptToUse(ability));
    }

    public BattleState state() {
        return new BattleState(resistance, maxResistance, student.stats(), performance,
                currentQuestion, lastAnswer, timed, nextCorrectAnswerDoubled);
    }

    /** @return the outcome, or empty while the battle is in progress */
    public Optional<BattleOutcome> outcome() {
        return outcome;
    }

    public boolean isOver() {
        return outcome.isPresent();
    }

    private Attempt attemptToUse(Item item) {
        Objects.requireNonNull(item, "Item must not be null");
        return new Attempt(item.name(), services.effects().effectOf(item),
                student.inventory().contains(item), "is not owned",
                () -> student.inventory().remove(item));
    }

    private Attempt attemptToUse(Ability ability) {
        Objects.requireNonNull(ability, "Ability must not be null");
        Optional<BattleEffect> effect = Optional.of(ability.effect())
                .filter(available -> services.effects().hasAbility(ability));
        int cost = ability.concentrationCost();
        return new Attempt(ability.name(), effect, student.stats().canSpend(cost), "needs more concentration",
                () -> student.updateStats(stats -> stats.spendConcentration(cost)));
    }

    /**
     * The single check behind {@code canUse} and {@code use}: the reason why the attempt is
     * refused, or empty if it can be used.
     */
    private Optional<RuntimeException> refusalOf(Attempt attempt) {
        if (currentQuestion.isEmpty()) {
            return Optional.of(new IllegalStateException("No question is waiting for an answer"));
        }
        if (attempt.effect().isEmpty()) {
            return Optional.of(new IllegalArgumentException(attempt.name() + " has no effect in this battle"));
        }
        if (!attempt.affordable()) {
            return Optional.of(new IllegalStateException(attempt.name() + " " + attempt.notAffordableReason()));
        }
        if (!attempt.effect().get().canBeUsedIn(state())) {
            return Optional.of(new IllegalStateException(attempt.name() + " cannot be used now"));
        }
        return Optional.empty();
    }

    private void use(Attempt attempt) {
        refusalOf(attempt).ifPresent(refusal -> {
            throw refusal;
        });
        attempt.payment().run();
        attempt.effect().orElseThrow().applyTo(controls);
        BattleState state = state();
        listeners.forEach(listener -> listener.onEffectApplied(state));
    }

    private Question requireCurrentQuestion() {
        return currentQuestion
                .map(PresentedQuestion::question)
                .orElseThrow(() -> new IllegalStateException("No question is waiting for an answer"));
    }

    private void evaluate(Question question, boolean correct) {
        if (correct) {
            rewardCorrectAnswer();
        } else {
            punishMistake();
        }
        currentQuestion = Optional.empty();
        timed = false;
        lastAnswer = Optional.of(new AnswerResult(question, correct));
        BattleState state = state();
        listeners.forEach(listener -> listener.onAnswerEvaluated(correct, state));
        continueOrEnd();
    }

    private void rewardCorrectAnswer() {
        resistance = Math.max(0, resistance - damage());
        nextCorrectAnswerDoubled = false;
        int concentration = services.rules().concentrationPerCorrectAnswer();
        student.updateStats(stats -> stats.recoverConcentration(concentration));
        performance = performance.withCorrectAnswer();
    }

    private int damage() {
        int damage = services.rules().damageOf(student.stats());
        return nextCorrectAnswerDoubled ? damage * services.rules().boostedDamageMultiplier() : damage;
    }

    private void punishMistake() {
        int energyLoss = services.rules().baseEnergyLoss(exam.difficulty()) * strategy.mistakeMultiplier();
        student.updateStats(stats -> stats.loseEnergy(energyLoss));
        performance = performance.withWrongAnswer();
    }

    private void continueOrEnd() {
        if (student.stats().isExhausted()) {
            end(BattleOutcome.failed(performance));
        } else if (resistance == 0) {
            endWithGrade();
        } else {
            presentNextQuestion();
        }
    }

    private void presentNextQuestion() {
        strategy.nextQuestion(state(), pool).ifPresentOrElse(this::present, this::endWithGrade);
    }

    private void present(Question question) {
        PresentedQuestion presented = PresentedQuestion.of(question, services.random());
        currentQuestion = Optional.of(presented);
        OptionalInt seconds = strategy.secondsPerQuestion();
        timed = seconds.isPresent();
        listeners.forEach(listener -> listener.onQuestionPresented(presented, seconds));
        seconds.ifPresent(time -> services.timer().start(time, clock));
    }

    private void endWithGrade() {
        end(services.calculator().calculate(performance)
                .map(grade -> BattleOutcome.passed(grade, performance))
                .orElseGet(() -> BattleOutcome.failed(performance)));
    }

    private void end(BattleOutcome result) {
        services.timer().stop();
        currentQuestion = Optional.empty();
        timed = false;
        outcome = Optional.of(result);
        listeners.forEach(listener -> listener.onBattleEnded(result));
    }

    /** Forwards the ticks to the listeners and turns an expired time into a wrong answer. */
    private final class Clock implements TimerListener {

        @Override
        public void onTick(int secondsLeft) {
            listeners.forEach(listener -> listener.onTimeTick(secondsLeft));
        }

        @Override
        public void onExpired() {
            currentQuestion.map(PresentedQuestion::question)
                    .ifPresent(question -> evaluate(question, false));
        }
    }

    /**
     * What an effect may change, and nothing else (encapsulation: the change is not forbidden,
     * it is controlled). Passed only to effects whose use has been checked.
     */
    private final class Controls implements BattleControls {

        private static final int WRONG_OPTIONS_REMOVED = 2;

        @Override
        public void addExtraTime() {
            services.timer().addSeconds(services.rules().extraSeconds());
        }

        @Override
        public void removeTwoWrongOptions() {
            currentQuestion = currentQuestion.map(
                    question -> question.withoutWrongOptions(WRONG_OPTIONS_REMOVED, services.random()));
        }

        @Override
        public void showHint() {
            currentQuestion = currentQuestion.map(PresentedQuestion::withHintShown);
            performance = performance.withHint();
        }

        @Override
        public void restoreEnergy() {
            int energy = services.rules().energyRestored();
            student.updateStats(stats -> stats.restoreEnergy(energy));
        }

        @Override
        public void doubleNextCorrectAnswer() {
            nextCorrectAnswerDoubled = true;
        }
    }

    /**
     * An attempt to use an item or an ability, so that both go through the same checks.
     *
     * @param name                shown in the error messages
     * @param effect              the effect, empty if not available in this battle
     * @param affordable          whether the item is owned or the concentration is enough
     * @param notAffordableReason the message when it is not affordable
     * @param payment             consumes the item or spends the concentration
     */
    private record Attempt(String name, Optional<BattleEffect> effect, boolean affordable,
                           String notAffordableReason, Runnable payment) {
    }
}
