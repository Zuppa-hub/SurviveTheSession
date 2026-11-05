package it.unicam.cs.mpgc.rpg127295.engine.effect;

import static it.unicam.cs.mpgc.rpg127295.engine.TestQuestions.choice;

import it.unicam.cs.mpgc.rpg127295.engine.FakeRandom;
import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleState;
import it.unicam.cs.mpgc.rpg127295.engine.battle.PresentedQuestion;
import it.unicam.cs.mpgc.rpg127295.engine.grading.Performance;
import it.unicam.cs.mpgc.rpg127295.model.question.Question;
import it.unicam.cs.mpgc.rpg127295.model.student.Stats;
import java.util.Optional;

/** Battle states for the effect tests: only the part each effect looks at changes. */
final class States {

    static final Stats FULL = Stats.initial(5, 10, 4);

    private States() {
    }

    static BattleState showing(PresentedQuestion question) {
        return state(Optional.of(question), false, FULL, false);
    }

    static BattleState showing(Question question) {
        return showing(PresentedQuestion.of(question, FakeRandom.first()));
    }

    static BattleState timed(boolean timed) {
        return state(Optional.of(PresentedQuestion.of(choice("Q-1"), FakeRandom.first())), timed, FULL, false);
    }

    static BattleState withStats(Stats stats) {
        return state(Optional.empty(), false, stats, false);
    }

    static BattleState doubled(boolean doubled) {
        return state(Optional.empty(), false, FULL, doubled);
    }

    private static BattleState state(Optional<PresentedQuestion> question, boolean timed, Stats stats,
                                     boolean doubled) {
        return new BattleState(10, 10, stats, Performance.none(), question, Optional.empty(), timed, doubled);
    }
}
