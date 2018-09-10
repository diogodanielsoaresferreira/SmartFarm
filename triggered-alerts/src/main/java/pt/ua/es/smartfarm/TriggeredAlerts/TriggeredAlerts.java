
package pt.ua.es.smartfarm.TriggeredAlerts;


import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Data
@Table(name="TriggeredAlerts")
public class TriggeredAlerts implements Serializable {
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    
    @NotNull
    private Long alertId;
    
    @NotBlank
    private String valueSensor;
    
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;
    
}
