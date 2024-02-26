package ru.hse.server;

import org.junit.jupiter.api.BeforeEach;
import ru.hse.server.entity.UserEntity;
import ru.hse.server.repository.UserLocalRepository;
import ru.hse.server.service.UserService;
import ru.hse.server.controller.UserController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @Test
    public void getUser() throws Exception {
        Mockito.when(userService.getUserByID(1L)).thenReturn(user);
        this.mockMvc.perform(get("/users/userById?id=1")).andDo(print()).andExpect(status().isOk()).andExpect(content().json(userPayload));
    }

    @Test
    public void postUserTwice() throws Exception {
        ReflectionTestUtils.setField(userService, "userRepository", new UserLocalRepository());
        Mockito.when(userService.registration(any())).thenCallRealMethod();
        Mockito.when(userService.getUserByLogin(any())).thenCallRealMethod();

        this.mockMvc.perform(post("/users/registration").contentType("application/json").content(userPayload)).andDo(print()).andExpect(status().isOk());
        this.mockMvc.perform(post("/users/registration").contentType("application/json").content(userPayload)).andDo(print()).andExpect(status().isBadRequest());
        this.mockMvc.perform(get("/users/userByLogin?login=" + user.getLogin())).andDo(print()).andExpect(status().isOk()).andExpect(content().json(userPayload));
    }


    @Test
    public void deleteUser() throws Exception {
        ReflectionTestUtils.setField(userService, "userRepository", new UserLocalRepository());
        Mockito.when(userService.registration(any())).thenCallRealMethod();
        Mockito.when(userService.getUserByID(any())).thenCallRealMethod();
        Mockito.doCallRealMethod().when(userService).deleteUserById(anyLong());

        this.mockMvc.perform(post("/users/registration").contentType("application/json").content(userPayload)).andDo(print()).andExpect(status().isOk());
        this.mockMvc.perform(get("/users/userById?id=1")).andDo(print()).andExpect(status().isOk()).andExpect(content().json(userPayload));
        this.mockMvc.perform(delete("/users/userById?id=1")).andDo(print()).andExpect(status().isOk());
        this.mockMvc.perform(get("/users/userById?id=1")).andDo(print()).andExpect(status().isBadRequest());
        this.mockMvc.perform(delete("/users/userById?id=1")).andDo(print()).andExpect(status().isBadRequest());
    }
}
