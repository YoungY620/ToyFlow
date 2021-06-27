package cn.yy.toyflow.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class ReqActEntry {
    private String id;
    private String actionID;
    private String actionName;
    private String nextStateID;
    private String nextStateName;
}
