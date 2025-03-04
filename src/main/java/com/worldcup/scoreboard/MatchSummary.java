package com.worldcup.scoreboard;

public record MatchSummary() {
    static MatchSummary from(Match match){
        return new MatchSummary();
    }
}
