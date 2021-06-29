package cn.yy.toyflow.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class ResponseBean <T> {
    private String stateCode;
    private T data;

    public static ResponseBean<ErrorRespBody> defaultError = new ResponseBean<>();
    public static ResponseBean<Object> defaultOk = new ResponseBean<>();
    public static String STATE_OK = "200";
    public static String STATE_ERROR = "500";

    static {
        defaultError.stateCode = "500";
        defaultError.data = null;
        defaultOk.data = null;
        defaultOk.stateCode = "200";
    }
    public static ResponseBean<ErrorRespBody> error(String reason, String detail){
        ResponseBean<ErrorRespBody> error = new ResponseBean<>();
        error.data = new ErrorRespBody(reason,detail);
        return error;
    }

    public String getStateCode() {
        return stateCode;
    }

    public ResponseBean<T> setStateCode(String stateCode) {
        this.stateCode = stateCode;
        return this;
    }

    public T getData() {
        return data;
    }

    public ResponseBean<T> setData(T data) {
        this.data = data;
        return this;
    }
}
