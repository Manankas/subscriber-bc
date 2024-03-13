package canal.plus.subscriber.controller;

import canal.plus.subscriber.model.Subscriber;
import canal.plus.subscriber.repository.SearchSubscriberRepository;
import canal.plus.subscriber.repository.SubscriberRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    private ResponseEntity<Subscriber> createSubscriber(@RequestBody Subscriber subscriber) {
        Subscriber savedSubscriber = subscriberRepository.save(subscriber);
        URI locationOfNewSubscriber = UriComponentsBuilder.newInstance()
                .path("subscribers/{id}")
                .buildAndExpand(savedSubscriber.getId())
                .toUri();
        return ResponseEntity.created(locationOfNewSubscriber).build();

    }

   @GetMapping("/{id}")
    private ResponseEntity<Subscriber> findById(@PathVariable String id) {
        Optional<Subscriber> subscriber = subscriberRepository.findById(id);
        if (subscriber.isPresent()) {
            return ResponseEntity.ok(subscriber.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/search")
    private ResponseEntity<List<Subscriber>> searchByCriteria(@RequestBody Subscriber subscriberToSearch) {
        List<Subscriber> results = searchRepository.findByCriteria(subscriberToSearch);
        return ResponseEntity.ok(results);

    }

}
