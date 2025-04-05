package com.itgr.zhaojbackendquestionservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itgr.zhaojbackendmodel.model.entity.Bank;
import com.itgr.zhaojbackendmodel.model.vo.BankVO;

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

    /**
     * 获取热榜前三的题库 TODO 目前暂无热榜，改为采取创建前三的题库
     *
     * @return 题库列表
     */
    List<BankVO> getBankTopThree();

    /**
     * 获取所有题库
     *
     * @return 题库列表
     */
    List<BankVO> getBankAll();

    /**
     * 更新题库题目数量
     *
     * @param bankIds 题库id列表
     */
    void updateBankQuestionNum(List<Long> bankIds);
}
