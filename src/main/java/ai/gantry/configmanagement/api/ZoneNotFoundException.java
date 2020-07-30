package ai.gantry.configmanagement.api;

public class ZoneNotFoundException extends Exception {
    public ZoneNotFoundException() {
    }

    public ZoneNotFoundException(String message) {
        super(message);
    }
}
