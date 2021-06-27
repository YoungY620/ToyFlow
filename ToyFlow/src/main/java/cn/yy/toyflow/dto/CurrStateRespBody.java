package cn.yy.toyflow.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode
public class CurrStateRespBody {
    List<ReqStateEntry> requests;
}
