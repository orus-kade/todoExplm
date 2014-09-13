package ru.dokwork.todo;

public class WronAssertionException extends RuntimeException {
    public WronAssertionException() {
    }

    public WronAssertionException(String message) {
        super(message);
    }

    public WronAssertionException(String message, Throwable cause) {
        super(message, cause);
    }

    public WronAssertionException(Throwable cause) {
        super(cause);
    }
}
