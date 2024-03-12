package canal.plus.subscriber.dto;

public record SubscriberDto(Long id,

                            String firstname, String lastname,

                            String mail,

                            String phone,

                            boolean activ) {
}
