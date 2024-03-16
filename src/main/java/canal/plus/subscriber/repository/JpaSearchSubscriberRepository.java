package canal.plus.subscriber.repository;

import canal.plus.subscriber.model.Subscriber;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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
           List<Predicate> predicates = new ArrayList<>();

           if (id != null) {
               predicates.add(criteriaBuilder.equal(root.get("id"), id));
           }
           if (firstname != null && !firstname.isBlank()) {
               predicates.add(criteriaBuilder.equal(root.get("firstname"), firstname));
           }
           if (lastname != null && !lastname.isBlank()) {
               predicates.add(criteriaBuilder.equal(root.get("lastname"), lastname));
           }
           if (phone != null && !phone.isBlank()) {
               predicates.add(criteriaBuilder.equal(root.get("phone"), phone));
           }
           if (mail != null && !mail.isBlank()) {
               predicates.add(criteriaBuilder.equal(root.get("mail"), mail));
           }
           if (isActive != null) {
               predicates.add(criteriaBuilder.equal(root.get("isActiv"), isActive));
           }
           return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
       },
       pageable)
       .getContent();

    }

}
