package util;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestUtils {
    private static final Logger log = LoggerFactory.getLogger(HttpRequestUtils.class);

    public static String getMethod(String firstLine) {
        String[] splited = firstLine.split(" ");
        String method = splited[0];
        log.debug("method : {}", method);
        return method;
    }

    public static String getUrl(String firstLine) {
        String[] splited = firstLine.split(" ");
        String path = splited[1];
        log.debug("request path : {}", path);
        return path;
    }

    public static int getContentLength(String line) {
        String[] headerTokens = line.split(":");
        return Integer.parseInt(headerTokens[1].trim());
    }

    public static boolean isLogined(String line) {
        String[] headerTokens = line.split(":");
        Map<String, String> cookies = parseCookies(headerTokens[1].trim());
        String value = cookies.get("logined");
        if (value == null) {
            return false;
        }
        return Boolean.parseBoolean(value);
    }

    /**
     * @param queryString
     *            URL에서 ? 이후에 전달되는 field1=value1&field2=value2 형식임
     * @return
     */
    public static Map<String, String> parseQueryString(String queryString) {
        return parseValues(queryString, "&");
    }

    /**
     * @param cookies
     *            값은 name1=value1; name2=value2 형식임
     * @return
     */
    public static Map<String, String> parseCookies(String cookies) {
        return parseValues(cookies, ";");
    }

    private static Map<String, String> parseValues(String values, String separator) {
        if (Strings.isNullOrEmpty(values)) {
            return Maps.newHashMap();
        }

        String[] tokens = values.split(separator);
        return Arrays.stream(tokens).map(t -> getKeyValue(t, "=")).filter(p -> p != null)
                .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
    }

    static Pair getKeyValue(String keyValue, String regex) {
        if (Strings.isNullOrEmpty(keyValue)) {
            return null;
        }

        String[] tokens = keyValue.split(regex);
        if (tokens.length != 2) {
            return null;
        }

        return new Pair(tokens[0], tokens[1]);
    }

    public static Pair parseHeader(String header) {
        return getKeyValue(header, ": ");
    }

    public static class Pair {
        String key;
        String value;

        Pair(String key, String value) {
            this.key = key.trim();
            this.value = value.trim();
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((key == null) ? 0 : key.hashCode());
            result = prime * result + ((value == null) ? 0 : value.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Pair other = (Pair) obj;
            if (key == null) {
                if (other.key != null)
                    return false;
            } else if (!key.equals(other.key))
                return false;
            if (value == null) {
                if (other.value != null)
                    return false;
            } else if (!value.equals(other.value))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return "Pair [key=" + key + ", value=" + value + "]";
        }
    }

//    public static String getUrl(String line) {
//        String url = "/index.html";
//        if (line != null) {
//            String[] tokens = line.split(" ");
//            if (tokens.length == 3) {
//                if (!tokens[1].equals("/")) {
//                    url = tokens[1];
//                }
//            }
//        }
//        return url;
//    }

    public static byte[] getBody(String url) throws IOException {
        return Files.readAllBytes(new File("./webapp" + url).toPath());
    }

//    public static boolean isHtml(String url) {
//        return url.endsWith(".html");
//    }
//
//    public static Map<String, String> getParams(String url) {
//        Map<String, String> params = new HashMap<>();
//        String[] tokens = url.split("\\?");
//        if (tokens.length == 2) {
//            params = parseQueryString(tokens[1]);
//        }
//        return params;
//    }
//
//    public static User paramsToUser(Map<String, String> params) {
//        String userId = params.get("userId");
//        String password = params.get("password");
//        String name = params.get("name");
//        String email = params.get("email");
//        return new User(userId, password, name, email);
//    }
}
