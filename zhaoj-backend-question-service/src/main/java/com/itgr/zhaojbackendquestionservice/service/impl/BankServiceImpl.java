package com.itgr.zhaojbackendquestionservice.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itgr.zhaojbackendcommon.constant.CommonConstant;
import com.itgr.zhaojbackendcommon.utils.SqlUtils;
import com.itgr.zhaojbackendmodel.model.dto.bank.BankQueryRequest;
import com.itgr.zhaojbackendmodel.model.entity.Bank;
import com.itgr.zhaojbackendmodel.model.vo.BankVO;
import com.itgr.zhaojbackendquestionservice.service.BankService;
import com.itgr.zhaojbackendquestionservice.mapper.BankMapper;
import org.apache.commons.lang3.StringUtils;
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
    public QueryWrapper<Bank> getQueryWrapper(BankQueryRequest bankQueryRequest) {
        QueryWrapper<Bank> queryWrapper = new QueryWrapper<>();
        if (bankQueryRequest == null) return queryWrapper;

        String title = bankQueryRequest.getTitle();
        String sortField = bankQueryRequest.getSortField();
        String sortOrder = bankQueryRequest.getSortOrder();

        // 拼接查询条件
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }
}




