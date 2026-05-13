import java.io.PrintStream;

public class BenchmarkMain {
    private static final int RUNS = 10000;

    public static void main(String[] args) {
        // Suppress output during benchmarking
        PrintStream original = System.out;
        System.setOut(new PrintStream(new java.io.OutputStream() {
            public void write(int b) {}
        }));

        // Count calls during a single run
        Metrics.reset();
        runOnce();
        int callsPerRun = Metrics.callCount;

        // Warm-up pass (let JVM settle)
        for (int i = 0; i < 1000; i++) runOnce();

        // Timed runs
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

        System.out.println("Baseline Benchmark");
        System.out.println("Runs:              " + RUNS);
        System.out.println("Calls per run:     " + callsPerRun);
        System.out.printf( "Mean time (ns):    %.2f%n", mean);
        System.out.printf( "Std dev  (ns):     %.2f%n", stddev);
    }

    private static void runOnce() {
        Database database = new Database();
        NotificationService notificationService = new NotificationService();
        EvaluationManager evaluationManager = new EvaluationManager(notificationService, database);
        ReviewerManager reviewerManager = new ReviewerManager(database);
        Validator validator = new Validator();
        SubmissionController controller = new SubmissionController(validator, reviewerManager, evaluationManager, database);
        UI ui = new UI(controller);
        Researcher researcher = new Researcher("Alice", "My research data", ui);
        notificationService.setResearcher(researcher);

        database.addReviewer(new Reviewer("Reviewer1", 8, evaluationManager));
        database.addReviewer(new Reviewer("Reviewer2", 9, evaluationManager));
        database.addReviewer(new Reviewer("Reviewer3", 8, evaluationManager));

        researcher.submitResearchOutput("My research data");
    }
}
