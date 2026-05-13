import java.util.List;

public class SubmissionController {
    private Validator validator;
    private Database database;
    private ReviewerManager reviewerManager;
    private EvaluationManager evaluationManager;

    public SubmissionController(Validator validator, Database database, ReviewerManager reviewerManager, EvaluationManager evaluationManager) {
        this.validator = validator;
        this.database = database;
        this.reviewerManager = reviewerManager;
        this.evaluationManager = evaluationManager;
    }

    public boolean submit(String data, Researcher researcher) {
        Metrics.callCount++;
        if (!validator.validateFormat(data)) {
            return false;
        }
        database.saveSubmission(data);
        List<Reviewer> reviewers = reviewerManager.getAvailableReviewers();
        evaluationManager.startEvaluation(reviewers, researcher);
        return true;
    }
}
