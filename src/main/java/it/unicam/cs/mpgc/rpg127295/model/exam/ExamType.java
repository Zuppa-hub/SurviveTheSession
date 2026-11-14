package it.unicam.cs.mpgc.rpg127295.model.exam;

/**
 * The kind of an exam.
 * <p>
 * It has no behavior on purpose: each type is mapped to its strategy in the composition root
 * through an {@code EnumMap}, so the engine never switches on the type.
 */
public enum ExamType {
    WRITTEN,
    ORAL,
    PROJECT
}
