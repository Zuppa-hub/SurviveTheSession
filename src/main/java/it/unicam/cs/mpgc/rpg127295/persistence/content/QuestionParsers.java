package it.unicam.cs.mpgc.rpg127295.persistence.content;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/** Builds the map from XML element to parser by reading {@link QuestionElement}. */
public final class QuestionParsers {

    private QuestionParsers() {
    }

    /**
     * @return element name → parser; unmodifiable
     * @throws ContentException if a parser has no {@link QuestionElement}, or two parsers declare
     *                          the same element
     */
    public static Map<String, QuestionParser> byElement(Collection<? extends QuestionParser> parsers) {
        Objects.requireNonNull(parsers, "Parsers must not be null");
        Map<String, QuestionParser> byElement = new HashMap<>();
        for (QuestionParser parser : parsers) {
            QuestionParser previous = byElement.put(elementOf(parser), parser);
            if (previous != null) {
                throw new ContentException("Two parsers for element <" + elementOf(parser) + ">: "
                        + previous.getClass().getSimpleName() + " and " + parser.getClass().getSimpleName());
            }
        }
        return Map.copyOf(byElement);
    }

    private static String elementOf(QuestionParser parser) {
        QuestionElement element = parser.getClass().getAnnotation(QuestionElement.class);
        if (element == null) {
            throw new ContentException("Parser " + parser.getClass().getSimpleName()
                    + " does not declare its element with @QuestionElement");
        }
        return element.value();
    }
}
