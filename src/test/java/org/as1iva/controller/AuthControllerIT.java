package org.as1iva.controller;

import org.as1iva.config.TestConfig;
import org.as1iva.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {TestConfig.class})
class AuthControllerIT {

    @Autowired
    private AuthService authService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    public static final String TEST_LOGIN = "test_login";

    public static final String TEST_PASSWORD = "test_password";

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    void givenUserExists_whenSignUpWithSameLogin_thenShouldReject() throws Exception {
        authService.signUpUser(TEST_LOGIN, TEST_PASSWORD);

        mockMvc.perform(post("/signup")
                        .param("login", TEST_LOGIN)
                        .param("password", TEST_PASSWORD)
                        .param("repeatPassword", TEST_PASSWORD))
                .andExpect(status().isOk())
                .andExpect(view().name("sign-up"))
                .andExpect(model().attributeHasFieldErrors("userInfo", "login"));
    }
}