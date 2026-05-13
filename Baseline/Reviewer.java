// Sequence diagram messages involving Reviewer:
//   [loop] SubmissionController -> Reviewer: assignReview()
//   [loop each reviewer] Reviewer -> EvaluationManager: submitScore(score)
public class Reviewer {
    private String name;
    private int simulatedScore; // seeded score simulating the reviewer's judgment after review
    private int score;          // set during assignReview() to represent post-assignment scoring
    private EvaluationManager evaluationManager;

    public Reviewer(String name, int simulatedScore, EvaluationManager evaluationManager) {
        this.name = name;
        this.simulatedScore = simulatedScore;
        this.evaluationManager = evaluationManager;
    }

    // Sequence diagram: SubmissionController -> Reviewer: assignReview()
    // Assigning the review triggers the reviewer to evaluate — score is determined here.
    public void assignReview() {
        Metrics.callCount++;
        this.score = simulatedScore; // simulate: reviewer reviews submission and decides score
        System.out.println("[Reviewer] " + name + " assigned to review.");
    }

    // Sequence diagram: Reviewer -> EvaluationManager: submitScore(score)
    public void submitScore() {
        Metrics.callCount++;
        //this.score = simulatedScore; //could be set here instead as the score is determined during the review process which is after being assigned, 
        ////but for clarity I set it in assignReview()
        System.out.println("[Reviewer] " + name + " submitting score: " + score);
        evaluationManager.submitScore(score);
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
}
