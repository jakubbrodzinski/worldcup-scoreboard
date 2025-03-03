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

    @ParameterizedTest
    @CsvSource({"-1,0", "2,-3", "-3,-1"})
    void shouldThrowExceptionWhenScoreIsNegative(int homeTeamScore, int awayTeamScore) {
        assertThatThrownBy(() -> new Match("team-A", "team-B", homeTeamScore, awayTeamScore, Instant.now()))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Match score cannot be negative");
    }
}