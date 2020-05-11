package org.zipper.transport.controller;

import org.zipper.transport.DtxTransportApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DtxTransportApplication.class)
public class DBControllerTest {

    @Autowired
    WebApplicationContext context;
    MockMvc mockMvc;

    @Before
    public void before() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void addOne() throws Exception {
        mockMvc.perform(
                post("/db/addOne")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content("{\"dbName\":\"test1\",\"dbType\":1,\"host\":\"localhost\",\"port\":3306,\"user\":\"root\",\"password\":\"123456\"}"))
//                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }


    @Test
    public void listPage() throws Exception {

        mockMvc.perform(get("/db/listPage")
                .param("dbType","1"))
                .andDo(print()).andReturn().getResponse().getContentAsString();
    }
}