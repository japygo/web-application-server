package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> parameters = new HashMap<>();
    private Map<String, String> cookies = new HashMap<>();
    private RequestLine requestLine;

    public HttpRequest(InputStream in) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String line = br.readLine();
            if (line == null) return;

            requestLine = new RequestLine(line);

            while (line != null && !"".equals(line)) {
                line = br.readLine();
                log.debug("header : {}", line);
                if (line != null && !"".equals(line)) {
                    String[] headerTokens = line.split(":");
                    if (headerTokens.length == 2) {
                        headers.put(headerTokens[0].trim(), headerTokens[1].trim());
                    }
                }
            }

            cookies = HttpRequestUtils.parseCookies(getHeader("Cookie"));

            if (getMethod().isPost()) {
                String requestBody = IOUtils.readData(br, Integer.parseInt(getHeader("Content-Length")));
                this.parameters = HttpRequestUtils.parseQueryString(requestBody);
            } else {
                this.parameters = requestLine.getParams();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        log.debug("method : {}, path : {}, header : {}, parameter : {}", getMethod(), getPath(), headers, parameters);
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }

    public String getCookie(String key) {
        return cookies.get(key);
    }
}
