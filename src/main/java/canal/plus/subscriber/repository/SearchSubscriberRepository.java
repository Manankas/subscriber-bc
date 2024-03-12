package canal.plus.subscriber.repository;

import canal.plus.subscriber.model.Subscriber;

import java.util.List;

public interface SearchSubscriberRepository {
    List<Subscriber> findByCriteria(Subscriber subscriber);
}
