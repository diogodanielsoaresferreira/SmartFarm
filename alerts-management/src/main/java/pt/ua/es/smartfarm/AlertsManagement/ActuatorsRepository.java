package pt.ua.es.smartfarm.AlertsManagement;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;


@CrossOrigin
@RepositoryRestResource(collectionResourceRel = "actuators", path="actuators")
public interface ActuatorsRepository extends CrudRepository<Actuators, Long> {
    public List<Actuators> findBySensorsIn(String sensorId);
    public Actuators findByAlertId(String alertId);
}
