package taskmangers.erros;

public class ManagerIllegalIdException extends RuntimeException{
    public ManagerIllegalIdException() {
    }

    public ManagerIllegalIdException(String message) {
        super(message);
    }

    public ManagerIllegalIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public ManagerIllegalIdException(Throwable cause) {
        super(cause);
    }

    public ManagerIllegalIdException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
