package com.worldcup.scoreboard;

import static java.util.Objects.requireNonNull;

public record MatchSummary(String summary) {
    public MatchSummary {
        requireNonNull(summary);
    }

    static MatchSummary from(Match match) {
        return new MatchSummary(buildSummary(match));
    }

    private static String buildSummary(Match match) {
        return "%s %d - %d %s".formatted(match.homeTeamName(), match.homeTeamScore(), match.awayTeamScore(), match.awayTeamName());
    }
}
