package webserver;

import controller.Controller;
import http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            HttpRequest request = new HttpRequest(in);
            HttpResponse response = new HttpResponse(out);

            if (request.getCookie(HttpSessions.SESSION_ID) == null) {
                HttpSession session = request.getSession();
                response.addHeader(HttpResponse.SET_COOKIE, HttpSessions.SESSION_ID + "=" + session.getId());
            }

            String path = getDefaultPath(request.getPath());
            Controller controller = RequestMapping.getController(path);
            if (controller != null) {
                controller.service(request, response);
            } else {
                response.forward(path);
            }

//            if (url.equals("/user/create")) {
//                User user = new User(request.getParameter("userId"), request.getParameter("password"), request.getParameter("name"), request.getParameter("email"));
//                log.debug("User : {}", user);
//                DataBase.addUser(user);
//
//                url = "/index.html";
//                response.sendRedirect(url);
//            } else if (url.equals("/user/login")) {
//                String userId = request.getParameter("userId");
//                String password = request.getParameter("password");
//                log.debug("userId : {}, password : {}", userId, password);
//
//                User user = DataBase.findUserById(userId);
//                if (user == null) {
//                    log.debug("User Not Found!");
//                    url = "/user/login_failed.html";
//                    response.addHeader("Set-Cookie", "logined=false");
//                    response.sendRedirect(url);
//                    return;
//                }
//
//                if (user.getPassword().equals(password)) {
//                    log.debug("Login Success!");
//                    url = "/index.html";
//                    response.addHeader("Set-Cookie", "logined=true");
//                    response.sendRedirect(url);
//                } else {
//                    log.debug("Password Mismatch!");
//                    url = "/user/login_failed.html";
//                    response.addHeader("Set-Cookie", "logined=false");
//                    response.sendRedirect(url);
//                }
//            } else if (url.equals("/user/list")) {
//                if (!logined) {
//                    url = "/user/login.html";
//                    response.addHeader("Set-Cookie", "logined=false");
//                    response.sendRedirect(url);
//                    return;
//                }
//                Collection<User> users = DataBase.findAll();
//                StringBuilder sb = new StringBuilder();
//                sb.append("<table border='1'>");
//                for (User user : users) {
//                    sb.append("<tr>");
//                    sb.append("<td>" + user.getUserId() + "</td>");
//                    sb.append("<td>" + user.getName() + "</td>");
//                    sb.append("<td>" + user.getEmail() + "</td>");
//                    sb.append("</tr>");
//                }
//                sb.append("</table>");
//                response.forwardBody(sb.toString());
//            } else {
//                response.forward(url);
//            }


//            String url = HttpRequestUtils.getUrl(line);
//            byte[] body;
//            if (HttpRequestUtils.isHtml(url)) {
//                body = HttpRequestUtils.getBody(url);
//                DataOutputStream dos = new DataOutputStream(out);
//                response200Header(dos, body.length);
//                responseBody(dos, body);
//            } else if (url.endsWith(".js")) {
//                body = HttpRequestUtils.getBody(url);
//                DataOutputStream dos = new DataOutputStream(out);
//                response200Header(dos, body.length);
//                responseBody(dos, body);
//            } else if (url.endsWith(".css")) {
//                body = HttpRequestUtils.getBody(url);
//                DataOutputStream dos = new DataOutputStream(out);
//                responseCss(dos, body.length);
//                responseBody(dos, body);
//            } else {
//                if (url.startsWith("/user/create")) {
//                    Map<String, String> params;
//                    if (line.startsWith("GET")) {
//                        params = HttpRequestUtils.getParams(url);
//                    } else {
//                        int contentLength = 0;
//                        while ((line = br.readLine()) != null) {
//                            if (line.startsWith("Content-Length")) {
//                                contentLength = Integer.parseInt(HttpRequestUtils.parseHeader(line).getValue());
//                            }
//                            if (line.equals("")) {
//                                break;
//                            }
//                        }
//                        params = HttpRequestUtils.parseQueryString(IOUtils.readData(br, contentLength));
//                    }
//                    boolean result = userService.createUser(HttpRequestUtils.paramsToUser(params));
//                    body = HttpRequestUtils.getBody("/index.html");
//                    DataOutputStream dos = new DataOutputStream(out);
//                    response302Header(dos, body.length);
//                    responseBody(dos, body);
//                } else if (url.startsWith("/user/login")) {
//                    Map<String, String> params;
//                    int contentLength = 0;
//                    while ((line = br.readLine()) != null) {
//                        if (line.startsWith("Content-Length")) {
//                            contentLength = Integer.parseInt(HttpRequestUtils.parseHeader(line).getValue());
//                        }
//                        if (line.equals("")) {
//                            break;
//                        }
//                    }
//                    params = HttpRequestUtils.parseQueryString(IOUtils.readData(br, contentLength));
//                    String userId = params.get("userId");
//                    String password = params.get("password");
//                    String cookie = "";
//                    boolean result = userService.loginUser(userId, password);
//                    if (result) {
//                        body = HttpRequestUtils.getBody("/index.html");
//                        cookie = "logined=true";
//                    } else {
//                        body = HttpRequestUtils.getBody("/user/login_failed.html");
//                        cookie = "logined=false";
//                    }
//                    DataOutputStream dos = new DataOutputStream(out);
//                    responseCookie(dos, body.length, cookie);
//                    responseBody(dos, body);
//                } else if (url.startsWith("/user/list")) {
//                    Map<String, String> cookie = new HashMap<>();
//                    while ((line = br.readLine()) != null) {
//                        if (line.startsWith("Cookie")) {
//                            log.debug(line);
//                            cookie = HttpRequestUtils.parseCookies(HttpRequestUtils.parseHeader(line).getValue());
//                        }
//                        if (line.equals("")) {
//                            break;
//                        }
//                    }
//                    if (Boolean.parseBoolean(cookie.get("logined"))) {
//                        StringBuilder sb = new StringBuilder();
//                        sb.append("<table>");
//                        sb.append("<tr>");
//                        sb.append("<th>아이디</th>");
//                        sb.append("<th>이름</th>");
//                        sb.append("<th>이메일</th>");
//                        sb.append("</tr>");
//                        userService.getUsers().forEach(user -> {
//                            sb.append("<tr>");
//                            sb.append("<td>").append(user.getUserId()).append("</td>");
//                            sb.append("<td>").append(user.getName()).append("</td>");
//                            sb.append("<td>").append(user.getName()).append("</td>");
//                            sb.append("</tr>");
//                        });
//                        sb.append("</table>");
//                        body = sb.toString().getBytes();
//                    } else {
//                        body = HttpRequestUtils.getBody("/user/login.html");
//                    }
//                    DataOutputStream dos = new DataOutputStream(out);
//                    response200Header(dos, body.length);
//                    responseBody(dos, body);
//                }
//            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String getDefaultPath(String path) {
        if ("/".equals(path)) {
            return "/index.html";
        }
        return path;
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

    private void response200CssHeader(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String url) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + url + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302HeaderWithCookie(DataOutputStream dos, String url, String cookie) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Set-Cookie: " + cookie + " \r\n");
            dos.writeBytes("Location: " + url + " \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseResource(OutputStream out, String url) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
        response200Header(dos, body.length);
        responseBody(dos, body);
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

    private void responseCss(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
