package it.unicam.cs.mpgc.rpg127295.persistence.content;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Reads values relative to an element with XPath.
 * <p>
 * XPath makes these reads short and precise: {@code "wrong"} selects only the direct children
 * named {@code wrong} (DOM's {@code getElementsByTagName} searches at any depth) and an
 * optional element is just an expression that may match nothing. Each expression is compiled
 * once and reused. Not thread-safe: content is read at startup by a single thread.
 */
public final class XmlReader {

    private final Map<String, XPathExpression> compiled = new HashMap<>();

    /** @return the text of the first matching node, without surrounding whitespace */
    public String text(Node context, String expression) {
        return rawText(context, expression).strip();
    }

    /** @return the text of the first matching node, whitespace included */
    public String rawText(Node context, String expression) {
        return (String) evaluate(context, expression, XPathConstants.STRING);
    }

    /** @return the stripped text of the first matching node, or empty if nothing matches */
    public Optional<String> optionalText(Node context, String expression) {
        return Optional.ofNullable((Node) evaluate(context, expression, XPathConstants.NODE))
                .map(node -> node.getTextContent().strip());
    }

    /** @return the stripped texts of all matching nodes, in document order */
    public List<String> texts(Node context, String expression) {
        return elements(context, expression).stream()
                .map(element -> element.getTextContent().strip())
                .toList();
    }

    /** @return the matching elements, in document order */
    public List<Element> elements(Node context, String expression) {
        NodeList nodes = (NodeList) evaluate(context, expression, XPathConstants.NODESET);
        return IntStream.range(0, nodes.getLength())
                .mapToObj(nodes::item)
                .filter(Element.class::isInstance)
                .map(Element.class::cast)
                .toList();
    }

    private Object evaluate(Node context, String expression, QName resultType) {
        try {
            return compile(expression).evaluate(context, resultType);
        } catch (XPathExpressionException error) {
            throw new IllegalStateException("Cannot evaluate XPath expression: " + expression, error);
        }
    }

    private XPathExpression compile(String expression) throws XPathExpressionException {
        XPathExpression cached = compiled.get(expression);
        if (cached == null) {
            cached = XPathFactory.newInstance().newXPath().compile(expression);
            compiled.put(expression, cached);
        }
        return cached;
    }
}
