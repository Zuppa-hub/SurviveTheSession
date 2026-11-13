package it.unicam.cs.mpgc.rpg127295.ui;

import it.unicam.cs.mpgc.rpg127295.engine.game.Game;
import it.unicam.cs.mpgc.rpg127295.model.transcript.Transcript;
import it.unicam.cs.mpgc.rpg127295.model.transcript.TranscriptEntry;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * The texts of the graduation screen: the transcript, the weighted average, the credits and the
 * final grade, all computed by the engine.
 *
 * @param studentName the student who graduates
 * @param exams       one line per passed exam, in the order they were passed; copied
 * @param average     the weighted average of the grades
 * @param credits     the total credits
 * @param finalGrade  the final grade on 110
 */
public record GraduationSummary(String studentName, List<String> exams, double average, int credits,
                                int finalGrade) {

    public GraduationSummary {
        Objects.requireNonNull(studentName, "Student name must not be null");
        exams = List.copyOf(Objects.requireNonNull(exams, "Exams must not be null"));
    }

    /** @throws IllegalStateException if the student cannot graduate yet */
    public static GraduationSummary of(Game game) {
        Objects.requireNonNull(game, "Game must not be null");
        int finalGrade = game.graduationGrade()
                .orElseThrow(() -> new IllegalStateException("The session is not complete yet"));
        Transcript transcript = game.student().transcript();
        return new GraduationSummary(game.student().name(), examLines(transcript),
                transcript.weightedAverage().orElseThrow(), transcript.totalCredits(), finalGrade);
    }

    /** @return the average with two decimals, in Italian format */
    public String averageLabel() {
        return String.format(Locale.ITALY, "%.2f", average);
    }

    private static List<String> examLines(Transcript transcript) {
        return transcript.entries().stream()
                .map(GraduationSummary::examLine)
                .toList();
    }

    private static String examLine(TranscriptEntry entry) {
        return entry.exam().subject().name() + " · " + entry.exam().credits() + " CFU · " + entry.grade().label();
    }
}
