package canal.plus.subscriber.repository;

import canal.plus.subscriber.model.Subscriber;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SearchSubscriberRepository {
    List<Subscriber> findByCriteria(String id, String firstname, String lastname, String phone, String mail, Boolean isActive, Pageable pageable);
}
