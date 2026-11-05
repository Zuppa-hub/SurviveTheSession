package it.unicam.cs.mpgc.rpg127295.engine.grading;

import it.unicam.cs.mpgc.rpg127295.model.exam.Grade;
import java.util.Optional;

/**
 * Computes the grade of an exam from the student's performance.
 * <p>
 * Contract of the type, valid for every implementation:
 * <ul>
 *   <li>a sufficient performance gives a {@link Grade} (18-30 or 30 with honors); an
 *       insufficient one gives {@link Optional#empty()};</li>
 *   <li>never null and never an exception for a valid {@link Performance};</li>
 *   <li>never a value out of range: guaranteed by {@link Grade}, which cannot exist outside
 *       18-31;</li>
 *   <li>a performance with no correct answer is insufficient;</li>
 *   <li>a performance with no mistakes and no hints is sufficient;</li>
 *   <li>with the same number of answers and hints, one more correct answer never worsens the
 *       result;</li>
 *   <li>with the same correct and wrong answers, one more hint never improves the result.</li>
 * </ul>
 * In the last two rules a failure counts as lower than any grade.
 */
@FunctionalInterface
public interface GradeCalculator {

    /** @return the grade, or empty if the performance is insufficient */
    Optional<Grade> calculate(Performance performance);
}
