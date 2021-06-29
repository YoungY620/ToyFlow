package cn.yy.toyflow.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode
@ToString
public class CurrStateRespBody {
    List<ReqStateEntry> requests;
}
