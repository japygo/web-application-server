package service;

import db.DataBase;
import model.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserServiceTest {

    @Test
    public void createUser() {
        User user = new User("javajigi", "password", "", "");
        DataBase.addUser(user);
        User findUser = DataBase.findUserById("javajigi");
        assertThat(user).isEqualTo(findUser);
    }
}
