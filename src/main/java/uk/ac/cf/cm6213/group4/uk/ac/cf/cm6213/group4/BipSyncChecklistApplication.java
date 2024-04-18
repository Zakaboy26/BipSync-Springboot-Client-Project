package uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BipSyncChecklistApplication {

	public static void main(String[] args) {
		SpringApplication.run(BipSyncChecklistApplication.class, args);
	}

}
