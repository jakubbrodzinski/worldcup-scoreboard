package com.worldcup.scoreboard.exceptions;

public class DomainValidationException extends IllegalStateException {
    public DomainValidationException(String message) {
        super(message);
    }
}
