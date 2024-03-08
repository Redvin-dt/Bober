package ru.hse.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.hse.server.entity.UserEntity;
import ru.hse.server.service.UserService;
import ru.hse.server.controller.UserController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private static UserEntity user;
    private static String userPayload;

    @BeforeEach
    public void generateUser() throws Exception {
        user = new UserEntity();
        user.setId((long) 1);
        user.setLogin("admin");
        user.setPassword("admin");

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        userPayload = ow.writeValueAsString(user);
    }

    // TODO: write tests

    @Test
    public void getUser() throws Exception {
    }

    @Test
    public void postUserTwice() throws Exception {
    }


    @Test
    public void deleteUser() throws Exception {
    }

    @Test
    public void postProtoUser() throws Exception {
    }
}
