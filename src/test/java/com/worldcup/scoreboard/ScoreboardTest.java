package com.worldcup.scoreboard;

import com.worldcup.scoreboard.exceptions.DomainValidationException;
import com.worldcup.scoreboard.exceptions.MatchNotFoundException;
import com.worldcup.scoreboard.exceptions.TeamPartOfLiveMatchException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ScoreboardTest {
    private Scoreboard scoreboard;

    @BeforeEach
    void setUp() {
        scoreboard = new Scoreboard();
    }

    @Nested
    class StartMatch {
        @Test
        void shouldStartMatch() {
            assertThatCode(() -> scoreboard.startMatch("team A", "team B"))
                    .doesNotThrowAnyException();
        }

        @ParameterizedTest
        @ValueSource(strings = {"team A", "team B"})
        void shouldThrowExceptionWhenHomeTeamIsPartOfDifferentLiveMatch(String homeTeamName) {
            scoreboard.startMatch("team A", "team B");

            assertThatThrownBy(() -> scoreboard.startMatch(homeTeamName, "dummy team name"))
                    .isInstanceOf(TeamPartOfLiveMatchException.class)
                    .hasMessageContaining(homeTeamName);
        }

        @ParameterizedTest
        @ValueSource(strings = {"team A", "team B"})
        void shouldThrowExceptionWhenAwayTeamIsPartOfDifferentLiveMatch(String awayTeamName) {
            scoreboard.startMatch("team A", "team B");

            assertThatThrownBy(() -> scoreboard.startMatch("dummy team name", awayTeamName))
                    .isInstanceOf(TeamPartOfLiveMatchException.class)
                    .hasMessageContaining(awayTeamName);
        }

        @ParameterizedTest
        @CsvSource(value = {"null, valid team name", "valid team name, null"}, nullValues = "null")
        void shouldThrowExceptionWhenTeamNameIsNull(String homeTeamName, String awayTeamName) {
            assertThatThrownBy(() -> scoreboard.startMatch(homeTeamName, awayTeamName))
                    .isInstanceOf(DomainValidationException.class)
                    .hasMessageContaining("cannot be null");
        }

        @Test
        void shouldThrowExceptionWhenTeamNameIsBlank() {
            assertThatThrownBy(() -> scoreboard.startMatch("team A", "\t"))
                    .isInstanceOf(DomainValidationException.class);
        }
    }

    @Nested
    class UpdateMatch {
        @Test
        void shouldUpdateMatch() {
            scoreboard.startMatch("team A", "team B");

            assertThatCode(() -> scoreboard.updateMatch("team A", "team B", new MatchScore(5, 3)))
                    .doesNotThrowAnyException();
        }

        @Test
        void shouldThrowExceptionWhenTeamAreNotPartOfAnyMatch() {
            assertThatThrownBy(() -> scoreboard.updateMatch("team A", "team B", new MatchScore(5, 3)))
                    .isInstanceOf(MatchNotFoundException.class)
                    .hasMessageContainingAll("team A", "team B");
        }
    }

    @Nested
    class FinishMatch {

    }

    @Nested
    class GetSummary {

    }
}