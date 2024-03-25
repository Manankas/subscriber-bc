package canal.plus.subscriber.exception;

public class SubscriberNotFoundException extends RuntimeException {
    private String message;

    public SubscriberNotFoundException(String message) {
        super(message);
    }
}
