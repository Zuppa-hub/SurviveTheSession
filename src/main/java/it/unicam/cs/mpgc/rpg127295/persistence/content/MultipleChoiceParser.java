package it.unicam.cs.mpgc.rpg127295.persistence.content;

import it.unicam.cs.mpgc.rpg127295.model.question.MultipleChoiceQuestion;
import it.unicam.cs.mpgc.rpg127295.model.question.Question;
import it.unicam.cs.mpgc.rpg127295.model.question.QuestionDetails;
import org.w3c.dom.Element;

/** Builds a {@link MultipleChoiceQuestion} from a {@code <multipleChoice>} element. */
@QuestionElement("multipleChoice")
public final class MultipleChoiceParser implements QuestionParser {

    @Override
    public Question parse(Element element, QuestionDetails details, XmlReader reader) {
        ChoiceOptions options = ChoiceOptions.read(element, reader);
        return new MultipleChoiceQuestion(details, options.all(), options.correct());
    }
}
