package it.unicam.cs.mpgc.rpg127295.engine;

import java.util.random.RandomGenerator;

/**
 * Deterministic random generator for the tests: every bounded choice returns the first or
 * the last index, so the tests do not depend on chance.
 */
public final class FakeRandom implements RandomGenerator {

    private final boolean last;

    private FakeRandom(boolean last) {
        this.last = last;
    }

    /** @return a generator that always picks index 0 */
    public static FakeRandom first() {
        return new FakeRandom(false);
    }

    /** @return a generator that always picks the last index */
    public static FakeRandom last() {
        return new FakeRandom(true);
    }

    @Override
    public int nextInt(int bound) {
        return last ? bound - 1 : 0;
    }

    @Override
    public long nextLong() {
        return 0;
    }
}
