package it.unicam.cs.mpgc.rpg127295.persistence.content;

import java.util.List;
import java.util.stream.Stream;
import org.w3c.dom.Element;

/**
 * The options of a choice question as written in the content: one {@code <correct>} and
 * several {@code <wrong>} elements. The correct option comes first; the engine shuffles them.
 */
record ChoiceOptions(String correct, List<String> all) {

    static ChoiceOptions read(Element element, XmlReader reader) {
        String correct = reader.text(element, "correct");
        List<String> all = Stream.concat(Stream.of(correct), reader.texts(element, "wrong").stream()).toList();
        return new ChoiceOptions(correct, all);
    }
}
