package cn.yy.toyflow.mapper;

import cn.yy.toyflow.dto.ReqActEntry;
import cn.yy.toyflow.dto.ReqStateEntry;
import cn.yy.toyflow.entity.RequestAction;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author yy
 * @since 2021-06-26
 */
public interface RequestActionMapper extends BaseMapper<RequestAction> {
    @Select("select * from request_action as ra\n" +
            "where ra.active=true and ra.completed=false \n" +
            "  and (action_id in (\n" +
            "    select id from action as a\n" +
            "    where (\n" +
            "        a.target_type_id = 3 and (${userID} in (\n" +
            "            select gm.user_id\n" +
            "            from group_member as gm\n" +
            "            where gm.group_id = a.group_id\n" +
            "        ))\n" +
            "    ) or (\n" +
            "        a.target_type_id = 2 and (${userID} in (\n" +
            "            select gm.user_id\n" +
            "            from group_member as gm\n" +
            "            where gm.group_id = a.group_id\n" +
            "        )) and (${userID} in (\n" +
            "            select sh.user_id\n" +
            "            from stakeholder as sh\n" +
            "            where sh.request_id=ra.request_id\n" +
            "        ))\n" +
            "    )\n" +
            "));")
    List<RequestAction> selectRelavantOpenActions(@Param("userID") String userID);

    @Select("select r.id, p.id procID, p.name as procName, r.name as reqName, r.request_time as requestTime,\n" +
            "       u.id as requestExterID, u.name as requestExterName, s.id as currStateID,\n" +
            "       s.name as cuurStateName, st.name as currStateType, ra.id requestActionId\n" +
            "from `request_action` ra, `process` p, `request` r, `user` u, `state` s, `state_type` st,\n" +
            "     `action` a, `state` s2, `transition` t\n" +
            "where ra.id=${id} and ra.request_id = r.id and r.process_id = p.id\n" +
            "  and r.current_state = s.id and r.requester_user_id=u.id and st.id = s.state_type_id\n" +
            "  and ra.action_id=a.id and ra.transition_id=t.id and s2.id=t.next_state;")
    ReqStateEntry getRequestStateEntryByReqActId(@Param("id") String requestActionID);

    @Select("select distinct ra.id, a.id actionID, a.name actionName,\n" +
            "       s2.id as nextStateID, s2.name as nextStateName\n" +
            "from `request_action` ra, `action` a, `state` s2, `transition` t\n" +
            "where ra.id=${id} and ra.action_id=a.id and ra.transition_id=t.id and s2.id=t.next_state;")
    ReqActEntry getReqActEntriesByRAId(@Param("id") String requestActionId);
}
