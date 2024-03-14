package ru.hse.server;

import com.google.protobuf.InvalidProtocolBufferException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.hse.server.controller.ChapterController;
import ru.hse.server.proto.EntitiesProto;
import ru.hse.server.proto.EntitiesProto.GroupModel;
import ru.hse.server.proto.EntitiesProto.ChapterModel;
import ru.hse.server.service.ChapterService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChapterController.class)
public class ChapterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChapterService chapterService;

    private static EntitiesProto.ChapterModel chapterModel;
    private static ChapterModel incorrectChapterModel;

    @BeforeAll
    public static void beforeAll(){
        var groupBuilder = GroupModel.newBuilder();
        GroupModel userModel = groupBuilder.setId(1).setName("testGroup").setPasswordHash("testPassword").build();
        var chapterBuilder = ChapterModel.newBuilder();
        chapterModel = chapterBuilder.setId(1).setGroup(userModel).setName("testChapter").build();
        groupBuilder.clear();
        chapterBuilder.clear();
        incorrectChapterModel = chapterBuilder.setId(1).setName("testIncorrectChapter").build();
    }

    @Test
    public void createChapter() throws Exception {
        doReturn(chapterModel).when(chapterService).createChapter(any());
        var response =
                mockMvc.perform(post("/chapters").contentType(MediaType.APPLICATION_PROTOBUF_VALUE).content(chapterModel.toByteArray())).andDo(print()).andExpect(status().isOk()).andReturn();
        verify(chapterService, times(1)).createChapter(eq(chapterModel));

        compareResponse(chapterModel, response);
    }

    @Test
    public void getChapterById() throws Exception {
        when(chapterService.getChapterByID(1L)).thenReturn(chapterModel);
        var response = mockMvc.perform(get("/chapters?id=1")).andDo(print()).andExpect(status().isOk()).andReturn();

        compareResponse(chapterModel, response);
    }

    @Test
    public void deleteGroup() throws Exception {
        mockMvc.perform(delete("/chapters?id=1")).andDo(print()).andExpect(status().isOk());
        verify(chapterService, times(1)).deleteChapter(1L);
    }

    @Test
    public void badPostRequest() throws Exception {
        doCallRealMethod().when(chapterService).createChapter(any());
        mockMvc.perform(post("/chapters").contentType(MediaType.APPLICATION_PROTOBUF_VALUE).content(incorrectChapterModel.toByteArray())).andDo(print()).andExpect(status().isBadRequest());
    }

    private void compareResponse(ChapterModel model, MvcResult response) throws InvalidProtocolBufferException {
        Assertions.assertEquals(MediaType.APPLICATION_PROTOBUF_VALUE, response.getResponse().getContentType());
        ChapterModel responseModel = ChapterModel.parseFrom(response.getResponse().getContentAsByteArray());

        Assertions.assertEquals(responseModel, model);
    }
}
