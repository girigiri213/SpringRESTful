package com.girigiri.dao;

import com.girigiri.SpringMvcApplication;
import com.girigiri.dao.models.ComponentRequest;
import com.girigiri.dao.models.RepairHistory;
import com.girigiri.dao.services.ComponentRequestRepository;
import com.girigiri.dao.services.RepairHistoryRepository;
import com.sun.corba.se.spi.logging.CORBALogDomains;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.girigiri.utils.TestUtil.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by JianGuo on 6/26/16.
 * Unit test for {@link RepairHistoryRepository}
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringMvcApplication.class)
@WebAppConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//TODO: pass this test with ComponentRequest passed into
public class RepairHistoryRepositoryTests {

    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private RepairHistoryRepository repairHistoryRepository;


    @Autowired
    private ComponentRequestRepository componentRequestRepository;

    private static Validator validator;

    private RepairHistory rep;

    @BeforeClass
    public static void onCreate() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Before
    public void setup() {
        mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        RepairHistory repairHistory = new RepairHistory();
        repairHistory.setDelayType(2);
        repairHistory.setRepairState(2);
        ComponentRequest request = new ComponentRequest("name", "serial number", 10);
        List<ComponentRequest> componentRequestList = new ArrayList<>();
        componentRequestList.add(request);
        request = new ComponentRequest("new name", "new serial number", 20);
        componentRequestList.add(request);
        repairHistory.setComponentRequests(componentRequestList);
        rep = repairHistoryRepository.save(repairHistory);
    }


    @Test
    public void createHistoryOutOfBoundary() {
        RepairHistory repairHistory = new RepairHistory();
        repairHistory.setRepairState(10);
        Set<ConstraintViolation<RepairHistory>> constraintViolations =
                validator.validate(repairHistory);
        assertEquals(1, constraintViolations.size());
        assertEquals(
                "must be less than or equal to 4",
                constraintViolations.iterator().next().getMessage()
        );

        repairHistory = new RepairHistory();
        repairHistory.setDelayType(10);
        constraintViolations = validator.validate(repairHistory);
        assertEquals(1, constraintViolations.size());
        assertEquals(
                "must be less than or equal to 3",
                constraintViolations.iterator().next().getMessage()
        );

        repairHistory = new RepairHistory();
        repairHistory.setRepairState(0);
        constraintViolations = validator.validate(repairHistory);
        assertEquals(1, constraintViolations.size());
        assertEquals("must be greater than or equal to 1",
                constraintViolations.iterator().next().getMessage());

        repairHistory = new RepairHistory();
        repairHistory.setDelayType(0);
        constraintViolations = validator.validate(repairHistory);
        assertEquals(1, constraintViolations.size());
        assertEquals("must be greater than or equal to 1",
                constraintViolations.iterator().next().getMessage());

    }


    @Test
    public void addHistory() throws Exception {
        RepairHistory repairHistory = new RepairHistory();
        repairHistory.setDelayType(1);
        repairHistory.setRepairState(1);
        MvcResult result = mockMvc.perform(post("/api/histories")
                .content(objToJson(repairHistory))
                .contentType(contentType))
                .andExpect(status().isCreated())
                .andReturn();
    }


    @Test
    public void addHistoryOutOfBoundary() throws Exception {
        RepairHistory repairHistory = new RepairHistory();
        repairHistory.setDelayType(0);
        repairHistory.setRepairState(0);
        mockMvc.perform(post("/api/histories")
                .content(objToJson(repairHistory))
                .contentType(contentType))
                .andExpect(status().isBadRequest());
        repairHistory.setDelayType(5);
        repairHistory.setRepairState(4);
        mockMvc.perform(post("/api/histories")
                .content(objToJson(repairHistory))
                .contentType(contentType))
                .andExpect(status().isBadRequest());
        repairHistory.setDelayType(1);
        repairHistory.setRepairState(1);
        repairHistory.setAssignTime("1234-31-ef");
        mockMvc.perform(post("/api/histories").content(objToJson(repairHistory)).contentType(contentType))
                .andExpect(status().isBadRequest());
        repairHistory.setAssignTime("2016-01-02");
        repairHistory.setRepairTime("12341esaf");
        mockMvc.perform(post("/api/histories").content(objToJson(repairHistory)).contentType(contentType))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getHistory() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/histories/{id}", rep.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.repairState", is(rep.getRepairState())))
                .andExpect(jsonPath("$.repairState", is(rep.getDelayType())))
                .andExpect(jsonPath("$._links.componentRequests.href", is("http://localhost/api/histories/" + rep.getId() + "/componentRequests")))
                .andReturn();
//        System.err.println(result.getResponse().getContentAsString());
    }

    @Test
    public void removeHistoryWillNotRemoveComponentRequest() throws Exception {
        mockMvc.perform(delete("/api/histories/{id}", rep.getId()))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/componentRequests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.componentRequests", hasSize(rep.getComponentRequests().size())));
    }

    @Test
    public void updateHistory() throws Exception {
        RepairHistory repairHistory = new RepairHistory();
        repairHistory.setDelayType(3);
        repairHistory.setRepairState(3);
        mockMvc.perform(put("/api/histories/{id}", rep.getId())
                .contentType(contentType)
                .content(objToJson(repairHistory)))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/histories/{id}", rep.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.delayType", is(3)))
                .andExpect(jsonPath("$.repairState", is(3)));
    }


    @After
    public void onDestroy() {
        repairHistoryRepository.deleteAll();
        componentRequestRepository.deleteAll();
    }

}
