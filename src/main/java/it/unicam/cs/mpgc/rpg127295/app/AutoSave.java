package it.unicam.cs.mpgc.rpg127295.app;

import it.unicam.cs.mpgc.rpg127295.engine.game.ExamReport;
import it.unicam.cs.mpgc.rpg127295.engine.game.GameListener;
import it.unicam.cs.mpgc.rpg127295.engine.repository.SaveGameRepository;
import it.unicam.cs.mpgc.rpg127295.engine.repository.SaveMapper;
import it.unicam.cs.mpgc.rpg127295.model.student.Student;
import java.util.Objects;

/**
 * Saves the game after every exam. It is a {@link GameListener} registered by the application:
 * the game does not know that it is saved, nor where.
 */
final class AutoSave implements GameListener {

    private final SaveGameRepository saves;
    private final SaveMapper mapper;
    private final String slot;
    private final Student student;

    AutoSave(SaveGameRepository saves, SaveMapper mapper, String slot, Student student) {
        this.saves = Objects.requireNonNull(saves, "Save repository must not be null");
        this.mapper = Objects.requireNonNull(mapper, "Save mapper must not be null");
        this.slot = SaveGameRepository.requireValidSlot(slot);
        this.student = Objects.requireNonNull(student, "Student must not be null");
    }

    @Override
    public void onExamConcluded(ExamReport report) {
        saves.save(slot, mapper.toData(student));
    }
}
