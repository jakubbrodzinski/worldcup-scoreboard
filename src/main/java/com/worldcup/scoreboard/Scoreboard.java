package com.worldcup.scoreboard;

import com.worldcup.scoreboard.exceptions.TeamPartOfLiveMatchException;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class Scoreboard {
    private final InMemoryMatchRepository matchRepository = new Scoreboard.InMemoryMatchRepository();

    public void startMatch(String homeTeamName, String awayTeamName) {
        validateForOngoingMatch(homeTeamName);
        validateForOngoingMatch(awayTeamName);
        matchRepository.save(new Match(homeTeamName, awayTeamName, Instant.now()));
    }

    public void updateMatch(String homeTeamName, String awayTeamName, MatchScore matchScore) {
    }

    private void validateForOngoingMatch(String teamName) {
        if (matchRepository.existsByTeamName(teamName)) {
            throw new TeamPartOfLiveMatchException(teamName);
        }
    }

    //TODO Consider extracting it as a separate class.
    private class InMemoryMatchRepository {
        private Set<String> teamsWithLiveMatch = new HashSet<>();

        Match save(Match match) {
            teamsWithLiveMatch.add(match.homeTeamName());
            teamsWithLiveMatch.add(match.awayTeamName());
            return match;
        }

        boolean existsByTeamName(String teamName) {
            return teamsWithLiveMatch.contains(teamName);
        }
    }
}
