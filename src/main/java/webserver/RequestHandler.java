package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.UserService;
import util.HttpRequestUtils;
import util.IOUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private static final UserService userService = new UserService();

    private final Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String line = br.readLine();
            log.debug("HTTP Header : {}", line);
            String url = HttpRequestUtils.getUrl(line);
            byte[] body;
            if (HttpRequestUtils.isHtml(url)) {
                body = HttpRequestUtils.getBody(url);
                DataOutputStream dos = new DataOutputStream(out);
                response200Header(dos, body.length);
                responseBody(dos, body);
            } else if (url.endsWith(".css")) {
                body = HttpRequestUtils.getBody(url);
                DataOutputStream dos = new DataOutputStream(out);
                response200Header(dos, body.length);
                responseBody(dos, body);
            } else {
                if (url.startsWith("/user/create")) {
                    Map<String, String> params;
                    if (line.startsWith("GET")) {
                        params = HttpRequestUtils.getParams(url);
                    } else {
                        int contentLength = 0;
                        while ((line = br.readLine()) != null) {
                            if (line.startsWith("Content-Length")) {
                                contentLength = Integer.parseInt(HttpRequestUtils.parseHeader(line).getValue());
                            }
                            if (line.equals("")) {
                                break;
                            }
                        }
                        params = HttpRequestUtils.parseQueryString(IOUtils.readData(br, contentLength));
                    }
                    boolean result = userService.createUser(HttpRequestUtils.paramsToUser(params));
                    body = HttpRequestUtils.getBody("/index.html");
                    DataOutputStream dos = new DataOutputStream(out);
                    response302Header(dos, body.length);
                    responseBody(dos, body);
                } else if (url.startsWith("/user/login")) {
                    Map<String, String> params;
                    int contentLength = 0;
                    while ((line = br.readLine()) != null) {
                        if (line.startsWith("Content-Length")) {
                            contentLength = Integer.parseInt(HttpRequestUtils.parseHeader(line).getValue());
                        }
                        if (line.equals("")) {
                            break;
                        }
                    }
                    params = HttpRequestUtils.parseQueryString(IOUtils.readData(br, contentLength));
                    String userId = params.get("userId");
                    String password = params.get("password");
                    String cookie = "";
                    boolean result = userService.loginUser(userId, password);
                    if (result) {
                        body = HttpRequestUtils.getBody("/index.html");
                        cookie = "logined=true";
                    } else {
                        body = HttpRequestUtils.getBody("/user/login_failed.html");
                        cookie = "logined=false";
                    }
                    DataOutputStream dos = new DataOutputStream(out);
                    responseCookie(dos, body.length, cookie);
                    responseBody(dos, body);
                } else if (url.startsWith("/user/list")) {
                    Map<String, String> cookie = new HashMap<>();
                    while ((line = br.readLine()) != null) {
                        if (line.startsWith("Cookie")) {
                            log.debug(line);
                            cookie = HttpRequestUtils.parseCookies(HttpRequestUtils.parseHeader(line).getValue());
                        }
                        if (line.equals("")) {
                            break;
                        }
                    }
                    if (Boolean.parseBoolean(cookie.get("logined"))) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("<table>");
                        sb.append("<tr>");
                        sb.append("<th>아이디</th>");
                        sb.append("<th>이름</th>");
                        sb.append("<th>이메일</th>");
                        sb.append("</tr>");
                        userService.getUsers().forEach(user -> {
                            sb.append("<tr>");
                            sb.append("<td>").append(user.getUserId()).append("</td>");
                            sb.append("<td>").append(user.getName()).append("</td>");
                            sb.append("<td>").append(user.getName()).append("</td>");
                            sb.append("</tr>");
                        });
                        sb.append("</table>");
                        body = sb.toString().getBytes();
                    } else {
                        body = HttpRequestUtils.getBody("/user/login.html");
                    }
                    DataOutputStream dos = new DataOutputStream(out);
                    response200Header(dos, body.length);
                    responseBody(dos, body);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseCookie(DataOutputStream dos, int lengthOfBodyContent, String cookie) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("Set-Cookie: " + cookie + "\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
