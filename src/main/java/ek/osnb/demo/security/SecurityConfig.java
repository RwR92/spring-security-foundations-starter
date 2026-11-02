package ek.osnb.demo.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(CsrfConfigurer::spa)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/index.html", "/app.js", "/api.js", "/style.css", "/components/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/notes").authenticated()
                        .requestMatchers("/api/public").permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginProcessingUrl("/api/login")
                        .successHandler((_, res, _) -> res.setStatus(HttpServletResponse.SC_NO_CONTENT))
                        .failureHandler((_, res, _) -> res.setStatus(HttpServletResponse.SC_UNAUTHORIZED))
                )
                .logout(logout -> logout
                        .logoutUrl("/api/logout")
                        .logoutSuccessHandler((_, res, _) -> res.setStatus(HttpServletResponse.SC_NO_CONTENT))
                )
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(
                                (_, res, ex) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                );
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        int strength = 10;
        return new BCryptPasswordEncoder(strength);
    }

//    @Bean
//    UserDetailsService userDetailsService(PasswordEncoder encoder) {
//        UserDetails user1 = User.builder()
//                .username("user1")
//                .password(encoder.encode("pw"))
//                .roles("USER")
//                .build();
//        UserDetails user2 = User.builder()
//                .username("user2")
//                .password(encoder.encode("pw"))
//                .roles("ADMIN")
//                .build();
//
//        return new InMemoryUserDetailsManager(user1,user2);
//    }
}
