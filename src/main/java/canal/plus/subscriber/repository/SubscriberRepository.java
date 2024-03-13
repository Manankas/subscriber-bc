package canal.plus.subscriber.repository;

import canal.plus.subscriber.model.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, String> {
}
