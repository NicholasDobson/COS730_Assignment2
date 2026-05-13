public class Main {
    public static void main(String[] args) {
        Database database = new Database();
        NotificationService notificationService = new NotificationService();
        EvaluationManager evaluationManager = new EvaluationManager(database, notificationService);
        ReviewerManager reviewerManager = new ReviewerManager(database);
        Validator validator = new Validator();
        SubmissionController controller = new SubmissionController(validator, database, reviewerManager, evaluationManager);
        UI ui = new UI(controller);
        Researcher researcher = new Researcher("Alice", ui);

        database.addReviewer(new Reviewer("Reviewer1", 8));
        database.addReviewer(new Reviewer("Reviewer2", 9));
        database.addReviewer(new Reviewer("Reviewer3", 8));

        researcher.submitResearchOutput("My research data");
    }
}
