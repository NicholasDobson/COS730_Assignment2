//No optimisation needed for UI, as it is just a thin layer that delegates to the controller.
public class UI {
    private SubmissionController submissionController;

    public UI(SubmissionController submissionController) {
        this.submissionController = submissionController;
    }

    public void submitResearchOutput(String data, Researcher researcher) {
        Metrics.callCount++;
        boolean success = submissionController.submit(data, researcher);
        if (!success) {
            System.out.println("[UI] Error: submission was invalid.");
        }
    }
}
