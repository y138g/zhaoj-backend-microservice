package com.itgr.zhaojbackendquestionservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itgr.zhaojbackendmodel.model.entity.Bank;
import com.itgr.zhaojbackendquestionservice.service.BankService;
import com.itgr.zhaojbackendrankingservice.mapper.BankMapper;
import org.springframework.stereotype.Service;

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
}




