// Sequence diagram messages involving NotificationService:
//   [accepted] EvaluationManager -> NotificationService: notifyAcceptance()
//   [rejected] EvaluationManager -> NotificationService: notifyRejection()
//   [revision] EvaluationManager -> NotificationService: notifyRevision()
//   NotificationService -> Researcher: sendNotification()
public class NotificationService {
    private String notificationMessage;
    private Researcher researcher;

    public void setResearcher(Researcher researcher) {
        this.researcher = researcher;
    }

    // Sequence diagram: EvaluationManager -> NotificationService: notifyAcceptance()
    public void notifyAcceptance() {
        Metrics.callCount++;
        notificationMessage = "Accepted";
        System.out.println("[NotificationService] Decision: " + notificationMessage);
        researcher.sendNotification();
    }

    // Sequence diagram: EvaluationManager -> NotificationService: notifyRejection()
    public void notifyRejection() {
        Metrics.callCount++;
        notificationMessage = "Rejected";
        System.out.println("[NotificationService] Decision: " + notificationMessage);
        researcher.sendNotification();
    }

    // Sequence diagram: EvaluationManager -> NotificationService: notifyRevision()
    public void notifyRevision() {
        Metrics.callCount++;
        notificationMessage = "Revision";
        System.out.println("[NotificationService] Decision: " + notificationMessage);
        researcher.sendNotification();
    }
}
