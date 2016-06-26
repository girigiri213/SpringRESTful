package com.girigiri.dao;

import com.girigiri.SpringMvcApplication;
import com.girigiri.dao.models.Customer;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

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
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CustomerRepositoryTests {


    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            SUBTYPE);

    private static final String SUBTYPE = "hal+json";

    private MockMvc mockMvc;
    private static Validator validator;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private CustomerRepository customerRepository;

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
        Customer customer = customerRepository.save(new Customer("420104********1617", "1301****139", "my address", "my contactName"));
        setupId = customer.getId();
    }


    @Test
    public void updateCustomer() throws Exception {
        Customer customer = new Customer("420123********1617", "1304****139", "my new address", "my new contactName");
        mockMvc.perform(put("/api" + "/customers/{id}", setupId)
                .content(objToJson(customer))
                .contentType(contentType))
                .andExpect(status().isNoContent())
                .andReturn();
        mockMvc.perform(get("/api" + "/customers/{id}", setupId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is("420123********1617")))
                .andExpect(jsonPath("$.mobile", is("1304****139")))
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
                .andExpect(jsonPath("$.userId", is("420104********1617")))
                .andExpect(jsonPath("$.type", is(1)))
                .andExpect(jsonPath("$.contactName", is("my contactName")))
                .andExpect(jsonPath("$.mobile", is("1301****139")));

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
        customer.setType(0);
        mockMvc.perform(post("/api" + "/customers").content(objToJson(customer)).contentType(contentType))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].entity", is("Customer")))
                .andExpect(jsonPath("errors[0].message", is("size must be between 11 and 11")))
                .andExpect(jsonPath("errors[0].property", is("mobile")))
                .andReturn();
        customer.setType(0);
        customer.setEmail("test");
        mockMvc.perform(post("/api" + "/customers").content(objToJson(customer)).contentType(contentType))
                .andExpect(status().isBadRequest());
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
