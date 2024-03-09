package ru.hse.server;

import ru.hse.server.repository.UserLocalRepository;
import ru.hse.server.service.UserService;
import ru.hse.server.controller.UserController;
import ru.hse.server.proto.EntitiesProto.UserProto;

import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebMvcTest(UserController.class)
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private static UserProto user;

    @BeforeEach
    public void generateUser() throws Exception {
        user = UserProto.newBuilder().setId(1).setLogin("admin").setPasswordHash("admin").build();
    }

    @Test
    public void getUser() throws Exception {
        Mockito.when(userService.getUserByID(1L)).thenReturn(user);
        mockMvc.perform(get("/users/userById?id=1")).andDo(print()).andExpect(status().isOk()).andExpect(content().bytes(user.toByteArray()));
    }

    @Test
    public void postUser() throws Exception {
        mockMvc.perform(post("/users/registration").contentType(MediaType.APPLICATION_PROTOBUF_VALUE).content(user.toByteArray())).andDo(print()).andExpect(status().isOk());
        verify(userService, times(1)).registration(any());
    }


    @Test
    public void deleteUser() throws Exception {
        mockMvc.perform(delete("/users/userById?id=1")).andDo(print()).andExpect(status().isOk());
        verify(userService, times(1)).deleteUserById(1L);
    }

    @Test
    public void postProtoUserTwice() throws Exception {
        ReflectionTestUtils.setField(userService, "userRepository", new UserLocalRepository());
        when(userService.registration(any())).thenCallRealMethod();
        when(userService.getUserByLogin(any())).thenCallRealMethod();

        mockMvc.perform(post("/users/registration").contentType(MediaType.APPLICATION_PROTOBUF_VALUE).content(user.toByteArray())).andDo(print()).andExpect(status().isOk());
        verify(userService, times(1)).registration(any());

        mockMvc.perform(post("/users/registration").contentType(MediaType.APPLICATION_PROTOBUF_VALUE).content(user.toByteArray())).andDo(print()).andExpect(status().isBadRequest());
        verify(userService, times(2)).registration(any());
    }
}
