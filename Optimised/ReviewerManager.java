import java.util.List;

public class ReviewerManager {
    private Database database;

    public ReviewerManager(Database database) {
        this.database = database;
    }

    public List<Reviewer> getAvailableReviewers() {
        Metrics.callCount++;
        List<Reviewer> reviewers = database.fetchReviewers();
        return filterReviewers(reviewers);
    }

    private List<Reviewer> filterReviewers(List<Reviewer> reviewers) {
        System.out.println("[ReviewerManager] filterReviewers applied.");
        System.out.println("[ReviewerManager] Returning " + reviewers.size() + " filtered reviewer(s).");
        return reviewers;
    }
}
