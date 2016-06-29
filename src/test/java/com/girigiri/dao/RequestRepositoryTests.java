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
import static com.girigiri.utils.TestUtil.getResourceIdFromUrl;
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
        RepairHistory repairHistory = new RepairHistory();
        tmp.setCustomer(cus);
        tmp.setDevice(device);
        tmp.setRepairHistory(repairHistory);
        customer = customerRepository.save(cus);
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
    public void addRequest() throws Exception {
        Device device = new Device(1, "some error", 1);
        Request tmp = new Request(155, "2016-7-7", 1);
        tmp.setDevice(device);
        tmp.setCustomer(customer);
        String json = objToJson(tmp);
        System.err.println("request json: " + json);
        MvcResult result = mockMvc.perform(post("/api/requests").content(json)
                .contentType(contentType))
                .andExpect(status().isCreated()).andReturn();
        long id = getResourceIdFromUrl(result.getResponse().getRedirectedUrl());
        result = mockMvc.perform(get("/api/requests/{id}", id))
                .andExpect(status().isOk()).andReturn();
        System.out.println(result.getResponse().getContentAsString());
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
        request.setState(3);
        mockMvc.perform(put("/api" + "/requests/{id}", request.getId())
                .content(objToJson(request))
                .contentType(contentType))
                .andExpect(status().isNoContent());
        MvcResult result = mockMvc.perform(get("/api/requests/{id}", request.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("state", is(request.getState())))
                .andReturn();
    }

    @Test
    public void deleteRequestWillDeleteRepairHistoryAndDeviceButNotDeleteCustomer() throws Exception {
        mockMvc.perform(delete("/api/requests/{id}", request.getId()))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/customers/{id}", request.getCustomer().getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(request.getCustomer().getUserId())))
                .andExpect(jsonPath("$.mobile", is(request.getCustomer().getMobile())))
                .andExpect(jsonPath("$.address", is(request.getCustomer().getAddress())))
                .andExpect(jsonPath("$.contactName", is(request.getCustomer().getContactName())));
        mockMvc.perform(get("/api/devices/{id}", request.getDevice().getId()))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/repairHistories/{id}", request.getRepairHistory().getId()))
                .andExpect(status().isNotFound());
    }


    @After
    public void onDestroy() {
        requestRepository.deleteAll();
        customerRepository.deleteAll();
    }
}
