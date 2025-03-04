package com.worldcup.scoreboard;

import com.worldcup.scoreboard.exceptions.DomainValidationException;
import com.worldcup.scoreboard.exceptions.MatchNotFoundException;
import com.worldcup.scoreboard.exceptions.TeamPartOfLiveMatchException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.assertj.core.api.Assertions.*;

class ScoreboardTest {
    private Scoreboard scoreboard;

    @BeforeEach
    void setUp() {
        scoreboard = Scoreboard.defaultInstance();
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
        @NullTestCases
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

        @ParameterizedTest
        @NullTestCases
        void shouldThrowExceptionWhenTeamNameIsNull(String homeTeamName,String awayTeamName) {
            assertThatThrownBy(() -> scoreboard.updateMatch(homeTeamName, awayTeamName, new MatchScore(0, 0)))
                    .isInstanceOf(DomainValidationException.class);
        }

        @Test
        void shouldThrowExceptionWhenTeamNamesAreSwapped() {
            scoreboard.startMatch("team A", "team B");

            assertThatCode(() -> scoreboard.updateMatch("team B", "team A", new MatchScore(5, 3)))
                    .isInstanceOf(MatchNotFoundException.class);
        }
    }

    @Nested
    class FinishMatch {
        @Test
        void shouldNotThrowExceptionWhenFinishingNonExistingMatch() {
            assertThatCode(() -> scoreboard.finishMatch("team A", "team B"))
                    .doesNotThrowAnyException();
        }

        @ParameterizedTest
        @NullTestCases
        void shouldThrowExceptionWhenTeamNameIsNull(String homeTeamName, String awayTeamName) {
            assertThatThrownBy(() -> scoreboard.finishMatch(homeTeamName, awayTeamName))
                    .isInstanceOf(DomainValidationException.class)
                    .hasMessageContaining("cannot be null");
        }

        @Test
        void shouldAllowStartMatchAfterFinishingIt() {
            scoreboard.startMatch("team A", "team B");
            scoreboard.finishMatch("team A", "team B");

            assertThatCode(() -> scoreboard.startMatch("team A", "team B"))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    class GetMatchesSummary {
        @Test
        void shouldHandleEmptyScoreboard() {
            assertThat(scoreboard.getMatchesSummary()).isEmpty();
        }

        @Test
        void shouldReturnSummaryOfSavedMatch() {
            scoreboard.startMatch("home team", "away team");

            assertThat(scoreboard.getMatchesSummary())
                    .singleElement()
                    .extracting(MatchSummary::summary)
                    .isEqualTo("home team 0 - away team 0");
        }

        @Test
        void shouldReturnSummaryOfMatchWithUpdatedScore() {
            scoreboard.startMatch("home team", "away team");
            scoreboard.updateMatch("home team", "away team", new MatchScore(1, 3));

            assertThat(scoreboard.getMatchesSummary())
                    .singleElement()
                    .extracting(MatchSummary::summary)
                    .isEqualTo("home team 1 - away team 3");
        }

        @Test
        void shouldReturnNoSummaryWhenAllMatchesFinished() {
            scoreboard.startMatch("home team", "away team");
            scoreboard.updateMatch("home team", "away team", new MatchScore(1, 3));
            scoreboard.finishMatch("home team", "away team");

            assertThat(scoreboard.getMatchesSummary()).isEmpty();
        }

        @Test
        void shouldReturnSummaryOfReplayedMatch() {
            scoreboard.startMatch("home team", "away team");
            scoreboard.finishMatch("home team", "away team");
            scoreboard.startMatch("home team", "away team");

            assertThat(scoreboard.getMatchesSummary())
                    .singleElement()
                    .extracting(MatchSummary::summary)
                    .isEqualTo("home team 0 - away team 0");
        }

        @Test
        void shouldSortMatchesByTotalSumOfGoalsDesc() {
            scoreboard.startMatch("team A", "team B");
            scoreboard.startMatch("team C", "team D");
            scoreboard.updateMatch("team C", "team D", new MatchScore(0, 1));

            assertThat(scoreboard.getMatchesSummary())
                    .extracting(MatchSummary::summary)
                    .containsExactly("team C 0 - team D 1", "team A 0 - team B 0");
        }

        @Test
        @DisplayName("Complex example from the exercise")
        void shouldSortMatches() {
            scoreboard.startMatch("Mexico", "Canada");
            scoreboard.startMatch("Spain", "Brazil");
            scoreboard.startMatch("Germany", "France");
            scoreboard.startMatch("Uruguay", "Italy");
            scoreboard.startMatch("Argentina", "Australia");

            scoreboard.updateMatch("Mexico", "Canada", new MatchScore(0, 5));
            scoreboard.updateMatch("Spain", "Brazil", new MatchScore(10, 2));
            scoreboard.updateMatch("Germany", "France", new MatchScore(2, 2));
            scoreboard.updateMatch("Uruguay", "Italy", new MatchScore(0, 6));
            scoreboard.updateMatch("Uruguay", "Italy", new MatchScore(6, 6));
            scoreboard.updateMatch("Argentina", "Australia", new MatchScore(3, 1));

            assertThat(scoreboard.getMatchesSummary())
                    .extracting(MatchSummary::summary)
                    .containsExactly(
                            "Uruguay 6 - Italy 6",
                            "Spain 10 - Brazil 2",
                            "Mexico 0 - Canada 5",
                            "Argentina 3 - Australia 1",
                            "Germany 2 - France 2");
        }
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @CsvSource(value = {"null, valid team name", "valid team name, null"}, nullValues = "null")
    public @interface NullTestCases {
    }
}