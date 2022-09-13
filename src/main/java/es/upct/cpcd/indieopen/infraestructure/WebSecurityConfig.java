package es.upct.cpcd.indieopen.infraestructure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import es.upct.cpcd.indieopen.infraestructure.authenticate.JwtTokenAuthenticationFilter;
import es.upct.cpcd.indieopen.infraestructure.authenticate.JwtTokenProvider;
import es.upct.cpcd.indieopen.infraestructure.tenant.TenantFilter;
import es.upct.cpcd.indieopen.user.UserService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private static final String[] AUTH_WHITELIST = { "/auth/**", "/video/**", "/media/**", "/explore/**", "/course/**",
			"/userhandle/**", "/access/**", "/model/**" };

	private final UserService userService;
	private final JwtTokenProvider jwtTokenProvider;

	@Autowired
	public WebSecurityConfig(UserService userService, JwtTokenProvider jwtTokenProvider) {
		this.userService = userService;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userService);
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.httpBasic().disable().csrf().disable().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests()
				.antMatchers(AUTH_WHITELIST).permitAll().anyRequest().authenticated().and()
				.addFilterBefore(tenantFilter(), UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(authFilter(), UsernamePasswordAuthenticationFilter.class);
	}

	@Bean
	public TenantFilter tenantFilter() {
		return new TenantFilter();
	}

	@Bean
	public JwtTokenAuthenticationFilter authFilter() {
		return new JwtTokenAuthenticationFilter(jwtTokenProvider);
	}

	// Disable filter registration ->
	// https://www.javaer101.com/en/article/499414.html
	@Bean
	public FilterRegistrationBean<TenantFilter> tenantRegister(TenantFilter tenantFilter) {
		FilterRegistrationBean<TenantFilter> registration = new FilterRegistrationBean<>(tenantFilter);
		registration.setEnabled(false);
		return registration;
	}

	@Bean
	public FilterRegistrationBean<JwtTokenAuthenticationFilter> registration(JwtTokenAuthenticationFilter authFilter) {
		FilterRegistrationBean<JwtTokenAuthenticationFilter> registration = new FilterRegistrationBean<>(authFilter);
		registration.setEnabled(false);
		return registration;
	}

}
