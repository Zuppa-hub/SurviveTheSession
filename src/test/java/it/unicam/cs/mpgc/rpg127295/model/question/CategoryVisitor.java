package it.unicam.cs.mpgc.rpg127295.model.question;

/** Test visitor that names the category of the visited question. */
final class CategoryVisitor implements QuestionVisitor<String> {

    static final String CHOICE = "choice";
    static final String FILL_IN = "fill-in";

    @Override
    public String visitChoice(ChoiceQuestion question) {
        return CHOICE;
    }

    @Override
    public String visitFillIn(FillInQuestion question) {
        return FILL_IN;
    }
}
