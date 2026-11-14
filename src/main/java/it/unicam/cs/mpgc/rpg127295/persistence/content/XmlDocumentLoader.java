package it.unicam.cs.mpgc.rpg127295.persistence.content;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Reads XML content files from the classpath and validates them against an XML Schema.
 * <p>
 * Content files are read with {@code getResourceAsStream}. Schemas are loaded from their
 * resource URL, so that relative {@code xs:include} locations resolve both in the build
 * directories and inside the JAR. Any problem becomes a {@link ContentException}.
 */
public final class XmlDocumentLoader {

    private static final String DISALLOW_DOCTYPE = "http://apache.org/xml/features/disallow-doctype-decl";
    private static final String ALLOWED_SCHEMA_PROTOCOLS = "file,jar";

    private final String contentRoot;
    private final String schemaRoot;

    /**
     * @param contentRoot classpath folder of the content files, such as "/content/"
     * @param schemaRoot  classpath folder of the schemas, such as "/content/schema/"
     */
    public XmlDocumentLoader(String contentRoot, String schemaRoot) {
        this.contentRoot = contentRoot;
        this.schemaRoot = schemaRoot;
    }

    /**
     * @param file   path of the content file, relative to the content root
     * @param schema name of the schema file, relative to the schema root
     * @return the parsed and validated document
     * @throws ContentException if the file or the schema is missing or invalid
     */
    public Document load(String file, String schema) {
        DocumentBuilder builder = validatingBuilder(loadSchema(schema));
        builder.setErrorHandler(new FailingErrorHandler());
        try (InputStream input = XmlDocumentLoader.class.getResourceAsStream(contentRoot + file)) {
            if (input == null) {
                throw new ContentException("Content file not found: " + file);
            }
            return builder.parse(input);
        } catch (SAXParseException error) {
            throw new ContentException(describe(file, error), error);
        } catch (SAXException | IOException error) {
            throw new ContentException("Cannot read " + file + ": " + error.getMessage(), error);
        }
    }

    private Schema loadSchema(String schema) {
        URL location = XmlDocumentLoader.class.getResource(schemaRoot + schema);
        if (location == null) {
            throw new ContentException("Schema not found: " + schema);
        }
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, ALLOWED_SCHEMA_PROTOCOLS);
            return factory.newSchema(location);
        } catch (SAXException error) {
            throw new ContentException("Invalid schema " + schema + ": " + error.getMessage(), error);
        }
    }

    private static DocumentBuilder validatingBuilder(Schema schema) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setSchema(schema);
            factory.setFeature(DISALLOW_DOCTYPE, true);
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);
            return factory.newDocumentBuilder();
        } catch (ParserConfigurationException error) {
            throw new IllegalStateException("XML parser not configurable", error);
        }
    }

    private static String describe(String file, SAXParseException error) {
        return "Invalid content in " + file
                + " (line " + error.getLineNumber() + ", column " + error.getColumnNumber() + "): "
                + error.getMessage();
    }

    /**
     * Turns every validation error into an exception: the default handler only prints it and
     * lets parsing go on. Warnings do not make content invalid, so they are ignored.
     */
    private static final class FailingErrorHandler implements ErrorHandler {

        @Override
        public void warning(SAXParseException exception) {
        }

        @Override
        public void error(SAXParseException exception) throws SAXException {
            throw exception;
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
            throw exception;
        }
    }
}
