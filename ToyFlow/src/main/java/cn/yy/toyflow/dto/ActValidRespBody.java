package cn.yy.toyflow.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@Data
@EqualsAndHashCode
public class ActValidRespBody {
    private boolean valid;
    private Map<String, String> requestData;
}
