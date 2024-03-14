package canal.plus.subscriber.controller;

import canal.plus.subscriber.model.Subscriber;
import canal.plus.subscriber.repository.SearchSubscriberRepository;
import canal.plus.subscriber.repository.SubscriberRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/subscribers")
public class SubscriberController {
    private final SubscriberRepository subscriberRepository;
    private final SearchSubscriberRepository searchRepository;

    public SubscriberController(SubscriberRepository subscriberRepository, SearchSubscriberRepository searchRepository) {
        this.subscriberRepository = subscriberRepository;
        this.searchRepository = searchRepository;
    }

    @PostMapping
    private ResponseEntity<String> createSubscriber(@Valid @RequestBody Subscriber subscriber) {
        Optional<Subscriber> result = subscriberRepository.findByMailOrPhoneAndIsActive(subscriber.getMail(), subscriber.getPhone(), true);
        if(result.isEmpty()) {
            Subscriber savedSubscriber = subscriberRepository.save(subscriber);
            URI locationOfNewSubscriber = UriComponentsBuilder.newInstance()
                    .path("subscribers/{id}")
                    .buildAndExpand(savedSubscriber.getId())
                    .toUri();
            return ResponseEntity.created(locationOfNewSubscriber).build();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Subscriber with these mail or phone already exists");

    }

   @GetMapping("/{id}")
    private ResponseEntity<Subscriber> findById(@PathVariable String id) {
        Optional<Subscriber> subscriber = subscriberRepository.findById(id);
       return subscriber.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    private ResponseEntity<String> updateSubscriber(@PathVariable String id, @Valid @RequestBody Subscriber subscriberToUpdate) {
        Optional<Subscriber> existingSubscriber = subscriberRepository.findByIdAndIsActiv(id, true);
        if (existingSubscriber.isPresent()) {

            //check if new value of mail or phone is already assigned to another subscriber
            Optional<Subscriber> result = subscriberRepository.findByMailOrPhoneAndIsActive(subscriberToUpdate.getMail(), subscriberToUpdate.getPhone(), true);
            if(result.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Subscriber with these mail or phone already exists");
            }

            final Subscriber updatedSubscriber = new Subscriber(existingSubscriber.get().getId(),
                    subscriberToUpdate.getFirstname(),
                    subscriberToUpdate.getLastname(),
                    subscriberToUpdate.getMail(),
                    subscriberToUpdate.getPhone(),
                    existingSubscriber.get().isIsActiv());
            subscriberRepository.save(updatedSubscriber);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/unsubscribe/{id}")
    private ResponseEntity<Void> unsubscribeSubscriber(@PathVariable String id) {
        Optional<Subscriber> existingSubscriber = subscriberRepository.findByIdAndIsActiv(id, true);
        if (existingSubscriber.isPresent()) {
            final Subscriber unsubscribed = existingSubscriber.get();
            unsubscribed.setIsActiv(false);
            subscriberRepository.save(unsubscribed);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    private ResponseEntity<Subscriber> searchByCriteria(@RequestParam(required = false) String id,
                                                        @RequestParam(required = false) String firstname,
                                                        @RequestParam(required = false) String lastname,
                                                        @RequestParam(required = false) String phone,
                                                        @RequestParam(required = false) String mail,
                                                        @RequestParam(required = false) Boolean isActive) {
        Optional<Subscriber> existingSubscriber = searchRepository.findByCriteria(id, firstname, lastname, phone, mail, isActive);
        return existingSubscriber.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

}
