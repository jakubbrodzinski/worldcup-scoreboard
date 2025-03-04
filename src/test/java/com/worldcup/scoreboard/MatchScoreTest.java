package com.worldcup.scoreboard;

import com.worldcup.scoreboard.exceptions.DomainValidationException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MatchScoreTest {
    @ParameterizedTest
    @CsvSource({"-1,0", "2,-3"})
    void shouldThrowExceptionWhenScoreIsNegative(int homeTeamScore, int awayTeamScore) {
        assertThatThrownBy(() -> new MatchScore(homeTeamScore, awayTeamScore))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Team score cannot be a negative number");
    }
}