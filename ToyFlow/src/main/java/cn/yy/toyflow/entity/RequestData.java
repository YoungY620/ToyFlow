package cn.yy.toyflow.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.annotations.Property;

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
    private String name;

    private String value;


}
