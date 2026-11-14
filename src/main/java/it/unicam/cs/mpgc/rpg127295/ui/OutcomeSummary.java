package it.unicam.cs.mpgc.rpg127295.ui;

import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleResult;
import it.unicam.cs.mpgc.rpg127295.engine.game.ExamReport;
import it.unicam.cs.mpgc.rpg127295.engine.game.Reward;
import it.unicam.cs.mpgc.rpg127295.engine.grading.Performance;
import it.unicam.cs.mpgc.rpg127295.model.exam.Grade;
import it.unicam.cs.mpgc.rpg127295.model.item.Item;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * The texts of the outcome screen, from the report of the game.
 *
 * @param title   how the exam ended
 * @param grade   the grade of a passed exam, as shown; empty otherwise
 * @param details answers, reward and levels, one line each; copied
 */
public record OutcomeSummary(String title, Optional<String> grade, List<String> details) {

    private static final Map<BattleResult, String> TITLES = new EnumMap<>(Map.of(
            BattleResult.PASSED, "Esame superato",
            BattleResult.FAILED, "Esame non superato",
            BattleResult.RETIRED, "Ritiro dall'esame"));
    private static final String TRY_AGAIN = "L'esame resta nella sessione: puoi riprovarlo.";

    public OutcomeSummary {
        Objects.requireNonNull(title, "Title must not be null");
        Objects.requireNonNull(grade, "Grade must not be null");
        details = List.copyOf(Objects.requireNonNull(details, "Details must not be null"));
    }

    public static OutcomeSummary of(ExamReport report) {
        Objects.requireNonNull(report, "Report must not be null");
        Optional<Grade> grade = report.outcome().grade();
        List<String> details = new ArrayList<>();
        details.add(answers(report.outcome().performance()));
        if (grade.isPresent()) {
            details.addAll(rewardLines(report.reward(), report.levelsGained()));
        } else {
            details.add(TRY_AGAIN);
        }
        return new OutcomeSummary(TITLES.get(report.outcome().result()), grade.map(Grade::label), details);
    }

    private static String answers(Performance performance) {
        return "Risposte giuste " + performance.correctAnswers() + " · sbagliate " + performance.wrongAnswers()
                + " · suggerimenti " + performance.hintsUsed();
    }

    private static List<String> rewardLines(Reward reward, int levelsGained) {
        List<String> lines = new ArrayList<>();
        if (reward.experience() > 0) {
            lines.add("Esperienza +" + reward.experience());
        }
        lines.addAll(itemLines(reward.items()));
        if (levelsGained > 0) {
            lines.add("Livello aumentato di " + levelsGained);
        }
        return lines;
    }

    private static List<String> itemLines(Map<Item, Integer> items) {
        return items.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(Item::name)))
                .map(entry -> "Ricevi " + entry.getKey().name() + " × " + entry.getValue())
                .toList();
    }
}
