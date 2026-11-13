package it.unicam.cs.mpgc.rpg127295.persistence.content;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unicam.cs.mpgc.rpg127295.engine.repository.ExamCatalog;
import it.unicam.cs.mpgc.rpg127295.model.exam.Exam;
import it.unicam.cs.mpgc.rpg127295.model.exam.ExamType;
import it.unicam.cs.mpgc.rpg127295.model.question.Difficulty;
import it.unicam.cs.mpgc.rpg127295.model.question.Subject;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class XmlExamCatalogTest {

    private static ExamCatalog load(String folder) {
        return XmlExamCatalog.load(new XmlDocumentLoader("/content-test/exams/" + folder + "/", "/content/schema/"));
    }

    @Test
    void loadsTheExamsInSessionOrder() {
        List<Exam> exams = load("valid").all();
        Exam programming = exams.getFirst();

        assertAll(
                () -> assertEquals(List.of("PROG1", "BD1"), exams.stream().map(Exam::code).toList()),
                () -> assertEquals(new Subject("PROG", "Programmazione"), programming.subject()),
                () -> assertEquals(ExamType.WRITTEN, programming.type()),
                () -> assertEquals(12, programming.credits()),
                () -> assertEquals(Difficulty.MEDIUM, programming.difficulty()));
    }

    @Test
    void resolvesEachExamSubject() {
        Exam databases = load("valid").findByCode("BD1").orElseThrow();

        assertAll(
                () -> assertEquals(new Subject("BD", "Basi di Dati"), databases.subject()),
                () -> assertEquals(ExamType.ORAL, databases.type()),
                () -> assertEquals(Difficulty.HARD, databases.difficulty()));
    }

    @Test
    void findByCodeIsEmptyForUnknownCodes() {
        assertEquals(Optional.empty(), load("valid").findByCode("RETI1"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"unknown-subject", "duplicate-code", "duplicate-subject", "zero-credits",
                            "unknown-type", "unknown-difficulty"})
    void rejectsInvalidFiles(String folder) {
        ContentException error = assertThrows(ContentException.class, () -> load(folder));

        assertTrue(error.getMessage().contains("exams.xml"), error.getMessage());
    }
}
