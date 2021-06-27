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
public class Stakeholder implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String requestId;

    private String userId;


}
