import java.util.ArrayList;
import java.util.List;

public class Database {
    private List<String> projects = new ArrayList<>();
    private List<Reviewer> reviewers = new ArrayList<>();
    private List<Integer> scores = new ArrayList<>();
    // private List<Researcher> researchers = new ArrayList<>();//hypothetical

    public String saveSubmission(String data) {
        Metrics.callCount++;
        projects.add(data);
        System.out.println("[Database] Submission saved: " + data);
        return "confirmation";
    }

    public List<Reviewer> fetchReviewers() {
        Metrics.callCount++;
        System.out.println("[Database] Fetching reviewers, count: " + reviewers.size());
        return reviewers;
    }

    public void saveScore(int score) {
        Metrics.callCount++;
        scores.add(score);
        System.out.println("[Database] Score saved: " + score);
    }

    // Used by Main to seed reviewer data before the flow begins
    public void addReviewer(Reviewer reviewer) {
        reviewers.add(reviewer);
    }
}
