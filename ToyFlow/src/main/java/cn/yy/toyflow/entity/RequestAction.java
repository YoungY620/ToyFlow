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
 * @since 2021-06-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RequestAction implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String requestId;

    private String transitionId;

    private String actionId;

    private Boolean active;

    private Boolean completed;


}
