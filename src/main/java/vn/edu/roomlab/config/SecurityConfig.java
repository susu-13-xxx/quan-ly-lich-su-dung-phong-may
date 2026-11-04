package vn.edu.roomlab.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/login",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/error",
                    "/access-denied"
                ).permitAll()

                // Chỉ Admin được truy cập khu vực quản trị và API quản lý tài khoản/vai trò.
                .requestMatchers(
                    "/admin/**",
                    "/api/users/**",
                    "/api/roles/**"
                ).hasRole("ADMIN")

                // Các chức năng nghiệp vụ dùng chung yêu cầu đăng nhập.
                .requestMatchers(
                    "/bookings/**",
                    "/api/bookings/**",
                    "/api/incidents/**",
                    "/api/rooms/**",
                    "/api/equipments/**"
                ).authenticated()

                // Dành sẵn cho các trang riêng theo vai trò khi được bổ sung.
                .requestMatchers("/teacher/**").hasRole("TEACHER")
                .requestMatchers("/technician/**").hasRole("TECHNICIAN")

                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(exception -> exception
                .accessDeniedPage("/access-denied")
            );

        return http.build();
    }
}