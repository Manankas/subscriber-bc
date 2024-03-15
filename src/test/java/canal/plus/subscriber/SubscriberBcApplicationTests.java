package canal.plus.subscriber;

import canal.plus.subscriber.dto.SubscriberPersonalInfo;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
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

	@ParameterizedTest
	@ValueSource(strings = {"id=uuid1",
							"firstname=toto",
							"firstname=toto&lastname=titi",
							"mail=toto@gmail.com&phone=123",
							"phone=123&isActive=true"})
	void shouldReturnASubscriberWhenCriteriaMatch(String criteria) {
		ResponseEntity<String> response = restTemplate.getForEntity("/subscribers/search?" + criteria, String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		String id = documentContext.read("$.id");
		String firstname = documentContext.read("$.firstname");

		assertThat(id).isEqualTo("uuid1");
		assertThat(firstname).isEqualTo("toto");
	}

	@Test
	void shouldReturnEmptyListWhenCriteriaDoNotMatch() {
		ResponseEntity<String> response = restTemplate.getForEntity("/subscribers/search?firstname=wrongLastname", String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void shouldCreateANewSubscriberWithGeneratedIDAndActiveByDefault() {
		SubscriberPersonalInfo newSubscriber = new SubscriberPersonalInfo("foo", "bar", "foo@gmail.com", "12345");
		ResponseEntity<Void> response = restTemplate.postForEntity("/subscribers", newSubscriber, Void.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		//check that the subscriber is well saved and new ID is generated
		URI locationOfNewSubscriber = response.getHeaders().getLocation();
		ResponseEntity<String> getResponse = restTemplate.getForEntity("/"+locationOfNewSubscriber.getPath(), String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		DocumentContext documentContext = JsonPath.parse(getResponse.getBody());

		String id = documentContext.read("$.id");
		boolean isActive = documentContext.read("$.isActiv");
		String firstname = documentContext.read("$.firstname");

		assertThat(id).isNotNull();
		assertThat(isActive).isEqualTo(true);
		assertThat(firstname).isEqualTo("foo");
	}

	@Test
	void shouldFailCreatingNewSubscriberWhenPersonalInformationIsNotComplete() {
		SubscriberPersonalInfo newSubscriber = new SubscriberPersonalInfo(null, "bar", "foo@gmail.com", "12345");
		ResponseEntity<Void> response = restTemplate.postForEntity("/subscribers", newSubscriber, Void.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void shouldFailCreatingNewSubscriberWhenASubscriberWithSameMailOrPhoneAlreadyExists() {
		//create new subscriber
		SubscriberPersonalInfo newSubscriber = new SubscriberPersonalInfo("foo", "bar", "mail@gmail.com", "12345");
		ResponseEntity<Void> okResponse = restTemplate.postForEntity("/subscribers", newSubscriber, Void.class);

		assertThat(okResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		// create new subscriber with same mail
		newSubscriber = new SubscriberPersonalInfo("abc", "def", "mail@gmail.com", "99999");
		ResponseEntity<String> koResponse1 = restTemplate.postForEntity("/subscribers", newSubscriber, String.class);
		assertThat(koResponse1.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
		assertThat(koResponse1.getBody()).isEqualTo("Subscriber with these mail or phone already exists");

		// create new subscriber with same phone
		newSubscriber = new SubscriberPersonalInfo("xxx", "yyy", "new@gmail.com", "12345");
		ResponseEntity<String> koResponse2 = restTemplate.postForEntity("/subscribers", newSubscriber, String.class);
		assertThat(koResponse2.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
		assertThat(koResponse2.getBody()).isEqualTo("Subscriber with these mail or phone already exists");
	}

	@Test
	void shouldUpdatePersonalInfoOfAnExistingSubscriber() {
		SubscriberPersonalInfo subscriberToUpdate = new SubscriberPersonalInfo("TOTO_2", "TITI_2",  "toto2@gmail.com", "888");
		HttpEntity<SubscriberPersonalInfo> request = new HttpEntity<>(subscriberToUpdate);
		ResponseEntity<Void> updateResponse = restTemplate
				.exchange("/subscribers/uuid1", HttpMethod.PUT, request, Void.class);
		assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		//checking updated data
		ResponseEntity<String> getResponse = restTemplate.getForEntity("/subscribers/uuid1", String.class);

		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
		String firstname = documentContext.read("$.firstname");


		assertThat(firstname).isEqualTo("TOTO_2");

		//checking id and isActive are not changed
		String id = documentContext.read("$.id");
		boolean isActive = documentContext.read("$.isActiv");
		assertThat(id).isEqualTo("uuid1");
		assertThat(isActive).isEqualTo(true);
	}

	@Test
	void shouldFailToUpdateWhenSubscriberDoNotExists() {
		SubscriberPersonalInfo subscriberToUpdate = new SubscriberPersonalInfo("TOTO_2", "TITI_2", "toto2@gmail.com", "+888");
		HttpEntity<SubscriberPersonalInfo> request = new HttpEntity<>(subscriberToUpdate);
		ResponseEntity<Void> updateResponse = restTemplate.exchange("/subscribers/wrong_id", HttpMethod.PUT, request, Void.class);
		assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@ParameterizedTest
	@NullSource
	@ValueSource(strings = {"", "  ","mailWithWrongFormat"})
	void shouldFailToUpdateSubscriberWhenMailIsInvalid(String invalidMail) {
		SubscriberPersonalInfo subscriberToUpdate = new SubscriberPersonalInfo("TOTO_2", "TITI_2", invalidMail, "888");
		HttpEntity<SubscriberPersonalInfo> request = new HttpEntity<>(subscriberToUpdate);
		ResponseEntity<Void> updateResponse = restTemplate.exchange("/subscribers/uuid1", HttpMethod.PUT, request, Void.class);
		assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@ParameterizedTest
	@NullSource
	@ValueSource(strings = {"", "  ","888x", "x888", "ABC"})
	void shouldFailToUpdateSubscriberWhenPhoneIsInvalid(String phone) {
		SubscriberPersonalInfo subscriberToUpdate = new SubscriberPersonalInfo("TOTO_2", "TITI_2", "toto2@gmail.com", phone);
		HttpEntity<SubscriberPersonalInfo> request = new HttpEntity<>(subscriberToUpdate);
		ResponseEntity<Void> updateResponse = restTemplate.exchange("/subscribers/uuid1", HttpMethod.PUT, request, Void.class);
		assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	void shouldFailToUpdateMailWhenItIsAlreadyUsedByAnotherActiveSubscriber() {
		final String USED_MAIL = "foo@gmail.com";

		//create new subscriber
		SubscriberPersonalInfo firstSubscriber = new SubscriberPersonalInfo("foo", "bar", USED_MAIL, "12345");
		restTemplate.postForEntity("/subscribers", firstSubscriber, Void.class);

		//create another subscriber
		SubscriberPersonalInfo secondSubscriber = new SubscriberPersonalInfo("foo2", "bar2", "foo2@gmail.com", "56789");
		ResponseEntity<Void> response = restTemplate.postForEntity("/subscribers", secondSubscriber, Void.class);
		URI locationOfNewSubscriber = response.getHeaders().getLocation();

		// update second subscriber
		SubscriberPersonalInfo secondSubscriberWithUsedMail = new SubscriberPersonalInfo("foo2", "bar2", USED_MAIL, "56789");
		HttpEntity<SubscriberPersonalInfo> request = new HttpEntity<>(secondSubscriberWithUsedMail);
		ResponseEntity<String> updateResponse = restTemplate.exchange("/"+locationOfNewSubscriber.getPath(), HttpMethod.PUT, request, String.class);
		assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
		assertThat(updateResponse.getBody()).isEqualTo("Subscriber with these mail or phone already exists");
	}

	@Test
	void shouldUnsubscribeExistingSubscriber() {
		ResponseEntity<Void> unsubscriptionResponse = restTemplate.exchange("/subscribers/uuid1", HttpMethod.DELETE, null, Void.class);
		assertThat(unsubscriptionResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		//checking updated data
		ResponseEntity<String> getResponse = restTemplate.getForEntity("/subscribers/uuid1", String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
		String id = documentContext.read("$.id");
		boolean isActive = documentContext.read("$.isActiv");
		assertThat(id).isEqualTo("uuid1");
		assertThat(isActive).isEqualTo(false);
	}

	@Test
	void shouldFailToUnsubscribeWhenSubscriberDoNotExists() {
		ResponseEntity<Void> unsubscriptionResponse = restTemplate.exchange("/subscribers/wrong_id", HttpMethod.DELETE, null, Void.class);
		assertThat(unsubscriptionResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void shouldFailToUnsubscribeWhenSubscriberIsAlreadyUnsubscribed() {
		ResponseEntity<Void> unsubscriptionResponse = restTemplate.exchange("/subscribers/uuid1", HttpMethod.DELETE, null, Void.class);
		assertThat(unsubscriptionResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		ResponseEntity<Void> secondUnsubscriptionResponse = restTemplate.exchange("/subscribers/uuid1", HttpMethod.DELETE, null, Void.class);
		assertThat(secondUnsubscriptionResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}
}
