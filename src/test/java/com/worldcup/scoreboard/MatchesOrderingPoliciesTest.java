package com.worldcup.scoreboard;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Comparator;
import java.util.stream.Stream;

import static com.worldcup.scoreboard.MatchesOrderingPolicies.highestScoringMatchesFirst;
import static com.worldcup.scoreboard.MatchesOrderingPolicies.recentlyStartedMatchesFirst;
import static org.assertj.core.api.Assertions.assertThat;

class MatchesOrderingPoliciesTest {
    @Nested
    class HighestScoringMatchesFirst {
        private final Comparator<Match> policy = highestScoringMatchesFirst();

        @Test
        void shouldOrderMatchesByTheScoreSumDesc() {
            var lowerScoredMatch = new Match("team C", "team D", 1, 1, Instant.ofEpochSecond(1));
            var higherScoredMatch = new Match("team A", "team B", 5, 3, Instant.ofEpochSecond(1));

            assertThat(Stream.of(lowerScoredMatch, higherScoredMatch).sorted(policy))
                    .containsExactly(higherScoredMatch, lowerScoredMatch);
        }
    }

    @Nested
    class RecentlyStartedMatchesFirst {
        private final Comparator<Match> policy = recentlyStartedMatchesFirst();

        @Test
        void shouldOrderMatchesByTheStartTimeDesc() {
            var firstMatch = new Match("team C", "team D", Instant.ofEpochSecond(100));
            var secondMatch = new Match("team A", "team B", Instant.ofEpochSecond(200));

            assertThat(Stream.of(firstMatch, secondMatch).sorted(policy))
                    .containsExactly(secondMatch, firstMatch);
        }
    }

}