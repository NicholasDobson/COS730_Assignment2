# COS 730 Assignment 2 — Optimised Implementation

Optimised version of the peer-review submission system. Aligned to the optimised sequence diagram with measurable reductions in inter-object coupling and message count compared to the baseline.

---

## How to Compile

Navigate into the `Optimised` folder, then compile all `.java` files:

```bash
cd Optimised
javac -d ClassFiles *.java
```

## How to Run

### Functional run
```bash
java -cp ClassFiles Main
```

### Benchmark run (10 000 timed iterations)
```bash
java -cp ClassFiles BenchmarkMain
```

---

## Expected Output (functional run)

```
[Validator] Validation result: Valid
[Database] Submission saved: My research data
[Database] Fetching reviewers, count: 3
[ReviewerManager] filterConflicts applied.
[ReviewerManager] checkWorkload applied.
[ReviewerManager] Returning 3 filtered reviewer(s).
[EvaluationManager] Starting evaluation.
[Reviewer] Reviewer1 assigned to review.
[Reviewer] Reviewer1 score: 8
[Reviewer] Reviewer2 assigned to review.
[Reviewer] Reviewer2 score: 9
[Reviewer] Reviewer3 assigned to review.
[Reviewer] Reviewer3 score: 8
[Database] Scores saved: [8, 9, 8]
[EvaluationManager] Average score: 8.333333333333334
[EvaluationManager] Consensus reached: false
[NotificationService] Decision: REVISION
[Researcher] Alice received notification.
```

---

## Class Responsibilities (Optimised Sequence Diagram Traceability)

| Class | Role in Optimised Sequence Diagram |
|---|---|
| `Researcher` | Actor. Calls `submitResearchOutput(data)` on UI, passing `this`. Receives `sendNotification()` from NotificationService via the outcome path. |
| `UI` | Receives submission and researcher reference. Delegates to `SubmissionController.submit(data, researcher)`. |
| `SubmissionController` | Orchestrates workflow: validates, saves, fetches reviewers, calls `startEvaluation(reviewers, researcher)`. No longer owns the reviewer-assignment loop. |
| `Validator` | Returns `true`/`false` from `validateFormat(data)`. |
| `Database` | Stores submissions. Returns reviewer list via `fetchReviewers()`. Accepts batch score save via `saveScores(List<Integer>)`. |
| `ReviewerManager` | Fetches reviewers from Database, applies `filterConflicts()` and `checkWorkload()` self-messages, returns filtered list. |
| `Reviewer` | Receives `assignReview()` from EvaluationManager (score is produced here). Exposes `getScore()` as a pure getter. No dependency on EvaluationManager. |
| `EvaluationManager` | Owns the single merged loop: calls `assignReview()` then `getScore()` per reviewer. Batches `saveScores()` to Database. Self-messages: `evaluate()` (average + consensus + rules). Calls `notify(outcome, researcher)` on NotificationService. |
| `NotificationService` | Receives `notify(outcome, researcher)`. Prints decision and calls `sendNotification()` on Researcher directly — no `setResearcher()` coupling. |
| `ReviewOutcome` | Enum: `ACCEPTED`, `REJECTED`, `REVISION`. Replaces three separate notification methods. |


## Message Flow (Optimised Sequence Diagram Trace)

```
Researcher           -> UI                   : submitResearchOutput(data)
UI                   -> SubmissionController : submit(data, researcher)
SubmissionController -> Validator            : validateFormat(data)
Validator            -> SubmissionController : true / false

[invalid]
  SubmissionController -> UI                 : return false  (flow ends)

[valid]
  SubmissionController -> Database           : saveSubmission(data)
  SubmissionController -> ReviewerManager    : getAvailableReviewers()
  ReviewerManager      -> Database           : fetchReviewers()
  Database             -> ReviewerManager    : reviewerList
  ReviewerManager      -> ReviewerManager    : filterConflicts(reviewerList)
  ReviewerManager      -> ReviewerManager    : checkWorkload(reviewerList)
  ReviewerManager      -> SubmissionController: filteredReviewers

  SubmissionController -> EvaluationManager  : startEvaluation(reviewers, researcher)

  [loop: each reviewer]
    EvaluationManager  -> Reviewer           : assignReview()
    EvaluationManager  -> Reviewer           : getScore()

  EvaluationManager    -> Database           : saveScores(scores)
  EvaluationManager    -> EvaluationManager  : evaluate(scores)

  [accepted]  EvaluationManager -> NotificationService : notify(ACCEPTED, researcher)
  [rejected]  EvaluationManager -> NotificationService : notify(REJECTED, researcher)
  [revision]  EvaluationManager -> NotificationService : notify(REVISION, researcher)

  NotificationService  -> Researcher         : sendNotification()
```

## Outcome Decision Logic

| Condition | Outcome |
|---|---|
| Consensus AND average >= 8 | `ACCEPTED` |
| Consensus AND average < 5  | `REJECTED` |
| All other cases             | `REVISION` |

Consensus = all submitted scores are identical.

## Key Optimisations vs Baseline

| Concern | Baseline | Optimised |
|---|---|---|
| Reviewer assignment owner | SubmissionController (loop 1) | EvaluationManager (merged into evaluation loop) |
| Score collection | Reviewer pushes via `submitScore()` → EvaluationManager | EvaluationManager pulls via `getScore()` after `assignReview()` |
| Database score writes | Per-reviewer `saveScore(score)` (3 calls) | Single `saveScores(List)` (1 call) |
| Notification methods | `notifyAcceptance()`, `notifyRejection()`, `notifyRevision()` (3 methods) | `notify(ReviewOutcome, Researcher)` (1 method, enum-driven) |
| Researcher coupling | `NotificationService.setResearcher()` post-construction | `Researcher` passed as parameter through the call chain |
| Inter-object messages | 24 | 14 |
| Instrumented calls per run | 22 | 17 |

## Notes

- Reviewer scores are seeded in `Main.java` (e.g. `new Reviewer("Reviewer1", 8)`) to simulate deterministic reviewer input. In a production system scores would come from user input or a database. However, since this is not the focus of this sequence diagram the dynamic and user input integration is not required! Thus, the seeded values were used.

- `assignReview()` is the activation trigger: `score` is initialised to `-1` and only set when `assignReview()` fires. `getScore()` is a pure getter with no side effects.

- No optimisations beyond those specified in the sequence diagram have been applied, the implementation is a direct trace of the optimised diagram. Which contains full functionality of the baseline diagram and implementation.
