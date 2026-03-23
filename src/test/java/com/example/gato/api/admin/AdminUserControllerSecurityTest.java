package com.example.gato.api.admin;

import com.example.gato.repository.AppUserRepository;
import com.example.gato.security.JwtService;
import com.example.gato.service.AdminUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminUserController.class)
@Import(AdminUserControllerSecurityTest.TestSecurityConfig.class)
class AdminUserControllerSecurityTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AdminUserService users;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private AppUserRepository appUserRepository;

    @Test
    void returnsUnauthorizedWhenAnonymous() throws Exception {
        mvc.perform(get("/api/admin/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void returnsForbiddenWhenAuthenticatedWithoutAdminAuthority() throws Exception {
        mvc.perform(get("/api/admin/users")
                        .with(user("user@example.com").authorities(new SimpleGrantedAuthority("USER"))))
                .andExpect(status().isForbidden());
    }

    @TestConfiguration
    @EnableMethodSecurity
    static class TestSecurityConfig {

        @Bean
        SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                    .httpBasic(httpBasic -> {
                    });
            return http.build();
        }
    }
}
