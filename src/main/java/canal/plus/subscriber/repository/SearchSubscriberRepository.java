package canal.plus.subscriber.repository;

import canal.plus.subscriber.model.Subscriber;

import java.util.Optional;

public interface SearchSubscriberRepository {
    Optional<Subscriber> findByCriteria(String id, String firstname, String lastname, String phone, String mail, Boolean isActive);
}
