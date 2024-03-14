package canal.plus.subscriber.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

/*
* Note :
* We don't use Java 17 record because records can’t be entities
* since records are immutable and don’t have no-args constructor and setters.
* We can use LOMBOK
* */

@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class Subscriber implements Serializable {
     @Id
     @Column(name="SUBSCRIBERID")
     @GeneratedValue(strategy = GenerationType.UUID)
     private String id;

     @Column(name="FNAME")
     private String firstname;

     @Column(name="LNAME")
     private String lastname;

     @Column(name="MAIL")
     private String mail;

     @Column(name="PHONE")
     private String phone;

     @Column(name="ISACTIV")
     private Boolean isActiv;

}
