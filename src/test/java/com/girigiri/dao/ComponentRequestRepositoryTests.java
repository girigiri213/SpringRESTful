package com.girigiri.dao;

import com.girigiri.SpringMvcApplication;
import com.girigiri.dao.models.ComponentRequest;
import com.girigiri.dao.models.RepairHistory;
import com.girigiri.dao.services.ComponentRequestRepository;
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
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by JianGuo on 6/26/16.
 * Unit test for {@link com.girigiri.dao.services.ComponentRequestRepository}
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringMvcApplication.class)
@WebAppConfiguration
public class ComponentRequestRepositoryTests {
    private MockMvc mockMvc;
    private static Validator validator;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ComponentRequestRepository componentRequestRepository;

    private long setupId;

    private ComponentRequest componentRequest;
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
        RepairHistory repairHistory = new RepairHistory();
        componentRequest = componentRequestRepository.save(new ComponentRequest("component name", "serial", 10));
        setupId = componentRequest.getId();
    }


    @Test
    public void addComponentRequest() throws Exception {
        ComponentRequest componentRequest = new ComponentRequest("component name", "serial", 10);
        mockMvc.perform(post("/api/componentRequests").content(objToJson(componentRequest)).contentType(contentType))
                .andExpect(status().isCreated());
    }


    @Test
    public void getComponentRequest() throws Exception {
        mockMvc.perform(get("/api/componentRequests/{id}", setupId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(componentRequest.getName())))
                .andExpect(jsonPath("$.size", is(componentRequest.getSize())));
    }

    @Test
    public void updateComponentRequest() throws Exception {
        componentRequest = new ComponentRequest("component name", "serial", 10);
        mockMvc.perform(put("/api/componentRequests/{id}", setupId).content(objToJson(componentRequest)).contentType(contentType))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/componentRequests/{id}", setupId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(componentRequest.getName())))
                .andExpect(jsonPath("$.size", is(componentRequest.getSize())));
    }

    @Test
    public void deleteComponentRequest() throws Exception {
        mockMvc.perform(delete("/api/componentRequests/{id}", setupId))
                .andExpect(status().isNoContent());
    }

    @Test
    public void createInvalidComponentRequest() {
        componentRequest = new ComponentRequest("component name", "serial", -10);
        Set<ConstraintViolation<ComponentRequest>> constraintViolations =
                validator.validate(componentRequest);
        assertEquals(1, constraintViolations.size());
        assertEquals("must be greater than or equal to 0",
                constraintViolations.iterator().next().getMessage());

        componentRequest.setSize(10);
        componentRequest.setName(null);
        constraintViolations =
                validator.validate(componentRequest);
        assertEquals(1, constraintViolations.size());
        assertEquals("may not be null",
                constraintViolations.iterator().next().getMessage());
    }


    @Test
    public void addInvalidComponentRequest() throws Exception {
        componentRequest = new ComponentRequest("component name", "serial", -10);
        mockMvc.perform(post("/api/componentRequests").content(objToJson(componentRequest)).contentType(contentType))
                .andExpect(status().isBadRequest());
    }

    @After
    public void onDestroy() {
        componentRequestRepository.deleteAll();
    }
}
