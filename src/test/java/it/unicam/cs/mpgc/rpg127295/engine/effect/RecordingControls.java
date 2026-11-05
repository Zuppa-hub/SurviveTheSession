package it.unicam.cs.mpgc.rpg127295.engine.effect;

import it.unicam.cs.mpgc.rpg127295.engine.battle.BattleControls;
import java.util.ArrayList;
import java.util.List;

/** Controls for the tests: records the name of every call. */
final class RecordingControls implements BattleControls {

    final List<String> calls = new ArrayList<>();

    @Override
    public void addExtraTime() {
        calls.add("addExtraTime");
    }

    @Override
    public void removeTwoWrongOptions() {
        calls.add("removeTwoWrongOptions");
    }

    @Override
    public void showHint() {
        calls.add("showHint");
    }

    @Override
    public void restoreEnergy() {
        calls.add("restoreEnergy");
    }

    @Override
    public void doubleNextCorrectAnswer() {
        calls.add("doubleNextCorrectAnswer");
    }
}
