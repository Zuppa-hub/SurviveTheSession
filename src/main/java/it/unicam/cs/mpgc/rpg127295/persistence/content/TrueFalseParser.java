package it.unicam.cs.mpgc.rpg127295.persistence.content;

import it.unicam.cs.mpgc.rpg127295.model.question.Question;
import it.unicam.cs.mpgc.rpg127295.model.question.QuestionDetails;
import it.unicam.cs.mpgc.rpg127295.model.question.TrueFalseQuestion;
import org.w3c.dom.Element;

/** Builds a {@link TrueFalseQuestion} from a {@code <trueFalse>} element. */
public final class TrueFalseParser implements QuestionParser {

    @Override
    public Question parse(Element element, QuestionDetails details, XmlReader reader) {
        return new TrueFalseQuestion(details, Boolean.parseBoolean(element.getAttribute("answer")));
    }
}
