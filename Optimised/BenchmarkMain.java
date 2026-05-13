import java.io.PrintStream;

public class BenchmarkMain {
    private static final int RUNS = 10000;

    public static void main(String[] args) {
        PrintStream original = System.out;
        System.setOut(new PrintStream(new java.io.OutputStream() {
            public void write(int b) {}
        }));

        Metrics.reset();
        runOnce();
        int callsPerRun = Metrics.callCount;

        for (int i = 0; i < 1000; i++) runOnce();

        long[] times = new long[RUNS];
        for (int i = 0; i < RUNS; i++) {
            long start = System.nanoTime();
            runOnce();
            times[i] = System.nanoTime() - start;
        }

        System.setOut(original);

        double mean = 0;
        for (long t : times) mean += t;
        mean /= RUNS;

        double variance = 0;
        for (long t : times) variance += (t - mean) * (t - mean);
        double stddev = Math.sqrt(variance / RUNS);

        System.out.println("=== Optimised Benchmark ===");
        System.out.println("Runs:              " + RUNS);
        System.out.println("Calls per run:     " + callsPerRun);
        System.out.printf( "Mean time (ns):    %.2f%n", mean);
        System.out.printf( "Std dev  (ns):     %.2f%n", stddev);
    }

    private static void runOnce() {
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
