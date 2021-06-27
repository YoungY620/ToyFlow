package cn.yy.toyflow.controller;

import cn.yy.toyflow.dto.ReqDefRequest;
import cn.yy.toyflow.service.IService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    @Autowired
    IService service;

    @PostMapping("/newRequest")
    public void setupRequest(@RequestBody ReqDefRequest request){

    }
}
