package it.unicam.cs.mpgc.rpg127295.persistence.content;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares the XML element a {@link QuestionParser} builds, such as {@code "multipleChoice"}.
 * The name stays next to the class that handles it, and the map of the parsers is built by
 * reading it ({@link QuestionParsers#byElement}) instead of repeating the names by hand.
 * <ul>
 *   <li>{@code RUNTIME}: read with reflection while the application starts;</li>
 *   <li>{@code TYPE}: it describes a whole parser class;</li>
 *   <li>{@code Documented}: it appears in the Javadoc of each parser.</li>
 * </ul>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface QuestionElement {

    /** @return the name of the XML element, as in questions.xsd */
    String value();
}
