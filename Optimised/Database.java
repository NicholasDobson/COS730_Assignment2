import java.util.ArrayList;
import java.util.List;

public class Database {
    private List<String> projects = new ArrayList<>();
    private List<Reviewer> reviewers = new ArrayList<>();
    private List<Integer> scores = new ArrayList<>();
    // private List<Researcher> researchers = new ArrayList<>();//hypothetical

    public boolean saveSubmission(String data) {
        Metrics.callCount++;
        projects.add(data);
        System.out.println("[Database] Submission saved: " + data);
        return true;
    }

    public List<Reviewer> fetchReviewers() {
        Metrics.callCount++;
        System.out.println("[Database] Fetching reviewers, count: " + reviewers.size());
        return reviewers;
    }

    public void saveScores(List<Integer> newScores) {
        Metrics.callCount++;
        scores.addAll(newScores);
        System.out.println("[Database] Scores saved: " + newScores);
    }

    public void addReviewer(Reviewer reviewer) {
        reviewers.add(reviewer);
    }
}
