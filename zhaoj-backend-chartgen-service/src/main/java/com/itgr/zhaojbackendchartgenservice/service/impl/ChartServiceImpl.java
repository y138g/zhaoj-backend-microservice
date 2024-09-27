package com.itgr.zhaojbackendchartgenservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itgr.zhaojbackendmodel.model.entity.Chart;
import com.itgr.zhaojbackendchartgenservice.service.ChartService;
import com.itgr.zhaojbackendchartgenservice.mapper.ChartMapper;
import org.springframework.stereotype.Service;

/**
* @author ygking
* @description 针对表【chart(图表信息表)】的数据库操作Service实现
* @createDate 2024-09-26 11:04:30
*/
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
    implements ChartService{

}




