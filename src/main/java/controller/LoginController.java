package controller;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.HttpRequest;
import webserver.HttpResponse;

public class LoginController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        String userId = request.getParameter("userId");
        String password = request.getParameter("password");
        log.debug("userId : {}, password : {}", userId, password);

        User user = DataBase.findUserById(userId);
        if (user == null) {
            log.debug("User Not Found!");
            response.addHeader("Set-Cookie", "logined=false");
            response.sendRedirect("/user/login_failed.html");
            return;
        }

        if (user.getPassword().equals(password)) {
            log.debug("Login Success!");
            response.addHeader("Set-Cookie", "logined=true");
            response.sendRedirect("/index.html");
        } else {
            log.debug("Password Mismatch!");
            response.addHeader("Set-Cookie", "logined=false");
            response.sendRedirect("/user/login_failed.html");
        }
    }
}
