package it.unicam.cs.mpgc.rpg127295.app;

import static it.unicam.cs.mpgc.rpg127295.engine.TestQuestions.CORRECT;
import static it.unicam.cs.mpgc.rpg127295.engine.TestQuestions.WRONG;
import static it.unicam.cs.mpgc.rpg127295.engine.TestQuestions.choice;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleListener;
import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleResult;
import it.unicam.cs.mpgc.rpg127295.engine.battle.ExamBattle;
import it.unicam.cs.mpgc.rpg127295.engine.game.Game;
import it.unicam.cs.mpgc.rpg127295.engine.timer.ManualScheduler;
import it.unicam.cs.mpgc.rpg127295.model.exam.Exam;
import it.unicam.cs.mpgc.rpg127295.model.question.Question;
import it.unicam.cs.mpgc.rpg127295.model.student.StudentProfile;
import it.unicam.cs.mpgc.rpg127295.persistence.content.XmlDocumentLoader;
import java.util.List;
import java.util.SplittableRandom;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Balance of the shipped content and rules (issue #12), checked by simulating players who answer
 * a fixed share of the questions correctly. Everything is real (exams, profiles, items, rules,
 * rewards, progression, number and difficulty of the questions) except the text of the
 * questions, replaced by questions with a known correct answer. No real time (manual timer) and
 * no randomness (fixed seed), so the outcome is always the same.
 * <ul>
 *   <li>With 70% correct answers, mistakes first (the unlucky order), every profile passes every
 *       exam, also as the first exam of the session, and graduates.</li>
 *   <li>With 40% correct answers, right answers first (the lucky order), every profile fails
 *       every exam, the final boss included.</li>
 * </ul>
 */
class BalanceTest {

    private static final long SEED = 127_295;

    private static GameContent content;

    @BeforeAll
    static void loadShippedContentWithKnownAnswers() {
        GameContent shipped = GameContent.load(
                new XmlDocumentLoader(GameContent.CONTENT_ROOT, GameContent.SCHEMA_ROOT));
        content = new GameContent(shipped.exams(), shipped.profiles(), shipped.items(),
                subject -> shipped.questions().findBySubject(subject).stream()
                        .map(BalanceTest::withKnownAnswer)
                        .toList());
    }

    private static Question withKnownAnswer(Question question) {
        return choice(question.id(), question.difficulty());
    }

    static Stream<StudentProfile> profiles() {
        return GameContent.load(new XmlDocumentLoader(GameContent.CONTENT_ROOT, GameContent.SCHEMA_ROOT))
                .profiles().all().stream();
    }

    @ParameterizedTest
    @MethodSource("profiles")
    void withSeventyPercentEveryExamCanBePassedAsTheFirstOne(StudentProfile profile) {
        for (Exam exam : nonBossExams()) {
            assertEquals(BattleResult.PASSED, take(newGame(profile), exam, Answers.mistakesFirst(70)),
                    profile.id() + " " + exam.code());
        }
    }

    @ParameterizedTest
    @MethodSource("profiles")
    void withSeventyPercentTheWholeSessionIsPassedAndTheStudentGraduates(StudentProfile profile) {
        Game game = newGame(profile);
        for (Exam exam : game.session().exams()) {
            assertEquals(BattleResult.PASSED, take(game, exam, Answers.mistakesFirst(70)),
                    profile.id() + " " + exam.code());
        }
        assertTrue(game.canGraduate(), profile.id());
    }

    @ParameterizedTest
    @MethodSource("profiles")
    void withFortyPercentEveryExamIsFailed(StudentProfile profile) {
        List<Exam> exams = content.exams().all();
        for (int failing = 0; failing < exams.size(); failing++) {
            Game game = newGame(profile);
            exams.subList(0, failing).forEach(passed -> take(game, passed, Answers.mistakesFirst(70)));
            Exam exam = exams.get(failing);
            assertNotEquals(BattleResult.PASSED, take(game, exam, Answers.rightFirst(40)),
                    profile.id() + " " + exam.code());
        }
    }

    private static List<Exam> nonBossExams() {
        List<Exam> exams = content.exams().all();
        return exams.subList(0, exams.size() - 1);
    }

    private static Game newGame(StudentProfile profile) {
        GameAssembly games = new GameAssembly(content, new ManualScheduler(), Runnable::run,
                new SplittableRandom(SEED), new InMemorySaveGameRepository(), failure -> {
                    throw new AssertionError(failure);
                });
        return games.newGame("Simulato", profile.id(), "simulato");
    }

    private static BattleResult take(Game game, Exam exam, Answers answers) {
        ExamBattle battle = game.startExam(exam, new BattleListener() {
        });
        while (!battle.isOver()) {
            battle.answer(answers.next() ? CORRECT : WRONG);
        }
        return battle.outcome().orElseThrow().result();
    }

    /**
     * A player who answers exactly {@code percent}% of the questions correctly, spread evenly:
     * after n answers the correct ones are n x percent / 100, rounded down (mistakes come first)
     * or up (right answers come first).
     */
    private static final class Answers {

        private static final int HUNDRED = 100;

        private final int percent;
        private final boolean roundUp;
        private int given;

        private Answers(int percent, boolean roundUp) {
            this.percent = percent;
            this.roundUp = roundUp;
        }

        static Answers mistakesFirst(int percent) {
            return new Answers(percent, false);
        }

        static Answers rightFirst(int percent) {
            return new Answers(percent, true);
        }

        boolean next() {
            given++;
            return correctAfter(given) > correctAfter(given - 1);
        }

        private int correctAfter(int answers) {
            int scaled = answers * percent;
            return roundUp ? (scaled + HUNDRED - 1) / HUNDRED : scaled / HUNDRED;
        }
    }
}
