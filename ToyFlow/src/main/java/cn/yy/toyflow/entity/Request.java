package cn.yy.toyflow.entity;

import java.time.LocalDateTime;
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
public class Request implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String processId;

    private String name;

    private LocalDateTime requestTime;

    private String requesterUserId;

    private String currentState;

    private Boolean blocked;

    private String blockerExternalId;


}
