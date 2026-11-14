package it.unicam.cs.mpgc.rpg127295.engine.repository;

import it.unicam.cs.mpgc.rpg127295.model.exam.Exam;
import it.unicam.cs.mpgc.rpg127295.model.exam.Grade;
import it.unicam.cs.mpgc.rpg127295.model.item.Item;
import it.unicam.cs.mpgc.rpg127295.model.student.Level;
import it.unicam.cs.mpgc.rpg127295.model.student.Stats;
import it.unicam.cs.mpgc.rpg127295.model.student.Student;
import it.unicam.cs.mpgc.rpg127295.model.student.StudentProfile;
import it.unicam.cs.mpgc.rpg127295.model.transcript.TranscriptEntry;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Converts a student to {@link SaveData} and back. Ids and codes are resolved with the
 * catalogs, so a saved game refers to the content instead of copying it. Restoring goes through
 * the domain constructors, which check every value.
 */
public final class SaveMapper {

    private final ProfileCatalog profiles;
    private final ItemCatalog items;
    private final ExamCatalog exams;

    public SaveMapper(ProfileCatalog profiles, ItemCatalog items, ExamCatalog exams) {
        this.profiles = Objects.requireNonNull(profiles, "Profile catalog must not be null");
        this.items = Objects.requireNonNull(items, "Item catalog must not be null");
        this.exams = Objects.requireNonNull(exams, "Exam catalog must not be null");
    }

    public SaveData toData(Student student) {
        Objects.requireNonNull(student, "Student must not be null");
        Stats stats = student.stats();
        return new SaveData(student.name(), student.profile().id(),
                new SaveData.StatsData(stats.knowledge(), stats.energy(), stats.maxEnergy(),
                        stats.concentration(), stats.maxConcentration()),
                student.level().number(), student.level().experience(),
                inventoryOf(student), transcriptOf(student));
    }

    /**
     * @throws SaveGameException if the data refer to content that does not exist or hold values
     *                           the domain does not accept
     */
    public Student toStudent(SaveData data) {
        Objects.requireNonNull(data, "Save data must not be null");
        try {
            Student student = new Student(data.studentName(), profileOf(data));
            SaveData.StatsData stats = data.stats();
            student.updateStats(current -> new Stats(stats.knowledge(), stats.energy(), stats.maxEnergy(),
                    stats.concentration(), stats.maxConcentration()));
            student.updateLevel(current -> new Level(data.level(), data.experience()));
            data.inventory().forEach((id, quantity) -> student.inventory().add(itemOf(id), quantity));
            data.transcript().forEach((code, grade) -> student.transcript().record(examOf(code), Grade.of(grade)));
            return student;
        } catch (IllegalArgumentException | IllegalStateException | NullPointerException invalid) {
            throw new SaveGameException("Invalid saved game: " + invalid.getMessage(), invalid);
        }
    }

    /** Sorted by id, so the same inventory is always written the same way. */
    private static Map<String, Integer> inventoryOf(Student student) {
        Map<String, Integer> inventory = new TreeMap<>();
        student.inventory().items().forEach((item, quantity) -> inventory.put(item.id(), quantity));
        return inventory;
    }

    private static Map<String, Integer> transcriptOf(Student student) {
        Map<String, Integer> transcript = new LinkedHashMap<>();
        for (TranscriptEntry entry : student.transcript().entries()) {
            transcript.put(entry.exam().code(), entry.grade().value());
        }
        return transcript;
    }

    private StudentProfile profileOf(SaveData data) {
        return profiles.findById(data.profileId())
                .orElseThrow(() -> new SaveGameException("Unknown profile in saved game: " + data.profileId()));
    }

    private Item itemOf(String id) {
        return items.findById(id)
                .orElseThrow(() -> new SaveGameException("Unknown item in saved game: " + id));
    }

    private Exam examOf(String code) {
        return exams.findByCode(code)
                .orElseThrow(() -> new SaveGameException("Unknown exam in saved game: " + code));
    }
}
