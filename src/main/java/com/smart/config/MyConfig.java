package com.smart.config;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class MyConfig {

	@Bean
	public UserDetailsService getUserDetailsService() {
		
		return new UserDetailsServiceImpl();
		
	}
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		
		return new BCryptPasswordEncoder();
	}
	
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		
		DaoAuthenticationProvider daoAuthenticationProvider= new DaoAuthenticationProvider();
		
		daoAuthenticationProvider.setUserDetailsService(this.getUserDetailsService());
		
		daoAuthenticationProvider.setPasswordEncoder(this.passwordEncoder());
		
		return daoAuthenticationProvider;
	}
	
	
	//Configure method(older methods which have now been depriciated)
	
	/*
	 * @Override protected void configure(AuthenticationManagerBuilder auth) throws
	 * Exception{
	 * 
	 * auth.authenticationProvider(this.authenticationProvider()); }
	 * 
	 * 
	 * @Override protected void configure(HttpSecurity http) throws Exception{
	 * 
	 * http.authorizeRequests().antMatchers("/admin/**").hasRole("ADMIN").
	 * antMatchers("/user/**").hasRole("USER").antMatchers("/**").permitAll().and().
	 * formLogin().and().csrf().disable(); }
	 */
	
	
	  @Bean 
	  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
	  
	  http.authorizeRequests().requestMatchers("/admin/**").hasRole("ADMIN").
	  requestMatchers("/user/**").hasRole("USER").requestMatchers("/**").permitAll(
	  ).and().formLogin()
	  .loginPage("/signin")
	  .loginProcessingUrl("/dologin")
	  .defaultSuccessUrl("/user/index")
	  .and().csrf().disable();
	  
	  
	  http.authenticationProvider(this.authenticationProvider());
	  
	  DefaultSecurityFilterChain defaultSecurityFilterChain = http.build();
	  
	  return defaultSecurityFilterChain;
	  
	  }
	 
	
	
	@Bean
	public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration configuration) throws Exception {
		
	    return	configuration.getAuthenticationManager();
	}
	
}
