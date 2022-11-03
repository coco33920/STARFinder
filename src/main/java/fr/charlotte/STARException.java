package fr.charlotte;

public class STARException extends Exception {
    private String name;
    private String message;

    public STARException(String name, String message) {
        super(message);
        this.message = message;
        this.name = name;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    public String getName() {
        return name + ": ";
    }
}
