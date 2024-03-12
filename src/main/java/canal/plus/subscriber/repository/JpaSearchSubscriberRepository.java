package canal.plus.subscriber.repository;

import canal.plus.subscriber.dto.SubscriberDto;
import canal.plus.subscriber.model.Subscriber;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class JpaSearchSubscriberRepository implements SearchSubscriberRepository {

    private final EntityManager entityManager;

    public JpaSearchSubscriberRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Subscriber> findByCriteria(SubscriberDto subscriber) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Subscriber> query = criteriaBuilder.createQuery(Subscriber.class);

        Root<Subscriber> sub = query.from(Subscriber.class);
        List<Predicate> predicates = new ArrayList<>();

        if (subscriber.id() != null) {
            predicates.add(criteriaBuilder.equal(sub.get("id"), subscriber.id()));
        }
        if (subscriber.firstname() != null) {
            predicates.add(criteriaBuilder.like(sub.get("firstname"), "%" + subscriber.firstname() + "%"));
        }
        if (subscriber.lastname() != null) {
            predicates.add(criteriaBuilder.like(sub.get("lastname"), "%" + subscriber.lastname() + "%"));
        }
        if (subscriber.phone() != null && !subscriber.phone().isBlank()) {
            predicates.add(criteriaBuilder.equal(sub.get("phone"), subscriber.phone()));
        }
        if (subscriber.mail() != null && subscriber.mail().isBlank()) {
            predicates.add(criteriaBuilder.equal(sub.get("mail"), subscriber.mail()));
        }
        if (subscriber.isActiv() != null) {
            predicates.add(criteriaBuilder.equal(sub.get("isActiv"), subscriber.isActiv()));
        }

        query.where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(query).getResultList();
    }

}
