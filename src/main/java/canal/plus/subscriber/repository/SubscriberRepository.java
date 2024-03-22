package canal.plus.subscriber.repository;

import canal.plus.subscriber.model.Subscriber;
import canal.plus.subscriber.utils.PredicatesBuilder;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, String>, JpaSpecificationExecutor {

    @Query("select s from Subscriber s where s.id != ?1 and (s.mail = ?2 or s.phone = ?3) and s.isActiv = true")
    Optional<Subscriber> findOtherActiveSubscriberByMailOrPhone(String id, String mail, String phone);

    @Query("select s from Subscriber s where (s.mail = ?1 or s.phone = ?2) and s.isActiv = ?3")
    Optional<Subscriber> findByMailOrPhoneAndIsActive(String mail, String phone, Boolean isActive);

    Optional<Subscriber> findByIdAndIsActiv(String id, Boolean isActive);

    default List<Subscriber> findByCriteria(String id, String firstname, String lastname, String phone, String mail, Boolean isActive, Pageable pageable) {

        return findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = PredicatesBuilder.build(id, firstname, lastname, phone, mail, isActive, root, criteriaBuilder);
            return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
        }, pageable).getContent();

    }
}
