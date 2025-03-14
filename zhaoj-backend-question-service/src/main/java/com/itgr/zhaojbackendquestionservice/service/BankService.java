package com.itgr.zhaojbackendquestionservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itgr.zhaojbackendmodel.model.entity.Bank;

import java.util.List;

/**
 * @author ygking
 * @description 针对表【bank(题库)】的数据库操作Service
 * @createDate 2025-03-10 13:04:32
 */
public interface BankService extends IService<Bank> {

    /**
     * 校验题库是否存在
     *
     * @param bankIds 题库id列表
     * @return 是否存在
     */
    boolean validBank(List<Long> bankIds);

}
