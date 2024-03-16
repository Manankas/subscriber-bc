package canal.plus.subscriber.utils;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class PredicatesBuilderTest {

    private Root root;
    private CriteriaBuilder criteriaBuilder;

    @BeforeEach
    public void setUp() {
        root = mock(Root.class);
        criteriaBuilder = mock(CriteriaBuilder.class);
    }

    @Test
    public void shouldBuildPredicateWithAllParameters() {
        String id = "1";
        String firstname = "John";
        String lastname = "Doe";
        String phone = "123456789";
        String mail = "john.doe@example.com";
        Boolean isActive = true;

        List<Predicate> predicates = PredicatesBuilder.build(id, firstname, lastname, phone, mail, isActive, root, criteriaBuilder);

        assertEquals(6, predicates.size());
    }

    @Test
    public void shouldBuildEmptyPredicateWhenNoParameterIsGiven() {
        List<Predicate> predicates = PredicatesBuilder.build(null, null, null, null, null, null, root, criteriaBuilder);
        assertEquals(0, predicates.size());
    }

    @Test
    public void shouldBuildPredicateWithOnlyGivenParameters() {
        String id = "1";
        String firstname = "John";

        List<Predicate> predicates = PredicatesBuilder.build(id, firstname, null, "", "", null, root, criteriaBuilder);

        assertEquals(2, predicates.size());
    }

}