package it.unicam.cs.mpgc.rpg127295.app;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unicam.cs.mpgc.rpg127295.model.exam.Exam;
import it.unicam.cs.mpgc.rpg127295.model.exam.ExamType;
import it.unicam.cs.mpgc.rpg127295.model.item.Item;
import it.unicam.cs.mpgc.rpg127295.model.question.ChoiceQuestion;
import it.unicam.cs.mpgc.rpg127295.model.question.CodeOutputQuestion;
import it.unicam.cs.mpgc.rpg127295.model.question.Difficulty;
import it.unicam.cs.mpgc.rpg127295.model.question.FillInQuestion;
import it.unicam.cs.mpgc.rpg127295.model.question.MultipleChoiceQuestion;
import it.unicam.cs.mpgc.rpg127295.model.question.Question;
import it.unicam.cs.mpgc.rpg127295.model.question.TrueFalseQuestion;
import it.unicam.cs.mpgc.rpg127295.model.student.Stats;
import it.unicam.cs.mpgc.rpg127295.model.student.StudentProfile;
import it.unicam.cs.mpgc.rpg127295.persistence.content.XmlDocumentLoader;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/** Loads the content shipped with the game: if a content file breaks a rule, the build fails. */
class ShippedContentTest {

    private static GameContent content;

    @BeforeAll
    static void loadShippedContent() {
        content = GameContent.load(new XmlDocumentLoader(GameContent.CONTENT_ROOT, GameContent.SCHEMA_ROOT));
    }

    @Test
    void containsTheProgrammingExam() {
        Exam programming = content.exams().findByCode("PROG1").orElseThrow();

        assertAll(
                () -> assertEquals("Programmazione", programming.subject().name()),
                () -> assertEquals(ExamType.WRITTEN, programming.type()),
                () -> assertEquals(12, programming.credits()),
                () -> assertEquals(Difficulty.MEDIUM, programming.difficulty()));
    }

    @Test
    void theSessionHasFiveExamsWithTheProjectAsFinalBoss() {
        List<Exam> exams = content.exams().all();

        assertAll(
                () -> assertEquals(List.of("PROG1", "ASD1", "BD1", "RETI1", "MDP1"),
                        exams.stream().map(Exam::code).toList()),
                () -> assertEquals(List.of(ExamType.WRITTEN, ExamType.WRITTEN, ExamType.ORAL, ExamType.ORAL,
                        ExamType.PROJECT), exams.stream().map(Exam::type).toList()),
                () -> assertEquals("Metodologie di Programmazione", exams.getLast().subject().name()));
    }

    @Test
    void containsTheThreeProfiles() {
        List<StudentProfile> profiles = content.profiles().all();

        assertAll(
                () -> assertEquals(List.of("Secchione", "Pendolare", "Nottambulo"),
                        profiles.stream().map(StudentProfile::name).toList()),
                () -> assertEquals(Stats.initial(8, 6, 4), profiles.get(0).initialStats()),
                () -> assertEquals(Stats.initial(3, 12, 4), profiles.get(1).initialStats()),
                () -> assertEquals(Stats.initial(5, 9, 6), profiles.get(2).initialStats()));
    }

    @Test
    void containsTheFourItems() {
        assertEquals(List.of("coffee", "notes", "summary", "energy-drink"),
                content.items().all().stream().map(Item::id).toList());
    }

    @Test
    void everyExamHasFifteenToTwentyQuestions() {
        content.exams().all().forEach(exam -> {
            int count = questionsOf(exam).size();
            assertTrue(count >= 15 && count <= 20, exam.code() + " has " + count + " questions");
        });
    }

    @Test
    void everyExamUsesEveryKindOfQuestion() {
        content.exams().all().forEach(exam -> {
            List<Question> questions = questionsOf(exam);
            assertAll(exam.code(),
                    () -> assertTrue(questions.stream().anyMatch(MultipleChoiceQuestion.class::isInstance)),
                    () -> assertTrue(questions.stream().anyMatch(TrueFalseQuestion.class::isInstance)),
                    () -> assertTrue(questions.stream().anyMatch(CodeOutputQuestion.class::isInstance)),
                    () -> assertTrue(questions.stream().anyMatch(FillInQuestion.class::isInstance)));
        });
    }

    @Test
    void everyExamHasQuestionsOfEveryDifficulty() {
        content.exams().all().forEach(exam -> assertEquals(EnumSet.allOf(Difficulty.class),
                questionsOf(exam).stream().map(Question::difficulty).collect(Collectors.toSet()), exam.code()));
    }

    @Test
    void notesCanBeUsedOnEveryFourOptionQuestion() {
        content.exams().all().stream()
                .flatMap(exam -> questionsOf(exam).stream())
                .filter(question -> !(question instanceof TrueFalseQuestion))
                .filter(ChoiceQuestion.class::isInstance)
                .map(ChoiceQuestion.class::cast)
                .forEach(question -> assertTrue(question.wrongOptions().size() >= 3, question.id()));
    }

    private static List<Question> questionsOf(Exam exam) {
        return content.questions().findBySubject(exam.subject());
    }
}
