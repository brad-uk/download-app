package uk.co.javawork.svcs.download.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
@Configuration
public class WebSecurity extends WebSecurityConfigurerAdapter {
	
	@Value("${user}")
	private String user;
	
	@Value("${password}")
	private String password;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
	    
		http
        .authorizeRequests()
        .antMatchers("/login").permitAll()//this needs to match the log in page request mapping below
        .antMatchers("/css/**","/js/**","/img/**").permitAll()
            .anyRequest().authenticated()
            .and()
        //.httpBasic();
        .formLogin().loginPage("/login").and().logout().logoutUrl("/logout");//this needs to match the request mapping in the controller
	}

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .inMemoryAuthentication()
                .withUser(user).password(password).roles("USER");
    }
}
