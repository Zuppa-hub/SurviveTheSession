package it.unicam.cs.mpgc.rpg127295.persistence.content;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unicam.cs.mpgc.rpg127295.model.question.Question;
import it.unicam.cs.mpgc.rpg127295.model.question.QuestionDetails;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;

class QuestionParsersTest {

    private static final List<QuestionParser> SHIPPED = List.of(
            new MultipleChoiceParser(), new TrueFalseParser(), new CodeOutputParser(), new FillInParser());

    @Test
    void theAnnotationIsKeptAtRuntimeOnTypesAndDocumented() {
        assertAll(
                () -> assertEquals(RetentionPolicy.RUNTIME, QuestionElement.class.getAnnotation(Retention.class).value()),
                () -> assertEquals(List.of(ElementType.TYPE),
                        List.of(QuestionElement.class.getAnnotation(Target.class).value())),
                () -> assertTrue(QuestionElement.class.isAnnotationPresent(Documented.class)));
    }

    @Test
    void mapsEveryParserToTheElementOfItsAnnotation() {
        Map<String, QuestionParser> parsers = QuestionParsers.byElement(SHIPPED);

        assertAll(
                () -> assertEquals(Set.of("multipleChoice", "trueFalse", "codeOutput", "fillIn"), parsers.keySet()),
                () -> assertInstanceOf(MultipleChoiceParser.class, parsers.get("multipleChoice")),
                () -> assertInstanceOf(FillInParser.class, parsers.get("fillIn")),
                () -> assertThrows(UnsupportedOperationException.class, () -> parsers.clear()));
    }

    @Test
    void aParserWithoutAnnotationStopsTheStartup() {
        List<QuestionParser> parsers = List.of(new FillInParser(), new Unannotated());

        ContentException error = assertThrows(ContentException.class, () -> QuestionParsers.byElement(parsers));
        assertAll(
                () -> assertTrue(error.getMessage().contains("Unannotated"), error.getMessage()),
                () -> assertTrue(error.getMessage().contains("@QuestionElement"), error.getMessage()));
    }

    @Test
    void twoParsersForTheSameElementStopTheStartup() {
        List<QuestionParser> parsers = List.of(new FillInParser(), new AnotherFillIn());

        ContentException error = assertThrows(ContentException.class, () -> QuestionParsers.byElement(parsers));
        assertTrue(error.getMessage().contains("fillIn"), error.getMessage());
    }

    @Test
    void rejectsNull() {
        assertThrows(NullPointerException.class, () -> QuestionParsers.byElement(null));
    }

    /** A parser that forgot to declare its element. */
    private static final class Unannotated implements QuestionParser {

        @Override
        public Question parse(Element element, QuestionDetails details, XmlReader reader) {
            throw new UnsupportedOperationException("Never called");
        }
    }

    /** A second parser for an element that already has one. */
    @QuestionElement("fillIn")
    private static final class AnotherFillIn implements QuestionParser {

        @Override
        public Question parse(Element element, QuestionDetails details, XmlReader reader) {
            throw new UnsupportedOperationException("Never called");
        }
    }
}
