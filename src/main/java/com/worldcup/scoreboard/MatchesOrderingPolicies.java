package com.worldcup.scoreboard;

import java.util.Comparator;

import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingInt;

class MatchesOrderingPolicies {
    private MatchesOrderingPolicies() {
    }

    static Comparator<Match> highestScoringMatchesFirst() {
        return comparingInt((Match match) -> match.homeTeamScore() + match.awayTeamScore()).reversed();
    }

    static Comparator<Match> recentlyStartedMatchesFirst() {
        return comparing(Match::startMatchTime).reversed();
    }
}
