package cn.yy.toyflow;

import cn.yy.toyflow.dto.*;
import cn.yy.toyflow.service.FlowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ServiceTest {
    @Autowired
    FlowService flowService;
    @Autowired
    UpdateSqlService updateSqlService;

    private static final String requesterExternalID = "1";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @BeforeEach
    void initDB(){
        updateSqlService.doExecuteSql();
    }

    @Test
    void newRequestTest() {
        ResponseBean<?> responseBean = newRequest();
        logger.debug("new request response: responseBean = " + responseBean);
        System.out.println("responseBean = " + responseBean);
        assertEquals(responseBean.toString(), "ResponseBean(stateCode=200, data=null)");
    }

    private ResponseBean<?>  newRequest() {
        ReqDefRequest reqDefRequest = new ReqDefRequest();
        reqDefRequest.setName("test request");
        reqDefRequest.setProcID("1");

        reqDefRequest.setTime(LocalDateTime.now());
        reqDefRequest.setExternalID(requesterExternalID);
        reqDefRequest.setStakeholders(Arrays.asList("1","2","3"));

        //        System.out.println("responseBean = " + responseBean);
        return flowService.newRequest(reqDefRequest);
    }

    @Test
    void checkState() {
        newRequestTest();

        ResponseBean<?> responseBean = flowService.getCurrentState("1");
        System.out.println("responseBean = " + responseBean);
    }

    @Test
    void fetchAnAction() {
        newRequestTest();

        ResponseBean<?> responseBean = flowService.getCurrentState(requesterExternalID);
        ReqStateEntry reqStateEntry = ((CurrStateRespBody)responseBean.getData()).getRequests().get(0);
        System.out.println("reqStateEntry = " + reqStateEntry);
        ReqActEntry reqActEntry = reqStateEntry.getRequestActions().get(0);
        System.out.println("reqActEntry = " + reqActEntry);
        ResponseBean<?> fetchResponse = flowService.checkActionRequest(requesterExternalID,reqStateEntry.getId(),reqActEntry.getId());
        System.out.println("fetchResponse = " + fetchResponse);
    }

    @Test
    void reportCompleted() {
        newRequestTest();

        ResponseBean<?> responseBean = flowService.getCurrentState(requesterExternalID);
        ReqStateEntry reqStateEntry = ((CurrStateRespBody)responseBean.getData()).getRequests().get(0);
        ReqActEntry reqActEntry = reqStateEntry.getRequestActions().get(0);
        ResponseBean<?> fetchResponse = flowService.checkActionRequest(requesterExternalID,reqStateEntry.getId(),reqActEntry.getId());
        System.out.println("fetchResponse = " + fetchResponse);

        ResponseBean<?> reportCompleted = flowService.reportActionCompleted(requesterExternalID,reqStateEntry.getId(),reqActEntry.getId(),new HashMap<>());
        System.out.println("reportCompleted = " + reportCompleted);
    }

    @Test
    void getRequestDataTest() {
        newRequestTest();
        ResponseBean<?> currState = flowService.getCurrentState(requesterExternalID);
        System.out.println("currState = " + currState);
        String requestID = ((CurrStateRespBody)currState.getData()).getRequests().get(0).getId();
        ResponseBean<?> dataResponse = flowService.getRequestData(requestID);
        System.out.println("dataResponse = " + dataResponse);
    }
}
