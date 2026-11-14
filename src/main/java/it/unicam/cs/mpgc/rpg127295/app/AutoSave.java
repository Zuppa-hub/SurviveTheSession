package it.unicam.cs.mpgc.rpg127295.app;

import it.unicam.cs.mpgc.rpg127295.engine.game.ExamReport;
import it.unicam.cs.mpgc.rpg127295.engine.game.GameListener;
import it.unicam.cs.mpgc.rpg127295.engine.repository.SaveGameException;
import it.unicam.cs.mpgc.rpg127295.engine.repository.SaveGameRepository;
import it.unicam.cs.mpgc.rpg127295.engine.repository.SaveMapper;
import it.unicam.cs.mpgc.rpg127295.model.student.Student;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Saves the game after every exam. It is a {@link GameListener} registered by the application:
 * the game does not know that it is saved, nor where.
 * <p>
 * A failed save does not stop the game: the {@link SaveGameException} goes to a callback, which
 * the user interface uses to show a warning, and the game goes on.
 */
final class AutoSave implements GameListener {

    private final SaveGameRepository saves;
    private final SaveMapper mapper;
    private final String slot;
    private final Student student;
    private final Consumer<SaveGameException> onFailure;

    AutoSave(SaveGameRepository saves, SaveMapper mapper, String slot, Student student,
             Consumer<SaveGameException> onFailure) {
        this.saves = Objects.requireNonNull(saves, "Save repository must not be null");
        this.mapper = Objects.requireNonNull(mapper, "Save mapper must not be null");
        this.slot = SaveGameRepository.requireValidSlot(slot);
        this.student = Objects.requireNonNull(student, "Student must not be null");
        this.onFailure = Objects.requireNonNull(onFailure, "Failure callback must not be null");
    }

    @Override
    public void onExamConcluded(ExamReport report) {
        try {
            saves.save(slot, mapper.toData(student));
        } catch (SaveGameException failure) {
            onFailure.accept(failure);
        }
    }
}
