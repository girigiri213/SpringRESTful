package com.girigiri.dao;

import com.girigiri.SpringMvcApplication;
import com.girigiri.dao.models.Customer;
import com.girigiri.utils.TestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import static com.girigiri.utils.TestUtil.getResourceIdFromUrl;
import static com.girigiri.utils.TestUtil.objToJson;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by JianGuo on 6/25/16.
 * Unit test for {@link CustomerRepository}
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringMvcApplication.class)
@WebAppConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CustomerRepositoryTests {


    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            SUBTYPE);

    private static final String SUBTYPE = "hal+json";

    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private CustomerRepository customerRepository;

    private long setupId;

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        customerRepository.deleteAll();
        Customer customer = customerRepository.save(new Customer("420104199601021617", "13018060139", "my address", "my contactName"));
        setupId = customer.getId();
    }


    @Test
    public void addCustomerAndUpdate() throws Exception {
        Customer customer = new Customer("my userId", "my mobile", "my address", "my contactName");
        MvcResult result = mockMvc.perform(post("/api" + "/customers")
        .content(objToJson(customer))
        .contentType(contentType))
                .andExpect(status().isCreated())
                .andReturn();
        long id = getResourceIdFromUrl(result.getResponse().getRedirectedUrl());
        Customer c2 = new Customer("my new userId", "my new mobile", "my new address", "my new contactName");
        mockMvc.perform(put("/api" + "/customers/{id}", id)
                .content(objToJson(c2))
                .contentType(contentType))
                .andExpect(status().isNoContent())
                .andReturn();
        mockMvc.perform(get("/api" + "/customers/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is("my new userId")))
                .andExpect(jsonPath("$.mobile", is("my new mobile")))
                .andExpect(jsonPath("$.address", is("my new address")))
                .andExpect(jsonPath("$.contactName", is("my new contactName")));
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
        mockMvc.perform(get("/api" + "/customers/{id}", setupId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.userId", is("420104199601021617")))
                .andExpect(jsonPath("$.type", is(1)))
                .andExpect(jsonPath("$.contactName", is("my contactName")))
                .andExpect(jsonPath("$.mobile", is("13018060139")));

    }

    @Test
    public void addNewCustomer() throws Exception {
        mockMvc.perform(post("/api" + "/customers")
                .content(objToJson(new Customer("my userId", "my mobile", "my other address", "my other contactName")))
                .contentType(contentType))
                .andExpect(status().isCreated());
    }


    @Test
    public void deleteCustomer() throws Exception {
        mockMvc.perform(delete("/api" + "/customers" + "/" + setupId))
                .andExpect(status().isNoContent());
    }


    @After
    public void onDestroy() {
        customerRepository.deleteAll();
    }













}
