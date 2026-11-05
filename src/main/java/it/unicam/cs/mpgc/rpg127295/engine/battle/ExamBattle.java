package it.unicam.cs.mpgc.rpg127295.engine.battle;

import it.unicam.cs.mpgc.rpg127295.engine.grading.Performance;
import it.unicam.cs.mpgc.rpg127295.engine.strategy.ExamStrategy;
import it.unicam.cs.mpgc.rpg127295.engine.timer.TimerListener;
import it.unicam.cs.mpgc.rpg127295.model.exam.Exam;
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
    private final int maxResistance;

    private int resistance;
    private Performance performance = Performance.none();
    private Optional<PresentedQuestion> currentQuestion = Optional.empty();
    private Optional<AnswerResult> lastAnswer = Optional.empty();
    private Optional<BattleOutcome> outcome = Optional.empty();
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

    public BattleState state() {
        return new BattleState(resistance, maxResistance, student.stats(), performance, currentQuestion, lastAnswer);
    }

    /** @return the outcome, or empty while the battle is in progress */
    public Optional<BattleOutcome> outcome() {
        return outcome;
    }

    public boolean isOver() {
        return outcome.isPresent();
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
        lastAnswer = Optional.of(new AnswerResult(question, correct));
        BattleState state = state();
        listeners.forEach(listener -> listener.onAnswerEvaluated(correct, state));
        continueOrEnd();
    }

    private void rewardCorrectAnswer() {
        resistance = Math.max(0, resistance - services.rules().damageOf(student.stats()));
        int concentration = services.rules().concentrationPerCorrectAnswer();
        student.updateStats(stats -> stats.recoverConcentration(concentration));
        performance = performance.withCorrectAnswer();
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
}
