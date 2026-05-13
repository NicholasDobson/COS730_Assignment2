public class NotificationService {
    public void notify(ReviewOutcome outcome, Researcher researcher) {
        Metrics.callCount++;
        System.out.println("[NotificationService] Decision: " + outcome);
        researcher.sendNotification();
    }
}
