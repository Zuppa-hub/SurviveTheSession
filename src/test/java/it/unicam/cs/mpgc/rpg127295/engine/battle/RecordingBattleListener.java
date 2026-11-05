package it.unicam.cs.mpgc.rpg127295.engine.battle;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

/** Listener for the tests: records every notification. */
final class RecordingBattleListener implements BattleListener {

    final List<PresentedQuestion> presented = new ArrayList<>();
    final List<OptionalInt> seconds = new ArrayList<>();
    final List<Boolean> evaluations = new ArrayList<>();
    final List<Integer> ticks = new ArrayList<>();
    final List<BattleOutcome> outcomes = new ArrayList<>();

    @Override
    public void onQuestionPresented(PresentedQuestion question, OptionalInt secondsToAnswer) {
        presented.add(question);
        seconds.add(secondsToAnswer);
    }

    @Override
    public void onAnswerEvaluated(boolean correct, BattleState state) {
        evaluations.add(correct);
    }

    @Override
    public void onTimeTick(int secondsLeft) {
        ticks.add(secondsLeft);
    }

    @Override
    public void onBattleEnded(BattleOutcome outcome) {
        outcomes.add(outcome);
    }
}
