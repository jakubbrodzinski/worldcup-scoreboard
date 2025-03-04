package com.worldcup.scoreboard;

import com.worldcup.scoreboard.exceptions.DomainValidationException;

public record MatchScore(int homeTeamScore, int awayTeamScore) {
    public MatchScore {
        validateScoresNotNegative(homeTeamScore, awayTeamScore);
    }

    private void validateScoresNotNegative(int homeTeamScore, int awayTeamScore) {
        if (homeTeamScore < 0 || awayTeamScore < 0) {
            throw new DomainValidationException("Team score cannot be a negative number");
        }
    }
}
