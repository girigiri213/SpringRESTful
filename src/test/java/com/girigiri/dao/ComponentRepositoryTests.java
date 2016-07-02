package com.girigiri.dao;

import com.girigiri.SpringMvcApplication;
import com.girigiri.dao.models.Component;
import com.girigiri.dao.models.Manager;
import com.girigiri.dao.services.ComponentRepository;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.TestPropertySource;
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
 * Unit test for {@link com.girigiri.dao.services.ComponentRepository}
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringMvcApplication.class)
@WebAppConfiguration
public class ComponentRepositoryTests {
    private MockMvc mockMvc;
    private static Validator validator;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ComponentRepository componentRepository;




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
    }


    @Test
    @WithMockUser(username = "guojian", roles = {"ENGINEER"})
    public void addComponentWithWrongRoleWillFail() throws Exception {
        Component component = new Component("new component needed", 20, 35, 20);
        assertEquals(1, component.getState());
        mockMvc.perform(post("/api/components").contentType(contentType).content(objToJson(component)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    public void addComponentWithoutLoginWillFail() throws Exception {
        Component component = new Component("new component needed", 20, 35, 20);
        assertEquals(1, component.getState());
        mockMvc.perform(post("/api/components").contentType(contentType).content(objToJson(component)))
                .andExpect(status().isUnauthorized());
    }
    @Test
    @WithMockUser(username = "guojian", roles = {"REPO_MANAGER"})
    public void addComponent() throws Exception {
        Component component = new Component("new component needed", 20, 35, 20);
        assertEquals(1, component.getState());
        mockMvc.perform(post("/api/components").contentType(contentType).content(objToJson(component)))
                .andExpect(status().isCreated());

    }



    @Test
    @WithAnonymousUser
    public void updateComponentWithoutLoginWillFail() throws Exception {
        Component component = new Component("new updated component", 45, 24, 35);
        assertEquals(3, component.getState());
        mockMvc.perform(put("/api/components/{id}", setupId).content(objToJson(component)).contentType(contentType))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "guojian", roles = {"ENGINEER"})
    public void updateComponentWithWrongRoleWillFail() throws Exception {
        Component component = new Component("new updated component", 45, 24, 35);
        assertEquals(3, component.getState());
        mockMvc.perform(put("/api/components/{id}", setupId).content(objToJson(component)).contentType(contentType))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "guojian", roles = {"REPO_MANAGER"})
    public void updateComponent() throws Exception {
        Component component = new Component("new updated component", 45, 24, 35);
        assertEquals(3, component.getState());
        mockMvc.perform(put("/api/components/{id}", setupId).content(objToJson(component)).contentType(contentType))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/components/{id}", setupId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(component.getName())))
                .andExpect(jsonPath("$.price", is(component.getPrice())))
                .andExpect(jsonPath("$.size", is(component.getSize())))
                .andExpect(jsonPath("$.warningSize", is(component.getWarningSize())));
    }

    @Test
    @WithMockUser(username = "guojian", roles = {"REPO_MANAGER"})
    public void deleteComponent() throws Exception {
        mockMvc.perform(delete("/api/components/{id}", setupId))
                .andExpect(status().isNoContent());
    }

    @Test
    public void createInvalidComponent() {
        Component component = new Component("some component", -10, 10, 30);
        Set<ConstraintViolation<Component>> constraintViolations =
                validator.validate(component);
        assertEquals(1, constraintViolations.size());
        assertEquals("must be greater than or equal to 0",
                constraintViolations.iterator().next().getMessage());

        component.setPrice(10);
        component.setSize(-10);
        constraintViolations = validator.validate(component);
        assertEquals(1, constraintViolations.size());
        assertEquals("must be greater than or equal to 0",
                constraintViolations.iterator().next().getMessage());

        component.setSize(10);
        component.setWarningSize(-10);
        constraintViolations = validator.validate(component);
        assertEquals(1, constraintViolations.size());
        assertEquals("must be greater than or equal to 0",
                constraintViolations.iterator().next().getMessage());
    }

    @Test
    @WithMockUser(username = "guojian", roles = {"REPO_MANAGER"})
    public void addInvalidComponent() throws Exception {
        Component component = new Component("some component", -10, 10, 30);
        mockMvc.perform(post("/api/components").contentType(contentType).content(objToJson(component)))
                .andExpect(status().isBadRequest());
    }


    @After
    public void onDestroy() {
//        componentRepository.deleteAll();
    }
}
