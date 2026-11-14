package it.unicam.cs.mpgc.rpg127295.engine.battle;

import static it.unicam.cs.mpgc.rpg127295.model.Preconditions.requireNonBlank;
import static it.unicam.cs.mpgc.rpg127295.model.Preconditions.requirePositive;

import java.util.Objects;

/**
 * An ability of the student: data only, its behaviour is the effect. A new ability is a new
 * record, not a new class.
 *
 * @param name              shown to the player; unique among the abilities of a battle
 * @param concentrationCost the concentration spent to use it; positive
 * @param effect            what it does
 */
public record Ability(String name, int concentrationCost, BattleEffect effect) {

    public Ability {
        Objects.requireNonNull(name, "Name must not be null");
        requireNonBlank(name, "Name");
        requirePositive(concentrationCost, "Concentration cost");
        Objects.requireNonNull(effect, "Effect must not be null");
    }
}
