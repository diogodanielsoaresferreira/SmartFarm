package pt.ua.es.smartfarm.ServiceLayer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.security.Principal;


/**
 * Spring boot main application.
 */
@SpringBootApplication
@RestController
public class ServiceLayerApplication {

    @Autowired
    private PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(ServiceLayerApplication.class, args);
	}

    private UserDetailsService userDetailsService(final SfusersRepository repository) {
		return username -> new SfusersDetails(repository.findByEmail(username).get(0));
    }
    
    /**
	 * Password grants are switched on by injecting an AuthenticationManager.
	 * Here, we setup the builder so that the userDetailsService is the one we coded.
	 * @param builder
	 * @param repository
	 * @throws Exception
     */
	@Autowired
	public void authenticationManager(AuthenticationManagerBuilder builder,
            SfusersRepository repository, SfusersService service) throws Exception {
		builder.userDetailsService(userDetailsService(repository))
            .passwordEncoder(passwordEncoder);
    }

    @RequestMapping("/user")
	public Principal user(Principal user) {
		return user;
	}

}

@Configuration
class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
