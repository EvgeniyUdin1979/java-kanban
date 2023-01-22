package taskmangers.erros;

public class HttpManagerConnectException extends RuntimeException{
    public HttpManagerConnectException() {
    }

    public HttpManagerConnectException(String message) {
        super(message);
    }

    public HttpManagerConnectException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpManagerConnectException(Throwable cause) {
        super(cause);
    }

    public HttpManagerConnectException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
