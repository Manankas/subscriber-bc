package canal.plus.subscriber.utils;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;

public class PredicatesBuilder {
    public static List<Predicate> build(String id, String firstname, String lastname, String phone, String mail, Boolean isActive, Root root, CriteriaBuilder criteriaBuilder) {
        final List<Predicate> predicates = new ArrayList<>();

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
        return predicates;
    }
}
