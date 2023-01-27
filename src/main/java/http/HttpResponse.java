package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);
    private final DataOutputStream dos;
    private final Map<String, String> headers = new HashMap<>();

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String LOCATION = "Location";
    public static final String SET_COOKIE = "Set-Cookie";

    public HttpResponse(OutputStream out) {
        this.dos = new DataOutputStream(out);
    }

    public void forward(String url) {
        try {
            ContentType contentType = ContentType.HTML;
            if (url.endsWith(".css")) {
                contentType = ContentType.CSS;
            } else if (url.endsWith(".js")){
                contentType = ContentType.JS;
            }
            byte[] body = HttpRequestUtils.getBody(url);
            addHeader(CONTENT_TYPE, contentType.value());
            addHeader(CONTENT_LENGTH, String.valueOf(body.length));

            responseHeader(HttpStatus.OK);
            processHeaders();
            responseBody(body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void sendRedirect(String path) {
        addHeader(LOCATION, path);

        responseHeader(HttpStatus.FOUND);
        processHeaders();
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    private void processHeaders() {
        try {
            headers.forEach((key, value) -> {
                try {
                    dos.writeBytes(key + ": " + value + " \r\n");
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            });
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseHeader(HttpStatus status) {
        try {
            ResponseLine line = new ResponseLine(status);
            dos.writeBytes(line.getLine() + "\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void forwardBody(String data) {
        byte[] body = data.getBytes();
        addHeader(CONTENT_TYPE, ContentType.HTML.value());
        addHeader(CONTENT_LENGTH, String.valueOf(body.length));

        responseHeader(HttpStatus.OK);
        processHeaders();
        responseBody(body);
    }
}
