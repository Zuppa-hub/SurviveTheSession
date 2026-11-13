package it.unicam.cs.mpgc.rpg127295.persistence.content;

import it.unicam.cs.mpgc.rpg127295.model.question.Question;
import it.unicam.cs.mpgc.rpg127295.model.question.QuestionDetails;
import org.w3c.dom.Element;

/**
 * Builds one kind of question from its XML element.
 * <p>
 * Every parser declares its element with {@link QuestionElement}; the composition root builds the
 * map element → parser by reading it, so a new kind of question needs a new annotated parser,
 * not a change to the repository (OCP).
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
