package it.unicam.cs.mpgc.rpg127295.engine.timer;

import java.util.ArrayList;
import java.util.List;

/** Listener for the tests: records ticks and expiries. */
final class RecordingTimerListener implements TimerListener {

    final List<Integer> ticks = new ArrayList<>();
    int expiries;

    @Override
    public void onTick(int secondsLeft) {
        ticks.add(secondsLeft);
    }

    @Override
    public void onExpired() {
        expiries++;
    }
}
