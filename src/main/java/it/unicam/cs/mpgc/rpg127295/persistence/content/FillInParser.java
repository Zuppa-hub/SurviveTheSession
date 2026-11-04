package it.unicam.cs.mpgc.rpg127295.persistence.content;

import it.unicam.cs.mpgc.rpg127295.model.question.FillInQuestion;
import it.unicam.cs.mpgc.rpg127295.model.question.Question;
import it.unicam.cs.mpgc.rpg127295.model.question.QuestionDetails;
import java.util.HashSet;
import org.w3c.dom.Element;

/** Builds a {@link FillInQuestion} from a {@code <fillIn>} element. */
public final class FillInParser implements QuestionParser {

    @Override
    public Question parse(Element element, QuestionDetails details, XmlReader reader) {
        return new FillInQuestion(details, new HashSet<>(reader.texts(element, "answer")));
    }
}
