package pt.ua.es.smartfarm.ServiceLayer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;


@RestController
@CrossOrigin
@RequestMapping("/users")
public class SfuserController {
    SfusersService userService;

    @Autowired
    PasswordEncoder passwordEncoder;
    
    @Autowired
    public SfuserController(SfusersService userService) {
        this.userService = userService;
    }

    @RequestMapping( value = "", method = RequestMethod.POST )
    public Sfusers addEntity(@RequestBody Sfusers user) {
        return userService.addEntity(user);
    }

    @RequestMapping( value = "", method = RequestMethod.PUT )
    public Sfusers updateEntity(@RequestBody Sfusers user) {
        return userService.updateEntity(user);
    }
}
