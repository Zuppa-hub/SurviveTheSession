package it.unicam.cs.mpgc.rpg127295.ui;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Objects;

/** Derives the name of a save slot from the name the player types. */
public final class SlotNames {

    private static final String DEFAULT_SLOT = "partita";

    private SlotNames() {
    }

    /**
     * Lower case, accents removed, every other character that is not a letter, a digit or an
     * underscore turned into a single dash: "Àndrea Cazzàto" becomes "andrea-cazzato".
     *
     * @return a valid slot name; "partita" if nothing usable is left
     */
    public static String fromPlayerName(String name) {
        Objects.requireNonNull(name, "Name must not be null");
        String withoutAccents = Normalizer.normalize(name, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        String slot = withoutAccents.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9_]+", "-")
                .replaceAll("^-+|-+$", "");
        return slot.isEmpty() ? DEFAULT_SLOT : slot;
    }
}
