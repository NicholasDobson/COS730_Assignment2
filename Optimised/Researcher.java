public class Researcher {
    private String name;
    private UI ui;

    public Researcher(String name, UI ui) {
        this.name = name;
        this.ui = ui;
    }

    public void submitResearchOutput(String data) {
        Metrics.callCount++;
        ui.submitResearchOutput(data, this);
    }

    public void sendNotification() {
        Metrics.callCount++;
        System.out.println("[Researcher] " + name + " received notification.");
    }

    public String getName() { return name; }
}
