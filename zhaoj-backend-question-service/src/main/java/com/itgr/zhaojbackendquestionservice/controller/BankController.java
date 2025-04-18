package com.itgr.zhaojbackendquestionservice.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itgr.zhaojbackendcommon.annotation.AuthCheck;
import com.itgr.zhaojbackendcommon.common.BaseResponse;
import com.itgr.zhaojbackendcommon.common.ErrorCode;
import com.itgr.zhaojbackendcommon.common.ResultUtils;
import com.itgr.zhaojbackendcommon.constant.UserConstant;
import com.itgr.zhaojbackendcommon.exception.ThrowUtils;
import com.itgr.zhaojbackendmodel.model.dto.bank.BankAddRequest;
import com.itgr.zhaojbackendmodel.model.dto.bank.BankQueryRequest;
import com.itgr.zhaojbackendmodel.model.dto.bank.BankUpdateRequest;
import com.itgr.zhaojbackendmodel.model.entity.Bank;
import com.itgr.zhaojbackendmodel.model.entity.User;
import com.itgr.zhaojbackendmodel.model.vo.BankVO;
import com.itgr.zhaojbackendquestionservice.service.BankService;
import com.itgr.zhaojbackendserviceclient.service.UserFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

/**
 * 题库接口
 *
 * @author y138g
 * @from <a href="https://github.com/y138g">yg的开源仓库</a>
 */
@RestController
@RequestMapping("/bank")
@Slf4j
public class BankController {

    @Resource
    private BankService bankService;

    @Resource
    private UserFeignClient userFeignClient;

    /**
     * 新增题库
     *
     * @param request 新增题库请求
     * @return 题库 id
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addBank(@RequestBody BankAddRequest bankAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(bankAddRequest.getTitle() == null, ErrorCode.NOT_FOUND_ERROR,
                "题库标题不能为空");
        ThrowUtils.throwIf(bankAddRequest.getContent() == null, ErrorCode.NOT_FOUND_ERROR,
                "题库描述不能为空");

        User loginUser = userFeignClient.getLoginUser(request);
        ThrowUtils.throwIf(!Objects.equals(loginUser.getUserRole(), UserConstant.ADMIN_ROLE),
                ErrorCode.FORBIDDEN_ERROR, "权限不足！");

        Bank bank = new Bank();
        BeanUtil.copyProperties(bankAddRequest, bank);
        bank.setUserId(loginUser.getId());

        boolean save = bankService.save(bank);
        ThrowUtils.throwIf(!save, ErrorCode.SYSTEM_ERROR);
        return ResultUtils.success(bank.getId());
    }

    /**
     * 修改题库相关信息
     *
     * @param bankUpdateRequest 修改题库请求
     * @param request           request
     * @return 题库 id
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> updateBank(@RequestBody BankUpdateRequest bankUpdateRequest, HttpServletRequest request) {

        ThrowUtils.throwIf(bankUpdateRequest.getId() == null, ErrorCode.NOT_FOUND_ERROR,
                "请指定修改数据！");

        User loginUser = userFeignClient.getLoginUser(request);
        ThrowUtils.throwIf(!Objects.equals(loginUser.getUserRole(), UserConstant.ADMIN_ROLE),
                ErrorCode.FORBIDDEN_ERROR, "权限不足！");

        Long id = bankUpdateRequest.getId();
        QueryWrapper<Bank> qw = new QueryWrapper<>();
        qw.eq("id", id);
        ThrowUtils.throwIf(bankService.getOne(qw) == null, ErrorCode.NOT_FOUND_ERROR);

        Bank bank = new Bank();
        bank.setId(id);
        if (!bankUpdateRequest.getTitle().isEmpty()) {
            bank.setTitle(bankUpdateRequest.getTitle());
        }
        if (!bankUpdateRequest.getContent().isEmpty()) {
            bank.setContent(bankUpdateRequest.getContent());
        }

        boolean save = bankService.updateById(bank);
        ThrowUtils.throwIf(!save, ErrorCode.SYSTEM_ERROR);
        return ResultUtils.success(bank.getId());
    }

    /**
     * 查询热度前三的题库
     *
     * @return 热度前三的题库列表
     */
    @GetMapping("/top")
    public BaseResponse<List<BankVO>> getBankTopThree() {
        return ResultUtils.success(bankService.getBankTopThree());
    }

    /**
     * 查询所有题库
     *
     * @return 所有题库列表
     */
    @GetMapping("/get/all")
    public BaseResponse<List<BankVO>> getBankAll() {
        return ResultUtils.success(bankService.getBankAll());
    }

    /**
     * 分页获取题库列表（仅管理员）
     *
     * @param bankQueryRequest 分页请求
     * @param request          request
     * @return 题库列表
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Bank>> listBankByPage(@RequestBody BankQueryRequest bankQueryRequest,
                                                       HttpServletRequest request) {
        long current = bankQueryRequest.getCurrent();
        long size = bankQueryRequest.getPageSize();
        Page<Bank> bankPage = bankService.page(new Page<>(current, size),
                bankService.getQueryWrapper(bankQueryRequest));
        return ResultUtils.success(bankPage);
    }
}
