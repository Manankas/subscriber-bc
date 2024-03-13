package canal.plus.subscriber.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

/*
* Note :
* We don't use Java 17 record because records can’t be entities
* since records are immutable and don’t have no-args constructor and setters.
* We can use LOMBOK
* */

@Entity
public class Subscriber implements Serializable {
     @Id
     @Column(name="SUBSCRIBERID")
     @GeneratedValue(strategy = GenerationType.UUID)
     private String id;

     @NotNull
     @Column(name="FNAME")
     private String firstname;
     @NotNull
     @Column(name="LNAME")
     private String lastname;

     @NotNull
     @Email
     @Column(name="MAIL")
     private String mail;

     @NotNull

     @Column(name="PHONE")
     private String phone;

     @Column(name="ISACTIV")
     private Boolean isActiv;


     /*
     * Activate subsciber on creation
     * */
     @PrePersist
     void preInsert() {
          if (this.isActiv == null)
               this.isActiv = true;
     }

     public Subscriber(String id, String firstname, String lastname, String mail, String phone, Boolean isActiv) {
          this.id = id;
          this.firstname = firstname;
          this.lastname = lastname;
          this.mail = mail;
          this.phone = phone;
          this.isActiv = isActiv;
     }

     public Subscriber(String firstname, String lastname, String mail, String phone, Boolean isActiv) {
          this.firstname = firstname;
          this.lastname = lastname;
          this.mail = mail;
          this.phone = phone;
          this.isActiv = isActiv;
     }

     public Subscriber(String firstname, String lastname, String mail, String phone) {
          this.firstname = firstname;
          this.lastname = lastname;
          this.mail = mail;
          this.phone = phone;
     }

     public Subscriber() {
     }

     public String getId() {
          return id;
     }

     public void setId(String id) {
          this.id = id;
     }

     public String getFirstname() {
          return firstname;
     }

     public void setFirstname(String firstname) {
          this.firstname = firstname;
     }

     public String getLastname() {
          return lastname;
     }

     public void setLastname(String lastname) {
          this.lastname = lastname;
     }

     public String getMail() {
          return mail;
     }

     public void setMail(String mail) {
          this.mail = mail;
     }

     public String getPhone() {
          return phone;
     }

     public void setPhone(String phone) {
          this.phone = phone;
     }

     public Boolean isIsActiv() {
          return isActiv;
     }

     public void setIsActiv(Boolean isActiv) {
          this.isActiv = isActiv;
     }
}
