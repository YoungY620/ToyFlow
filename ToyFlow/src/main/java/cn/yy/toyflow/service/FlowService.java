package cn.yy.toyflow.service;

import cn.yy.toyflow.dto.ReqDefRequest;
import cn.yy.toyflow.dto.ResponseBean;

import java.util.Map;

public interface FlowService {
    ResponseBean<?> newRequest(ReqDefRequest request);

    ResponseBean<?> getCurrentState(String externalID);

    ResponseBean<?> checkActionRequest(String externalID, String requestID, String actionID);

    ResponseBean<?> reportActionCompleted(String blockerExternalID, String requestID,
                                          String requestActionID, Map<String, String> updatedRequestData);

    ResponseBean<?> getRequestData(String requestID);
}
