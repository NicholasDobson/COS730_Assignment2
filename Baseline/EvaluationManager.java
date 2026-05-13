import java.util.ArrayList;
import java.util.List;

// Sequence diagram messages involving EvaluationManager:
//   SubmissionController -> EvaluationManager: startEvaluation()
//   [loop each reviewer] Reviewer -> EvaluationManager: submitScore(score)
//   [loop each reviewer] EvaluationManager -> Database: saveScore(score)
//   EvaluationManager -> EvaluationManager: calculateAverage()
//   EvaluationManager -> EvaluationManager: checkConsensus()
//   EvaluationManager -> EvaluationManager: applyRules()
//   [accepted]  EvaluationManager -> NotificationService: notifyAcceptance()
//   [rejected]  EvaluationManager -> NotificationService: notifyRejection()
//   [revision]  EvaluationManager -> NotificationService: notifyRevision()
public class EvaluationManager {
    private List<Integer> scores = new ArrayList<>();
    private double averageScore;
    private boolean consensusReached;
    private NotificationService notificationService;
    private Database database;

    public EvaluationManager(NotificationService notificationService, Database database) {
        this.notificationService = notificationService;
        this.database = database;
    }

    // Sequence diagram: SubmissionController -> EvaluationManager: startEvaluation()
    // Triggers the reviewer submit-score loop, then self-messages for average/consensus/rules.
    public void startEvaluation(List<Reviewer> assignedReviewers) {
        Metrics.callCount++;
        System.out.println("[EvaluationManager] Starting evaluation.");

        // [loop each reviewer] Reviewer -> EvaluationManager: submitScore(score)
        for (Reviewer reviewer : assignedReviewers) {
            reviewer.submitScore();
        }

        calculateAverage();
        checkConsensus();
        applyRules();
    }

    // Sequence diagram: 
    // Reviewer          -> EvaluationManager: submitScore(score)
    // EvaluationManager -> Database:          saveScore(score)
    public void submitScore(int score) {
        Metrics.callCount++;
        scores.add(score);
        database.saveScore(score);
    }

    // Sequence diagram: EvaluationManager -> EvaluationManager: calculateAverage()
    private void calculateAverage() {
        averageScore = scores.stream().mapToInt(Integer::intValue).average().orElse(0);
        System.out.println("[EvaluationManager] Average score: " + averageScore);
    }

    // Sequence diagram: EvaluationManager -> EvaluationManager: checkConsensus()
    private void checkConsensus() {
        consensusReached = scores.stream().distinct().count() == 1;
        System.out.println("[EvaluationManager] Consensus reached: " + consensusReached);
    }

    // Sequence diagram: EvaluationManager -> EvaluationManager: applyRules()
    // [accepted] / [rejected] / [revision] alt branches
    private void applyRules() {
        if (consensusReached && averageScore >= 8) {
            notificationService.notifyAcceptance();
        } else if (consensusReached && averageScore < 5) {
            notificationService.notifyRejection();
        } else {
            notificationService.notifyRevision();
        }
    }
}
