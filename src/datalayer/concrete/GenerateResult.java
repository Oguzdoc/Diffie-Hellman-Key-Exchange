package datalayer.concrete;

public class GenerateResult 
{
    private ResultCode Code;
    private String Message;    
    
    public enum ResultCode
    {
    Error,
    Success,
    Warning
    }

    public GenerateResult() {
        super();
    }
    
    public GenerateResult(ResultCode code, String message) {
        this.Code = code;
        this.Message = message;
    }
    
    
    public void Update(ResultCode code, String message) {
        this.Code = code;
        this.Message = message;
    }

    public ResultCode getCode() {
        return Code;
    }

    public void setCode(ResultCode Code) {
        this.Code = Code;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }
}