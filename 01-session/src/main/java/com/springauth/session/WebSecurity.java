package com.springauth.session;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter{

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.cors().and().csrf().disable()
				.authorizeRequests()
				.anyRequest().authenticated()
				.and()
				.httpBasic();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowCredentials(true);
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration.applyPermitDefaultValues());
		return source;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth)
			throws Exception {
		auth
				.inMemoryAuthentication()
				.withUser("user").password("password").roles("USER").and()
				.withUser("admin").password("password").roles("USER", "ADMIN");
	}
}
