package it.unicam.cs.mpgc.rpg127295.model.question;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class MultipleChoiceQuestionTest extends ChoiceQuestionContractTest {

    private static final List<String> OPTIONS = List.of("List", "Set", "Map", "Queue");

    @Override
    MultipleChoiceQuestion createQuestion(String id, String prompt) {
        return new MultipleChoiceQuestion(details(id, prompt), OPTIONS, "Set");
    }

    @Override
    String correctAnswer() {
        return "Set";
    }

    @Override
    String wrongAnswer() {
        return "Map";
    }

    private static MultipleChoiceQuestion withOptions(List<String> options, String correctOption) {
        return new MultipleChoiceQuestion(details("Q-1", "Domanda"), options, correctOption);
    }

    @Test
    void keepsOptionsInTheGivenOrder() {
        assertEquals(OPTIONS, withOptions(OPTIONS, "Set").options());
    }

    @Test
    void copiesTheGivenOptions() {
        List<String> options = new ArrayList<>(OPTIONS);
        MultipleChoiceQuestion question = withOptions(options, "Set");

        options.set(0, "Array");

        assertEquals(OPTIONS, question.options());
    }

    @Test
    void requiresExactlyFourOptions() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> withOptions(List.of("List", "Set", "Map"), "Set")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> withOptions(List.of("List", "Set", "Map", "Queue", "Deque"), "Set")));
    }

    @Test
    void isCaseSensitive() {
        assertFalse(createQuestion("Q-1", "Domanda").isCorrect("set"));
    }

    @Test
    void rejectsDuplicateOptions() {
        assertThrows(IllegalArgumentException.class, () -> withOptions(List.of("List", "Set", "Set", "Map"), "Set"));
    }

    @Test
    void rejectsBlankOptions() {
        assertThrows(IllegalArgumentException.class, () -> withOptions(List.of("List", "Set", " ", "Map"), "Set"));
    }

    @Test
    void rejectsCorrectOptionNotAmongOptions() {
        assertThrows(IllegalArgumentException.class, () -> withOptions(OPTIONS, "Deque"));
    }

    @Test
    void rejectsMissingReferences() {
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> withOptions(null, "Set")),
                () -> assertThrows(NullPointerException.class,
                        () -> withOptions(Arrays.asList("List", null, "Map", "Queue"), "Map")),
                () -> assertThrows(NullPointerException.class, () -> withOptions(OPTIONS, null)),
                () -> assertThrows(NullPointerException.class,
                        () -> new MultipleChoiceQuestion(null, OPTIONS, "Set")));
    }
}
