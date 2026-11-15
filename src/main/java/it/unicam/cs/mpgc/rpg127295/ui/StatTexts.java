package it.unicam.cs.mpgc.rpg127295.ui;

import static it.unicam.cs.mpgc.rpg127295.model.Preconditions.requirePositive;

import it.unicam.cs.mpgc.rpg127295.engine.game.Graduation;
import it.unicam.cs.mpgc.rpg127295.model.student.Stats;
import java.util.Locale;
import java.util.OptionalDouble;

/** The texts shown on more than one screen, written once so that the screens say the same thing. */
public final class StatTexts {

    private static final String NO_EXAM_PASSED = "Nessun esame superato";

    private StatTexts() {
    }

    public static String energy(Stats stats) {
        return "Energia " + outOf(stats.energy(), stats.maxEnergy());
    }

    public static String concentration(Stats stats) {
        return "Concentrazione " + outOf(stats.concentration(), stats.maxConcentration());
    }

    public static String resistance(int resistance, int maxResistance) {
        return "Resistenza dell'esame " + outOf(resistance, maxResistance);
    }

    /**
     * @return the value as a share of the maximum, for a progress bar
     * @throws IllegalArgumentException if the maximum is not positive
     */
    public static double share(int value, int max) {
        requirePositive(max, "Maximum");
        return (double) value / max;
    }

    /** @return the average with two decimals, in Italian format */
    public static String average(double average) {
        return String.format(Locale.ITALY, "%.2f", average);
    }

    public static String average(OptionalDouble average) {
        return average.stream()
                .mapToObj(value -> "Media ponderata " + average(value))
                .findFirst()
                .orElse(NO_EXAM_PASSED);
    }

    public static String credits(int credits) {
        return credits + " CFU";
    }

    public static String graduationGrade(int grade) {
        return "Voto di laurea " + grade + "/" + Graduation.GRADUATION_SCALE;
    }

    private static String outOf(int value, int max) {
        return value + " / " + max;
    }
}
