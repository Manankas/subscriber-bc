package canal.plus.subscriber.repository;

import canal.plus.subscriber.model.Subscriber;
import canal.plus.subscriber.utils.PredicatesBuilder;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JpaSearchSubscriberRepository implements SearchSubscriberRepository {

    private final SubscriberRepository subscriberRepository;

    public JpaSearchSubscriberRepository(SubscriberRepository subscriberRepository) {
        this.subscriberRepository = subscriberRepository;
    }

    @Override
    public List<Subscriber> findByCriteria(String id, String firstname, String lastname, String phone, String mail, Boolean isActive, Pageable pageable) {

       return subscriberRepository.findAll((root, query, criteriaBuilder) -> {
                           List<Predicate> predicates = PredicatesBuilder.build(id, firstname, lastname, phone, mail, isActive, root, criteriaBuilder);
                           return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
       }, pageable).getContent();

    }

}
