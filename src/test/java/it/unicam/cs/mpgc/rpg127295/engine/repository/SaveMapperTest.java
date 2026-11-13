package it.unicam.cs.mpgc.rpg127295.engine.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.unicam.cs.mpgc.rpg127295.model.exam.Exam;
import it.unicam.cs.mpgc.rpg127295.model.exam.ExamType;
import it.unicam.cs.mpgc.rpg127295.model.exam.Grade;
import it.unicam.cs.mpgc.rpg127295.model.item.Item;
import it.unicam.cs.mpgc.rpg127295.model.question.Difficulty;
import it.unicam.cs.mpgc.rpg127295.model.question.Subject;
import it.unicam.cs.mpgc.rpg127295.model.student.Level;
import it.unicam.cs.mpgc.rpg127295.model.student.Stats;
import it.unicam.cs.mpgc.rpg127295.model.student.Student;
import it.unicam.cs.mpgc.rpg127295.model.student.StudentProfile;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

class SaveMapperTest {

    private static final Subject SUBJECT = new Subject("PROG", "Programmazione");
    private static final Exam PROG = new Exam("PROG1", SUBJECT, ExamType.WRITTEN, 12, Difficulty.MEDIUM);
    private static final Exam RETI = new Exam("RETI1", SUBJECT, ExamType.ORAL, 6, Difficulty.EASY);
    private static final Item COFFEE = new Item("coffee", "Caffè", "Più tempo");
    private static final Item NOTES = new Item("notes", "Appunti", "50/50");
    private static final StudentProfile NERD =
            new StudentProfile("nerd", "Secchione", "Studia sempre", Stats.initial(5, 6, 4));

    private final SaveMapper mapper = new SaveMapper(
            new ProfileCatalog() {
                @Override
                public List<StudentProfile> all() {
                    return List.of(NERD);
                }

                @Override
                public Optional<StudentProfile> findById(String id) {
                    return find(all(), StudentProfile::id, id);
                }
            },
            new ItemCatalog() {
                @Override
                public List<Item> all() {
                    return List.of(COFFEE, NOTES);
                }

                @Override
                public Optional<Item> findById(String id) {
                    return find(all(), Item::id, id);
                }
            },
            new ExamCatalog() {
                @Override
                public List<Exam> all() {
                    return List.of(PROG, RETI);
                }

                @Override
                public Optional<Exam> findByCode(String code) {
                    return find(all(), Exam::code, code);
                }
            });

    private static <T> Optional<T> find(List<T> all, Function<T, String> key, String value) {
        return all.stream().filter(element -> key.apply(element).equals(value)).findFirst();
    }

    private static Student playedStudent() {
        Student student = new Student("Andrea", NERD);
        student.updateStats(stats -> new Stats(6, 4, 8, 3, 5));
        student.updateLevel(level -> new Level(2, 50));
        student.inventory().add(COFFEE, 2);
        student.transcript().record(RETI, Grade.of(31));
        student.transcript().record(PROG, Grade.of(27));
        return student;
    }

    private static SaveData dataWith(String profile, Map<String, Integer> inventory, Map<String, Integer> transcript) {
        return new SaveData("Andrea", profile, SaveDataTest.STATS, 2, 50, inventory, transcript);
    }

    @Test
    void turnsAStudentIntoPlainData() {
        assertEquals(SaveDataTest.sample(), mapper.toData(playedStudent()));
    }

    @Test
    void restoresTheSameStudent() {
        Student original = playedStudent();

        Student restored = mapper.toStudent(mapper.toData(original));

        assertAll(
                () -> assertEquals(original.name(), restored.name()),
                () -> assertEquals(original.profile(), restored.profile()),
                () -> assertEquals(original.stats(), restored.stats()),
                () -> assertEquals(original.level(), restored.level()),
                () -> assertEquals(original.inventory().items(), restored.inventory().items()),
                () -> assertEquals(original.transcript().entries(), restored.transcript().entries()));
    }

    @Test
    void rejectsDataThatDoesNotMatchTheContent() {
        Map<String, Integer> none = Map.of();

        assertAll(
                () -> assertThrows(SaveGameException.class, () -> mapper.toStudent(dataWith("astronaut", none, none))),
                () -> assertThrows(SaveGameException.class,
                        () -> mapper.toStudent(dataWith("nerd", Map.of("pen", 1), none))),
                () -> assertThrows(SaveGameException.class,
                        () -> mapper.toStudent(dataWith("nerd", none, Map.of("ASD1", 25)))));
    }

    @Test
    void rejectsValuesTheDomainDoesNotAccept() {
        Map<String, Integer> none = Map.of();
        SaveData negativeEnergy = new SaveData("Andrea", "nerd", new SaveData.StatsData(6, -1, 8, 3, 5), 2, 50,
                none, none);

        assertAll(
                () -> assertThrows(SaveGameException.class, () -> mapper.toStudent(negativeEnergy)),
                () -> assertThrows(SaveGameException.class,
                        () -> mapper.toStudent(dataWith("nerd", none, Map.of("PROG1", 17)))),
                () -> assertThrows(SaveGameException.class,
                        () -> mapper.toStudent(dataWith("nerd", Map.of("coffee", 0), none))),
                () -> assertThrows(SaveGameException.class, () -> mapper.toStudent(
                        new SaveData(" ", "nerd", SaveDataTest.STATS, 2, 50, none, none))));
    }

    @Test
    void rejectsNullArguments() {
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> mapper.toData(null)),
                () -> assertThrows(NullPointerException.class, () -> mapper.toStudent(null)),
                () -> assertThrows(NullPointerException.class, () -> new SaveMapper(null, null, null)));
    }
}
