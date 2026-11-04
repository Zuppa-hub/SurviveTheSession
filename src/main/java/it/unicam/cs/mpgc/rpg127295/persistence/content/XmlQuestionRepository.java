package it.unicam.cs.mpgc.rpg127295.persistence.content;

import it.unicam.cs.mpgc.rpg127295.engine.repository.QuestionRepository;
import it.unicam.cs.mpgc.rpg127295.model.question.Difficulty;
import it.unicam.cs.mpgc.rpg127295.model.question.Question;
import it.unicam.cs.mpgc.rpg127295.model.question.QuestionDetails;
import it.unicam.cs.mpgc.rpg127295.model.question.Subject;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.w3c.dom.Element;

/**
 * The questions read from {@code questions/<subject id in lower case>.xml}, one file per
 * subject, validated by {@code questions.xsd}. Immutable.
 * <p>
 * Each question element is turned into a question by the parser registered for its element
 * name, so this class never switches on the kind of question. Question ids must be unique
 * across all subjects: two questions with the same id are equal, so a {@link HashSet}
 * detects duplicates.
 */
public final class XmlQuestionRepository implements QuestionRepository {

    private static final String SCHEMA = "questions.xsd";

    private final Map<Subject, List<Question>> questionsBySubject;

    private XmlQuestionRepository(Map<Subject, List<Question>> questionsBySubject) {
        this.questionsBySubject = Map.copyOf(questionsBySubject);
    }

    /**
     * @param parsers  the parser of each question element, by element name
     * @param subjects the subjects whose question files are loaded
     * @throws ContentException if a file is missing or invalid, or a question id is repeated
     */
    public static XmlQuestionRepository load(XmlDocumentLoader loader, Map<String, QuestionParser> parsers,
                                             Collection<Subject> subjects) {
        SubjectFileReader fileReader = new SubjectFileReader(loader, Map.copyOf(parsers));
        Map<Subject, List<Question>> questions = new HashMap<>();
        subjects.forEach(subject -> questions.put(subject, fileReader.read(subject)));
        return new XmlQuestionRepository(questions);
    }

    @Override
    public List<Question> findBySubject(Subject subject) {
        return questionsBySubject.getOrDefault(subject, List.of());
    }

    /** Reads the question file of each subject, remembering the ids already seen. */
    private static final class SubjectFileReader {

        private final XmlDocumentLoader loader;
        private final Map<String, QuestionParser> parsers;
        private final XmlReader reader = new XmlReader();
        private final Set<Question> seen = new HashSet<>();

        SubjectFileReader(XmlDocumentLoader loader, Map<String, QuestionParser> parsers) {
            this.loader = loader;
            this.parsers = parsers;
        }

        List<Question> read(Subject subject) {
            String file = fileOf(subject);
            Element root = loader.load(file, SCHEMA).getDocumentElement();
            requireSubject(root, subject, file);
            return reader.elements(root, "*").stream()
                    .map(element -> toQuestion(element, subject, file))
                    .toList();
        }

        private Question toQuestion(Element element, Subject subject, String file) {
            QuestionParser parser = parserFor(element, file);
            String location = file + ", question " + element.getAttribute("id");
            Question question = ContentException.building(location,
                    () -> parser.parse(element, details(element, subject), reader));
            if (!seen.add(question)) {
                throw new ContentException("Invalid content in " + location + ": id already used in another subject");
            }
            return question;
        }

        private QuestionParser parserFor(Element element, String file) {
            QuestionParser parser = parsers.get(element.getTagName());
            if (parser == null) {
                throw new ContentException(
                        "Invalid content in " + file + ": no parser for <" + element.getTagName() + ">");
            }
            return parser;
        }

        private QuestionDetails details(Element element, Subject subject) {
            return new QuestionDetails(
                    element.getAttribute("id"),
                    subject,
                    Difficulty.valueOf(element.getAttribute("difficulty")),
                    reader.text(element, "prompt"),
                    reader.optionalText(element, "hint"));
        }

        private static void requireSubject(Element root, Subject subject, String file) {
            String declared = root.getAttribute("subject");
            if (!declared.equals(subject.id())) {
                throw new ContentException("Invalid content in " + file + ": declares subject " + declared
                        + " but belongs to " + subject.id());
            }
        }

        private static String fileOf(Subject subject) {
            return "questions/" + subject.id().toLowerCase(Locale.ROOT) + ".xml";
        }
    }
}
