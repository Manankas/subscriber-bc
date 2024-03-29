package canal.plus.subscriber.controller;

import canal.plus.subscriber.dto.SubscriberPersonalInfo;
import canal.plus.subscriber.exception.SubscriberNotFoundException;
import canal.plus.subscriber.model.Subscriber;
import canal.plus.subscriber.repository.SubscriberRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/subscribers")
public class SubscriberController {
    private final SubscriberRepository subscriberRepository;

    public SubscriberController(SubscriberRepository subscriberRepository) {
        this.subscriberRepository = subscriberRepository;
    }

    @Operation(summary = "Create new subscriber with his personal information. ID is automatically generated and new subscriber is active by default")
    @PostMapping
    private ResponseEntity<String> createSubscriber(@Valid @RequestBody SubscriberPersonalInfo subscriber) {
        Optional<Subscriber> result = subscriberRepository.findByMailOrPhoneAndIsActive(subscriber.mail(), subscriber.phone(), true);
        if(result.isEmpty()) {
            final Subscriber newSubscriber = new Subscriber(null,
                                                                subscriber.firstname(),
                                                                subscriber.lastname(),
                                                                subscriber.mail(),
                                                                subscriber.phone(),
                                                                true );
            Subscriber savedSubscriber = subscriberRepository.save(newSubscriber);
            URI locationOfNewSubscriber = UriComponentsBuilder.newInstance()
                    .path("subscribers/{id}")
                    .buildAndExpand(savedSubscriber.getId())
                    .toUri();
            return ResponseEntity.created(locationOfNewSubscriber).build();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Subscriber with these mail or phone already exists");

    }
   @Operation(summary = "Get a subscriber by his id")
   @GetMapping("/{id}")
    private ResponseEntity<Subscriber> findById(@PathVariable String id) {
        Optional<Subscriber> subscriber = subscriberRepository.findById(id);
       return subscriber.map(ResponseEntity::ok).orElseThrow(() -> new SubscriberNotFoundException("This subscriber does not exist or is not active"));
    }

    @Operation(summary = "Update subscriber's personal info.")
    @PutMapping("/{id}")
    private ResponseEntity<String> updateSubscriber(@PathVariable String id, @Valid @RequestBody SubscriberPersonalInfo subscriberToUpdate) {
        Optional<Subscriber> existingSubscriber = subscriberRepository.findByIdAndIsActiv(id, true);
        if (existingSubscriber.isPresent()) {

            //check if new value of mail or phone is already assigned to another subscriber
            Optional<Subscriber> result = subscriberRepository.findOtherActiveSubscriberByMailOrPhone(id, subscriberToUpdate.mail(), subscriberToUpdate.phone());
            if(result.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Subscriber with these mail or phone already exists");
            }

            final Subscriber updatedSubscriber = new Subscriber(existingSubscriber.get().getId(),
                    subscriberToUpdate.firstname(),
                    subscriberToUpdate.lastname(),
                    subscriberToUpdate.mail(),
                    subscriberToUpdate.phone(),
                    existingSubscriber.get().getIsActiv());
            subscriberRepository.save(updatedSubscriber);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("This subscriber does not exist or is not active");
    }

    @Operation(summary = "Unsubscribe subscriber. This is a soft delete")
    @DeleteMapping("/{id}")
    private ResponseEntity<String> unsubscribeSubscriber(@PathVariable String id) {
        Optional<Subscriber> existingSubscriber = subscriberRepository.findByIdAndIsActiv(id, true);
        if (existingSubscriber.isPresent()) {
            final Subscriber unsubscribed = existingSubscriber.get();
            unsubscribed.setIsActiv(false);
            subscriberRepository.save(unsubscribed);
            return ResponseEntity.status(HttpStatus.OK).body("Unsubscription successful");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("This subscriber does not exist or is not active");
    }

    @Operation(summary = """
                        Search for a subscriber based on certain criteria in the request parameter.
                        If no parameters are given, it returns all subscribers.
                        It can be paginated by sending page, size and sort in the request parameter
                        """)
    @GetMapping
    private ResponseEntity<List<Subscriber>> searchByCriteria(@RequestParam(required = false) String id,
                                                              @RequestParam(required = false) String firstname,
                                                              @RequestParam(required = false) String lastname,
                                                              @RequestParam(required = false) String phone,
                                                              @RequestParam(required = false) String mail,
                                                              @RequestParam(required = false) Boolean isActive,
                                                              Pageable pageable) {

        List<Subscriber> results = subscriberRepository.findByCriteria(id, firstname, lastname, phone, mail, isActive, pageable);
        return ResponseEntity.ok(results);
    }
}
