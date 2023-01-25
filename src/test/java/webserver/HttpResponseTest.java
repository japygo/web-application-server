package webserver;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpResponseTest {
    private OutputStream out;

    @BeforeEach
    public void setting() {
        out = new ByteArrayOutputStream();
    }

    @AfterEach
    public void finish() {
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void responseForward() throws Exception {
        HttpResponse response = new HttpResponse(out);
        response.forward("/index.html");

        String result = out.toString();
        assertThat(result).contains("<title>SLiPP Java Web Programming</title>")
                .contains("Content-Length: 10276");
    }

    @Test
    public void responseRedirect() throws Exception {
        HttpResponse response = new HttpResponse(out);
        response.sendRedirect("/index.html");

        String result = out.toString();
        assertThat(result).contains("HTTP/1.1 302 Found")
                .contains("Location: /index.html");
    }

    @Test
    public void responseCookies() throws Exception {
        HttpResponse response = new HttpResponse(out);
        response.addHeader("Set-Cookie", "logined=true");
        response.sendRedirect("/index.html");

        String result = out.toString();
        assertThat(result).contains("HTTP/1.1 302 Found")
                .contains("Location: /index.html")
                .contains("Set-Cookie: logined=true");
    }
}
