package it.unicam.cs.mpgc.rpg127295.persistence.content;

import it.unicam.cs.mpgc.rpg127295.model.question.Question;
import it.unicam.cs.mpgc.rpg127295.model.question.QuestionDetails;
import org.w3c.dom.Element;

/**
 * Builds one kind of question from its XML element.
 * <p>
 * Parsers are registered by element name in a map built in the composition root, so a new
 * kind of question needs a new parser and a new map entry, not a change to the repository (OCP).
 */
@FunctionalInterface
public interface QuestionParser {

    /**
     * @param element the question element, already validated by the schema
     * @param details the data common to every question, already read by the repository
     * @param reader  reads values relative to the element
     * @throws IllegalArgumentException if the domain rejects the data
     */
    Question parse(Element element, QuestionDetails details, XmlReader reader);
}
