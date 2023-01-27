package http;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ResponseLineTest {
    @Test
    public void create_line() {
        ResponseLine line = new ResponseLine(HttpStatus.OK);

        assertThat(line.getLine()).isEqualTo("HTTP/1.1 200 OK");

        line = new ResponseLine(HttpStatus.FOUND);

        assertThat(line.getLine()).isEqualTo("HTTP/1.1 302 Found");
    }
}
