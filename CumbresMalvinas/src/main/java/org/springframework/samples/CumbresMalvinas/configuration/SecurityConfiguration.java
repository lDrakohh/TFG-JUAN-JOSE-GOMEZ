package org.springframework.samples.CumbresMalvinas.configuration;

import static org.springframework.security.config.Customizer.withDefaults;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.samples.CumbresMalvinas.configuration.jwt.AuthEntryPointJwt;
import org.springframework.samples.CumbresMalvinas.configuration.jwt.AuthTokenFilter;
import org.springframework.samples.CumbresMalvinas.configuration.services.UserDetailsServiceImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

	@Autowired
	UserDetailsServiceImpl userDetailsService;

	@Autowired
	private AuthEntryPointJwt unauthorizedHandler;

	@Value("${spring.profiles.active:}")
	private String activeProfile;

	@Autowired
	DataSource dataSource;

	private static final String ADMIN = "ADMIN";	

	@Bean
	protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
		
		http
			.cors(withDefaults())		
			.csrf(AbstractHttpConfigurer::disable)		
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))			
			.headers((headers) -> headers.frameOptions((frameOptions) -> frameOptions.disable()))
			.exceptionHandling((exepciontHandling) -> exepciontHandling.authenticationEntryPoint(unauthorizedHandler))			
			
			.authorizeHttpRequests(authorizeRequests ->	authorizeRequests
			.requestMatchers("/resources/**", "/webjars/**", "/static/**", "/swagger-resources/**").permitAll()			
			.requestMatchers( "/", "/oups","/api/v1/auth/**","/v3/api-docs/**","/swagger-ui.html","/swagger-ui/**").permitAll()												
			.requestMatchers("/api/v1/developers").permitAll()												
			.requestMatchers(AntPathRequestMatcher.antMatcher("+/**")).hasAuthority(ADMIN)
			.requestMatchers("/api/v1/previsiones").permitAll()
			.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/previsiones/**")).permitAll()
			.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET,"/api/v1/previsiones/**")).permitAll()	
			.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST,"/api/v1/previsiones/**")).permitAll()	
			.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.PUT,"/api/v1/previsiones/**")).permitAll()	
			.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.DELETE,"/api/v1/previsiones/**")).permitAll()
			.requestMatchers("/frutas").hasAuthority(ADMIN)
			.requestMatchers("/frutas/**").hasAuthority(ADMIN)
			.requestMatchers("/api/v1/frutas").hasAuthority(ADMIN)
			.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/frutas/**")).hasAuthority(ADMIN)
			.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET,"/api/v1/frutas/**")).hasAuthority(ADMIN)
			.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST,"/api/v1/frutas/**")).hasAuthority(ADMIN)
			.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.PUT,"/api/v1/frutas/**")).hasAuthority(ADMIN)
			.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.DELETE,"/api/v1/frutas/**")).hasAuthority(ADMIN)
			.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/registros/**")).permitAll()
			.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET,"/api/v1/registros/**")).permitAll()
			.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST,"/api/v1/registros/**")).permitAll()	
			.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.PUT,"/api/v1/registros/**")).permitAll()
			.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.DELETE,"/api/v1/registros/**")).permitAll()
			.requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()
			.anyRequest().authenticated())					
			
			.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
			
			if (!"test".equals(activeProfile)) {
				http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
			} 
		return http.build();
	}

	@Bean
	public AuthTokenFilter authenticationJwtTokenFilter() {
		return new AuthTokenFilter();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
		return config.getAuthenticationManager();
	}	


	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	
	
}
