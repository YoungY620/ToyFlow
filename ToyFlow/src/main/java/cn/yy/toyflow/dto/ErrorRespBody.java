package cn.yy.toyflow.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class ErrorRespBody {
    private String reason;
    private String detail;

    public ErrorRespBody() {
    }

    public ErrorRespBody(String reason, String detail) {
        this.reason = reason;
        this.detail = detail;
    }
}
