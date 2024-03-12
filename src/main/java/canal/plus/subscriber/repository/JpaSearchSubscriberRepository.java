package canal.plus.subscriber.repository;

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
    public List<Subscriber> findByCriteria(Subscriber subscriber) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Subscriber> query = criteriaBuilder.createQuery(Subscriber.class);

        Root<Subscriber> sub = query.from(Subscriber.class);
        List<Predicate> predicates = new ArrayList<>();

        if (subscriber.getId() != null) {
            predicates.add(criteriaBuilder.equal(sub.get("id"), subscriber.getId()));
        }
        if (subscriber.getFirstname() != null) {
            predicates.add(criteriaBuilder.like(sub.get("firstname"), "%" + subscriber.getFirstname() + "%"));
        }
        if (subscriber.getLastname() != null) {
            predicates.add(criteriaBuilder.like(sub.get("lastname"), "%" + subscriber.getLastname() + "%"));
        }
        if (subscriber.getPhone() != null && !subscriber.getPhone().isBlank()) {
            predicates.add(criteriaBuilder.equal(sub.get("phone"), subscriber.getPhone()));
        }
        if (subscriber.getMail() != null && !subscriber.getMail().isBlank()) {
            predicates.add(criteriaBuilder.equal(sub.get("mail"), subscriber.getMail()));
        }
        if (subscriber.isIsActiv() != null) {
            predicates.add(criteriaBuilder.equal(sub.get("isActiv"), subscriber.isIsActiv()));
        }

        query.where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(query).getResultList();
    }

}
