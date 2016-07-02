package com.girigiri.dao;

import com.girigiri.SpringMvcApplication;
import com.girigiri.dao.models.ComponentRequest;
import com.girigiri.dao.models.Manager;
import com.girigiri.dao.models.RepairHistory;
import com.girigiri.dao.services.ComponentRequestRepository;
import com.girigiri.dao.services.ManagerRepository;
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
public class RepairHistoryRepositoryTests {

    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private RepairHistoryRepository repairHistoryRepository;


    @Autowired
    private ComponentRequestRepository componentRequestRepository;


    @Autowired
    private ManagerRepository managerRepository;

    private static Validator validator;

    private RepairHistory rep;

    private Manager manager;

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
        manager = managerRepository.save(new Manager("guojian", "root", Manager.ROLE_ENGINEER));
        RepairHistory repairHistory = new RepairHistory();
        repairHistory.setDelayType(2);
//        repairHistory.setRepairState(2);
        repairHistory.setManagerId(manager.getId());
        ComponentRequest request = new ComponentRequest("name", "serial number", 10);
        List<ComponentRequest> componentRequestList = new ArrayList<>();
        componentRequestList.add(request);
//        repairHistory.setComponentRequests(componentRequestList);
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
    public void addHistoryIsNotAllowed() throws Exception {
        RepairHistory repairHistory = new RepairHistory();
        repairHistory.setDelayType(1);
        repairHistory.setRepairState(1);
        mockMvc.perform(post("/api/histories")
                .content(objToJson(repairHistory))
                .contentType(contentType))
                .andExpect(status().isMethodNotAllowed());
    }


    @Test
    public void getHistory() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/histories/{id}", rep.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.repairState", is(rep.getRepairState())))
                .andExpect(jsonPath("$.repairState", is(rep.getDelayType())))
//                .andExpect(jsonPath("$.managerId", is(manager.getId())))
                .andReturn();
        System.err.println(result.getResponse().getContentAsString());
    }

    @Test
    public void removeHistoryWillNotRemoveComponentRequest() throws Exception {
        mockMvc.perform(delete("/api/histories/{id}", rep.getId()))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/com_requests"))
                .andExpect(status().isOk());
    }



    @Test
    public void updateHistoryWithComponentRequestWillSuccess() throws Exception {
        RepairHistory repairHistory = new RepairHistory();
        repairHistory.setManagerId(manager.getId());
        List<ComponentRequest> list = new ArrayList<>();
        ComponentRequest componentRequest = new ComponentRequest("name", "new serial", 10);
        list.add(componentRequest);
        mockMvc.perform(put("/api/histories/{id}", rep.getId())
                .contentType(contentType)
                .content(objToJson(repairHistory)))
                .andExpect(status().isNoContent());
        MvcResult result = mockMvc.perform(get("/api/histories/{id}", rep.getId())
                .contentType(contentType)
                .content(objToJson(repairHistory)))
                .andExpect(status().isOk()).andReturn();
        System.err.println(result.getResponse().getContentAsString());
//                .andExpect(jsonPath("$._embedded.componentRequests", hasSize(rep.getComponentRequests().size() + 1)));
    }

    @Test
    public void updateHistoryWithRightManagerWillSuccess() throws Exception {
        RepairHistory repairHistory = new RepairHistory();
        repairHistory.setManagerId(manager.getId());
        repairHistory.setRepairState(2);
        repairHistory.setMaterialPrice(200);
        mockMvc.perform(put("/api/histories/{id}", rep.getId())
                .contentType(contentType)
                .content(objToJson(repairHistory)))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/histories/{id}", rep.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.repairState", is(repairHistory.getRepairState())))
                .andExpect(jsonPath("$.materialPrice", is(repairHistory.getMaterialPrice())));

    }


    @Test
    public void updateHistoryWithWrongManagerWillFail() throws Exception {
        Manager manager = managerRepository.save(new Manager("sunpen", "sunpen", Manager.ROLE_USER));
        RepairHistory repairHistory = new RepairHistory();
        repairHistory.setDelayType(3);
        repairHistory.setManagerId(manager.getId());
        mockMvc.perform(put("/api/histories/{id}", rep.getId())
                .contentType(contentType)
                .content(objToJson(repairHistory)))
                .andExpect(status().isBadRequest());
    }


    @After
    public void onDestroy() {
        managerRepository.deleteAll();
        repairHistoryRepository.deleteAll();
        componentRequestRepository.deleteAll();
    }

}
