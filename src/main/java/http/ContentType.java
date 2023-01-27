package http;

public enum ContentType {
    HTML("text/html;charset=utf-8"),
    CSS("text/css"),
    JS("application/javascript"),
    ;

    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
