# SUBSCRIBER-BC

### Description:

Subscriber-bc est une petite application sous forme d'API pour gérer les abonnés de canal+.  
Elle expose les opérations de CRUD des abonnés.

### Pré-requis : 
Java 17 et Maven ( Il existe `mvnw` , un script shell Unix exécutable utilisé à la place d'un Maven entièrement installé)

### Architecture :
Subscriber-BC est un microservice spring boot.  
Pour des raisons de simplicité, il sera divisé en deux couches :  
- application (controller + logique metier)  
- accès aux données (model, repository)  

L'application se base sur Java 17, spring-boot 3 et maven avec un packaging **.jar**. 

### Dépendances :
1. [ ] H2 database pour stocker ses données en mémoire et les données dans `data.sql` sont insérées au démarrage de l'application.  
2. [ ] Jpa hibernate sera utilisé comme ORM.  
3. [ ] Spring validation sera exploité pour vérifier et valider les données entrées par l'utilisateur.  
4. [ ] Lombok pour simplifier l'écriture des classes
5. [ ] Swagger sera utilisé pour la documentation.  

### Lancement :
* Se mettre à la racine du projet
* Faire le packaging via la commande : `./mvnw clean package`  ou  `mvn clean package` (si maven est installé) 
* Run l'application via : `java -jar  ./target/subscriber-bc 0.0.1-SNAPSHOT.jar`

> [!TIP]
> La documentation des APIs est disponible via Swagger sur l'URL : http://localhost:8080/swagger-ui  
> **_Bonus_** : Une collection postman `Subscriber-bc-api.postman_collection.json`  est aussi disponible sur la racine du projet pour pouvoir tester.

> [!IMPORTANT]
> ### Fonctionnalités et règles :
> #### ➢ Un endpoint permet de créer un nouveau abonné  :dart:
> 1. Toutes les données personnelles doivent être fournies
> 2. L'identifiant de l'abonné est généré automatiquement
> 3. Un nouveau abonné est actif par défaut
> 4. On ne peut pas créer un abonné inactif (résilié)
> 5. Si un abonné **actif** avec le même email ou téléphone existe déjà, la création échoue et le retour du service doit permettre de le savoir
> #### ➢ Un endpoint permet de récupérer un ou des abonnés  :dart:
> 1.  N’importe quel critère de recherche peut être utilisé  
> 2.  Les critères de recherche sont envoyés dans l'url  
  Exemple : http://localhost:8080/subscribers/search?firstname=toto&lastname=titi 
> 3.  Si aucun critère n'est passé, tous les abonnés sont retournés selon la pagination voulue  
  Exemple : http://localhost:8080/subscribers?page=0&size=2&sort=firstname,desc
> #### ➢ Un endpoint permet de « résilier » un abonné :dart:
> 1. L’abonné doit être désactivé mais pas supprimé, il s'agit ici d'un "soft delete" 
> 2. Pas de résiliation pour un abonné qui est déjà résilié
> #### ➢ Un endpoint permet de mettre à jour les données personnelles d’un abonné :dart:
> 1. Seules les données personnelles peuvent être modifiées
> 2. Les données fournies sont soumises à des validations (Ex : le mail doit être valide)
> 3. On ne peut pas mettre à jour un abonné en lui attribuant les mêmes mail ou phone d'un autre abonnés existant actif

> #### ➢ Un endpoint permet de récupérer via son ID pour ne pas passer par la recherche avec critère (BONUS) :thumbsup:

### Piste d'amélioration :
- Soustraire les logiques métier du controller pour les mettre dans une couche service dédiée
- Logging  
- fichier unique contenant tous les messages  
- Mettre en place spring security pour sécuriser l'application
