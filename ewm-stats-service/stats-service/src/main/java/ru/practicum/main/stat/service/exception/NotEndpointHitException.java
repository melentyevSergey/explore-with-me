package ru.practicum.main.stat.service.exception;

public class NotEndpointHitException extends RuntimeException {
    public NotEndpointHitException(String message) {
        super(message);
    }
}
