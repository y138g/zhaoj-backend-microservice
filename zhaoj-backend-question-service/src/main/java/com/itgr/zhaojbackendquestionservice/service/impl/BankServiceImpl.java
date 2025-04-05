package com.itgr.zhaojbackendquestionservice.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itgr.zhaojbackendmodel.model.entity.Bank;
import com.itgr.zhaojbackendmodel.model.vo.BankVO;
import com.itgr.zhaojbackendquestionservice.service.BankService;
import com.itgr.zhaojbackendrankingservice.mapper.BankMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ygking
 * @description 针对表【bank(题库)】的数据库操作Service实现
 * @createDate 2025-03-10 13:04:32
 */
@Service
public class BankServiceImpl extends ServiceImpl<BankMapper, Bank>
        implements BankService {

    @Override
    public boolean validBank(List<Long> bankIds) {
        return bankIds.stream().noneMatch(bankId -> this.getById(bankId) == null);
    }

    @Override
    public List<BankVO> getBankTopThree() {
        QueryWrapper<Bank> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("createTime").last("limit 3");
        List<Bank> bankList = this.baseMapper.selectList(queryWrapper);
        return BeanUtil.copyToList(bankList, BankVO.class);
    }

    @Override
    public List<BankVO> getBankAll() {
        List<Bank> bankList = this.getBaseMapper().selectList(new QueryWrapper<>());
        List<BankVO> bankVOList = new ArrayList<>();
        for (Bank bank : bankList) {
            bankVOList.add(BeanUtil.copyProperties(bank, BankVO.class));
        }
        return bankVOList;
    }

    @Override
    public void updateBankQuestionNum(List<Long> bankIds) {
        for (Long bankId : bankIds) {

        }
    }
}




