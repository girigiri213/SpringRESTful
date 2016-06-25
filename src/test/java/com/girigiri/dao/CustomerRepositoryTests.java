package com.girigiri.dao;

import com.girigiri.SpringMvcApplication;
import com.girigiri.dao.models.Customer;
import com.girigiri.utils.TestUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
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
    //// FIXME: 6/26/16 Status Code always be 204, not 200!
    public void changeCustomer() throws Exception {
        mockMvc.perform(put("/api" + "/customers/{id}", setupId)
                .content(TestUtil.objToJson(new Customer("my new userId", "my new mobile", "my new address", "my new contactName")))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is("my new userId")))
                .andExpect(jsonPath("$.contactName", is("my new contactName")))
                .andExpect(jsonPath("$.mobile", is("my new mobile")))
                .andExpect(jsonPath("$.address", is("my new address")));
    }


//    @Test
//    public void readAllCustomers() throws Exception {
//        mockMvc.perform(get("/api" + "/customers"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(contentType))
//                .andExpect(jsonPath("$._embedded.customers", hasSize(1)));
//    }
//
//    @Test
//    public void readOneCustomer() throws Exception {
//        mockMvc.perform(get("/api" + "/customers/{id}", setupId))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(contentType))
//                .andExpect(jsonPath("$.userId", is("420104199601021617")))
//                .andExpect(jsonPath("$.type", is(1)))
//                .andExpect(jsonPath("$.contactName", is("my contactName")))
//                .andExpect(jsonPath("$.mobile", is("13018060139")));
//
//    }
//
//    @Test
//    public void addNewCustomer() throws Exception {
//        mockMvc.perform(post("/api" + "/customers")
//                .content(TestUtil.objToJson(new Customer("my userId", "my mobile", "my other address", "my other contactName")))
//                .contentType(contentType))
//                .andExpect(status().isCreated());
//    }


//    @Test
//    public void deleteCustomer() throws Exception {
//        mockMvc.perform(delete("/api" + "/customers" + "/" + setupId))
//                .andExpect(status().isNoContent());
//    }









}
