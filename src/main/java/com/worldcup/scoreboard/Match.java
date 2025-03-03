package com.worldcup.scoreboard;

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
}
