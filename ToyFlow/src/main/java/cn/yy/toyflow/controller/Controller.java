package cn.yy.toyflow.controller;

import cn.yy.toyflow.dto.ReqDefRequest;
import cn.yy.toyflow.service.FlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    @Autowired
    FlowService service;

    @PostMapping("/newRequest")
    public void setupRequest(@RequestBody ReqDefRequest request){

    }
}
