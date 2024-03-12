package canal.plus.subscriber.repository;

import canal.plus.subscriber.entity.Subscriber;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SearchSubscriberRepository {

    private EntityManager em;

    public SearchSubscriberRepository(EntityManager em) {
        this.em = em;
    }

    /*  List<Subscriber> findBy(String authorName, String title) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Book> cq = cb.createQuery(Book.class);

        Root<Book> book = cq.from(Book.class);
        List<Predicate> predicates = new ArrayList<>();

        if (authorName != null) {
            predicates.add(cb.equal(book.get("author"), authorName));
        }
        if (title != null) {
            predicates.add(cb.like(book.get("title"), "%" + title + "%"));
        }
        cq.where(predicates.toArray(new Predicate[0]));

        return em.createQuery(cq).getResultList();
    }

   */
}
