package ru.hse.server;

import com.google.protobuf.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import ru.hse.protobuf.entities.ProtoUser;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @Test
    public void getUser() throws Exception {
        //Mockito.when(userService.getUserByID(1L)).thenReturn(user);
        //this.mockMvc.perform(get("/users/userById?id=1")).andDo(print()).andExpect(status().isOk()).andExpect(content().json(userPayload));
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

    @Test
    public void postProtoUser() throws Exception {

        ProtoUser.User.Builder builder = ProtoUser.User.newBuilder();
        builder.setName("Test");
        builder.setPassword("Pass");
        builder.setId(10020202);
        var response = this.mockMvc.perform(post("/users/registration").contentType("application/x-protobuf").content(builder.build().toByteArray())).andExpect(status().isOk()).andReturn();
        //this.mockMvc.perform(post("/users/registration").content(builder.build().toByteArray())).andDo(print())
        // .andExpect(status().isOk());
    }
}
