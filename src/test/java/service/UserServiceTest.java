package service;

import db.DataBase;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserServiceTest {

    private UserService userService;

    @BeforeEach
    public void setup() {
        userService = new UserService();
    }

    @Test
    public void createUser() {
        User user = new User("javajigi", "password", "", "");
        userService.createUser(user);
        User findUser = DataBase.findUserById("javajigi");
        assertThat(user).isEqualTo(findUser);
    }

    @Test
    public void loginUser() {
        User user = new User("javajigi", "password", "", "");
        DataBase.addUser(user);

        boolean result = userService.loginUser("javajigi", "password");
        assertThat(result).isTrue();

        result = userService.loginUser("javajigi", "1234");
        assertThat(result).isFalse();
    }
}
