package com.worldcup.scoreboard;

import com.worldcup.scoreboard.exceptions.TeamPartOfLiveMatchException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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

        @Test
        void shouldThrowExceptionWhenTeamIsPartOfDifferentLiveMatch() {
            scoreboard.startMatch("dummy team", "team A");

            assertThatThrownBy(() -> scoreboard.startMatch("team A", "another dummy team"))
                    .isInstanceOf(TeamPartOfLiveMatchException.class)
                    .hasMessageContaining("team A");
        }
    }

    @Nested
    class UpdateMatch {

    }

    @Nested
    class FinishMatch {

    }

    @Nested
    class GetSummary {

    }
}