package canal.plus.subscriber;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class SubscriberBcApplication {

	public static void main(String[] args) {
		SpringApplication.run(SubscriberBcApplication.class, args);
	}

}
