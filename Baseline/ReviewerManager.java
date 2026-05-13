import java.util.List;

// Sequence diagram messages involving ReviewerManager:
//   SubmissionController -> ReviewerManager: getAvailableReviewers()
//   ReviewerManager -> Database: fetchReviewers()
//   Database -> ReviewerManager: reviewerList
//   ReviewerManager -> ReviewerManager: filterConflicts(reviewerList)
//   ReviewerManager -> ReviewerManager: checkWorkload(reviewerList)
//   ReviewerManager -> SubmissionController: filteredReviewers
public class ReviewerManager {
    private Database database;

    public ReviewerManager(Database database) {
        this.database = database;
    }

    // Sequence diagram: SubmissionController -> ReviewerManager: getAvailableReviewers()
    public List<Reviewer> getAvailableReviewers() {
        Metrics.callCount++;
        // ReviewerManager -> Database: fetchReviewers()
        List<Reviewer> reviewerList = database.fetchReviewers();

        // ReviewerManager -> ReviewerManager: filterConflicts(reviewerList)
        filterConflicts(reviewerList);

        // ReviewerManager -> ReviewerManager: checkWorkload(reviewerList)
        checkWorkload(reviewerList);

        System.out.println("[ReviewerManager] Returning " + reviewerList.size() + " filtered reviewer(s).");
        return reviewerList;
    }

    // Sequence diagram: ReviewerManager -> ReviewerManager: filterConflicts(reviewerList)
    private void filterConflicts(List<Reviewer> reviewerList) {
        System.out.println("[ReviewerManager] filterConflicts applied.");
    }

    // Sequence diagram: ReviewerManager -> ReviewerManager: checkWorkload(reviewerList)
    private void checkWorkload(List<Reviewer> reviewerList) {
        System.out.println("[ReviewerManager] checkWorkload applied.");
    }
}
