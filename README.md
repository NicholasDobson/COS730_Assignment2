# COS 730 Assignment 2: General User Guide

## Requirements
- Java 11 or Java 21

## Structure
Each implementation folder contains its own `Readme.md` with full details.

## Baseline

### Functional run
```bash
cd Baseline
javac -d ClassFiles *.java
java -cp ClassFiles Main
```

### Benchmark run
```bash
cd Baseline
javac -d ClassFiles *.java
java -cp ClassFiles BenchmarkMain
```

## Optimised

### Functional run
```bash
cd Optimised
javac -d ClassFiles *.java
java -cp ClassFiles Main
```

### Benchmark run
```bash
cd Optimised
javac -d ClassFiles *.java
java -cp ClassFiles BenchmarkMain
```

## Baseline Class Responsibilities (Sequence Diagram Traceability)

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

## Baseline Message Flow (Sequence Diagram Trace)

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

## Optimised Class Responsibilities (Optimised Sequence Diagram Traceability)

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


## Optimised Message Flow (Optimised Sequence Diagram Trace)

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
---