package com.girigiri.dao;


import com.girigiri.SpringMvcApplication;
import com.girigiri.dao.models.Customer;
import com.girigiri.dao.models.Device;
import com.girigiri.dao.models.RepairHistory;
import com.girigiri.dao.models.Request;
import com.girigiri.dao.services.CustomerRepository;
import com.girigiri.dao.services.RequestRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import java.util.Set;

import static com.girigiri.utils.TestUtil.contentType;
import static com.girigiri.utils.TestUtil.objToJson;
import static org.hamcrest.Matchers.hasKey;
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
 * Unit test for {@link RequestRepository}
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringMvcApplication.class)
@WebAppConfiguration
public class RequestRepositoryTests {

    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private CustomerRepository customerRepository;


    private static Validator validator;
    private Customer customer;

    private Request request;

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
        Device device = new Device(1, "some error", 1);
        Customer cus = new Customer("420302133404031213", "13020202134", "new address", "my contactName");
        Request tmp = new Request(155, "2016-7-7", 1);
        tmp.setDevice(device);
        customer = customerRepository.save(cus);
        tmp.setCusId(customer.getId());
        request = requestRepository.save(tmp);
    }

    @Test
    public void getRequest() throws Exception {
        mockMvc.perform(get("/api/requests/{id}", request.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.predictTime", is("2016-7-7")))
                .andExpect(jsonPath("$.predictPrice", is(155)))
                .andExpect(jsonPath("$").value(hasKey("created")))
                .andExpect(jsonPath("$.state", is(1)));

    }

    @Test
    public void addRequestWillAddRepairHistoryAutomatically() throws Exception {
        Device device = new Device(1, "some error", 1);
        Request tmp = new Request(155, "2016-7-7", 1);
        tmp.setDevice(device);
        tmp.setCusId(customer.getId());
        String json = objToJson(tmp);
        System.err.println("request json: " + json);
        MvcResult result = mockMvc.perform(post("/api/requests").content(json)
                .contentType(contentType))
                .andExpect(status().isCreated()).andReturn();
        System.out.println(result.getResponse().getContentAsString());
        mockMvc.perform(get("/api/histories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.repairHistories", hasSize(1)));
    }

    @Test
    public void addRequestWithInvalidCustomerWillFail() throws Exception {
        Request request = new Request(200, "2041-7-3", 2);
        request.setCusId((long)0);
        request.setDevice(new Device(1, "some error", 1));
        mockMvc.perform(post("/api/requests").content(objToJson(request))
                .contentType(contentType))
                .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    public void createRequestOutOfBoundary() {
        request.setState(4);
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
        request.setState(4);
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
        Request request = new Request();
        request.setState(3);
        request.setCusId(customer.getId());
        String json = objToJson(request);
        System.err.println("put json" + json);
        mockMvc.perform(put("/api/requests/{id}", this.request.getId())
                .content(objToJson(request))
                .contentType(contentType))
                .andExpect(status().isNoContent());
        MvcResult result = mockMvc.perform(get("/api/requests/{id}", this.request.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("state", is(request.getState())))
                .andReturn();
        System.err.println("response json " + result.getResponse().getContentAsString());
    }

    @Test
    public void deleteRequestWillDeleteDeviceButNotDeleteCustomer() throws Exception {
        mockMvc.perform(delete("/api/requests/{id}", request.getId()))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/customers/{id}", request.getCusId()))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/devices/{id}", request.getDevice().getId()))
                .andExpect(status().isNotFound());
    }

    @After
    public void onDestroy() {
        requestRepository.deleteAll();
        customerRepository.deleteAll();
    }
}
