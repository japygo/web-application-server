package http;

public enum HttpStatus {
    OK(200, "OK"),
    FOUND(302, "Found"),
    ;

    private final int value;
    private final String message;

    HttpStatus(int value, String message) {
        this.value = value;
        this.message = message;
    }

    public int value() {
        return value;
    }

    public String message() {
        return message;
    }
}
