package it.unicam.cs.mpgc.rpg127295.persistence.content;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;

class XmlReaderTest {

    private static final String XML = """
            <question id="Q-1">
                <prompt>  Quale collezione?  </prompt>
                <wrong>List</wrong>
                <wrong> Map </wrong>
                <nested><wrong>Deque</wrong></nested>
                <code>
                    int x = 1;
                </code>
            </question>
            """;

    private final XmlReader reader = new XmlReader();
    private Element question;

    @BeforeEach
    void parse() throws Exception {
        question = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(new ByteArrayInputStream(XML.getBytes(StandardCharsets.UTF_8)))
                .getDocumentElement();
    }

    @Test
    void textIsStripped() {
        assertEquals("Quale collezione?", reader.text(question, "prompt"));
    }

    @Test
    void rawTextKeepsWhitespace() {
        assertEquals("\n        int x = 1;\n    ", reader.rawText(question, "code"));
    }

    @Test
    void optionalTextIsEmptyWhenTheNodeIsMissing() {
        assertAll(
                () -> assertEquals(Optional.of("Quale collezione?"), reader.optionalText(question, "prompt")),
                () -> assertEquals(Optional.empty(), reader.optionalText(question, "hint")));
    }

    @Test
    void textsReturnsOnlyDirectChildrenInDocumentOrder() {
        assertEquals(List.of("List", "Map"), reader.texts(question, "wrong"));
    }

    @Test
    void elementsReturnsTheMatchingElements() {
        List<Element> children = reader.elements(question, "*");

        assertEquals(List.of("prompt", "wrong", "wrong", "nested", "code"),
                children.stream().map(Element::getTagName).toList());
    }
}
