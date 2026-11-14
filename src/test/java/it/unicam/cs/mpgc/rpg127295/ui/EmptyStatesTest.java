package it.unicam.cs.mpgc.rpg127295.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import it.unicam.cs.mpgc.rpg127295.engine.game.Reward;
import org.junit.jupiter.api.Test;

class EmptyStatesTest {

    @Test
    void anEmptyInventoryTellsHowToGetItemsWithTheGradeOfTheRules() {
        assertEquals("Lo zaino è vuoto. Gli oggetti si ottengono superando un esame con almeno 24.",
                EmptyStates.inventory(TestGames.guide()));
    }

    @Test
    void withoutItemsAsRewardItOnlySaysTheInventoryIsEmpty() {
        GameGuide guide = TestGames.guide();
        GameGuide noItems = new GameGuide(guide.session(), guide.profiles(), guide.starterKit(),
                guide.battleRules(), guide.strategies(), guide.abilities(), guide.items(), guide.progression(),
                (exam, grade) -> Reward.none(), guide.grades());

        assertEquals("Lo zaino è vuoto.", EmptyStates.inventory(noItems));
    }

    @Test
    void noSavedGamesSaysHowAGameIsSaved() {
        assertEquals("Nessuna partita salvata. Inizia una nuova partita: si salva da sola alla fine di ogni esame.",
                EmptyStates.savedGames());
    }
}
