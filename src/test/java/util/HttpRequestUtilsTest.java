package util;

import org.junit.jupiter.api.Test;
import util.HttpRequestUtils.Pair;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpRequestUtilsTest {
    @Test
    public void parseQueryString() {
        String queryString = "userId=javajigi";
        Map<String, String> parameters = HttpRequestUtils.parseQueryString(queryString);
        assertThat(parameters.get("userId")).isEqualTo("javajigi");
        assertThat(parameters.get("password")).isNull();

        queryString = "userId=javajigi&password=password2";
        parameters = HttpRequestUtils.parseQueryString(queryString);
        assertThat(parameters.get("userId")).isEqualTo("javajigi");
        assertThat(parameters.get("password")).isEqualTo("password2");
    }

    @Test
    public void parseQueryString_null() {
        Map<String, String> parameters = HttpRequestUtils.parseQueryString(null);
        assertThat(parameters).isEmpty();

        parameters = HttpRequestUtils.parseQueryString("");
        assertThat(parameters).isEmpty();

        parameters = HttpRequestUtils.parseQueryString(" ");
        assertThat(parameters).isEmpty();
    }

    @Test
    public void parseQueryString_invalid() {
        String queryString = "userId=javajigi&password";
        Map<String, String> parameters = HttpRequestUtils.parseQueryString(queryString);
        assertThat(parameters.get("userId")).isEqualTo("javajigi");
        assertThat(parameters.get("password")).isNull();
    }

    @Test
    public void parseCookies() {
        String cookies = "logined=true; JSessionId=1234";
        Map<String, String> parameters = HttpRequestUtils.parseCookies(cookies);
        assertThat(parameters.get("logined")).isEqualTo("true");
        assertThat(parameters.get("JSessionId")).isEqualTo("1234");
        assertThat(parameters.get("session")).isNull();
    }

    @Test
    public void getKeyValue() throws Exception {
        Pair pair = HttpRequestUtils.getKeyValue("userId=javajigi", "=");
        assertThat(pair).isEqualTo(new Pair("userId", "javajigi"));
    }

    @Test
    public void getKeyValue_invalid() throws Exception {
        Pair pair = HttpRequestUtils.getKeyValue("userId", "=");
        assertThat(pair).isNull();
    }

    @Test
    public void parseHeader() throws Exception {
        String header = "Content-Length: 59";
        Pair pair = HttpRequestUtils.parseHeader(header);
        assertThat(pair).isEqualTo(new Pair("Content-Length", "59"));
    }

//    @Test
//    public void getUrl() {
//        String line = "GET /index.html HTTP/1.1";
//        String url = HttpRequestUtils.getUrl(line);
//        assertThat(url).isEqualTo("/index.html");
//
//        line = "GET / HTTP/1.1";
//        url = HttpRequestUtils.getUrl(line);
//        assertThat(url).isEqualTo("/index.html");
//    }
//
//    @Test
//    public void getBody() throws IOException {
//        String url = "/index.html";
//        byte[] body = HttpRequestUtils.getBody(url);
//        assertThat(body.length).isEqualTo(10276);
//        assertThat(new String(body)).contains("SLiPP Java Web Programming");
//    }
//
//    @Test
//    public void isHtml() {
//        String url = "/index.html";
//        boolean isHtml = HttpRequestUtils.isHtml(url);
//        assertThat(isHtml).isTrue();
//
//        url = "/user/create";
//        isHtml = HttpRequestUtils.isHtml(url);
//        assertThat(isHtml).isFalse();
//    }
//
//    @Test
//    public void getParams() {
//        String url = "/user/create?userId=javajigi&password=password&name=JaeSung&email=javajigi%40slipp.net";
//        Map<String, String> params = HttpRequestUtils.getParams(url);
//        assertThat(params.get("userId")).isEqualTo("javajigi");
//        assertThat(params.get("password")).isEqualTo("password");
//        assertThat(params.get("name")).isEqualTo("JaeSung");
//        assertThat(params.get("email")).isEqualTo("javajigi%40slipp.net");
//    }
//
//    @Test
//    public void paramsToUser() {
//        Map<String, String> params = new HashMap<>();
//        params.put("userId", "javajigi");
//        params.put("password", "password");
//        User user = HttpRequestUtils.paramsToUser(params);
//        assertThat(user.getUserId()).isEqualTo("javajigi");
//        assertThat(user.getPassword()).isEqualTo("password");
//    }
}
