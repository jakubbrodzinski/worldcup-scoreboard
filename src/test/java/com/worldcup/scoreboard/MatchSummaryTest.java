package com.worldcup.scoreboard;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class MatchSummaryTest {
    @Test
    void shouldCreateMatchSummary() {
        var match = new Match("Spain", "England", 3, 6, Instant.ofEpochSecond(1));

        var matchSummary = MatchSummary.from(match);

        assertThat(matchSummary.summary()).isEqualTo("Spain 3 - England 6");
    }
}