package com.worldcup.scoreboard;

import com.worldcup.scoreboard.exceptions.DomainValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MatchTest {
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