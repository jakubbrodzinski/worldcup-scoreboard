package com.worldcup.scoreboard;

import com.worldcup.scoreboard.exceptions.DomainValidationException;
import com.worldcup.scoreboard.exceptions.MatchNotFoundException;
import com.worldcup.scoreboard.exceptions.TeamPartOfLiveMatchException;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class Scoreboard {
    private final InMemoryMatchRepository matchRepository = new Scoreboard.InMemoryMatchRepository();

    public void startMatch(String homeTeamName, String awayTeamName) {
        validateNonNull(homeTeamName, awayTeamName);
        validateForLiveMatch(homeTeamName);
        validateForLiveMatch(awayTeamName);

        matchRepository.save(new Match(homeTeamName, awayTeamName, Instant.now()));
    }

    public void updateMatch(String homeTeamName, String awayTeamName, MatchScore matchScore) {
        validateNonNull(homeTeamName, awayTeamName);

        matchRepository.findByTeamNames(homeTeamName, awayTeamName)
                .orElseThrow(() -> new MatchNotFoundException(homeTeamName, awayTeamName));
    }

    private void validateForLiveMatch(String teamName) {
        if (matchRepository.existsByTeamName(teamName)) {
            throw new TeamPartOfLiveMatchException(teamName);
        }
    }

    private void validateNonNull(String homeTeamName, String awayTeamName) {
        if (homeTeamName == null || awayTeamName == null) {
            throw new DomainValidationException("Teams' name cannot be null");
        }
    }

    //TODO Consider extracting it as a separate class.
    private class InMemoryMatchRepository {
        private static final String KEY_SEPARATOR = "#";
        private Set<String> teamsWithLiveMatch = new HashSet<>();
        private HashMap<String, Match> liveMatchesByKey = new HashMap<>();

        Match save(Match match) {
            teamsWithLiveMatch.add(match.homeTeamName());
            teamsWithLiveMatch.add(match.awayTeamName());
            liveMatchesByKey.put(buildKey(match), match);
            return match;
        }

        boolean existsByTeamName(String teamName) {
            return teamsWithLiveMatch.contains(teamName);
        }

        Optional<Match> findByTeamNames(String homeTeamName, String awayTeamName) {
            return Optional.ofNullable(liveMatchesByKey.get(buildKey(homeTeamName, awayTeamName)));
        }

        private String buildKey(String homeTeamName, String awayTeamName) {
            return homeTeamName + KEY_SEPARATOR + awayTeamName;
        }

        private String buildKey(Match match) {
            return buildKey(match.homeTeamName(), match.awayTeamName());
        }
    }
}
