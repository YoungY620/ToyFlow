package cn.yy.toyflow.entity;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author yy
 * @since 2021-06-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RequestData implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String requestId;

    private String key;

    private String val;


}
