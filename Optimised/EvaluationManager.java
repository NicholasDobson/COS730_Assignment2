import java.util.ArrayList;
import java.util.List;

public class EvaluationManager {
    private static final double ACCEPT_THRESHOLD = 8.0;
    private static final double REJECT_THRESHOLD = 5.0;

    private Database database;
    private NotificationService notificationService;

    public EvaluationManager(Database database, NotificationService notificationService) {
        this.database = database;
        this.notificationService = notificationService;
    }

    public ReviewOutcome startEvaluation(List<Reviewer> reviewers, Researcher researcher) {
        Metrics.callCount++;
        System.out.println("[EvaluationManager] Starting evaluation.");
        List<Integer> scores = new ArrayList<>();
        for (Reviewer reviewer : reviewers) {
            reviewer.assignReview();          // EvaluationManager assigns reviewer to submission
            int score = reviewer.getScore();  // pull score after assignment
            System.out.println("[Reviewer] " + reviewer.getName() + " score: " + score);
            scores.add(score);
        }
        database.saveScores(scores);
        ReviewOutcome outcome = evaluate(scores);
        notificationService.notify(outcome, researcher);
        return outcome;
    }

    private ReviewOutcome evaluate(List<Integer> scores) {
        double avg = scores.stream().mapToInt(i -> i).average().orElse(0);
        boolean consensus = scores.stream().distinct().count() == 1;
        System.out.println("[EvaluationManager] Average score: " + avg);
        System.out.println("[EvaluationManager] Consensus reached: " + consensus);
        if (consensus && avg >= ACCEPT_THRESHOLD) return ReviewOutcome.ACCEPTED;
        if (consensus && avg < REJECT_THRESHOLD)  return ReviewOutcome.REJECTED;
        return ReviewOutcome.REVISION;
    }
}
