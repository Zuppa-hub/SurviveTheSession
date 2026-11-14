package it.unicam.cs.mpgc.rpg127295.ui;

import it.unicam.cs.mpgc.rpg127295.engine.battle.Ability;
import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleRules;
import it.unicam.cs.mpgc.rpg127295.engine.game.ProgressionRules;
import it.unicam.cs.mpgc.rpg127295.model.exam.Exam;
import it.unicam.cs.mpgc.rpg127295.model.exam.ExamType;
import it.unicam.cs.mpgc.rpg127295.model.exam.Grade;
import it.unicam.cs.mpgc.rpg127295.model.item.Item;
import it.unicam.cs.mpgc.rpg127295.model.question.Difficulty;
import it.unicam.cs.mpgc.rpg127295.model.student.StudentProfile;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The pages of "Come si gioca". The text is written here, but every number of the rules is
 * read from the {@link GameGuide}: changing the balance changes these pages too (DRY).
 */
public final class HowToPlay {

    /** What each type of exam does differently, in a sentence. */
    private static final Map<ExamType, Function<GameGuide, String>> EXAM_TYPES = new EnumMap<>(Map.of(
            ExamType.WRITTEN, guide -> "Scritto: domande di ogni difficoltà, ognuna con un timer di "
                    + guide.secondsPerQuestion(ExamType.WRITTEN).orElseThrow() + " secondi. "
                    + "Il tempo scaduto conta come una risposta sbagliata.",
            ExamType.ORAL, guide -> "Orale: niente timer, ma dopo un errore il professore incalza e la domanda "
                    + "successiva è più difficile.",
            ExamType.PROJECT, guide -> "Progetto: niente timer, ma ogni errore costa ×"
                    + guide.strategyOf(ExamType.PROJECT).mistakeMultiplier() + " di Energia."));

    private HowToPlay() {
    }

    /** A page: a title and its paragraphs, separated by blank lines. */
    public record Page(String title, String text) {

        public Page {
            Objects.requireNonNull(title, "Title must not be null");
            Objects.requireNonNull(text, "Text must not be null");
        }
    }

    public static List<Page> pages(GameGuide guide) {
        Objects.requireNonNull(guide, "Guide must not be null");
        return List.of(goal(guide), stats(guide), examTypes(guide), itemsAndAbilities(guide), grade(guide),
                rewardsAndLevels(guide), bossGraduationAndSaves(guide));
    }

    private static Page goal(GameGuide guide) {
        return page("Obiettivo",
                "Sei uno studente all'ultima sessione: per laurearti devi superare tutti i "
                        + guide.session().exams().size() + " esami.",
                "Ogni esame è uno scontro a turni. Rispondi a domande vere sulla materia: ogni risposta giusta "
                        + "abbassa la resistenza dell'esame, ogni errore ti toglie Energia.",
                "Gli esami superati entrano nel libretto con il voto e i CFU.");
    }

    private static Page stats(GameGuide guide) {
        BattleRules rules = guide.battleRules();
        StudentProfile example = guide.profiles().getFirst();
        return page("Le statistiche",
                "Conoscenza: più è alta, più resistenza toglie ogni risposta giusta (un " + example.name()
                        + " con Conoscenza " + example.initialStats().knowledge() + " toglie "
                        + rules.damageOf(example.initialStats()) + " punti).",
                "Energia: è la tua vita. Ogni errore la riduce e a zero l'esame è bocciato. Si ricarica "
                        + "alla fine di ogni esame.",
                "Concentrazione: si spende per le abilità e cresce di +" + rules.concentrationPerCorrectAnswer()
                        + " a ogni risposta giusta. Passa da un esame all'altro.",
                "Il profilo che scegli decide i valori di partenza.");
    }

    private static Page examTypes(GameGuide guide) {
        String types = Arrays.stream(ExamType.values())
                .map(type -> EXAM_TYPES.get(type).apply(guide))
                .collect(Collectors.joining("\n\n"));
        return page("I tipi d'esame", types,
                "Energia persa per un errore, secondo la difficoltà dell'esame: " + energyLossByDifficulty(guide) + ".");
    }

