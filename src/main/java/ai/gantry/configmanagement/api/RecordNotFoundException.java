package ai.gantry.configmanagement.api;

public class RecordNotFoundException extends Exception {
    public RecordNotFoundException() {
    }

    public RecordNotFoundException(String message) {
        super(message);
    }
}
