package ru.hse.server;

import com.google.protobuf.InvalidProtocolBufferException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.hse.server.controller.GroupController;
import ru.hse.server.proto.EntitiesProto.GroupModel;
import ru.hse.server.proto.EntitiesProto.UserModel;
import ru.hse.server.service.GroupService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GroupController.class)
public class GroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GroupService groupService;

    private static GroupModel groupModel;
    private static GroupModel incorrectGroupModel;

    @BeforeAll
    public static void beforeAll(){
        var userBuilder = UserModel.newBuilder();
        UserModel userModel = userBuilder.setId(1).setLogin("testUser").setPasswordHash("testPassword").build();
        var groupBuilder = GroupModel.newBuilder();
        groupModel = groupBuilder.setId(1).setAdmin(userModel).setName("testGroup").setPasswordHash("testPassword").build();
        groupBuilder.clear();
        incorrectGroupModel = groupBuilder.setId(1).setName("testIncorrectGroup").setPasswordHash("testPassword").build();
    }

    @Test
    public void createGroup() throws Exception {
        doReturn(groupModel).when(groupService).createGroup(any());
        var response =
                mockMvc.perform(post("/groups").contentType(MediaType.APPLICATION_PROTOBUF_VALUE).content(groupModel.toByteArray())).andDo(print()).andExpect(status().isOk()).andReturn();
        verify(groupService, times(1)).createGroup(eq(groupModel));

        compareResponse(groupModel, response);
    }

    @Test
    public void getGroupById() throws Exception {
        when(groupService.findGroupById(1L)).thenReturn(groupModel);
        var response = mockMvc.perform(get("/groups/groupById?id=1")).andDo(print()).andExpect(status().isOk()).andReturn();

        compareResponse(groupModel, response);
    }

    @Test
    public void deleteGroup() throws Exception {
        mockMvc.perform(delete("/groups?id=1")).andDo(print()).andExpect(status().isOk());
        verify(groupService, times(1)).deleteGroup(1L);
    }

    @Test
    public void badPostRequest() throws Exception {
        doCallRealMethod().when(groupService).createGroup(any());
        mockMvc.perform(post("/groups").contentType(MediaType.APPLICATION_PROTOBUF_VALUE).content(incorrectGroupModel.toByteArray())).andDo(print()).andExpect(status().isBadRequest());
    }

    private void compareResponse(GroupModel model, MvcResult response) throws InvalidProtocolBufferException {
        Assertions.assertEquals(MediaType.APPLICATION_PROTOBUF_VALUE, response.getResponse().getContentType());
        GroupModel responseModel = GroupModel.parseFrom(response.getResponse().getContentAsByteArray());
        Assertions.assertEquals(responseModel, model);
    }
}
