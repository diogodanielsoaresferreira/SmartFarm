package pt.ua.es.smartfarm.ServiceLayer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.*;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import java.util.HashMap;
import java.util.Map;

@Service
public class SfusersService {

    @Autowired
    private SfusersRepository sfuserRepository;

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    
    public Sfusers addEntity(Sfusers user) {
        //Here we tell the password setter to generate the salt
        PasswordEncoder passwordEncoder = getPasswordEncoder();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return sfuserRepository.save(user);
    }

    public Sfusers updateEntity(Sfusers user) {
        Sfusers oldUser = sfuserRepository.findById(user.getId()).orElse(null);
        /*
        *This step is necessary to maintain the same password since if we do not do this 
        *in the database a null is generated in the password field, 
        *this happens since the JSON that arrives from the client application does not 
        *contain the password field, This is because to carry out the modification of 
        *the password a different procedure has to be performed
        */
        user.setPassword(oldUser.getPassword());

        return sfuserRepository.save(user);
    }
}
