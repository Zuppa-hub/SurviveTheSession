package it.unicam.cs.mpgc.rpg127295.engine.battle;

import static it.unicam.cs.mpgc.rpg127295.engine.TestQuestions.choice;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.unicam.cs.mpgc.rpg127295.engine.FakeRandom;
import it.unicam.cs.mpgc.rpg127295.engine.TestQuestions;
import it.unicam.cs.mpgc.rpg127295.engine.grading.GradeCalculator;
import it.unicam.cs.mpgc.rpg127295.engine.repository.QuestionRepository;
import it.unicam.cs.mpgc.rpg127295.engine.battle.ExamStrategy;
import it.unicam.cs.mpgc.rpg127295.engine.strategy.WrittenExamStrategy;
import it.unicam.cs.mpgc.rpg127295.engine.timer.ManualQuestionTimer;
import it.unicam.cs.mpgc.rpg127295.model.exam.ExamType;
import it.unicam.cs.mpgc.rpg127295.model.exam.Grade;
import it.unicam.cs.mpgc.rpg127295.model.question.Subject;
import it.unicam.cs.mpgc.rpg127295.model.student.Stats;
import it.unicam.cs.mpgc.rpg127295.model.student.Student;
import it.unicam.cs.mpgc.rpg127295.model.student.StudentProfile;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ExamBattleFactoryTest {

    private static final GradeCalculator CALCULATOR = performance -> Optional.of(Grade.of(25));
    private static final QuestionRepository QUESTIONS = subject -> List.of(choice("Q-1"));

    private final ManualQuestionTimer timer = new ManualQuestionTimer();
    private final Student student = new Student("Andrea",
            new StudentProfile("nerd", "Secchione", "Studia sempre", Stats.initial(5, 6, 4)));

    private ExamBattleFactory factory(Map<ExamType, ExamStrategy> strategies, QuestionRepository questions) {
        return new ExamBattleFactory(strategies, questions, FixedBattleRules.standard(), CALCULATOR, timer,
                FakeRandom.first());
    }

    @Test
    void usesTheQuestionsOfTheSubjectOfTheExam() {
        List<Subject> requested = new ArrayList<>();
        QuestionRepository recording = subject -> {
            requested.add(subject);
            return List.of(choice("Q-1"));
        };

        factory(ExamBattleTest.strategies(), recording).create(student, ExamBattleTest.exam(ExamType.ORAL));

        assertEquals(List.of(TestQuestions.SUBJECT), requested);
    }

    @Test
    void usesTheStrategyOfTheExamType() {
        Map<ExamType, ExamStrategy> strategies = new EnumMap<>(ExamBattleTest.strategies());
        strategies.put(ExamType.ORAL, new WrittenExamStrategy());

        factory(strategies, QUESTIONS).create(student, ExamBattleTest.exam(ExamType.ORAL)).begin();

        assertEquals(30, timer.secondsLeft());
    }

    @Test
    void rejectsStrategiesThatDoNotCoverEveryExamType() {
        Map<ExamType, ExamStrategy> withoutProject = new EnumMap<>(ExamBattleTest.strategies());
        withoutProject.remove(ExamType.PROJECT);

        assertThrows(IllegalArgumentException.class, () -> factory(withoutProject, QUESTIONS));
    }

    @Test
    void isNotAffectedByChangesToTheOriginalMap() {
        Map<ExamType, ExamStrategy> strategies = new EnumMap<>(ExamBattleTest.strategies());
        ExamBattleFactory factory = factory(strategies, QUESTIONS);

        strategies.put(ExamType.ORAL, new WrittenExamStrategy());
        factory.create(student, ExamBattleTest.exam(ExamType.ORAL)).begin();

        assertEquals(0, timer.secondsLeft());
    }

    @Test
    void rejectsNullArguments() {
        ExamBattleFactory factory = factory(ExamBattleTest.strategies(), QUESTIONS);
        Map<ExamType, ExamStrategy> strategies = ExamBattleTest.strategies();
        FixedBattleRules rules = FixedBattleRules.standard();
        FakeRandom random = FakeRandom.first();

        assertAll(
                () -> assertThrows(NullPointerException.class,
                        () -> new ExamBattleFactory(null, QUESTIONS, rules, CALCULATOR, timer, random)),
                () -> assertThrows(NullPointerException.class,
                        () -> new ExamBattleFactory(strategies, null, rules, CALCULATOR, timer, random)),
                () -> assertThrows(NullPointerException.class,
                        () -> new ExamBattleFactory(strategies, QUESTIONS, null, CALCULATOR, timer, random)),
                () -> assertThrows(NullPointerException.class,
                        () -> new ExamBattleFactory(strategies, QUESTIONS, rules, null, timer, random)),
                () -> assertThrows(NullPointerException.class,
                        () -> new ExamBattleFactory(strategies, QUESTIONS, rules, CALCULATOR, null, random)),
                () -> assertThrows(NullPointerException.class,
                        () -> new ExamBattleFactory(strategies, QUESTIONS, rules, CALCULATOR, timer, null)),
                () -> assertThrows(NullPointerException.class,
                        () -> factory.create(null, ExamBattleTest.exam(ExamType.ORAL))),
                () -> assertThrows(NullPointerException.class, () -> factory.create(student, null)));
    }
}
