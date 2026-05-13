public class Main {
    public static void main(String[] args) {
        // Instantiate all components (matches sequence diagram participants)
        Database database = new Database();
        Validator validator = new Validator();
        ReviewerManager reviewerManager = new ReviewerManager(database);
        NotificationService notificationService = new NotificationService();
        EvaluationManager evaluationManager = new EvaluationManager(notificationService, database);
        SubmissionController controller = new SubmissionController(validator, reviewerManager, evaluationManager, database);
        UI ui = new UI(controller);

        // Researcher is the actor — needs a UI reference to submit output
        Researcher researcher = new Researcher("Alice", "Comp Sci Research Data", ui);

        // NotificationService needs a Researcher reference to call sendNotification()
        // This is set after construction to avoid a circular dependency
        notificationService.setResearcher(researcher);

        // Seed database with reviewers (each holds their score for simulation)
        database.addReviewer(new Reviewer("Reviewer1", 8, evaluationManager));
        database.addReviewer(new Reviewer("Reviewer2", 9, evaluationManager));
        database.addReviewer(new Reviewer("Reviewer3", 8, evaluationManager));

        // Kick off the sequence diagram: Researcher -> UI: submitResearchOutput(data)
        researcher.submitResearchOutput("My research data");
    }
}
