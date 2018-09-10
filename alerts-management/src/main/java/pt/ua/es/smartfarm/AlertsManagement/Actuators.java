
package pt.ua.es.smartfarm.AlertsManagement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
@Table(name="Actuators")
public class Actuators implements Serializable  {
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    
    @NotBlank
    private String alertId;
    
    @NotBlank
    private String valueToBeSent;
    
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> sensors = new ArrayList<String>();
    
}
