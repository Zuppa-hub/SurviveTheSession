package it.unicam.cs.mpgc.rpg127295.ui;

import static it.unicam.cs.mpgc.rpg127295.engine.TestQuestions.choice;

import it.unicam.cs.mpgc.rpg127295.engine.FakeRandom;
import it.unicam.cs.mpgc.rpg127295.engine.battle.Ability;
import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleEffects;
import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleServices;
import it.unicam.cs.mpgc.rpg127295.engine.battle.ExamBattleFactory;
import it.unicam.cs.mpgc.rpg127295.engine.battle.ExamStrategy;
import it.unicam.cs.mpgc.rpg127295.engine.battle.StandardBattleRules;
import it.unicam.cs.mpgc.rpg127295.engine.effect.DoubleDamageEffect;
import it.unicam.cs.mpgc.rpg127295.engine.effect.ExtraTimeEffect;
import it.unicam.cs.mpgc.rpg127295.engine.effect.FiftyFiftyEffect;
import it.unicam.cs.mpgc.rpg127295.engine.effect.HintEffect;
import it.unicam.cs.mpgc.rpg127295.engine.game.Game;
import it.unicam.cs.mpgc.rpg127295.engine.game.Reward;
import it.unicam.cs.mpgc.rpg127295.engine.game.StandardProgressionRules;
import it.unicam.cs.mpgc.rpg127295.engine.strategy.OralExamStrategy;
import it.unicam.cs.mpgc.rpg127295.engine.strategy.ProjectExamStrategy;
import it.unicam.cs.mpgc.rpg127295.engine.strategy.WrittenExamStrategy;
import it.unicam.cs.mpgc.rpg127295.engine.timer.ManualQuestionTimer;
import it.unicam.cs.mpgc.rpg127295.model.exam.Exam;
import it.unicam.cs.mpgc.rpg127295.model.exam.ExamSession;
import it.unicam.cs.mpgc.rpg127295.model.exam.ExamType;
import it.unicam.cs.mpgc.rpg127295.model.exam.Grade;
import it.unicam.cs.mpgc.rpg127295.model.item.Item;
import it.unicam.cs.mpgc.rpg127295.model.student.Stats;
import it.unicam.cs.mpgc.rpg127295.model.student.Student;
import it.unicam.cs.mpgc.rpg127295.model.student.StudentProfile;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Games for the tests of the interface support classes: one choice question per exam, every
 * passed exam graded 28, no reward, a manual timer and two items and two abilities with effects.
 */
final class TestGames {

    static final Item COFFEE = new Item("coffee", "Caffè", "Più tempo per rispondere");
    static final Item NOTES = new Item("notes", "Appunti", "Elimina due opzioni sbagliate");
    static final Ability QUICK_REVIEW = new Ability("Ripasso lampo", 2, new HintEffect());
    static final Ability STROKE_OF_GENIUS = new Ability("Colpo di genio", 3, new DoubleDamageEffect());

    private TestGames() {
    }

    /** @return a game of the student "Andrea", with concentration 4, facing the exams in order */
    static Game game(Exam... exams) {
        Map<ExamType, ExamStrategy> strategies = Map.of(
                ExamType.WRITTEN, new WrittenExamStrategy(),
                ExamType.ORAL, new OralExamStrategy(),
                ExamType.PROJECT, new ProjectExamStrategy());
        BattleEffects effects = new BattleEffects(
                Map.of(COFFEE, new ExtraTimeEffect(), NOTES, new FiftyFiftyEffect()),
                List.of(QUICK_REVIEW, STROKE_OF_GENIUS));
        BattleServices services = new BattleServices(new StandardBattleRules(),
                performance -> Optional.of(Grade.of(28)), new ManualQuestionTimer(), FakeRandom.first(), effects);
        ExamBattleFactory battles = new ExamBattleFactory(strategies, subject -> List.of(choice("Q-1")), services);
        Student student = new Student("Andrea",
                new StudentProfile("nerd", "Secchione", "Studia sempre", Stats.initial(5, 6, 4)));
        return new Game(student, new ExamSession(List.of(exams)), battles, new StandardProgressionRules(),
                (exam, grade) -> Reward.none());
    }
}
