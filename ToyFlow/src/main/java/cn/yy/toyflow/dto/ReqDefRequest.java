package cn.yy.toyflow.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode
public class ReqDefRequest {
    private String procID;
    private String name;
    private LocalDateTime time;
    private String externalID;
    private List<String> stakeholders;
}
