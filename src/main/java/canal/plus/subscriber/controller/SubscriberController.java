package canal.plus.subscriber.controller;

import canal.plus.subscriber.entity.Subscriber;
import canal.plus.subscriber.repository.SubscriberRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/subscribers")
public class SubscriberController {
    private final SubscriberRepository subscriberRepository;

    public SubscriberController(SubscriberRepository subscriberRepository) {
        this.subscriberRepository = subscriberRepository;
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

}
