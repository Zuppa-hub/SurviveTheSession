package it.unicam.cs.mpgc.rpg127295.engine.repository;

import it.unicam.cs.mpgc.rpg127295.model.student.StudentProfile;
import java.util.List;
import java.util.Optional;

/** The student profiles to choose from. Defined here, where it is used, and implemented in persistence. */
public interface ProfileCatalog {

    /** @return every profile, in content order; unmodifiable */
    List<StudentProfile> all();

    /** @return the profile with the given id, or empty if none exists */
    Optional<StudentProfile> findById(String id);
}