    private static String energyLossByDifficulty(GameGuide guide) {
        return Arrays.stream(Difficulty.values())
                .map(difficulty -> Labels.of(difficulty) + " " + guide.battleRules().baseEnergyLoss(difficulty))
                .collect(Collectors.joining(", "));
    }

    private static Page itemsAndAbilities(GameGuide guide) {
        BattleRules rules = guide.battleRules();
        return page("Oggetti e abilità",
                "Nel tuo turno rispondi oppure usi un oggetto o un'abilità; puoi anche ritirarti, e l'esame "
                        + "resta da sostenere. Un oggetto o un'abilità che non serve non si consuma.",
                "Oggetti:\n" + guide.items().stream()
                        .map(item -> "• " + item.name() + ": " + item.description())
                        .collect(Collectors.joining("\n")),
                "Il tempo in più vale " + rules.extraSeconds() + " secondi e l'Energia recuperata "
                        + rules.energyRestored() + " punti di Energia. " + starterKit(guide),
                "Abilità, pagate con la Concentrazione: " + guide.abilities().stream()
                        .map(HowToPlay::ability)
                        .collect(Collectors.joining(", "))
                        + ". Una risposta giusta potenziata toglie ×" + rules.boostedDamageMultiplier() + " di resistenza.");
    }

    private static String starterKit(GameGuide guide) {
        return "Parti con: " + guide.starterKit().items().keySet().stream()
                .map(Item::name)
                .sorted()
                .collect(Collectors.joining(", ")) + ".";
    }

    private static String ability(Ability ability) {
        return ability.name() + " (" + ability.concentrationCost() + " Concentrazione)";
    }

    private static Page grade(GameGuide guide) {
        return page("Il voto",
                "L'esame finisce quando la sua resistenza arriva a zero o quando le domande finiscono. "
                        + "Lo superi con almeno il " + guide.passingPercentage() + "% di risposte giuste.",
                "Più risposte giuste danno un voto più alto. Ogni suggerimento abbassa il voto, e la lode "
                        + "arriva solo senza errori né suggerimenti.",
                "Se l'Energia finisce l'esame è bocciato, senza voto: resta nella sessione e puoi riprovarlo.");
    }

    private static Page rewardsAndLevels(GameGuide guide) {
        ProgressionRules progression = guide.progression();
        String items = guide.lowestGradeWithAnItem()
                .map(grade -> "Con un voto da " + grade.label() + " in su ricevi anche oggetti, di più con i voti "
                        + "più alti.")
                .orElse("");
        return page("Ricompense e livelli",
                "Un esame superato dà esperienza: di più con più CFU e un voto più alto (" + experienceExample(guide)
                        + "). " + items,
                "Per passare dal livello 1 al 2 servono " + progression.experienceToLevelUp(1)
                        + " punti di esperienza, poi sempre di più. Ogni livello dà +" + progression.knowledgeBonus()
                        + " Conoscenza, +" + progression.maxEnergyBonus() + " Energia massima e +"
                        + progression.maxConcentrationBonus() + " Concentrazione massima.");
    }

    private static String experienceExample(GameGuide guide) {
        Exam exam = guide.session().exams().getFirst();
        Grade grade = guide.lowestGradeWithAnItem().orElse(Grade.all().getFirst());
        return exam.subject().name() + " con " + grade.label() + " dà "
                + guide.rewards().rewardFor(exam, grade).experience() + " punti";
    }

    private static Page bossGraduationAndSaves(GameGuide guide) {
        return page("Boss finale, laurea e salvataggi",
                "L'ultimo esame, " + guide.session().boss().subject().name() + ", è il boss finale: si sblocca "
                        + "solo quando hai superato tutti gli altri.",
                "Superati tutti gli esami ti laurei: il voto di laurea è la media dei voti pesata sui CFU, "
                        + "riportata in centodecimi.",
                "La partita si salva da sola alla fine di ogni esame, con il tuo nome: la ritrovi nel menu, "
                        + "tra le partite salvate.");
    }

    private static Page page(String title, String... paragraphs) {
        return new Page(title, String.join("\n\n", paragraphs));
    }
}
