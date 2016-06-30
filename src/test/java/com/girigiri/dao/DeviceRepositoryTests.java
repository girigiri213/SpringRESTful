package com.girigiri.dao;

import com.girigiri.SpringMvcApplication;
import com.girigiri.dao.models.Customer;
import com.girigiri.dao.models.Device;
import com.girigiri.dao.models.Request;
import com.girigiri.dao.services.CustomerRepository;
import com.girigiri.dao.services.DeviceRepository;
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
import org.springframework.web.context.WebApplicationContext;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static com.girigiri.utils.TestUtil.contentType;
import static com.girigiri.utils.TestUtil.objToJson;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by JianGuo on 6/26/16.
 * Unit test for {@link com.girigiri.dao.services.DeviceRepository}
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringMvcApplication.class)
@WebAppConfiguration
public class DeviceRepositoryTests {

    private MockMvc mockMvc;
    private static Validator validator;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private RequestRepository requestRepository;


    @Autowired
    private CustomerRepository customerRepository;

    private long setupId;
    private Request rst;

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
        Request request = new Request(155, "2016-7-7", 1);
        request.setDevice(device);

        Customer customer1 = customerRepository.save(customer);
        request.setCusId(customer1.getId());
        rst = requestRepository.save(request);
        setupId = request.getDevice().getId();
    }

    @Test
    public void addDevice() throws Exception {
        Device device = new Device(2, "some other error", 2);
        mockMvc.perform(post("/api/devices").contentType(contentType).content(objToJson(device)))
                .andExpect(status().isCreated());
    }

    @Test
    public void getDevice() throws Exception {
        mockMvc.perform(get("/api/devices/{id}", setupId))
                .andExpect(jsonPath("$.type", is(1)))
                .andExpect(jsonPath("$.error", is("some error")))
                .andExpect(jsonPath("$.errorType", is(1)));
    }

    @Test
    public void deleteDeviceIsNotAllowed() throws Exception {
        mockMvc.perform(delete("/api/devices/{id}", setupId))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void updateDevice() throws Exception {
        Device device = new Device(3, "some other new error", 1);
        mockMvc.perform(put("/api/devices/{id}", setupId).content(objToJson(device)).contentType(contentType))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/devices/{id}", setupId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type", is(device.getType())))
                .andExpect(jsonPath("$.error", is(device.getError())))
                .andExpect(jsonPath("$.errorType", is(device.getErrorType())));
    }


    @Test
    public void addInvalidDevice() throws Exception {
        Device device = new Device(0, "some error", 1);
        mockMvc.perform(post("/api/devices").contentType(contentType).content(objToJson(device)))
                .andExpect(status().isBadRequest());
        device.setType(1);
        device.setError(null);
        mockMvc.perform(post("/api/devices").contentType(contentType).content(objToJson(device)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createInvalidDevice() {
        Device device = new Device(0, "some error", 1);
        Set<ConstraintViolation<Device>> constraintViolations =
                validator.validate(device);
        assertEquals(1, constraintViolations.size());
        assertEquals("must be greater than or equal to 1",
                constraintViolations.iterator().next().getMessage());
        device.setType(6);

        constraintViolations =
                validator.validate(device);
        assertEquals(1, constraintViolations.size());
        assertEquals("must be less than or equal to 5",
                constraintViolations.iterator().next().getMessage());
        device.setType(1);

        device.setErrorType(0);
        constraintViolations =
                validator.validate(device);
        assertEquals(1, constraintViolations.size());
        assertEquals("must be greater than or equal to 1",
                constraintViolations.iterator().next().getMessage());

        device.setErrorType(3);
        constraintViolations =
                validator.validate(device);
        assertEquals(1, constraintViolations.size());
        assertEquals("must be less than or equal to 2",
                constraintViolations.iterator().next().getMessage());

    }




    @After
    public void onDestroy() {
        requestRepository.deleteAll();
        customerRepository.deleteAll();
        deviceRepository.deleteAll();
    }
}
