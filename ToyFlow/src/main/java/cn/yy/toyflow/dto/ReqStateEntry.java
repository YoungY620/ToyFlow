package cn.yy.toyflow.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode
public class ReqStateEntry {
    private String id;
    private String procID;
    private String procName;
    private String reqName;
    private LocalDateTime requestTime;
    private String requestExterID;
    private String requestExterName;
    private String currStateID;
    private String cuurStateName;
    private String currStateType;
    private List<ReqActEntry> requestActions;
}
