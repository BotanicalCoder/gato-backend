package com.example.gato.api.admin;

import com.example.gato.api.admin.dto.AdminBadgeDto;
import com.example.gato.repository.AppUserRepository;
import com.example.gato.security.JwtService;
import com.example.gato.service.AdminBadgeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminBadgeController.class)
@Import(AdminBadgeControllerTest.TestSecurityConfig.class)
class AdminBadgeControllerTest {

        @Autowired
        private MockMvc mvc;

        @MockitoBean
        private AdminBadgeService badges;

        @MockitoBean
        private JwtService jwtService;

        @MockitoBean
        private AppUserRepository appUserRepository;

        @Test
        void rejectsNonAdminUser() throws Exception {
                mvc.perform(get("/api/admin/badges")
                                .with(user("user@example.com").authorities(new SimpleGrantedAuthority("USER"))))
                                .andExpect(status().isForbidden());
        }

        @Test
        void listsBadgesForAdmin() throws Exception {
                UUID badgeId = UUID.randomUUID();
                when(badges.list()).thenReturn(List.of(new AdminBadgeDto(
                                badgeId,
                                "FIRST_DONE",
                                "First Done",
                                "Awarded for first todo",
                                null,
                                null,
                                1)));

                mvc.perform(get("/api/admin/badges")
                                .with(user("admin@example.com").authorities(new SimpleGrantedAuthority("ADMIN"))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].code").value("FIRST_DONE"))
                                .andExpect(jsonPath("$[0].minCompletedTodos").value(1));
        }

        @Test
        void createsBadgeForAdmin() throws Exception {
                UUID badgeId = UUID.randomUUID();
                when(badges.create(org.mockito.ArgumentMatchers.any())).thenReturn(new AdminBadgeDto(
                                badgeId,
                                "POWER_USER",
                                "Power User",
                                "Earn 100 points",
                                100,
                                null,
                                null));

                mvc.perform(post("/api/admin/badges")
                                .with(user("admin@example.com").authorities(new SimpleGrantedAuthority("ADMIN")))
                                .contentType("application/json")
                                .content("""
                                                {
                                                  "code": "POWER_USER",
                                                  "name": "Power User",
                                                  "description": "Earn 100 points",
                                                  "minPoints": 100
                                                }
                                                """))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value("POWER_USER"))
                                .andExpect(jsonPath("$.minPoints").value(100));
        }

        @Test
        void updatesBadgeForAdmin() throws Exception {
                UUID badgeId = UUID.randomUUID();
                when(badges.update(org.mockito.ArgumentMatchers.eq(badgeId), org.mockito.ArgumentMatchers.any()))
                                .thenReturn(new AdminBadgeDto(
                                                badgeId,
                                                "STREAK_14",
                                                "14 Day Streak",
                                                "Two-week streak",
                                                null,
                                                14,
                                                null));

                mvc.perform(put("/api/admin/badges/{id}", badgeId)
                                .with(user("admin@example.com").authorities(new SimpleGrantedAuthority("ADMIN")))
                                .contentType("application/json")
                                .content("""
                                                {
                                                  "code": "STREAK_14",
                                                  "name": "14 Day Streak",
                                                  "description": "Two-week streak",
                                                  "minStreakCount": 14
                                                }
                                                """))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.minStreakCount").value(14));
        }

        @Test
        void deletesBadgeForAdmin() throws Exception {
                UUID badgeId = UUID.randomUUID();

                mvc.perform(delete("/api/admin/badges/{id}", badgeId)
                                .with(user("admin@example.com").authorities(new SimpleGrantedAuthority("ADMIN"))))
                                .andExpect(status().isOk());

                verify(badges).delete(badgeId);
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
