package pt.ua.es.smartfarm.ServiceLayer;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
//import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Sensors repository.
 */
@CrossOrigin
//@PreAuthorize("hasRole('ROLE_BASIC')")
@RepositoryRestResource(collectionResourceRel = "sensors", path="sensors")
public interface SensorsRepository extends CrudRepository<Sensors, Long> {
    
}
