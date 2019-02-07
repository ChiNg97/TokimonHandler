package ca.cmpt213.as2;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;

public class TokimonTeam {
    private List<Tokimon> teamMember = new ArrayList<>();

    public TokimonTeam(Tokimon teamMember) {
        this.teamMember.add(teamMember);
    }

    public void addMember(Tokimon teamMember) {
        this.teamMember.add(teamMember);
    }

    public List<Tokimon> getTeamMember () {
        return this.teamMember;
    }

    public boolean containsTokimon(String id) {
        for (Tokimon each : teamMember) {
            if (each.matchID(id))
                return true;
        }
        return false;
    }

    public boolean containsInFeedback(String id) {
        for (Tokimon each : teamMember) {
            if (each.matchFeedbackID(id))
                return true;
        }
        return false;
    }

    public void sort() {
        Collections.sort(teamMember);
    }

    public void print(){
        for (Tokimon each:teamMember) {
            System.out.print(each.getID());
        }
    }

}
