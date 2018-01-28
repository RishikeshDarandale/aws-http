package in.rishikeshdarandale.aws.http;

public class CouldNotConvertException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * {@inheritDoc}
     */
    public CouldNotConvertException(String message, Throwable cause) {
        super(message, cause);
    }
}
