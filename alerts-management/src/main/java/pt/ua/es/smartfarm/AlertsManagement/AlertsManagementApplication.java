package pt.ua.es.smartfarm.AlertsManagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class AlertsManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlertsManagementApplication.class, args);
	}
}
