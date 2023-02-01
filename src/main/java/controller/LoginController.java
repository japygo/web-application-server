package controller;

import db.DataBase;
import http.HttpSession;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import http.HttpRequest;
import http.HttpResponse;

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
            response.sendRedirect("/user/login_failed.html");
            return;
        }

        if (user.login(password)) {
            log.debug("Login Success!");
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            response.sendRedirect("/index.html");
        } else {
            log.debug("Password Mismatch!");
            response.sendRedirect("/user/login_failed.html");
        }
    }
}
