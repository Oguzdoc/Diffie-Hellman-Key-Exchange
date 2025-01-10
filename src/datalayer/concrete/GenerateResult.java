package datalayer.concrete;

/**
 * The GenerateResult class represents the outcome of an operation, providing a status code and a message.
 */
public class GenerateResult 
{
    private final ResultCode code;
    private final String message;

    /**
     * Enum to represent the possible result codes.
     */
    public enum ResultCode
    {
        SUCCESS,
        ERROR
    }

    /**
     * Constructor to initialize the GenerateResult with a result code and a message.
     *
     * @param code    The result code (e.g., SUCCESS, ERROR).
     * @param message A message providing additional information about the result.
     */
    public GenerateResult(ResultCode code, String message)
    {
        this.code = code;
        this.message = message;
    }

    /**
     * Gets the result code.
     *
     * @return The result code.
     */
    public ResultCode getCode()
    {
        return code;
    }

    /**
     * Gets the result message.
     *
     * @return The result message.
     */
    public String getMessage()
    {
        return message;
    }
}
