package http;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RequestLineTest {
    @Test
    public void create_method() {
        RequestLine line = new RequestLine("GET /index.html HTTP/1.1");

        assertThat(line.getMethod()).isEqualTo(HttpMethod.GET);
        assertThat(line.getPath()).isEqualTo("/index.html");

        line = new RequestLine("POST /index.html HTTP/1.1");

        assertThat(line.getMethod()).isEqualTo(HttpMethod.POST);
        assertThat(line.getPath()).isEqualTo("/index.html");
    }

    @Test
    public void create_path_and_params() {
        RequestLine line = new RequestLine("GET /user/create?userId=javajigi&password=password HTTP/1.1");

        assertThat(line.getMethod()).isEqualTo(HttpMethod.GET);
        assertThat(line.getPath()).isEqualTo("/user/create");
        assertThat(line.getParams()).hasSize(2);
    }
}
