
package pt.ua.es.smartfarm.ServiceLayer;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * Users repository.
 */
@RepositoryRestResource(collectionResourceRel = "sfusers", path="sfusers")
public interface SfusersRepository extends CrudRepository<Sfusers, Long> {
    List<Sfusers> findByEmail(@Param("email") String email);
}
