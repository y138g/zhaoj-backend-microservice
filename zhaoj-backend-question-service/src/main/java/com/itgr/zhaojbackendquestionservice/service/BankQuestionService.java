package com.itgr.zhaojbackendquestionservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itgr.zhaojbackendmodel.model.entity.BankQuestion;

import java.util.List;

/**
 * @author ygking
 * @description 针对表【bank_question(题库-题目)】的数据库操作Service
 * @createDate 2025-03-11 11:59:48
 */
public interface BankQuestionService extends IService<BankQuestion> {

    /**
     * 新增题库和题目中间表
     *
     * @param questionId 题目id
     * @param bankIds    题库id列表
     * @return 是否成功
     */
    boolean addBankAndQuestion(Long questionId, List<Long> bankIds);

    /**
     * 根据题目id删除题库和题目中间表
     *
     * @param questionId 题目id
     * @return 是否成功
     */
    boolean removeByQuestionId(Long questionId);

    /**
     * 根据题目id获取题库id列表
     *
     * @param questionId 题目id
     * @return 题库id列表
     */
    List<Long> getBankQuestionById(Long questionId);
}
