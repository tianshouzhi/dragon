package com.tianshouzhi.dragon.console.web.rest;

import com.tianshouzhi.dragon.sharding.pipeline.handler.statics.SqlExecutionStatics;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by TIANSHOUZHI336 on 2017/3/23.
 */
@RestController
public class SqlStaticsController {

    @PostMapping("/batchadd")
    public void batchAdd(@RequestBody List<SqlExecutionStatics> sqlExecutionStaticsList){

    }
}
