package tn.monetique.cardmanagment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CardManagmentApplication {

    public static void main(String[] args) {
        SpringApplication.run(CardManagmentApplication.class, args);
    }

}
