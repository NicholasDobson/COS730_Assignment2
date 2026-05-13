// Sequence diagram messages involving UI:
//   Researcher -> UI: submitResearchOutput(data)
//   UI -> SubmissionController: submit(data)
//   [invalid] SubmissionController -> UI: return error
public class UI {
    private SubmissionController submissionController;

    public UI(SubmissionController submissionController) {
        this.submissionController = submissionController;
    }

    // Sequence diagram: UI -> SubmissionController: submit(data)
    public void submitResearchOutput(String data) {
        Metrics.callCount++;
        String result = submissionController.submit(data);
        if ("error".equals(result)) {
            System.out.println("[UI] Error returned: submission was invalid or could not be saved.");
        }
    }
}
