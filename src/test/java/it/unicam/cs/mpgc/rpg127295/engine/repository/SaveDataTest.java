package it.unicam.cs.mpgc.rpg127295.engine.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class SaveDataTest {

    static final SaveData.StatsData STATS = new SaveData.StatsData(6, 4, 8, 3, 5);

    static SaveData sample() {
        Map<String, Integer> transcript = new LinkedHashMap<>();
        transcript.put("RETI1", 31);
        transcript.put("PROG1", 27);
        return new SaveData("Andrea", "nerd", STATS, 2, 50, Map.of("coffee", 2), transcript);
    }

    @Test
    void keepsTheTranscriptInRecordingOrder() {
        assertEquals(List.of("RETI1", "PROG1"), List.copyOf(sample().transcript().keySet()));
    }

    @Test
    void isNotAffectedByChangesToTheOriginalMaps() {
        Map<String, Integer> inventory = new LinkedHashMap<>(Map.of("coffee", 1));
        SaveData data = new SaveData("Andrea", "nerd", STATS, 1, 0, inventory, Map.of());

        inventory.put("notes", 3);

        assertAll(
                () -> assertEquals(Map.of("coffee", 1), data.inventory()),
                () -> assertThrows(UnsupportedOperationException.class, () -> data.inventory().clear()),
                () -> assertThrows(UnsupportedOperationException.class, () -> data.transcript().clear()));
    }

    @Test
    void rejectsMissingParts() {
        Map<String, Integer> none = Map.of();

        assertAll(
                () -> assertThrows(NullPointerException.class,
                        () -> new SaveData(null, "nerd", STATS, 1, 0, none, none)),
                () -> assertThrows(NullPointerException.class,
                        () -> new SaveData("Andrea", null, STATS, 1, 0, none, none)),
                () -> assertThrows(NullPointerException.class,
                        () -> new SaveData("Andrea", "nerd", null, 1, 0, none, none)),
                () -> assertThrows(NullPointerException.class,
                        () -> new SaveData("Andrea", "nerd", STATS, 1, 0, null, none)),
                () -> assertThrows(NullPointerException.class,
                        () -> new SaveData("Andrea", "nerd", STATS, 1, 0, none, null)));
    }
}
