package it.unicam.cs.mpgc.rpg127295.ui;

import it.unicam.cs.mpgc.rpg127295.engine.game.Game;
import it.unicam.cs.mpgc.rpg127295.engine.repository.SaveGameException;
import it.unicam.cs.mpgc.rpg127295.model.student.StudentProfile;
import java.util.List;
import java.util.Optional;

/**
 * What the user interface needs to start or resume a game. Defined here, where it is used, and
 * implemented by the composition root: the interface depends only on engine and model (DIP).
 */
public interface GameService {

    /** @return the profiles the player can choose, in content order */
    List<StudentProfile> profiles();

    /** @return the slots with a saved game, in alphabetical order */
    List<String> savedGames();

    /**
     * @return a new game, saved in the slot after every exam
     * @throws IllegalArgumentException if the profile is unknown or the slot name invalid
     */
    Game newGame(String name, String profileId, String slot);

    /**
     * @return the game saved in the slot, or empty if there is none
     * @throws SaveGameException if the saved game cannot be read
     */
    Optional<Game> loadGame(String slot);
}
