package datalayer.concrete;

public class GenerateResult {
    private final ResultCode code;
    private final String message;

    public enum ResultCode {
        SUCCESS,
        ERROR
    }

    public GenerateResult(ResultCode code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResultCode getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}