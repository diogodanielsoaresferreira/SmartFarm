package pt.ua.es.smartfarm.ServiceLayer;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Group of permissions to users.
 */
@Entity
@Data
public class PermissionGroups implements Serializable {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @NotNull
    private Boolean owner;

    @NotBlank
    private Boolean readPermission;

    @NotBlank
    private Boolean writePermission;

    @ManyToMany(mappedBy = "permissionGroupSet")
    private Set<Sfusers> sfuserSet = new HashSet<>();

    @ManyToOne(fetch=FetchType.LAZY) 
    @JoinColumn(name = "farm_id")
    private Farms farm;
}
