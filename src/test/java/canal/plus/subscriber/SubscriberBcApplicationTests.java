package canal.plus.subscriber;

import canal.plus.subscriber.model.Subscriber;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)  //start our Spring Boot application
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // For each test case, force Spring to start with a clean slate
class SubscriberBcApplicationTests {

	@Autowired
	TestRestTemplate restTemplate;  //allow us to make HTTP requests

	@Test
	void contextLoads() {
	}

	@Test
	void shouldReturnNotFoundResponseWhenDataIsNotSaved() {
		ResponseEntity<String> response = restTemplate.getForEntity("/subscribers/111", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void shouldReturnASubscriberWhenDataIsSaved() {
		ResponseEntity<String> response = restTemplate.getForEntity("/subscribers/1", String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		Number id = documentContext.read("$.id");
		String firstname = documentContext.read("$.firstname");

		assertThat(id).isEqualTo(1);
		assertThat(firstname).isEqualTo("toto");
	}

	@Test
	void shouldReturnASubscriberWhenFirstnameCriteriaMatch() {
		Subscriber subscriberToSearch = new Subscriber(null, "toto", "", null, null, null);
		ResponseEntity<String> response = restTemplate.postForEntity("/subscribers/search", subscriberToSearch, String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		JSONArray jsonArray = documentContext.read("$[*]");
		assertThat(jsonArray.size()).isEqualTo(1);

		int id = documentContext.read("$[0].id");
		assertThat(id).isEqualTo(1);
	}

	@Test
	void shouldReturnEmptyListWhenCriteriaDoNotMatch() {
		Subscriber subscriberToSearch = new Subscriber(null, "toto", "wrongLastname", null, null, null);
		ResponseEntity<String> response = restTemplate.postForEntity("/subscribers/search", subscriberToSearch, String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		JSONArray jsonArray = documentContext.read("$[*]");
		assertThat(jsonArray.size()).isEqualTo(0);
	}
}
