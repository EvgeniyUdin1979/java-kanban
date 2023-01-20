package api.errors;

public class ServerTaskIdException extends RuntimeException{
    public ServerTaskIdException() {
    }

    public ServerTaskIdException(String message) {
        super(message);
    }

    public ServerTaskIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerTaskIdException(Throwable cause) {
        super(cause);
    }

    public ServerTaskIdException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
