public class Reviewer {
    private String name;
    private int simulatedScore; // seeded score representing reviewer's judgment after review
    private int score = -1;     // -1 = not yet reviewed; only set when assignReview() fires

    public Reviewer(String name, int simulatedScore) {
        this.name = name;
        this.simulatedScore = simulatedScore;
    }

    // EvaluationManager assigns the reviewer to the submission — score produced here.
    // Reviewer has no result before this fires.
    public void assignReview() {
        Metrics.callCount++;
        score = simulatedScore; // simulate: reviewer reviews submission and decides score
        System.out.println("[Reviewer] " + name + " assigned to review.");
    }

    // Pure getter — EvaluationManager pulls the score after assignment.
    public int getScore() {
        Metrics.callCount++;
        return score;
    }

    public String getName() { return name; }
}
