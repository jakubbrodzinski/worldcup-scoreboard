package com.worldcup.scoreboard;

import com.worldcup.scoreboard.exceptions.DomainValidationException;
import com.worldcup.scoreboard.exceptions.MatchNotFoundException;
import com.worldcup.scoreboard.exceptions.TeamPartOfLiveMatchException;

import java.time.Instant;
import java.util.*;

import static com.worldcup.scoreboard.MatchesOrderingPolicies.highestScoringMatchesFirst;
import static com.worldcup.scoreboard.MatchesOrderingPolicies.recentlyStartedMatchesFirst;
import static java.util.Collections.emptyList;

public class Scoreboard {
    private final InMemoryMatchRepository matchRepository;

    public static Scoreboard defaultInstance() {
        return new Scoreboard(
                new Scoreboard.InMemoryMatchRepository(
                        highestScoringMatchesFirst()
                                .thenComparing(recentlyStartedMatchesFirst())));
    }

    private Scoreboard(InMemoryMatchRepository repository) {
        this.matchRepository = repository;
    }

    public void startMatch(String homeTeamName, String awayTeamName) {
        validateNonNull(homeTeamName, awayTeamName);
        validateForLiveMatch(homeTeamName);
        validateForLiveMatch(awayTeamName);

        matchRepository.save(new Match(homeTeamName, awayTeamName, Instant.now()));
    }

    public void updateMatch(String homeTeamName, String awayTeamName, MatchScore matchScore) {
        validateNonNull(homeTeamName, awayTeamName);

        matchRepository.findByTeamNames(homeTeamName, awayTeamName)
                .map(match -> match.update(matchScore))
                .ifPresentOrElse(matchRepository::save, () -> {
                    throw new MatchNotFoundException(homeTeamName, awayTeamName);
                });
    }

    public void finishMatch(String homeTeamName, String awayTeamName) {
        validateNonNull(homeTeamName, awayTeamName);
        matchRepository.deleteByTeamNames(homeTeamName, awayTeamName);
    }

    public List<MatchSummary> getSummary() {
        return matchRepository.queryOrdered().stream()
                .map(MatchSummary::from)
                .toList();
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

    private static class InMemoryMatchRepository {
        private static final String KEY_SEPARATOR = "#";

        private final Set<String> teamsWithLiveMatch = new HashSet<>();
        private final Map<String, Match> liveMatchesByKey = new HashMap<>();
        private List<Match> matchesOrderedIndex = emptyList();

        private final Comparator<Match> matchesOrderingPolicy;

        private InMemoryMatchRepository(Comparator<Match> matchesOrderingPolicy) {
            this.matchesOrderingPolicy = matchesOrderingPolicy;
        }

        List<Match> queryOrdered() {
            return matchesOrderedIndex;
        }

        Match save(Match match) {
            teamsWithLiveMatch.add(match.homeTeamName());
            teamsWithLiveMatch.add(match.awayTeamName());
            liveMatchesByKey.put(buildKey(match), match);
            rebuildOrderedIndex();
            return match;
        }

        boolean existsByTeamName(String teamName) {
            return teamsWithLiveMatch.contains(teamName);
        }

        Optional<Match> findByTeamNames(String homeTeamName, String awayTeamName) {
            return Optional.ofNullable(liveMatchesByKey.get(buildKey(homeTeamName, awayTeamName)));
        }

        void deleteByTeamNames(String homeTeamName, String awayTeamName) {
            teamsWithLiveMatch.remove(homeTeamName);
            teamsWithLiveMatch.remove(awayTeamName);
            liveMatchesByKey.remove(buildKey(homeTeamName, awayTeamName));
            rebuildOrderedIndex();
        }

        private void rebuildOrderedIndex() {
            this.matchesOrderedIndex = this.liveMatchesByKey.values().stream()
                    .sorted(this.matchesOrderingPolicy)
                    .toList();
        }

        private String buildKey(String homeTeamName, String awayTeamName) {
            return homeTeamName + KEY_SEPARATOR + awayTeamName;
        }

        private String buildKey(Match match) {
            return buildKey(match.homeTeamName(), match.awayTeamName());
        }
    }
}
