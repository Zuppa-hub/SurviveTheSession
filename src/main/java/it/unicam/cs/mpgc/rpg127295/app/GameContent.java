package it.unicam.cs.mpgc.rpg127295.app;

import it.unicam.cs.mpgc.rpg127295.engine.repository.ExamCatalog;
import it.unicam.cs.mpgc.rpg127295.engine.repository.ItemCatalog;
import it.unicam.cs.mpgc.rpg127295.engine.repository.ProfileCatalog;
import it.unicam.cs.mpgc.rpg127295.engine.repository.QuestionRepository;
import it.unicam.cs.mpgc.rpg127295.model.exam.Exam;
import it.unicam.cs.mpgc.rpg127295.model.question.Subject;
import it.unicam.cs.mpgc.rpg127295.persistence.content.CodeOutputParser;
import it.unicam.cs.mpgc.rpg127295.persistence.content.ContentException;
import it.unicam.cs.mpgc.rpg127295.persistence.content.FillInParser;
import it.unicam.cs.mpgc.rpg127295.persistence.content.MultipleChoiceParser;
import it.unicam.cs.mpgc.rpg127295.persistence.content.QuestionParser;
import it.unicam.cs.mpgc.rpg127295.persistence.content.QuestionParsers;
import it.unicam.cs.mpgc.rpg127295.persistence.content.TrueFalseParser;
import it.unicam.cs.mpgc.rpg127295.persistence.content.XmlDocumentLoader;
import it.unicam.cs.mpgc.rpg127295.persistence.content.XmlExamCatalog;
import it.unicam.cs.mpgc.rpg127295.persistence.content.XmlItemCatalog;
import it.unicam.cs.mpgc.rpg127295.persistence.content.XmlProfileCatalog;
import it.unicam.cs.mpgc.rpg127295.persistence.content.XmlQuestionRepository;
import java.util.List;
import java.util.Map;

/**
 * All the read-only content of the game, loaded and validated at startup.
 * <p>
 * Part of the composition root: it chooses the XML implementations and the parsers of the
 * questions, mapped to their elements through {@code @QuestionElement}.
 */
public record GameContent(ExamCatalog exams, ProfileCatalog profiles, ItemCatalog items,
                          QuestionRepository questions) {

    public static final String CONTENT_ROOT = "/content/";
    public static final String SCHEMA_ROOT = "/content/schema/";

    /** @throws ContentException if any content file is missing or invalid */
    public static GameContent load(XmlDocumentLoader loader) {
        ExamCatalog exams = XmlExamCatalog.load(loader);
        return new GameContent(
                exams,
                XmlProfileCatalog.load(loader),
                XmlItemCatalog.load(loader),
                XmlQuestionRepository.load(loader, questionParsers(), subjectsOf(exams)));
    }

    /** The element of each parser is read from its {@code @QuestionElement} annotation. */
    private static Map<String, QuestionParser> questionParsers() {
        return QuestionParsers.byElement(List.of(
                new MultipleChoiceParser(), new TrueFalseParser(), new CodeOutputParser(), new FillInParser()));
    }

    private static List<Subject> subjectsOf(ExamCatalog exams) {
        return exams.all().stream()
                .map(Exam::subject)
                .distinct()
                .toList();
    }
}
