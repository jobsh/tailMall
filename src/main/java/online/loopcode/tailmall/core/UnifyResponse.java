package online.loopcode.tailmall.core;


import online.loopcode.tailmall.exception.CreateSuccess;

public class UnifyResponse {
    private int code;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getRequest() {
        return request;
    }

    private String message;
    private String request;

    public UnifyResponse(int code, String message, String request){
        this.code = code;
        this.message = message;
        this.request = request;
    }

    public static void createSuccess(int code) {
        throw new CreateSuccess(code);
    }
}
