import java.util.List;

// Sequence diagram messages involving SubmissionController:
//   UI -> SubmissionController: submit(data)
//   SubmissionController -> Validator: validateFormat(data)
//   [invalid] SubmissionController -> UI: return error
//   [valid]   SubmissionController -> Database: saveSubmission(data)
//   SubmissionController -> ReviewerManager: getAvailableReviewers()
//   [loop] SubmissionController -> Reviewer: assignReview()
//   SubmissionController -> EvaluationManager: startEvaluation()
public class SubmissionController {
    private Validator validator;
    private ReviewerManager reviewerManager;
    private EvaluationManager evaluationManager;
    private Database database;

    public SubmissionController(Validator validator, ReviewerManager reviewerManager,
                                EvaluationManager evaluationManager, Database database) {
        this.validator = validator;
        this.reviewerManager = reviewerManager;
        this.evaluationManager = evaluationManager;
        this.database = database;
    }

    // Returns "error" on validation/save failure, "ok" on success
    public String submit(String data) {
        Metrics.callCount++;
        // SubmissionController -> Validator: validateFormat(data)
        String validation = validator.validateFormat(data);

        // [invalid] alt: return error -> UI
        if ("Invalid".equals(validation)) {
            System.out.println("[SubmissionController] Validation failed. Returning error to UI.");
            return "error";
        }

        // [valid]: SubmissionController -> Database: saveSubmission(data)
        String confirmation = database.saveSubmission(data);
        if (!"confirmation".equals(confirmation)) {
            System.out.println("[SubmissionController] Could not save submission.");
            return "error";
        }

        // SubmissionController -> ReviewerManager: getAvailableReviewers()
        List<Reviewer> filteredReviewers = reviewerManager.getAvailableReviewers();

        // [loop] SubmissionController -> Reviewer: assignReview()
        for (Reviewer reviewer : filteredReviewers) {
            reviewer.assignReview();
        }

        // SubmissionController -> EvaluationManager: startEvaluation()
        evaluationManager.startEvaluation(filteredReviewers);
        return "ok";
    }
}
