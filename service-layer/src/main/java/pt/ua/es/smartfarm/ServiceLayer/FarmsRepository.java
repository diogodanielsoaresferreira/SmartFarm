package pt.ua.es.smartfarm.ServiceLayer;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * Repository of farms.
 */
@CrossOrigin
@RepositoryRestResource(collectionResourceRel = "farms", path="farms")
public interface FarmsRepository extends CrudRepository<Farms, Long> {
    List<Farms> findByAddress(@Param("address") String address);
}
