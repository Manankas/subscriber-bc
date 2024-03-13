package canal.plus.subscriber.repository;

import canal.plus.subscriber.model.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, String> {

    @Query("select s from Subscriber s where (s.mail = ?1 or s.phone = ?2) and s.isActiv = ?3")
    Optional<Subscriber> findByMailOrPhoneAndIsActive(String mail, String phone, Boolean isActive);

    Optional<Subscriber> findByIdAndIsActiv(String id, Boolean isActive);
}
