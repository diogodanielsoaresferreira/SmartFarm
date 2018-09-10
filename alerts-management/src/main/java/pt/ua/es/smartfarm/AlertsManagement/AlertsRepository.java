package pt.ua.es.smartfarm.AlertsManagement;

import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@CrossOrigin
@RepositoryRestResource(collectionResourceRel = "alerts", path="alerts")
public interface AlertsRepository extends CrudRepository<Alerts, Long> {
	//public List<Alerts> findBySensorId(String sensorId);

	public List<Alerts> findBySensorsIn(String sensorId);
}
