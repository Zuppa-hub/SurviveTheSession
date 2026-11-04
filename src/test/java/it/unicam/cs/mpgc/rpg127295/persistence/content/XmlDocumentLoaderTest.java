package it.unicam.cs.mpgc.rpg127295.persistence.content;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

class XmlDocumentLoaderTest {

    private final XmlDocumentLoader loader =
            new XmlDocumentLoader("/content-test/loader/data/", "/content-test/loader/schema/");

    private ContentException failureLoading(String file, String schema) {
        return assertThrows(ContentException.class, () -> loader.load(file, schema));
    }

    @Test
    void loadsAValidDocument() {
        Document document = loader.load("valid.xml", "note.xsd");

        assertEquals("note", document.getDocumentElement().getTagName());
    }

    @Test
    void appliesTypesFromIncludedSchemas() {
        ContentException error = failureLoading("blank-text.xml", "note.xsd");

        assertAll(
                () -> assertTrue(error.getMessage().contains("blank-text.xml"), error.getMessage()),
                () -> assertTrue(error.getMessage().contains("line 3"), error.getMessage()));
    }

    @Test
    void rejectsMalformedXml() {
        assertTrue(failureLoading("malformed.xml", "note.xsd").getMessage().contains("malformed.xml"));
    }

    @Test
    void rejectsDoctypeDeclarations() {
        assertTrue(failureLoading("doctype.xml", "note.xsd").getMessage().contains("doctype.xml"));
    }

    @Test
    void reportsAMissingFile() {
        assertTrue(failureLoading("missing.xml", "note.xsd").getMessage().contains("missing.xml"));
    }

    @Test
    void reportsAMissingSchema() {
        assertTrue(failureLoading("valid.xml", "missing.xsd").getMessage().contains("missing.xsd"));
    }

    @Test
    void reportsAnInvalidSchema() {
        assertTrue(failureLoading("valid.xml", "broken.xsd").getMessage().contains("broken.xsd"));
    }
}
