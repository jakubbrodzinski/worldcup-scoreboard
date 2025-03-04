# Live Football World Cup Score Board

### Assumptions / Remarks

- Each team can only take part only in one Live Match (Live Match is the one that is not finished - is on the scoreboard).
- The main functionality of the scoreboard is to get the summary of all matches.
- The moment of invoking `startMatch(...)` is treated as a moment of starting the match. The time is an internal impl. detail and is not exposed anywhere.
- Really wanted to make Match an internal model of the Scoreboard (implementation detail) and do not expose it on the outside.
- Assumed that `getMatchesSummary` will be invoked **much more** frequently than the methods that modify the order of matches on scoreboard. As a result, I maintain an "index" to not sort the matches on fly (each time summary is requested).
- I don't track the duration of the match, update timestamps, overtimes etc.
- Any score updates are valid, meaning match from `5:3`, can go to `2:1`.
  - That's a one way to handle overtimes/penalties shot out.
  - Let's not limit the user in case of any "faulty" data.
- Although starting the same match twice would throw an exception, finishing non-existing match does not result in a failure.
  - Starting the match twice, could potentially mean that someone forgot to finish some match "from 1 week ago".
  - Design decision that could be easily challenged.
- `InMemoryMatchRepository` is treated as an internal impl. detail of `Scoreboard`. Any expose of the repository wouldn't add much value to the **current** solution.
  - Testing it on its own, would duplicate some of the tests between scoreboard and repo tests.
  - It would add some complexity. 
  - Inner class can be treated as a singal of the specific use case that Repository covers.
  - Introducing any potential persistence to such Lib would introduce some problems that are totally out of scope of such exercise.
- Some the amount of boilerplate code could be reduced by using _Lombok_. Decided not to add it, as I mostly relied on the records.
- The validation of negative score is duplicated - wanted to avoid fetching the match in case of negative scores and fail fast.
- Ignored the case where multiple matches started at the same exact time and have the same exact score.
- Added some basic sanitization of the input, although it could be treated as a lib's user responsibility.
- Library is suited for single thread usage. 
  - It was not mentioned in the requirements. 
  - It would be a good improvement to add in the next version.
  - Would probably do it around `InMemoryMatchRepository`, as it's the place where data resides.
  - Could impact testing layer.

### Rejected ideas
- Define the `Scoreboard` as an interface, and provide two implementations: one that is resposinble for the actual requirements and one that would only specialize in validating the input.
  - Rejected solely due to the complexity and potential overengineering - the validations are rather simple (KISS).
- Building abstraction over Repository (described above).
- `ScoreboarFactory` as a way to initialize Scoreboard - ended up with simple static factory method. 
  - It could be the way to initialize Scoreboards if the lib would offer i.e. different custom sorting policies. 
- Parametrization on `startMatchTime` - working with `Instant` was good enough for simple testing.