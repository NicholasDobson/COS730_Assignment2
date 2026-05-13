// Simulates the Researcher actor from the sequence diagram.
// Sequence diagram messages involving Researcher:
//   Researcher -> UI: submitResearchOutput(data)
//   NotificationService -> Researcher: sendNotification()
public class Researcher {
    private String name;
    private String researchOutput;
    private UI ui;

    public Researcher(String name, String researchOutput) {
        this.name = name;
        this.researchOutput = researchOutput;
    }

    public Researcher(String name, String researchOutput, UI ui) {
        this.name = name;
        this.researchOutput = researchOutput;
        this.ui = ui;
    }

    // Sequence diagram: Researcher -> UI: submitResearchOutput(data)
    public void submitResearchOutput(String data) {
        Metrics.callCount++;
        ui.submitResearchOutput(data);
    }

    // Sequence diagram: NotificationService -> Researcher: sendNotification()
    public void sendNotification() {
        Metrics.callCount++;
        System.out.println("[Researcher] " + name + " received notification.");
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getResearchOutput() { return researchOutput; }
    public void setResearchOutput(String researchOutput) { this.researchOutput = researchOutput; }
}
