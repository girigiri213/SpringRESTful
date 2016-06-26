package com.girigiri.dao;


import com.girigiri.SpringMvcApplication;
import com.girigiri.dao.models.Request;
import com.girigiri.dao.services.RequestRepository;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static com.girigiri.utils.TestUtil.contentType;
import static com.girigiri.utils.TestUtil.objToJson;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by JianGuo on 6/26/16.
 * Unit test for {@link RequestRepository}
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringMvcApplication.class)
@WebAppConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RequestRepositoryTests {

    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private RequestRepository requestRepository;

    private static Validator validator;
    private long setupId;


    @BeforeClass
    public static void onCreate() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        Request request = requestRepository.save(new Request(155, "2016-7-7", 1));
        setupId = request.getId();
    }

    @Test
    public void getRequest() throws Exception {
        mockMvc.perform(get("/api/requests/{id}", setupId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.predictTime", is("2016-7-7")))
                .andExpect(jsonPath("$.predictPrice", is(155)))
                .andExpect(jsonPath("$").value(hasKey("created")))
                .andExpect(jsonPath("$.state", is(1)));
    }

    @Test
    public void addRequest() throws Exception {
        Request request = new Request(180, "2016-8-4", 2);
        mockMvc.perform(post("/api/requests").content(objToJson(request))
                .contentType(contentType))
                .andExpect(status().isCreated());
    }

    @Test
    public void createRequestOutOfBoundary() {
        Request request = new Request(290, "2016-5-4", 4);
        Set<ConstraintViolation<Request>> constraintViolations =
                validator.validate(request);
        assertEquals(constraintViolations.size(), 1);
        assertEquals("must be less than or equal to 3",
                constraintViolations.iterator().next().getMessage());

        request.setState(0);
        constraintViolations = validator.validate(request);
        assertEquals(constraintViolations.size(), 1);
        assertEquals("must be greater than or equal to 1",
                constraintViolations.iterator().next().getMessage());

    }


    @Test
    public void addRequestOutOfBoundary() throws Exception {
        Request request = new Request(300, "2017-4-5", 4);
        mockMvc.perform(post("/api/requests").content(objToJson(request)).contentType(contentType))
                .andExpect(status().isBadRequest());
        request.setState(0);
        mockMvc.perform(post("/api/requests").content(objToJson(request)).contentType(contentType))
                .andExpect(status().isBadRequest());
        request.setState(1);
        request.setPredictTime("3343q234");
        mockMvc.perform(post("/api/requests").content(objToJson(request)).contentType(contentType))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateRequest() throws Exception {
        Request request = new Request(450, "2018-3-4", 1);
        mockMvc.perform(put("/api" + "/requests/{id}", setupId)
                .content(objToJson(request))
                .contentType(contentType))
                .andExpect(status().isNoContent())
                .andReturn();
        mockMvc.perform(get("/api" + "/requests/{id}", setupId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.predictPrice", is(450)))
                .andExpect(jsonPath("$.predictTime", is("2018-3-4")))
                .andExpect(jsonPath("$.state", is(1)));
    }

    @After
    public void onDestroy() {
        requestRepository.deleteAll();
    }
}
