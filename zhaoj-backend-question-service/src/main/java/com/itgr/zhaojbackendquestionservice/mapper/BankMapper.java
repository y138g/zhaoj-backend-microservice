package com.itgr.zhaojbackendquestionservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itgr.zhaojbackendmodel.model.entity.Bank;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author ygking
 * @description 针对表【bank(题库)】的数据库操作Mapper
 * @createDate 2025-04-14
 */
@Mapper
public interface BankMapper extends BaseMapper<Bank> {
} 