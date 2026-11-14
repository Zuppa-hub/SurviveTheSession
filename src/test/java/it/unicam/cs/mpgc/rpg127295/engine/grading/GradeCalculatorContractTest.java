package it.unicam.cs.mpgc.rpg127295.engine.grading;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unicam.cs.mpgc.rpg127295.model.exam.Grade;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/**
 * Checks the contract of {@link GradeCalculator} that every implementation must respect (LSP).
 * Each implementation test extends this class and provides its calculator.
 * <p>
 * The rules are checked on a grid of performances: 0-10 correct answers, 0-10 wrong answers,
 * 0-3 hints.
 */
abstract class GradeCalculatorContractTest {

    private static final int MAX_ANSWERS = 10;
    private static final int MAX_HINTS = 3;

    abstract GradeCalculator createCalculator();

    private Optional<Grade> grade(int correct, int wrong, int hints) {
        return createCalculator().calculate(new Performance(correct, wrong, hints));
    }

    /** Orders results: a failure is below every grade. */
    private static int rank(Optional<Grade> result) {
        return result.map(Grade::value).orElse(0);
    }

    private static Stream<Performance> grid() {
        return IntStream.rangeClosed(0, MAX_ANSWERS).boxed()
                .flatMap(correct -> IntStream.rangeClosed(0, MAX_ANSWERS).boxed()
                        .flatMap(wrong -> IntStream.rangeClosed(0, MAX_HINTS)
                                .mapToObj(hints -> new Performance(correct, wrong, hints))));
    }

    @Test
    void neverReturnsNullForAnyValidPerformance() {
        GradeCalculator calculator = createCalculator();

        grid().forEach(performance -> assertNotNull(calculator.calculate(performance), performance.toString()));
    }

    @Test
    void noCorrectAnswerIsInsufficient() {
        IntStream.rangeClosed(0, MAX_ANSWERS).forEach(wrong ->
                IntStream.rangeClosed(0, MAX_HINTS).forEach(hints ->
                        assertEquals(Optional.empty(), grade(0, wrong, hints), wrong + " wrong, " + hints + " hints")));
    }

    @Test
    void aFlawlessPerformanceIsSufficient() {
        IntStream.rangeClosed(1, MAX_ANSWERS).forEach(correct ->
                assertTrue(grade(correct, 0, 0).isPresent(), correct + " correct"));
    }

    @Test
    void oneMoreCorrectAnswerNeverWorsensTheGrade() {
        grid().filter(performance -> performance.wrongAnswers() > 0).forEach(performance -> {
            int better = rank(grade(performance.correctAnswers() + 1, performance.wrongAnswers() - 1,
                    performance.hintsUsed()));
            int current = rank(grade(performance.correctAnswers(), performance.wrongAnswers(),
                    performance.hintsUsed()));
            assertTrue(better >= current, performance.toString());
        });
    }

    @Test
    void oneMoreHintNeverImprovesTheGrade() {
        grid().filter(performance -> performance.hintsUsed() < MAX_HINTS).forEach(performance -> {
            int withMoreHints = rank(grade(performance.correctAnswers(), performance.wrongAnswers(),
                    performance.hintsUsed() + 1));
            int current = rank(grade(performance.correctAnswers(), performance.wrongAnswers(),
                    performance.hintsUsed()));
            assertTrue(withMoreHints <= current, performance.toString());
        });
    }
}
