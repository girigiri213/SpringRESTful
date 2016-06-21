package com.girigiri.controller;

import com.girigiri.SpringMvcApplication;
import org.junit.Test;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/**
 * Created by JianGuo on 6/21/16.
 */
@SpringApplicationConfiguration(classes = SpringMvcApplication.class)
public class HomeControllerTest {
    @Test
    public void homePageTest() throws Exception {
        HomeController controller = new HomeController();
        MockMvc mockMvc = standaloneSetup(controller).build();
        mockMvc.perform(get("/"))
                .andExpect(view().name("hello"));

    }
}
