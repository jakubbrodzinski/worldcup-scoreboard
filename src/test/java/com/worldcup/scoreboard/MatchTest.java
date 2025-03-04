package com.worldcup.scoreboard;

import com.worldcup.scoreboard.exceptions.DomainValidationException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MatchTest {
    @Nested
    class Creation {
        @Test
        void shouldDefaultScoreToZero() {
            var match = new Match("team-A", "team-B", Instant.now());

            assertThat(match.awayTeamScore()).isZero();
            assertThat(match.homeTeamScore()).isZero();
        }

        @Test
        void shouldIgnoreWhitespaces() {
            var match = new Match("team-A\t", " team-B\n", Instant.now());

            assertThat(match.homeTeamName()).isEqualTo("team-A");
            assertThat(match.awayTeamName()).isEqualTo("team-B");
        }
    }

    @Nested
    class Validation {
        @ParameterizedTest
        @CsvSource(value = {"\t,team-A", "team-A,    ", "null,team-A", "team-A,null"}, ignoreLeadingAndTrailingWhitespace = false, nullValues = "null")
        void shouldFailWhenTeamNameIsBlank(String homeTeamName, String awayTeamName) {
            assertThatThrownBy(() -> new Match(homeTeamName, awayTeamName, Instant.now()))
                    .isInstanceOf(DomainValidationException.class);
        }

        @Test
        void shouldFailWhenTeamNameAreNotUnique() {
            assertThatThrownBy(() -> new Match("team A   ", " team A", Instant.now()))
                    .isInstanceOf(DomainValidationException.class)
                    .hasMessageContaining("Names of the teams has to be unique");
        }

        @ParameterizedTest
        @CsvSource({"-1,0", "2,-3", "-3,-1"})
        void shouldThrowExceptionWhenScoreIsNegative(int homeTeamScore, int awayTeamScore) {
            assertThatThrownBy(() -> new Match("team-A", "team-B", homeTeamScore, awayTeamScore, Instant.now()))
                    .isInstanceOf(DomainValidationException.class)
                    .hasMessageContaining("Team score cannot be a negative number");
        }
    }

    @Nested
    class ScoreUpdate {
        @Test
        void shouldUpdateScore() {
            var startMatchTime = Instant.ofEpochSecond(1050);
            var match = new Match("team A", "team B", startMatchTime);

            var result = match.update(new MatchScore(1, 3));

            assertThat(result.homeTeamName()).isEqualTo("team A");
            assertThat(result.awayTeamName()).isEqualTo("team B");
            assertThat(result.homeTeamScore()).isEqualTo(1);
            assertThat(result.awayTeamScore()).isEqualTo(3);
            assertThat(result.startMatchTime()).isEqualTo(startMatchTime);
        }

        @Test
        void shouldUpdateTheScoreWhenLoweringNumberOfGoals() {
            var match = new Match("team A", "team B", 3, 3, Instant.now());

            var result = match.update(new MatchScore(1, 2));

            assertThat(result.homeTeamScore()).isEqualTo(1);
            assertThat(result.awayTeamScore()).isEqualTo(2);
        }
    }
}