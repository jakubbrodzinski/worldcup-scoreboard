package com.worldcup.scoreboard.exceptions;

public class TeamPartOfLiveMatchException extends RuntimeException {
    public TeamPartOfLiveMatchException(String teamName) {
        super("%s part of another ongoing match stored in scorebaord".formatted(teamName));
    }
}
