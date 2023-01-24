package webserver;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpRequestTest {
    @Test
    public void request_GET() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("GET /user/create?userId=javajigi&password=password&name=JaeSung HTTP/1.1\r\n");
        sb.append("HOST: localhost:8080\r\n");
        sb.append("Connection: keep-alive\r\n");
        sb.append("Accept: */*\r\n");
        sb.append("\r\n");
        InputStream in = new ByteArrayInputStream(sb.toString().getBytes());
        HttpRequest request = new HttpRequest(in);

        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(request.getPath()).isEqualTo("/user/create");
        assertThat(request.getHeader("Connection")).isEqualTo("keep-alive");
        assertThat(request.getParameter("userId")).isEqualTo("javajigi");
    }

    @Test
    public void request_POST() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("POST /user/create HTTP/1.1\r\n");
        sb.append("Host: localhost:8080\r\n");
        sb.append("Connection: keep-alive\r\n");
        sb.append("Content-Length: 46\r\n");
        sb.append("Content-Type: application/x-www-form-urlencoded\r\n");
        sb.append("Accept: */*\r\n");
        sb.append("\r\n");
        sb.append("userId=javajigi&password=password&name=JaeSung");
        InputStream in = new ByteArrayInputStream(sb.toString().getBytes());
        HttpRequest request = new HttpRequest(in);

        assertThat(request.getMethod()).isEqualTo("POST");
        assertThat(request.getPath()).isEqualTo("/user/create");
        assertThat(request.getHeader("Connection")).isEqualTo("keep-alive");
        assertThat(request.getParameter("userId")).isEqualTo("javajigi");
    }
}
