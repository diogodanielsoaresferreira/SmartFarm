package pt.ua.es.smartfarm.ServiceLayer;

import lombok.Data;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import org.hibernate.annotations.CreationTimestamp;

/**
 * Sensor entity.
 */
@Entity
@Data
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Sensors implements Serializable {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String model;

    @NotBlank
    private String type;
    
    private String status;

    @NotNull
    private Boolean actuator;

    @NotNull
    private Double longitude;

    @NotNull
    private Double latitude;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;

    //@ManyToOne(fetch=FetchType.LAZY) 
    //@JoinColumn(name = "farm_id")
    //private Farms farm;
    @NotNull
    private Long farm;
}
