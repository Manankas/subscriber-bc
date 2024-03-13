package canal.plus.subscriber.controller;

import canal.plus.subscriber.model.Subscriber;
import canal.plus.subscriber.repository.SearchSubscriberRepository;
import canal.plus.subscriber.repository.SubscriberRepository;
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
    private final SearchSubscriberRepository searchRepository;

    public SubscriberController(SubscriberRepository subscriberRepository, SearchSubscriberRepository searchRepository) {
        this.subscriberRepository = subscriberRepository;
        this.searchRepository = searchRepository;
    }

    @PostMapping
    private ResponseEntity<String> createSubscriber(@RequestBody Subscriber subscriber) {
        Optional<Subscriber> result = subscriberRepository.findByMailOrPhoneAndIsActive(subscriber.getMail(), subscriber.getPhone(), true);
        if(result.isEmpty()) {
            Subscriber savedSubscriber = subscriberRepository.save(subscriber);
            URI locationOfNewSubscriber = UriComponentsBuilder.newInstance()
                    .path("subscribers/{id}")
                    .buildAndExpand(savedSubscriber.getId())
                    .toUri();
            return ResponseEntity.created(locationOfNewSubscriber).build();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Subscriber with these mail/phone already exists");

    }

   @GetMapping("/{id}")
    private ResponseEntity<Subscriber> findById(@PathVariable String id) {
        Optional<Subscriber> subscriber = subscriberRepository.findById(id);
       return subscriber.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    private ResponseEntity<Void> putSubscriber(@PathVariable String id, @RequestBody Subscriber subscriberToUpdate) {
        Optional<Subscriber> existingSubscriber = subscriberRepository.findByIdAndIsActiv(id, true);
        if (existingSubscriber.isPresent()) {
            Subscriber updatedSubscriber = new Subscriber(existingSubscriber.get().getId(),
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

    @PostMapping("/search")
    private ResponseEntity<List<Subscriber>> searchByCriteria(@RequestBody Subscriber subscriberToSearch) {
        List<Subscriber> results = searchRepository.findByCriteria(subscriberToSearch);
        return ResponseEntity.ok(results);
    }

}
