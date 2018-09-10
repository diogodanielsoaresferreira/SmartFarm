package pt.ua.es.smartfarm.ServiceLayer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;

/**
 * Entity that represents a farm.
 */
@Entity
@Data
public class Farms implements Serializable {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String address;

    @NotNull
    private Double area;

    @NotNull
    private Double longitude;

    @NotNull
    private Double latitude;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;

    //@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    //private List<Sensors> sensorsList = new ArrayList<>();

    //@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    //private List<PermissionGroups> groupsList = new ArrayList<>();

}
