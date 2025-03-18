package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.demo.service.UserDetailsServiceImpl;

import java.util.Arrays;


@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, UserDetailsServiceImpl userDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Enable CORS
                .csrf(csrf -> csrf.disable()) // Disable CSRF for API
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll() //Allow H2 console
                        .requestMatchers("/api/auth/login", "/api/auth/register").permitAll() //Allow login & register

                        //Allow Swagger UI Access
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        //Project related permissions
                        .requestMatchers(HttpMethod.GET, "/api/projects/{id}").hasAnyAuthority("USER", "ADMIN") //Users & Admins can view projects
                        .requestMatchers(HttpMethod.GET, "/api/projects").hasAnyAuthority("USER", "ADMIN") //Users & Admins can list projects
                        .requestMatchers(HttpMethod.POST, "/api/projects").hasAuthority("ADMIN") //Only Admins can create
                        .requestMatchers(HttpMethod.PUT, "/api/projects/{id}").hasAuthority("ADMIN") //Only Admins can update
                        .requestMatchers(HttpMethod.DELETE, "/api/projects/{id}").hasAuthority("ADMIN") //Only Admins can delete

                        //Task permissions (Updated)
                        .requestMatchers(HttpMethod.GET, "/api/tasks/{id}").hasAnyAuthority("USER", "ADMIN") //Users & Admins can view tasks
                        .requestMatchers(HttpMethod.GET, "/api/tasks").hasAnyAuthority("USER", "ADMIN") //Users & Admins can list tasks
                        .requestMatchers(HttpMethod.POST, "/api/tasks").hasAnyAuthority("USER", "ADMIN") //Users & Admins can create tasks
                        .requestMatchers(HttpMethod.PUT, "/api/tasks/{id}").hasAuthority("ADMIN") //Only Admins can update tasks
                        .requestMatchers(HttpMethod.DELETE, "/api/tasks/{id}").hasAuthority("ADMIN") //Only Admins can delete tasks
                        .requestMatchers(HttpMethod.GET, "/api/tasks/getTask").hasAnyAuthority("USER", "ADMIN") //Allow users & admins

                        .anyRequest().authenticated() //Protect all other endpoints
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable())); //Allow H2 Console

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200")); //Allow Angular frontend
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
