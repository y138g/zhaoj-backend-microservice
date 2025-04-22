package com.itgr.zhaojbackendquestionservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itgr.zhaojbackendmodel.model.entity.Bank;
import com.itgr.zhaojbackendmodel.model.entity.BankQuestion;
import com.itgr.zhaojbackendquestionservice.mapper.BankQuestionMapper;
import com.itgr.zhaojbackendquestionservice.service.BankQuestionService;
import com.itgr.zhaojbackendquestionservice.mapper.BankMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ygking
 * @description 针对表【bank_question(题库-题目)】的数据库操作Service实现
 * @createDate 2025-03-11 11:59:48
 */
@Service
public class BankQuestionServiceImpl extends ServiceImpl<BankQuestionMapper, BankQuestion>
        implements BankQuestionService {

    @Resource
    private BankMapper bankMapper;

    @Override
    public boolean addBankAndQuestion(Long questionId, List<Long> bankIds) {
        for (Long bankId : bankIds) {
            BankQuestion bankQuestion = new BankQuestion();
            bankQuestion.setBankId(bankId);
            bankQuestion.setQuestionId(questionId);
            boolean result = this.save(bankQuestion);
            if (!result) {
                return false;
            }
            UpdateWrapper<Bank> bankUpdateWrapper = new UpdateWrapper<>();
            bankUpdateWrapper.eq("id", bankId).setSql("questionNum = questionNum + 1");
            bankMapper.update(null, bankUpdateWrapper);
        }
        return true;
    }

    @Override
    public boolean removeByQuestionId(Long questionId) {
        QueryWrapper<BankQuestion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("questionId", questionId);
        // 删除前先获取对应的 bankId
        List<BankQuestion> bankQuestionList = this.list(queryWrapper);
        for (BankQuestion bankQuestion : bankQuestionList) {
            Long bankId = bankQuestion.getBankId();
            UpdateWrapper<Bank> bankUpdateWrapper = new UpdateWrapper<>();
            bankUpdateWrapper.eq("id", bankId).setSql("questionNum = questionNum - 1");
            bankMapper.update(null, bankUpdateWrapper);
        }
        return this.remove(queryWrapper);
    }

    @Override
    public List<Long> getBankQuestionById(Long questionId) {
        List<Long> bankQuestionIds = new ArrayList<>();
        QueryWrapper<BankQuestion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("questionId", questionId);
        List<BankQuestion> bankQuestionList = this.list(queryWrapper);
        bankQuestionList.forEach(bankQuestion -> {
            bankQuestionIds.add(bankQuestion.getBankId());
        });
        return bankQuestionIds;
    }
}




