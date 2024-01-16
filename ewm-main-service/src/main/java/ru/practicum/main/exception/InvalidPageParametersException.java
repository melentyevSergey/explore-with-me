package ru.practicum.main.exception;

public class InvalidPageParametersException extends RuntimeException {
    public InvalidPageParametersException(String message) {
        super(message);
    }
}