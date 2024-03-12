package canal.plus.subscriber.controller;

import canal.plus.subscriber.dto.SubscriberDto;
import canal.plus.subscriber.model.Subscriber;
import canal.plus.subscriber.repository.SearchSubscriberRepository;
import canal.plus.subscriber.repository.SubscriberRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

   @GetMapping("/{id}")
    private ResponseEntity<Subscriber> findById(@PathVariable Long id) {
        Optional<Subscriber> subscriber = subscriberRepository.findById(id);
        if (subscriber.isPresent()) {
            return ResponseEntity.ok(subscriber.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/search")
    private ResponseEntity<List<Subscriber>> searchByCriteria(@RequestBody SubscriberDto subscriberToSearch) {
        List<Subscriber> results = searchRepository.findByCriteria(subscriberToSearch);
        return ResponseEntity.ok(results);

    }

}
