package it.unicam.cs.mpgc.rpg127295.persistence.content;

import it.unicam.cs.mpgc.rpg127295.engine.repository.ExamCatalog;
import it.unicam.cs.mpgc.rpg127295.model.exam.Exam;
import it.unicam.cs.mpgc.rpg127295.model.exam.ExamType;
import it.unicam.cs.mpgc.rpg127295.model.question.Difficulty;
import it.unicam.cs.mpgc.rpg127295.model.question.Subject;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.w3c.dom.Element;

/**
 * The subjects and exams read from {@code exams.xml}, validated by {@code exams.xsd}. Immutable.
 * The schema guarantees that every exam refers to an existing subject.
 */
public final class XmlExamCatalog implements ExamCatalog {

    private static final String FILE = "exams.xml";
    private static final String SCHEMA = "exams.xsd";

    private final CatalogIndex<Exam> index;

    private XmlExamCatalog(CatalogIndex<Exam> index) {
        this.index = index;
    }

    /** @throws ContentException if the file is missing or invalid */
    public static XmlExamCatalog load(XmlDocumentLoader loader) {
        XmlReader reader = new XmlReader();
        Element root = loader.load(FILE, SCHEMA).getDocumentElement();
        Map<String, Subject> subjects = subjectsById(root, reader);
        List<Exam> exams = reader.elements(root, "exam").stream()
                .map(element -> toExam(element, subjects))
                .toList();
        return new XmlExamCatalog(CatalogIndex.of(exams, Exam::code, FILE));
    }

    @Override
    public List<Exam> all() {
        return index.all();
    }

    @Override
    public Optional<Exam> findByCode(String code) {
        return index.find(code);
    }

    private static Map<String, Subject> subjectsById(Element root, XmlReader reader) {
        return reader.elements(root, "subject").stream()
                .map(element -> toSubject(element, reader))
                .collect(Collectors.toUnmodifiableMap(Subject::id, Function.identity()));
    }

    private static Subject toSubject(Element element, XmlReader reader) {
        String id = element.getAttribute("id");
        return ContentException.building(FILE + ", subject " + id,
                () -> new Subject(id, reader.text(element, "name")));
    }

    private static Exam toExam(Element element, Map<String, Subject> subjects) {
        String code = element.getAttribute("code");
        Subject subject = subjects.get(element.getAttribute("subject"));
        if (subject == null) {
            throw new ContentException("Invalid content in " + FILE + ", exam " + code + ": unknown subject");
        }
        return ContentException.building(FILE + ", exam " + code, () -> new Exam(
                code,
                subject,
                ExamType.valueOf(element.getAttribute("type")),
                Integer.parseInt(element.getAttribute("credits")),
                Difficulty.valueOf(element.getAttribute("difficulty"))));
    }
}
