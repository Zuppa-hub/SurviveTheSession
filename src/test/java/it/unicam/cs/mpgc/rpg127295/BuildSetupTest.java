package it.unicam.cs.mpgc.rpg127295;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

class BuildSetupTest {

    private record Sample(String name, int value) {
    }

    @Test
    void gsonIsAvailableAndRoundTripsRecords() {
        Gson gson = new Gson();
        Sample original = new Sample("coffee", 2);

        Sample restored = gson.fromJson(gson.toJson(original), Sample.class);

        assertEquals(original, restored);
    }
}
