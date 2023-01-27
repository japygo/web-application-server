package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseLine {
    private static final Logger log = LoggerFactory.getLogger(ResponseLine.class);
    private final String version = "HTTP/1.1";
    private final HttpStatus status;

    public ResponseLine(HttpStatus status) {
        this.status = status;
    }

    public String getVersion() {
        return version;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getLine() {
        return version + " " + status.value() + " " + status.message();
    }
}
