package canal.plus.subscriber.controller;

import canal.plus.subscriber.dto.SubscriberPersonalInfo;
import canal.plus.subscriber.exception.NotValidSearchCriteriaException;
import canal.plus.subscriber.model.Subscriber;
import canal.plus.subscriber.repository.SearchSubscriberRepository;
import canal.plus.subscriber.repository.SubscriberRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
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
       return subscriber.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
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

    @Operation(summary = "Search subscriber by some criteria from request parameter, at least one criteria parameter is required")
    @GetMapping("/search")
    private ResponseEntity<Subscriber> searchByCriteria(@RequestParam(required = false) String id,
                                                        @RequestParam(required = false) String firstname,
                                                        @RequestParam(required = false) String lastname,
                                                        @RequestParam(required = false) String phone,
                                                        @RequestParam(required = false) String mail,
                                                        @RequestParam(required = false) Boolean isActive) {
        validateCriteria(id, firstname, lastname, phone, mail, isActive);
        Optional<Subscriber> existingSubscriber = searchRepository.findByCriteria(id, firstname, lastname, phone, mail, isActive);
        return existingSubscriber.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get all existing subscribers. It can be paginated by sending page, size and sort in request parameter")
    @GetMapping
    private ResponseEntity<List<Subscriber>> findAll(Pageable pageable) {
        Page<Subscriber> results = subscriberRepository.findAll(PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSortOr(Sort.by(Sort.Direction.ASC, "firstname"))
        ));
        return ResponseEntity.ok(results.getContent());
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

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NotValidSearchCriteriaException.class)
    public Map<String, String> handleCriteriaValidationExceptions(NotValidSearchCriteriaException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("Error", ex.getMessage());
        errors.put("Criteria parameter", "id, firstname, lastname, phone, mail, isActive ");
        return errors;
    }

    private void validateCriteria(String id, String firstname, String lastname, String phone, String mail, Boolean isActive) {
        if(id == null && firstname == null && lastname == null && phone == null && mail == null && isActive == null){
            throw new NotValidSearchCriteriaException("No criteria is defined, at least one criteria parameter is required");
        }
    }

}
