package it.unicam.cs.mpgc.rpg127295.persistence.content;

import it.unicam.cs.mpgc.rpg127295.model.question.CodeOutputQuestion;
import it.unicam.cs.mpgc.rpg127295.model.question.Question;
import it.unicam.cs.mpgc.rpg127295.model.question.QuestionDetails;
import java.util.List;
import org.w3c.dom.Element;

/**
 * Builds a {@link CodeOutputQuestion} from a {@code <codeOutput>} element.
 * <p>
 * The snippet keeps its relative indentation: blank lines around it are dropped and the
 * indentation shared by all lines, which comes from the XML layout, is removed.
 */
@QuestionElement("codeOutput")
public final class CodeOutputParser implements QuestionParser {

    @Override
    public Question parse(Element element, QuestionDetails details, XmlReader reader) {
        ChoiceOptions options = ChoiceOptions.read(element, reader);
        String code = withoutLayoutIndentation(reader.rawText(element, "code"));
        return new CodeOutputQuestion(details, code, options.all(), options.correct());
    }

    private static String withoutLayoutIndentation(String raw) {
        List<String> lines = raw.lines().toList();
        int first = firstNonBlank(lines);
        int last = lastNonBlank(lines);
        if (first > last) {
            return "";
        }
        return String.join("\n", lines.subList(first, last + 1)).stripIndent();
    }

    private static int firstNonBlank(List<String> lines) {
        int index = 0;
        while (index < lines.size() && lines.get(index).isBlank()) {
            index++;
        }
        return index;
    }

    private static int lastNonBlank(List<String> lines) {
        int index = lines.size() - 1;
        while (index >= 0 && lines.get(index).isBlank()) {
            index--;
        }
        return index;
    }
}
