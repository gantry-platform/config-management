package ai.gantry.configmanagement.api;

public class RecordAlreadyExistException extends Exception {
    public RecordAlreadyExistException() {
    }

    public RecordAlreadyExistException(String message) {
        super(message);
    }
}
