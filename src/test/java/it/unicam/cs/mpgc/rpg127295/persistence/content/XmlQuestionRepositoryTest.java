package it.unicam.cs.mpgc.rpg127295.persistence.content;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unicam.cs.mpgc.rpg127295.engine.repository.QuestionRepository;
import it.unicam.cs.mpgc.rpg127295.model.question.CodeOutputQuestion;
import it.unicam.cs.mpgc.rpg127295.model.question.Difficulty;
import it.unicam.cs.mpgc.rpg127295.model.question.FillInQuestion;
import it.unicam.cs.mpgc.rpg127295.model.question.MultipleChoiceQuestion;
import it.unicam.cs.mpgc.rpg127295.model.question.Question;
import it.unicam.cs.mpgc.rpg127295.model.question.Subject;
import it.unicam.cs.mpgc.rpg127295.model.question.TrueFalseQuestion;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class XmlQuestionRepositoryTest {

    private static final Subject PROGRAMMING = new Subject("PROG", "Programmazione");
    private static final Subject DATABASES = new Subject("BD", "Basi di Dati");

    private static final Map<String, QuestionParser> ALL_PARSERS = Map.of(
            "multipleChoice", new MultipleChoiceParser(),
            "trueFalse", new TrueFalseParser(),
            "codeOutput", new CodeOutputParser(),
            "fillIn", new FillInParser());

    private static QuestionRepository load(String folder, Map<String, QuestionParser> parsers,
                                           List<Subject> subjects) {
        XmlDocumentLoader loader = new XmlDocumentLoader("/content-test/questions/" + folder + "/", "/content/schema/");
        return XmlQuestionRepository.load(loader, parsers, subjects);
    }

    private static List<Question> validProgrammingQuestions() {
        return load("valid", ALL_PARSERS, List.of(PROGRAMMING, DATABASES)).findBySubject(PROGRAMMING);
    }

    @Test
    void loadsEveryQuestionTypeInFileOrder() {
        List<Question> questions = validProgrammingQuestions();

        assertAll(
                () -> assertEquals(List.of("PROG-01", "PROG-02", "PROG-03", "PROG-04"),
                        questions.stream().map(Question::id).toList()),
                () -> assertInstanceOf(MultipleChoiceQuestion.class, questions.get(0)),
                () -> assertInstanceOf(TrueFalseQuestion.class, questions.get(1)),
                () -> assertInstanceOf(CodeOutputQuestion.class, questions.get(2)),
                () -> assertInstanceOf(FillInQuestion.class, questions.get(3)));
    }

    @Test
    void readsTheCommonDetails() {
        Question first = validProgrammingQuestions().getFirst();
        Question second = validProgrammingQuestions().get(1);

        assertAll(
                () -> assertEquals(PROGRAMMING, first.subject()),
                () -> assertEquals(Difficulty.EASY, first.difficulty()),
                () -> assertEquals("Quale collezione non ammette duplicati?", first.prompt()),
                () -> assertEquals(Optional.of("Pensa agli insiemi"), first.hint()),
                () -> assertEquals(Optional.empty(), second.hint()));
    }

    @Test
    void readsMultipleChoiceOptionsWithTheCorrectOneFirst() {
        MultipleChoiceQuestion question = (MultipleChoiceQuestion) validProgrammingQuestions().getFirst();

        assertAll(
                () -> assertEquals(List.of("Set", "List", "Queue", "Deque"), question.options()),
                () -> assertTrue(question.isCorrect("Set")));
    }

    @Test
    void readsTheTrueFalseAnswer() {
        assertTrue(validProgrammingQuestions().get(1).isCorrect("Falso"));
    }

    @Test
    void readsCodeRemovingOnlyTheCommonIndentation() {
        CodeOutputQuestion question = (CodeOutputQuestion) validProgrammingQuestions().get(2);

        assertAll(
                () -> assertEquals("""
                        int x = 1;
                        if (x > 0) {
                            x += x++;
                        }
                        System.out.println(x);""", question.code()),
                () -> assertTrue(question.isCorrect("2")));
    }

    @Test
    void readsEveryAcceptedFillInAnswer() {
        Question question = validProgrammingQuestions().get(3);

        assertAll(
                () -> assertTrue(question.isCorrect("lifo")),
                () -> assertTrue(question.isCorrect("last in first out")));
    }

    @Test
    void keepsQuestionsOfEachSubjectSeparate() {
        QuestionRepository repository = load("valid", ALL_PARSERS, List.of(PROGRAMMING, DATABASES));

        assertAll(
                () -> assertEquals(List.of("BD-01"), repository.findBySubject(DATABASES).stream().map(Question::id).toList()),
                () -> assertEquals(DATABASES, repository.findBySubject(DATABASES).getFirst().subject()),
                () -> assertEquals(List.of(), repository.findBySubject(new Subject("RETI", "Reti"))));
    }

    @Test
    void returnedListsCannotBeModified() {
        List<Question> questions = validProgrammingQuestions();

        assertThrows(UnsupportedOperationException.class, () -> questions.removeFirst());
    }

    @Test
    void requiresAParserForEveryQuestionElement() {
        Map<String, QuestionParser> withoutFillIn = Map.of(
                "multipleChoice", new MultipleChoiceParser(),
                "trueFalse", new TrueFalseParser(),
                "codeOutput", new CodeOutputParser());

        ContentException error = assertThrows(ContentException.class,
                () -> load("valid", withoutFillIn, List.of(PROGRAMMING)));

        assertTrue(error.getMessage().contains("fillIn"), error.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"three-options", "five-options", "duplicate-option", "code-three-outputs", "blank-code",
                            "unknown-difficulty", "invalid-true-false-answer", "duplicate-id", "missing-prompt",
                            "blank-prompt", "fill-in-without-answers", "unknown-element", "wrong-subject"})
    void rejectsInvalidFiles(String folder) {
        ContentException error = assertThrows(ContentException.class,
                () -> load(folder, ALL_PARSERS, List.of(PROGRAMMING)));

        assertTrue(error.getMessage().contains("questions/prog.xml"), error.getMessage());
    }

    @Test
    void rejectsTheSameIdInDifferentSubjects() {
        ContentException error = assertThrows(ContentException.class,
                () -> load("duplicate-across-files", ALL_PARSERS, List.of(PROGRAMMING, DATABASES)));

        assertAll(
                () -> assertTrue(error.getMessage().contains("SHARED-01"), error.getMessage()),
                () -> assertTrue(error.getMessage().contains("questions/bd.xml"), error.getMessage()));
    }

    @Test
    void reportsTheMissingFileOfASubject() {
        ContentException error = assertThrows(ContentException.class,
                () -> load("missing-file", ALL_PARSERS, List.of(PROGRAMMING, DATABASES)));

        assertTrue(error.getMessage().contains("questions/prog.xml"), error.getMessage());
    }
}
