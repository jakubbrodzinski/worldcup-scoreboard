# Live Football World Cup Score Board
## Assumptions / Limitations
- The moment of invoking `startMatch(...)` is treated as a moment of starting the match. The time is an internal impl. detail and is not exposed anywhere.
- It's a scoreboard for the World Cup - each team cannot participate in more than one match.
- Finishing non-existing match does not result in a failure.
- Should I even expose the Match class/record?
- Persistence is out of exercise scope. Is it?
- To keep the test as a totally blac
## L

Notes:
- Which match is the latest? Should I expose any info about the time match started?
- According to the API, we cannot add already started match, we can 
Open Questions:
- Concurrency 
- Overtime
- Performance of the different methods
- Should I even expose the Match class/record?
- What I should even expose?
- What should be parametrized?
  - Sorting logic
  - 