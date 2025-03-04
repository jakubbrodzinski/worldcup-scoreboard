package com.worldcup.scoreboard;

import com.worldcup.scoreboard.exceptions.DomainValidationException;

import java.time.Instant;

record Match(
        String homeTeamName,
        String awayTeamName,
        int homeTeamScore,
        int awayTeamScore,
        Instant startMatchTime) {

    Match(String homeTeamName, String awayTeamName, Instant startMatchTime) {
        this(homeTeamName, awayTeamName, 0, 0, startMatchTime);
    }

    Match {
        validateTeamName(homeTeamName);
        validateTeamName(awayTeamName);
        homeTeamName = homeTeamName.strip();
        awayTeamName = awayTeamName.strip();
        validateTeamsUniqueness(homeTeamName, awayTeamName);
        validateScoresNotNegative(homeTeamScore, awayTeamScore);
    }

    Match update(MatchScore matchScore) {
        return new Match(homeTeamName, awayTeamName, matchScore.homeTeamScore(), matchScore.awayTeamScore(), startMatchTime);
    }

    private void validateTeamName(String teamName) {
        if (teamName == null || teamName.isBlank()) {
            throw new DomainValidationException("Team's name cannot be null nor blank");
        }
    }

    private void validateTeamsUniqueness(String homeTeamName, String awayTeamName) {
        if (homeTeamName.equals(awayTeamName)) {
            throw new DomainValidationException("Names of the teams has to be unique");
        }
    }

    private void validateScoresNotNegative(int homeTeamScore, int awayTeamScore) {
        if (homeTeamScore < 0 || awayTeamScore < 0) {
            throw new DomainValidationException("Team score cannot be a negative number");
        }
    }
}
