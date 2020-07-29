package ai.gantry.configmanagement.api;

public class ZoneAlreadyExistException extends Exception {
    public ZoneAlreadyExistException(String message) {
        super(message);
    }
}
