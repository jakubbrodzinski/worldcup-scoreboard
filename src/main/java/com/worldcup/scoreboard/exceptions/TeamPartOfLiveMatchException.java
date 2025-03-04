package com.worldcup.scoreboard.exceptions;

public class TeamPartOfLiveMatchException extends RuntimeException {
    private static final String ERROR_FORMAT_TEMPLATE = "%s part of another ongoing match stored in scoreboard";

    public TeamPartOfLiveMatchException(String teamName) {
        super(ERROR_FORMAT_TEMPLATE.formatted(teamName));
    }
}
