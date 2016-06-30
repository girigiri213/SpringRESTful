package com.girigiri.dao;

import com.girigiri.SpringMvcApplication;
import com.girigiri.dao.models.Customer;
import com.girigiri.dao.models.Device;
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
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by JianGuo on 6/25/16.
 * Unit test for {@link CustomerRepository}
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringMvcApplication.class)
@WebAppConfiguration
public class CustomerRepositoryTests {


    private MockMvc mockMvc;
    private static Validator validator;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RequestRepository requestRepository;


    private Customer rst;

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

        Customer customer = new Customer("420104199601021617", "13018060139", "my address", "my contactName");
        Device device = new Device(1, "some error", 1);
        Request tmp = new Request(155, "2016-7-7", 1);
        tmp.setDevice(device);

        rst = customerRepository.save(customer);
        tmp.setCusId(rst.getId());
        request = requestRepository.save(tmp);
    }


    @Test
    public void updateCustomer() throws Exception {
        Customer customer = new Customer("420123********1617", "1304****139", "my new address", "my new contactName");
        mockMvc.perform(put("/api" + "/customers/{id}", rst.getId())
                .content(objToJson(customer))
                .contentType(contentType))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    public void readAllCustomers() throws Exception {
        mockMvc.perform(get("/api" + "/customers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$._embedded.customers", hasSize(1)));
    }

    @Test
    public void readOneCustomer() throws Exception {
        mockMvc.perform(get("/api" + "/customers/{id}", rst.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.userId", is(rst.getUserId())))
                .andExpect(jsonPath("$.type", is(rst.getType())))
                .andExpect(jsonPath("$.contactName", is(rst.getContactName())))
                .andExpect(jsonPath("$.mobile", is(rst.getMobile())));

    }


    @Test()
    public void createCustomerOutOfBoundary() {
        Customer customer = new Customer("420204********1617", "152*****345", "my other address", "my contactName");
        customer.setType(0);
        Set<ConstraintViolation<Customer>> constraintViolations =
                validator.validate(customer);
        assertEquals(1, constraintViolations.size());
        assertEquals(
                "must be greater than or equal to 1",
                constraintViolations.iterator().next().getMessage()
        );
        customer.setType(5);
        constraintViolations =
                validator.validate(customer);
        assertEquals(1, constraintViolations.size());
        assertEquals("must be less than or equal to 4",
                constraintViolations.iterator().next().getMessage());
        customer.setType(1);
        customer.setZip("4300234");
        constraintViolations =
                validator.validate(customer);
        assertEquals(1, constraintViolations.size());
        assertEquals("size must be between 6 and 6",
                constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void addCustomer() throws Exception {
        mockMvc.perform(post("/api" + "/customers")
                .content(objToJson(new Customer("420204********1617", "152*****345", "my other address", "my other contactName")))
                .contentType(contentType))
                .andExpect(status().isCreated());
    }

    @Test
    public void addCustomerOutOfBoundary() throws Exception {
        Customer customer = new Customer("420204********1617", "1528*****345", "my other address", "my contactName");
        customer.setType(1);
        MvcResult result = mockMvc.perform(post("/api" + "/customers").content(objToJson(customer)).contentType(contentType))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].entity", is("Customer")))
                .andExpect(jsonPath("errors[0].message", is("size must be between 11 and 11")))
                .andExpect(jsonPath("errors[0].property", is("mobile")))
                .andReturn();
        System.err.println(result.getResponse().getContentAsString());
        customer.setType(0);
        customer.setEmail("test");
        mockMvc.perform(post("/api" + "/customers").content(objToJson(customer)).contentType(contentType))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void deleteCustomerWithRequestsShouldDeleteOtherRequestsAndDevices() throws Exception {
        mockMvc.perform(delete("/api/customers/{id}",rst.getId()))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/requests/{id}", request.getId()))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/customers/{id}", rst.getId()))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/devices/{id}", request.getDevice().getId()))
                .andExpect(status().isNotFound());
    }


    @After
    public void onDestroy() {
        requestRepository.deleteAll();
        customerRepository.deleteAll();
    }


}
