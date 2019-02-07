package ca.cmpt213.as2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tokimon class stores information of a Tokimon
 */
public class Tokimon implements Comparable<Tokimon> {
    private String name;
    private String id;

    boolean isFeedbackSubmitted = false;
    private TokimonFeedback selfFeedback;
    private List<TokimonFeedback> otherFeedbacks = new ArrayList<>();

    public Tokimon(String name, String id, TokimonFeedback selfFeedback, boolean isFeedbackSubmitted) {
        this.name = name;
        this.id = id;
        this.selfFeedback = selfFeedback;
        this.isFeedbackSubmitted = isFeedbackSubmitted;
    }

    public Tokimon(Tokimon otherTokimon) {
        this.name = otherTokimon.name;
        this.id = otherTokimon.id;
    }

    public void addOtherFeedbacks(TokimonFeedback otherFeedback) {
        this.otherFeedbacks.add(otherFeedback);
    }

    // Getters
    public String getName() {
        return name;
    }
    public String getID() {
        return id;
    }
    public TokimonFeedback getSelfFeedback() {return this.selfFeedback;}
    public List<TokimonFeedback> getOtherFeedbacks() {
        return this.otherFeedbacks;
    }
    public boolean getIsFeedbackSubmitted() {
        return this.isFeedbackSubmitted;
    }


    // Setters
    public void setName(String name) {
        this.name = name;
    }
    public void setIsFeedbackSubmitted(Boolean value) {
        this.isFeedbackSubmitted = value;
    }


    public void print() {
        System.out.println(id);
        selfFeedback.print();
        for (TokimonFeedback each:otherFeedbacks)
            each.print();
        System.out.println();
    }

    public boolean matchID(String id) {
        if (this.getID().trim().equalsIgnoreCase(id.trim()))
            return true;
        return false;
    }

    public boolean matchFeedbackID(String id) {
        for (TokimonFeedback each : otherFeedbacks) {
            if (each.matchID(id))
                return true;
        }
        return false;
    }

    @Override
    public int compareTo(Tokimon otherToki) {
        return this.id.toLowerCase().compareTo(otherToki.getID().toLowerCase());
    }

//    public void sort() {
//        Collections.sort(otherFeedbacks);
//    }
}

