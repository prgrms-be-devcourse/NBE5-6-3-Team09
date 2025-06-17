package com.codemap.core.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

//    @Value("${remember-me.key}")
//    private String rememberMeKey;


//    @Bean
//    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
//        return http.getSharedObject(AuthenticationManagerBuilder.class)
//            .build();
//    }
//
//
//    @Bean
//    public AuthenticationSuccessHandler successHandler() {
//        return new AuthenticationSuccessHandler() {
//            @Override
//            public void onAuthenticationSuccess(HttpServletRequest request,
//                HttpServletResponse response, Authentication authentication)
//                throws IOException, ServletException {
//
//                boolean isAdmin = authentication.getAuthorities()
//                    .stream()
//                    .anyMatch(authority ->
//                        authority.getAuthority().equals("ROLE_ADMIN"));
//
//                if (isAdmin) {
//                    response.sendRedirect("/admin/manage-members");
//                    return;
//                }
//
//                response.sendRedirect("/routines");
//            }
//        };
//    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(
                (requests) -> requests
                    .requestMatchers(GET,"/", "/user/signup", "/user/signup/*", "/user/signin","/css/**", "/js/**", "/img/**")
                    .permitAll()
                    .requestMatchers(POST, "/user/signin", "/user/signup").permitAll()
                    .requestMatchers(POST, "/chatbot/**", "/chatbot/message/**").permitAll()
                    .requestMatchers("/user/main").hasRole("USER")
                    // /admin/** 경로는 ROLE_ADMIN 역할을 가진 사용자만 접근 가능
                    .requestMatchers("/admin/**").hasRole("ADMIN")

                    // /routines/** 경로는 ROLE_USER 역할을 가진 사용자만 접근 가능
                    .requestMatchers("/routines/**").hasRole("USER")
                    .anyRequest().authenticated()
            )
            //.rememberMe(rememberMe -> rememberMe.key(rememberMeKey))
            .logout(LogoutConfigurer::permitAll)
            .csrf(csrf -> csrf.ignoringRequestMatchers("/chatbot/**", "/chatbot/message/**", "/admin/**", "/todos/**"))
            .sessionManagement(session -> session
                .sessionFixation().none()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}