package service;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public boolean createUser(User user) {
        if (user.getUserId() == null) {
            return false;
        }
        DataBase.addUser(user);
        log.debug("Add user : {}", user);
        return true;
    }
}
