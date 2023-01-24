package webserver;

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
    private String method = null;
    private String path = null;
    private Map<String, String> header = new HashMap<>();
    private Map<String, String> parameter = new HashMap<>();

    public HttpRequest(InputStream in) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String line = br.readLine();
            if (line == null) throw new Exception();
            this.method = HttpRequestUtils.getMethod(line);
            this.path = HttpRequestUtils.getUrl(line);
            while (line != null && !"".equals(line)) {
                line = br.readLine();
                log.debug("header : {}", line);
                if (line != null && !"".equals(line)) {
                    String[] headerTokens = line.split(":");
                    if (headerTokens.length == 2) {
                        header.put(headerTokens[0].trim(), headerTokens[1].trim());
                    }
                }
            }
            if ("GET".equals(method)) {
                if (path.contains("?")) {
                    String[] pathTokens = this.path.split("\\?");
                    if (pathTokens.length == 2) {
                        this.path = pathTokens[0];
                        this.parameter = HttpRequestUtils.parseQueryString(pathTokens[1]);
                    }
                }
            }
            if ("POST".equals(method)) {
                String requestBody = IOUtils.readData(br, Integer.parseInt(getHeader("Content-Length")));
                this.parameter = HttpRequestUtils.parseQueryString(requestBody);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        log.debug("method : {}, path : {}, header : {}, parameter : {}", method, path, header, parameter);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getHeader(String key) {
        return header.get(key);
    }

    public String getParameter(String key) {
        return parameter.get(key);
    }
}
