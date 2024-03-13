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

import java.net.URI;

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
	void shouldReturnASubscriberWhenDataIsSavedAndSearchingById() {
		ResponseEntity<String> response = restTemplate.getForEntity("/subscribers/uuid1", String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		String id = documentContext.read("$.id");
		String firstname = documentContext.read("$.firstname");

		assertThat(id).isEqualTo("uuid1");
		assertThat(firstname).isEqualTo("toto");
	}

	@Test
	void shouldReturnASubscriberWhenFirstnameCriteriaMatch() {
		Subscriber subscriberToSearch = new Subscriber("toto", "", null, null, null);
		ResponseEntity<String> response = restTemplate.postForEntity("/subscribers/search", subscriberToSearch, String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		JSONArray jsonArray = documentContext.read("$[*]");
		assertThat(jsonArray.size()).isEqualTo(1);

		String id = documentContext.read("$[0].id");
		assertThat(id).isEqualTo("uuid1");
	}

	@Test
	void shouldReturnEmptyListWhenCriteriaDoNotMatch() {
		Subscriber subscriberToSearch = new Subscriber("toto", "wrongLastname", null, null, null);
		ResponseEntity<String> response = restTemplate.postForEntity("/subscribers/search", subscriberToSearch, String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		JSONArray jsonArray = documentContext.read("$[*]");
		assertThat(jsonArray.size()).isEqualTo(0);
	}

	@Test
	void shouldCreateANewSubscriberWithGeneratedID() {
		Subscriber newSubscriber = new Subscriber("foo", "bar", "foo@gmail.com", "12345");
		ResponseEntity<Void> response = restTemplate.postForEntity("/subscribers", newSubscriber, Void.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		//check that the subscriber is well saved and new ID is generated
		URI locationOfNewSubscriber = response.getHeaders().getLocation();
		ResponseEntity<String> getResponse = restTemplate.getForEntity("/"+locationOfNewSubscriber.getPath(), String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
		String id = documentContext.read("$.id");
		assertThat(id).isNotNull();
	}

	@Test
	void shouldFailedCreatingNewSubscriberWhenPersonalInformationIsNotComplete() {
		Subscriber newSubscriber = new Subscriber(null, "bar", "foo@gmail.com", "12345");
		ResponseEntity<Void> response = restTemplate.postForEntity("/subscribers", newSubscriber, Void.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Test
	void shouldFailedCreatingNewSubscriberWhenASubscriberWithSameMailOrPhoneAlreadyExists() {
		//create new subscriber
		Subscriber newSubscriber = new Subscriber("foo", "bar", "mail@gmail.com", "12345");
		ResponseEntity<Void> okResponse = restTemplate.postForEntity("/subscribers", newSubscriber, Void.class);

		assertThat(okResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		// create new subscriber with same mail
		newSubscriber = new Subscriber("abc", "def", "mail@gmail.com", "99999");
		ResponseEntity<String> koResponse1 = restTemplate.postForEntity("/subscribers", newSubscriber, String.class);
		assertThat(koResponse1.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
		assertThat(koResponse1.getBody()).isEqualTo("Subscriber with these mail/phone already exists");

		// create new subscriber with same phone
		newSubscriber = new Subscriber("xxx", "yyy", "new@gmail.com", "12345");
		ResponseEntity<String> koResponse2 = restTemplate.postForEntity("/subscribers", newSubscriber, String.class);
		assertThat(koResponse2.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
		assertThat(koResponse2.getBody()).isEqualTo("Subscriber with these mail/phone already exists");
	}
}
