package pt.ua.es.smartfarm.AlertsManagement;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;
import java.util.ArrayList;
import javax.persistence.ElementCollection;

@Data
@Entity
@Table(name="Alerts")
public class Alerts implements Serializable {
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    
    //@NotBlank
    //private String sensorId;
    
    @NotBlank
    private String alertType;
    
    @NotBlank
    private String threshold;
    
    @ElementCollection
    private List<String> sensors = new ArrayList<String>();
}

