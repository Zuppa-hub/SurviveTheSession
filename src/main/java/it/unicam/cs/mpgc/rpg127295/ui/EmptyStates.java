package it.unicam.cs.mpgc.rpg127295.ui;

import java.util.Objects;

/** What an empty list says, so that the player knows why it is empty and what to do. */
public final class EmptyStates {

    private EmptyStates() {
    }

    /** The grade that gives items is asked to the rules, not written here. */
    public static String inventory(GameGuide guide) {
        return "Lo zaino è vuoto." + Objects.requireNonNull(guide, "Guide must not be null").lowestGradeWithAnItem()
                .map(grade -> " Gli oggetti si ottengono superando un esame con almeno " + grade.label() + ".")
                .orElse("");
    }

    public static String savedGames() {
        return "Nessuna partita salvata. Inizia una nuova partita: si salva da sola alla fine di ogni esame.";
    }
}
