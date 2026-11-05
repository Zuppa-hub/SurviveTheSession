package it.unicam.cs.mpgc.rpg127295.engine.grading;

import static org.junit.jupiter.api.Assertions.assertEquals;

import it.unicam.cs.mpgc.rpg127295.model.exam.Grade;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PerformanceGradeCalculatorTest extends GradeCalculatorContractTest {

    private final GradeCalculator calculator = new PerformanceGradeCalculator();

    @Override
    GradeCalculator createCalculator() {
        return new PerformanceGradeCalculator();
    }

    private Optional<Grade> grade(int correct, int wrong, int hints) {
        return calculator.calculate(new Performance(correct, wrong, hints));
    }

    @Test
    void exactlySixtyPercentIsEighteen() {
        assertEquals(Optional.of(Grade.of(18)), grade(3, 2, 0));
    }

    @Test
    void belowSixtyPercentIsInsufficient() {
        assertEquals(Optional.empty(), grade(59, 41, 0));
    }

    @Test
    void noAnswersIsInsufficient() {
        assertEquals(Optional.empty(), grade(0, 0, 0));
    }

    @ParameterizedTest(name = "{0} correct out of {1} -> {2}")
    @CsvSource({
        " 8, 10, 24",
        " 9, 10, 27",
        " 7, 10, 21",
        "13, 20, 19",
        " 2,  3, 20"
    })
    void scalesLinearlyFromEighteenToThirtyRoundingDown(int correct, int answered, int expected) {
        assertEquals(Optional.of(Grade.of(expected)), grade(correct, answered - correct, 0));
    }

    @Test
    void aFlawlessPerformanceIsThirtyWithHonors() {
        assertEquals(Optional.of(Grade.of(31)), grade(6, 0, 0));
    }

    @Test
    void allCorrectWithAHintIsTwentyNine() {
        assertEquals(Optional.of(Grade.of(29)), grade(6, 0, 1));
    }

    @Test
    void eachHintCostsOnePoint() {
        assertEquals(Optional.of(Grade.of(22)), grade(8, 2, 2));
    }

    @Test
    void hintsNeverTurnASufficientGradeIntoAFailure() {
        assertEquals(Optional.of(Grade.of(18)), grade(61, 39, 5));
    }
}
