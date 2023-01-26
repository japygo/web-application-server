package webserver;

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
    private final Map<String, String> header = new HashMap<>();

    public HttpResponse(OutputStream out) {
        this.dos = new DataOutputStream(out);
    }

    public void forward(String path) {
        try {
            responseHeader(200);
            processHeaders();
            if (path.endsWith(".css")) {
                dos.writeBytes("Content-Type: text/css\r\n");
            } else {
                dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            }
            if ("/".equals(path)) {
                path = "/index.html";
            }
            byte[] body = HttpRequestUtils.getBody(path);
            dos.writeBytes("Content-Length: " + body.length + "\r\n");
            dos.writeBytes("\r\n");
            responseBody(body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void sendRedirect(String path) {
        try {
            addHeader("Location", path);
            responseHeader(302);
            processHeaders();
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void addHeader(String key, String value) {
        header.put(key, value);
    }

    private void processHeaders() {
        header.forEach((key, value) -> {
            try {
                dos.writeBytes(key + ": " + value + " \r\n");
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        });
    }

    private void responseHeader(int code) {
        try {
            switch (code) {
                case 200:
                    dos.writeBytes("HTTP/1.1 200 OK \r\n");
                    break;
                case 302:
                    dos.writeBytes("HTTP/1.1 302 Found \r\n");
                    break;
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void forwardBody(String data) {
        try {
            responseHeader(200);
            processHeaders();
            byte[] body = data.getBytes();
            dos.writeBytes("Content-Length: " + body.length + "\r\n");
            dos.writeBytes("\r\n");
            responseBody(body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
