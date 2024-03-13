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
import java.util.Optional;

@Repository
public class JpaSearchSubscriberRepository implements SearchSubscriberRepository {

    private final EntityManager entityManager;

    public JpaSearchSubscriberRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<Subscriber> findByCriteria(String id, String firstname, String lastname, String phone, String mail, Boolean isActive) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Subscriber> query = criteriaBuilder.createQuery(Subscriber.class);

        Root<Subscriber> sub = query.from(Subscriber.class);
        List<Predicate> predicates = new ArrayList<>();

        if (id != null) {
            predicates.add(criteriaBuilder.equal(sub.get("id"), id));
        }
        if (firstname != null && !firstname.isBlank()) {
            predicates.add(criteriaBuilder.equal(sub.get("firstname"), firstname));
        }
        if (lastname != null && !lastname.isBlank()) {
            predicates.add(criteriaBuilder.equal(sub.get("lastname"), lastname));
        }
        if (phone != null && !phone.isBlank()) {
            predicates.add(criteriaBuilder.equal(sub.get("phone"), phone));
        }
        if (mail != null && !mail.isBlank()) {
            predicates.add(criteriaBuilder.equal(sub.get("mail"), mail));
        }
        if (isActive != null) {
            predicates.add(criteriaBuilder.equal(sub.get("isActiv"), isActive));
        }

        query.where(predicates.toArray(new Predicate[0]));

        List<Subscriber> results = entityManager.createQuery(query).getResultList();
        return Optional.ofNullable(results.isEmpty() ? null : results.get(0));
    }

}
