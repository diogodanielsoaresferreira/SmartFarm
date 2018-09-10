
package pt.ua.es.smartfarm.ServiceLayer;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Repository of permission groups.
 */
@RepositoryRestResource(collectionResourceRel = "permissions", path="permissions")
public interface PermissionGroupsRepository extends CrudRepository<PermissionGroups, Long> {

}
