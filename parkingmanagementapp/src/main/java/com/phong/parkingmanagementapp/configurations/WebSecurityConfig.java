package com.phong.parkingmanagementapp.configurations;

import com.phong.parkingmanagementapp.services.impl.JwtRequestFilter;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    @Lazy
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    private List<String> publicUrls = List.of("/",
            "/api/auth/**",
            "/api/auth/authenticate",
            "/api/auth/register",
            "/login",
            "/logout",
            "/oauth2/**",
            "/api/payment/createPayment",
            "/api/successPayment",
            "/api/verifyMail/**",
            "/api/verifyOtp/**",
            "/api/receipt/**",
            "/api/floor/list",
            "/api/line/**",
            "/api/position/**",
            "/api/ticket/**",
            "/test/**",
            "/api/forgetPassword/**",
            "/api/pay/fast-payment"
    );

    private List<String> securityUrls = List.of("/",
            "/api/customer/list",
            "/api/security/list",
            "/api/customer/**",
            "/api/user/**",
            "/api/vehicle/**",
            "/api/recognize/**"
    );

    private List<String> customerUrls = List.of("/",
            "/api/customerr/**"
    );

    private List<String> securityUrlsFinal = List.of("/",
            //            "/api/entry/image/ticket/get",
            //            "/api/entry/ticket/process",
            //            "/api/entry/list",
            //            "/api/entry/**/ticket/getTicketId",
            //            "/api/entry/ticket/get/all",
            "/api/entry/**",
            //            "/api/ticket/**/position/get",
            //            "/api/ticket/list",
            //            "/api/ticket/**/delete",
            //            "/api/ticket/create/anonymous",
            "/api/ticket/**",
            //            "/api/position/**/updateState/in",
            //            "/api/position/**/updateState/out",
            //            "/api/position/**/plate/check",
            "/api/position/**",
            //            "/api/customer/**/deactivate",
            //            "/api/customer/create",
            //            "/api/customer/**/receipt/list",
            "/api/customer/**",
            "/api/recognize/out/record",
            "/api/security/ticket/paid",
            "/api/receipt/create",
            "/api/floorDTO/list",
            "/api/receipt/list",
            "/api/vehicle/list",
            "/api/vehicle/create",
            "/api/vehicle/list/blank",
            "/api/vehicle/blank/clear",
            "/api/recognize/in/record",
            "/api/security/list",
            "/api/user/**"
    );

    private List<String> customerUrlsFinal = List.of("/",
            "/api/vehicle/**/create",
            "/api/vehicle/types",
            "/api/analyzePlate",
            "/api/floor/list",
            "/api/ticket/price/list",
            "/api/ticket/type/list",
            "/api/ticket/price/totalPrice",
            "/api/ticket/create",
            "/api/user/**/vehicle/list",
            "/api/line/list",
            "/api/position/list",
            "/api/payment/createPayment",
            "/api/customer/**/receipt/list",
            "/api/customerr/ticket/**/list"
    );

    private List<String> adminUrls = List.of("/",
            "/area",
            "/addFloor",
            "/addLine",
            "/addPosition",
            "/tickets/**",
            "/tickets/add",
            "/getLinesByFloorId/**",
            "/getPositionsByLineId/**",
            "/users",
            "/getVehiclesByCustomerId/**"
    );

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error")
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                .requestMatchers(publicUrls.toArray(new String[0])).permitAll()
                .requestMatchers(adminUrls.toArray(new String[0])).hasRole("ADMIN")
                .requestMatchers(securityUrlsFinal.toArray(new String[0])).hasAnyRole("ADMIN", "SECURITY", "CUSTOMER")
                .requestMatchers(customerUrls.toArray(new String[0])).hasAnyRole("CUSTOMER", "ADMIN", "SECURITY")
                .anyRequest().authenticated()
                ).oauth2Login(oauth -> oauth
                .loginPage("/oauth2/login")
                .defaultSuccessUrl("http://localhost:3000/oauth2/success", true))
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll());

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
