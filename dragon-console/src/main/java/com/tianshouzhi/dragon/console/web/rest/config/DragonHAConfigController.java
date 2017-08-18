package com.tianshouzhi.dragon.console.web.rest.config;

import com.tianshouzhi.dragon.console.domain.DragonHAConfigurationDTO;
import com.tianshouzhi.dragon.console.domain.SingleDataSourceConfigDTO;
import com.tianshouzhi.dragon.ha.config.DragonHAConfiguration;
import com.tianshouzhi.dragon.ha.config.RealDatasourceConfig;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tianshouzhi on 2017/6/8.
 */
@RestController
@RequestMapping("/ha-config")
public class DragonHAConfigController {
    @RequestMapping("/create")
    public String create(DragonHAConfigurationDTO configDTO){

        String appName = configDTO.getAppName();
        String realClass = configDTO.getRealClass();
        List<RealDatasourceConfig> realDatasourceConfigList = transform(configDTO.getSingleDSConfigDTOList());
        DragonHAConfiguration dragonHADataSourceConfig =new DragonHAConfiguration();
        List<RealDatasourceConfig> dsConfigList = new ArrayList<RealDatasourceConfig>();

//        return DragonHAXmlConfigParser.toXml();
        return null;
    }

    private List<RealDatasourceConfig> transform(List<SingleDataSourceConfigDTO> dtos){
        List<RealDatasourceConfig> dsConfigList=new ArrayList<RealDatasourceConfig>();
        for (SingleDataSourceConfigDTO dto : dtos) {
            RealDatasourceConfig config=new RealDatasourceConfig();
            config.setIndex(dto.getIndex());
            config.setReadWeight(config.getReadWeight());
            config.setWriteWeight(config.getWriteWeight());
//            config.setProperties();
        }
        return null;
    }
}
