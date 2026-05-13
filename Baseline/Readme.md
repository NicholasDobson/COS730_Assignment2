# COS 730 Assignment 2, Task 1: Baseline Implementation
This is the baseline implementation of the system. 
implemented exactly as specified by the provided sequence diagram. 
No optimisations have been applied. 
All object interactions and responsibilities directly trace to the diagram.

## How to Compile
Create class files if needed (can skip)
```bash
cd Baseline
javac -d ClassFiles *.java
```

## How to Run classfiles

### Functionality run
```bash
java -cp ClassFiles Main
```

## Expected Output
```
[Validator] Validation result: Valid
[Database] Submission saved: My research data
[Database] Fetching reviewers, count: 3
[ReviewerManager] filterConflicts applied.
[ReviewerManager] checkWorkload applied.
[ReviewerManager] Returning 3 filtered reviewer(s).
[Reviewer] Reviewer1 assigned to review.
[Reviewer] Reviewer2 assigned to review.
[Reviewer] Reviewer3 assigned to review.
[EvaluationManager] Starting evaluation.
[Reviewer] Reviewer1 submitting score: 8
[Database] Score saved: 8
[Reviewer] Reviewer2 submitting score: 9
[Database] Score saved: 9
[Reviewer] Reviewer3 submitting score: 8
[Database] Score saved: 8
[EvaluationManager] Average score: 8.333333333333334
[EvaluationManager] Consensus reached: false
[NotificationService] Decision: Revision
[Researcher] Alice received notification.
```

## Class Responsibilities (Sequence Diagram Traceability)

| Class | Role in Sequence Diagram |
|---|---|
| `Researcher` | Actor. Calls `submitResearchOutput(data)` on UI. Receives `sendNotification()` from NotificationService. |
| `UI` | Receives submission from Researcher. Delegates to `SubmissionController.submit(data)`. Displays error if invalid. |
| `SubmissionController` | Orchestrates the full workflow: validates, saves, retrieves reviewers, assigns them, and starts evaluation. |
| `Validator` | Called by SubmissionController. Returns `"Valid"` or `"Invalid"` from `validateFormat(data)`. |
| `Database` | Stores submissions and scores. Returns reviewer list via `fetchReviewers()`. Called by SubmissionController, ReviewerManager, and EvaluationManager. |
| `ReviewerManager` | Fetches reviewer list from Database, applies `filterConflicts()` and `checkWorkload()` self-messages, returns filtered list to SubmissionController. |
| `Reviewer` | Receives `assignReview()` from SubmissionController. Calls `submitScore(score)` on EvaluationManager during the evaluation loop. |
| `EvaluationManager` | Triggered by `startEvaluation()`. Loops over reviewers to collect scores. Self-messages: `calculateAverage()`, `checkConsensus()`, `applyRules()`. Calls NotificationService with outcome. |
| `NotificationService` | Receives `notifyAcceptance()`, `notifyRejection()`, or `notifyRevision()` from EvaluationManager. Calls `sendNotification()` on Researcher. |

## Message Flow (Sequence Diagram Trace)

```
Researcher        -> UI                  : submitResearchOutput(data)
UI                -> SubmissionController: submit(data)
SubmissionController -> Validator        : validateFormat(data)
Validator         -> SubmissionController: "Valid" / "Invalid"

[invalid]
  SubmissionController -> UI             : return error  (flow ends)

[valid]
  SubmissionController -> Database       : saveSubmission(data)
  Database         -> SubmissionController: confirmation

  SubmissionController -> ReviewerManager: getAvailableReviewers()
  ReviewerManager  -> Database           : fetchReviewers()
  Database         -> ReviewerManager    : reviewerList
  ReviewerManager  -> ReviewerManager    : filterConflicts(reviewerList)
  ReviewerManager  -> ReviewerManager    : checkWorkload(reviewerList)
  ReviewerManager  -> SubmissionController: filteredReviewers

  [loop: assign reviewers]
    SubmissionController -> Reviewer     : assignReview()

  SubmissionController -> EvaluationManager: startEvaluation()

  [loop: each reviewer]
    Reviewer       -> EvaluationManager  : submitScore(score)
    EvaluationManager -> Database        : saveScore(score)

  EvaluationManager -> EvaluationManager : calculateAverage()
  EvaluationManager -> EvaluationManager : checkConsensus()
  EvaluationManager -> EvaluationManager : applyRules()

  [accepted]  EvaluationManager -> NotificationService: notifyAcceptance()
  [rejected]  EvaluationManager -> NotificationService: notifyRejection()
  [revision]  EvaluationManager -> NotificationService: notifyRevision()

  NotificationService -> Researcher      : sendNotification()
```

---

## Outcome Decision Logic (applyRules)

| Condition | Outcome |
|---|---|
| Consensus reached AND average >= 8 | Accepted |
| Consensus reached AND average < 5 | Rejected |
| All other cases | Revision |

Consensus is defined as all submitted scores being identical.

## Notes
- Reviewer scores are seeded in `Main.java` to simulate real reviewer input. In a production system these would come from user input or a database!
- `NotificationService.setResearcher()` is used instead of constructor injection to avoid a circular dependency between `NotificationService` and `Researcher`!
- No optimisations, caching, or abstractions have been introduced beyond what the sequence diagram specifies. This is intentional for Task 1!
