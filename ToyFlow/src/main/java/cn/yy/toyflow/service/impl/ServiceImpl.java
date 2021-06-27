package cn.yy.toyflow.service.impl;

import cn.yy.toyflow.dto.*;
import cn.yy.toyflow.entity.*;
import cn.yy.toyflow.entity.Action;
import cn.yy.toyflow.mapper.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import cn.yy.toyflow.service.IService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ServiceImpl implements IService{
    @Autowired
    RequestMapper requestMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    StakeholderMapper stakeholderMapper;
    @Autowired
    RequestActionMapper requestActionMapper;
    @Autowired
    RequestDataMapper requestDataMapper;
    @Autowired
    TransitionMapper transitionMapper;
    @Autowired
    ActionMapper actionMapper;
    @Autowired
    StateMapper stateMapper;

    /**
     * 新建一个请求， 并添加干系人
     * @param reqDefRequest 外部发来的包含所有信息的请求体
     * @return 若必须字段为空， 或干系人查无此人则返回错误
     */
    @Override
    public ResponseBean<?> newRequest(ReqDefRequest reqDefRequest){
        //todo 忘了添加初始的 request actions！
        Request newRequest = new Request();
        if (reqDefRequest.getProcID() == null || reqDefRequest.getName() == null
                || reqDefRequest.getExternalID() == null){
            return ResponseBean.error("必须字段不可为空","");
        }
        String startState = stateMapper.selectOne(
                new LambdaQueryWrapper<State>()
                        .eq(State::getProId, reqDefRequest.getProcID())
                        .eq(State::getStateTypeId, "1")
        ).getId();
        newRequest.setProcessId(reqDefRequest.getProcID());
        newRequest.setName(reqDefRequest.getName());
        newRequest.setRequestTime(reqDefRequest.getTime() == null? LocalDateTime.now():reqDefRequest.getTime());
        newRequest.setRequesterUserId(reqDefRequest.getExternalID());
        newRequest.setCurrentState(startState);
        newRequest.setBlocked(false);
        requestMapper.insert(newRequest);

        pushNewRequestActions(newRequest);

        return addStakeholders(reqDefRequest, newRequest);
    }

    /**
     * 干系人询问当前状态
     * @param externalID 询问者在外部系统的唯一 id
     * @return 返回体, 正常返回当前进行中的 request 流程, 及每个流程当前可执行的 Action
     */
    @Override
    public ResponseBean<?> getCurrentState(String externalID) {
        List<ReqStateEntry> reqStateEntries = getReqStateEntries(externalID);
        CurrStateRespBody body = new CurrStateRespBody();
        body.setRequests(reqStateEntries);
        ResponseBean<CurrStateRespBody> response = new ResponseBean<>();
        response.setStateCode(ResponseBean.STATE_OK);
        response.setData(body);

        return response;
    }

    /**
     * 干系人申领 Action, 由 ToyFlow 检查是否能执行
     * @param externalID
     * @param requestID
     * @param requestActionID
     * @return 若可以执行, 返回当前被请求 request 的 data
     */
    @Override
    public ResponseBean<?> checkActionRequest(String externalID, String requestID, String requestActionID) {
        ActValidRespBody body = checkValid(externalID, requestID, requestActionID);

        Map<String, String> dataMap = getRequestDataMap(requestID);
        body.setRequestData(dataMap);

        ResponseBean<ActValidRespBody> response = new ResponseBean<>();
        response.setStateCode(ResponseBean.STATE_OK);
        response.setData(body);
        return response;
    }

    /**
     * 报告 Action 已经完成
     * @param blockerExternalID
     * @param requestID
     * @param requestActionID
     * @param updatedRequestData
     * @return
     */
    @Override
    public ResponseBean<?> reportActionCompleted(String blockerExternalID, String requestID,
                                                 String requestActionID, Map<String, String> updatedRequestData) {
        Request request = requestMapper.selectById(requestID);
        RequestAction requestAction = requestActionMapper.selectById(requestActionID);

        if (request == null || requestAction == null){
            return ResponseBean.error("Wrong IDs of requests and request actions!","");
        }
        if (!request.getBlocked() || !request.getBlockerExternalId().equals(blockerExternalID)){
            return ResponseBean.error("The request is not blocked by this user.","");
        }

        pushFlow(requestID, request, requestAction);

        List<RequestData> requestDataList = updatedRequestData.entrySet().stream().map(e->{
            RequestData rd = new RequestData();
            rd.setKey(e.getKey()); rd.setVal(e.getValue());
            return rd;
        }).collect(Collectors.toList());
        for (RequestData rd: requestDataList){
            rd.setVal(updatedRequestData.get(rd.getKey()));
        }
        for (RequestData rd: requestDataList){
            requestDataMapper.updateById(rd);
        }
        return getCurrentState(blockerExternalID);
    }

    /**
     * 仅查看 当前数据
     * @param requestID
     * @return
     */
    @Override
    public ResponseBean<?> getRequestData(String requestID) {
        Map<String, String> requestDataMap = getRequestDataMap(requestID);
        return new ResponseBean<Map<String, String>>()
                .setStateCode(ResponseBean.STATE_OK).setData(requestDataMap);
    }

    /**
     * 更新 request action 表，以推动流程进行
     * @param requestID
     * @param request
     * @param requestAction
     */
    private void pushFlow(String requestID, Request request, RequestAction requestAction) {
        request.setBlocked(false);
        requestAction.setCompleted(true);
        requestAction.setActive(false);

        List<RequestAction> openActionOfThisTransition;
        openActionOfThisTransition = requestActionMapper.selectList(
                new LambdaQueryWrapper<RequestAction>()
                        .eq(RequestAction::getRequestId, requestID)
                        .eq(RequestAction::getTransitionId, requestAction.getTransitionId())
                        .eq(RequestAction::getCompleted, false)
                        .eq(RequestAction::getActive, true)
        );
        if (openActionOfThisTransition.size() == 0){
            List<RequestAction> requestActions = requestActionMapper.selectList(
                    new LambdaQueryWrapper<RequestAction>()
                            .eq(RequestAction::getRequestId, requestID)
                            .eq(RequestAction::getActive, true)
            );
            for (RequestAction ra:requestActions){
                requestActionMapper.updateById(ra);
            }
        }

        String transitionID = requestAction.getTransitionId();
        String nextState = transitionMapper.selectById(transitionID).getNextState();
        request.setCurrentState(nextState);
        pushNewRequestActions(request);
    }

    private void pushNewRequestActions(@NotNull Request request) {
        String fromState = request.getCurrentState();
        // 添加新的request action
        for (Transition t:transitionMapper.selectList(
                new LambdaQueryWrapper<Transition>().eq(Transition::getCurrState, fromState)
        )){
            for (Action a:actionMapper.selectList(
                    new LambdaQueryWrapper<Action>().eq(Action::getTransitionId, t.getId())
            )){
                RequestAction newRA = new RequestAction();
                newRA.setTransitionId(t.getId());
                newRA.setActionId(a.getId());
                newRA.setRequestId(request.getId());
                requestActionMapper.insert(newRA);
            }
        }
    }

    private ActValidRespBody checkValid(String externalID, String requestID, String requestActionID) {
        String userID = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getExternalId, externalID)
        ).getId();
        List<RequestAction> openActions = requestActionMapper.selectRelavantOpenActions(userID);
        openActions = openActions.stream()
                .filter(requestAction -> requestAction.getRequestId().equals(requestID)
                        && requestAction.getId().equals(requestActionID))
                .collect(Collectors.toList());
        ActValidRespBody body = new ActValidRespBody();
        Request request = requestMapper.selectById(requestID);
        if (openActions.size() == 0 || request.getBlocked()){
            body.setValid(false);
        } else {
            body.setValid(true);
            request.setBlocked(true);
            request.setBlockerExternalId(externalID);
            requestMapper.update(request,
                    new LambdaQueryWrapper<Request>().eq(Request::getId, requestID));
        }
        return body;
    }

    @NotNull
    private Map<String, String> getRequestDataMap(String requestID) {
        List<RequestData> dataEntries = requestDataMapper.selectList(
                new LambdaQueryWrapper<RequestData>().eq(RequestData::getRequestId, requestID)
        );
        Map<String, String> dataMap = new HashMap<>();
        for (RequestData e: dataEntries){
            dataMap.put(e.getKey(),e.getVal());
        }
        return dataMap;
    }

    private List<ReqStateEntry> getReqStateEntries(String externalID) {
        String userID = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getExternalId, externalID)
        ).getId();

        List<RequestAction> openActions = requestActionMapper.selectRelavantOpenActions(userID);
        List<String> openRequestIds = openActions
                .stream().map(RequestAction::getRequestId)
                .distinct().collect(Collectors.toList());
        List<ReqStateEntry> reqStateEntries = new ArrayList<>();
        for (String requestId:openRequestIds){
            ReqStateEntry entry = requestActionMapper.getRequestStateEntryById(requestId);
            reqStateEntries.add(entry);
        }
        return reqStateEntries;
    }

    private ResponseBean<?> addStakeholders(ReqDefRequest request, Request newRequest) {
        List<String> userIds = new ArrayList<>();
        List<String> nullExternalIds = new ArrayList<>();
        for(String seid: request.getStakeholders()){
            String uid = userMapper
                    .selectOne(new LambdaQueryWrapper<User>().eq(User::getExternalId,seid))
                    .getId();
            if (uid == null){
                nullExternalIds.add(seid);
            }else{
                userIds.add(uid);
            }
        }
        if (nullExternalIds.size() == 0){
            for(String uid:userIds){
                Stakeholder stakeholder = new Stakeholder();
                stakeholder.setUserId(uid);
                stakeholder.setRequestId(newRequest.getId());
                stakeholderMapper.insert(stakeholder);
            }
        } else {
            return ResponseBean.error("Users not found!"
                    +"You need to add following users: "
                    +nullExternalIds.toString(),null);
        }
        return ResponseBean.defaultOk;
    }
}
