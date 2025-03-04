package com.worldcup.scoreboard.exceptions;

public class MatchNotFoundException extends RuntimeException {
    private static final String ERROR_FORMAT_TEMPLATE = "Match between %s and %s not found";

    public MatchNotFoundException(String homeTeam, String awayTeam) {
        super(ERROR_FORMAT_TEMPLATE.formatted(homeTeam, awayTeam));
    }
}
