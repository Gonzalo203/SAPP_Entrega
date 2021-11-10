package es.storeapp.business.exceptions;

public class PasswordStrengthException extends Exception {

    private static final long serialVersionUID = 3026551774263231416L;

    public PasswordStrengthException(String message) {
        super(message);
    }

}
