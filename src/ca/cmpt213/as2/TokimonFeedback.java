package ca.cmpt213.as2;

/**
 * TokimonFeedback stores feedbacks received by a Tokimon
 */
public class TokimonFeedback {
    private String name;
    private String id;
    private float score;
    private String comment;
    private String extra_comment;

    public TokimonFeedback(String name, String id, float score, String comment, String extra_comment) {
        this.name = name;
        this.id = id;
        this.score = score;
        this.comment = comment;
        this.extra_comment = extra_comment;
    }

    public boolean matchID(String id) {
        if (this.getID().trim().equalsIgnoreCase(id)) {
            return true;
        }
        return false;
    }

    public String getID() {
        return id;
    }
    public float getScore() {
        return score;
    }
    public String getComment() {
        return comment;
    }
    public String getName() {
        return name;
    }
    public String getExtra_comment() {
        return extra_comment;
    }

    public void setExtra_comment(String extra_comment) {
        this.extra_comment = extra_comment;
    }

    public void print(){
        System.out.println(id+extra_comment);
    }


}

