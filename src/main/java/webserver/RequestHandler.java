package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
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
            byte[] body = new byte[0];
            if (HttpRequestUtils.isHtml(url)) {
                body = HttpRequestUtils.getBody(url);
            } else {
                if (url.startsWith("/user/create")) {
                    Map<String, String> params;
                    if (line.startsWith("GET")) {
                        params = HttpRequestUtils.getParams(url);
                    } else {
                        int contentLength = 0;
                        while ((line = br.readLine()) != null) {
                            log.debug(line);
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
                    if (result) {
                        body = "success".getBytes();
                    } else {
                        body = "fail".getBytes();
                    }
                }
            }
            DataOutputStream dos = new DataOutputStream(out);
            response200Header(dos, body.length);
            responseBody(dos, body);
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

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
