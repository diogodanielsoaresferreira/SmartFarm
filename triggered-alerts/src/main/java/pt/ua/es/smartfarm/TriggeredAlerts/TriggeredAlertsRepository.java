package pt.ua.es.smartfarm.TriggeredAlerts;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@CrossOrigin
@RepositoryRestResource(collectionResourceRel = "triggeredalerts", path="triggeredalerts")
public interface TriggeredAlertsRepository extends CrudRepository<TriggeredAlerts, Long> {
    
}
