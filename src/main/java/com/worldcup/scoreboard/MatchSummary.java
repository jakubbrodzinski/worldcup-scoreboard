package com.worldcup.scoreboard;

public record MatchSummary(String summary) {
    static MatchSummary from(Match match){
        return new MatchSummary("");
    }
}
