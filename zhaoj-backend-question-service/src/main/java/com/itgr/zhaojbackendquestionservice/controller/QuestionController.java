package com.itgr.zhaojbackendquestionservice.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.itgr.zhaojbackendcommon.annotation.AuthCheck;
import com.itgr.zhaojbackendcommon.common.BaseResponse;
import com.itgr.zhaojbackendcommon.common.DeleteRequest;
import com.itgr.zhaojbackendcommon.common.ErrorCode;
import com.itgr.zhaojbackendcommon.common.ResultUtils;
import com.itgr.zhaojbackendcommon.constant.UserConstant;
import com.itgr.zhaojbackendcommon.exception.BusinessException;
import com.itgr.zhaojbackendcommon.exception.ThrowUtils;
import com.itgr.zhaojbackendmodel.model.dto.question.*;
import com.itgr.zhaojbackendmodel.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.itgr.zhaojbackendmodel.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.itgr.zhaojbackendmodel.model.entity.*;
import com.itgr.zhaojbackendmodel.model.vo.*;
import com.itgr.zhaojbackendquestionservice.service.BankQuestionService;
import com.itgr.zhaojbackendquestionservice.service.BankService;
import com.itgr.zhaojbackendquestionservice.service.QuestionService;
import com.itgr.zhaojbackendquestionservice.service.QuestionSubmitService;
import com.itgr.zhaojbackendserviceclient.service.UserFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 题目接口
 *
 * @author y138g
 * @from <a href="https://github.com/y138g">yg的开源仓库</a>
 */
@RestController
@RequestMapping("/")
@Slf4j
public class QuestionController {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private BankService bankService;

    @Resource
    private BankQuestionService bankQuestionService;

    private final static Gson GSON = new Gson();

    // region 增删改查

    /**
     * 新增题目（仅管理员）
     *
     * @param questionAddRequest 题目信息
     * @param request            请求
     * @ return 题目id
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addQuestion(@RequestBody QuestionAddRequest questionAddRequest,
                                          HttpServletRequest request) {
        ThrowUtils.throwIf(questionAddRequest == null, ErrorCode.PARAMS_ERROR);

        ThrowUtils.throwIf(questionAddRequest.getBankIds().size() <= 0,
                ErrorCode.PARAMS_ERROR, "题目至少属于一个题库！");

        ThrowUtils.throwIf(!bankService.validBank(questionAddRequest.getBankIds()),
                ErrorCode.PARAMS_ERROR, "所选题库不存在！");

        ThrowUtils.throwIf(questionAddRequest.getDifficulty() == null,
                ErrorCode.PARAMS_ERROR, "难度不能为空！");

        Question question = new Question();
        BeanUtils.copyProperties(questionAddRequest, question);
        List<String> tags = questionAddRequest.getTags();
        if (tags != null) {
            question.setTags(GSON.toJson(tags));
        }
        List<JudgeCase> judgeCase = questionAddRequest.getJudgeCase();
        if (judgeCase != null) {
            question.setJudgeCase(GSON.toJson(judgeCase));
        }
        JudgeConfig judgeConfig = questionAddRequest.getJudgeConfig();
        if (judgeConfig != null) {
            question.setJudgeConfig(GSON.toJson(judgeConfig));
        }
        questionService.validQuestion(question, true);
        User loginUser = userFeignClient.getLoginUser(request);
        question.setUserId(loginUser.getId());
        question.setFavourNum(0);
        question.setThumbNum(0);
        boolean resultSaveQuestion = questionService.save(question);
        ThrowUtils.throwIf(!resultSaveQuestion, ErrorCode.OPERATION_ERROR);
        // 新增题库题目关联
        boolean resultSaveQuestionBank = bankQuestionService.
                addBankAndQuestion(question.getId(), questionAddRequest.getBankIds());
        ThrowUtils.throwIf(!resultSaveQuestionBank, ErrorCode.OPERATION_ERROR);
        long newQuestionId = question.getId();
        return ResultUtils.success(newQuestionId);

    }

    /**
     * 删除题目
     *
     * @param deleteRequest id
     * @param request       请求
     * @return 是否成功
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteQuestion(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userFeignClient.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldQuestion.getUserId().equals(user.getId()) && !userFeignClient.isAdmin(user)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 删除题库题目中间表
        boolean resultMiddle = bankQuestionService.removeByQuestionId(id);
        boolean result = questionService.removeById(id);
        ThrowUtils.throwIf(!(result && resultMiddle), ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新题目（仅管理员）
     *
     * @param questionUpdateRequest 更新信息
     * @return 是否成功
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestion(@RequestBody QuestionUpdateRequest questionUpdateRequest) {

        ThrowUtils.throwIf(questionUpdateRequest == null || questionUpdateRequest.getId() <= 0,
                ErrorCode.PARAMS_ERROR);

        ThrowUtils.throwIf(questionUpdateRequest.getBankIds().size() <= 0,
                ErrorCode.PARAMS_ERROR, "题目至少属于一个题库");

        ThrowUtils.throwIf(!bankService.validBank(questionUpdateRequest.getBankIds()),
                ErrorCode.PARAMS_ERROR, "所选题库不存在！");

        ThrowUtils.throwIf(questionUpdateRequest.getDifficulty() == null,
                ErrorCode.PARAMS_ERROR, "难度不能为空！");

        Question question = new Question();
        BeanUtils.copyProperties(questionUpdateRequest, question);
        List<String> tags = questionUpdateRequest.getTags();
        if (tags != null) {
            question.setTags(GSON.toJson(tags));
        }
        List<JudgeCase> judgeCase = questionUpdateRequest.getJudgeCase();
        if (judgeCase != null) {
            question.setJudgeCase(GSON.toJson(judgeCase));
        }
        JudgeConfig judgeConfig = questionUpdateRequest.getJudgeConfig();
        if (judgeConfig != null) {
            question.setJudgeConfig(GSON.toJson(judgeConfig));
        }
        // 参数校验
        questionService.validQuestion(question, false);
        long id = questionUpdateRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = questionService.updateById(question);
        // 新增题库题目关联 先删除再新增
        boolean remove = bankQuestionService.removeByQuestionId(id);
        boolean resultSaveQuestionBank = bankQuestionService.
                addBankAndQuestion(question.getId(), questionUpdateRequest.getBankIds());
        ThrowUtils.throwIf(!(result && remove && resultSaveQuestionBank), ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取题目（脱敏）
     *
     * @param id 题目 id
     * @return 题目信息
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionVO> getQuestionVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        Question question = questionService.getById(id);
        ThrowUtils.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR);
        QuestionVO questionVO = questionService.getQuestionVO(question, request);
        questionVO.setBankIds(bankQuestionService.getBankQuestionById(id));
        return ResultUtils.success(questionVO);
    }

    /**
     * 根据 id 获取题目（更新前操作）
     *
     * @param id 题目 id
     * @return 题目信息
     */
    @GetMapping("manage/get")
    public BaseResponse<ManageQuestionVO> getManageQuestionById(long id, HttpServletRequest request) {
        Question question = questionService.getById(id);
        ThrowUtils.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR);
        ManageQuestionVO manageQuestionVO = new ManageQuestionVO();
        BeanUtils.copyProperties(question, manageQuestionVO);
        QueryWrapper<BankQuestion> queryWrapper = new QueryWrapper<>();
        List<Object> bankIdList = bankQuestionService.listObjs(
                queryWrapper.eq("questionId", id).select("bankId"));
        List<String> bankTitle = new ArrayList<>();
        for (Object bankId : bankIdList) {
            QueryWrapper<Bank> bankQueryWrapper = new QueryWrapper<>();
            bankQueryWrapper.eq("id", bankId);
            bankTitle.add(bankService.getOne(bankQueryWrapper).getTitle());
        }
        manageQuestionVO.setBankTitle(bankTitle);
        return ResultUtils.success(manageQuestionVO);
    }

    /**
     * 分页获取题目列表（封装类）
     *
     * @param questionQueryRequest 查询条件
     * @param request              请求
     * @return 分页题目数据
     */
    @PostMapping("/list/page/vo")
    @Deprecated
    public BaseResponse<Page<QuestionVO>> listQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                               HttpServletRequest request) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param questionQueryRequest 查询条件
     * @param request              请求
     * @return 分页数据
     */
    @PostMapping("/my/list/page/vo")
    @Deprecated
    public BaseResponse<Page<QuestionVO>> listMyQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                                 HttpServletRequest request) {
        if (questionQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userFeignClient.getLoginUser(request);
        questionQueryRequest.setUserId(loginUser.getId());
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }


    // endregion

    /**
     * 编辑（用户）
     *
     * @param questionEditRequest 编辑信息
     * @param request             请求
     * @return 是否成功
     */
    @PostMapping("/edit")
    @Deprecated
    public BaseResponse<Boolean> editQuestion(@RequestBody QuestionEditRequest questionEditRequest,
                                              HttpServletRequest request) {
        if (questionEditRequest == null || questionEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionEditRequest, question);
        List<String> tags = questionEditRequest.getTags();
        if (tags != null) {
            question.setTags(GSON.toJson(tags));
        }
        List<JudgeCase> judgeCase = questionEditRequest.getJudgeCase();
        if (judgeCase != null) {
            question.setJudgeCase(GSON.toJson(judgeCase));
        }
        JudgeConfig judgeConfig = questionEditRequest.getJudgeConfig();
        if (judgeConfig != null) {
            question.setJudgeConfig(GSON.toJson(judgeConfig));
        }
        // 参数校验
        questionService.validQuestion(question, false);
        User loginUser = userFeignClient.getLoginUser(request);
        long id = questionEditRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldQuestion.getUserId().equals(loginUser.getId()) && !userFeignClient.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = questionService.updateById(question);
        // 新增题库题目关联 先删除再新增
        boolean remove = bankQuestionService.removeByQuestionId(id);
        boolean resultSaveQuestionBank = bankQuestionService.
                addBankAndQuestion(question.getId(), questionEditRequest.getBankIds());
        ThrowUtils.throwIf(!(result && remove && resultSaveQuestionBank), ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest 提交做题信息
     * @param request                  请求
     * @return 提交记录的 id
     */
    @PostMapping("/question_submit/do")
    public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest,
                                               HttpServletRequest request) {
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能做题
        final User loginUser = userFeignClient.getLoginUser(request);
        long questionSubmitId = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
        return ResultUtils.success(questionSubmitId);
    }

    /**
     * 分页获取题目提交列表（除了管理员外，普通用户只能看到非答案、提交代码等公开信息）
     *
     * @param questionSubmitQueryRequest 查询条件
     * @param request                    请求
     * @return 分页题目提交数据
     */
    @PostMapping("/question_submit/list/page")
    @Deprecated
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest
                                                                                 questionSubmitQueryRequest,
                                                                         HttpServletRequest request) {
        long current = questionSubmitQueryRequest.getCurrent();
        long size = questionSubmitQueryRequest.getPageSize();
        // 从数据库中查询原始的题目提交分页信息
        Page<QuestionSubmit> questionSubmitPage = questionSubmitService.page(new Page<>(current, size),
                questionSubmitService.getQueryWrapper(questionSubmitQueryRequest));
        final User loginUser = userFeignClient.getLoginUser(request);
        // 返回脱敏信息
        return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionSubmitPage, loginUser));
    }

    /**
     * 根据题库 id 获取题目列表
     *
     * @param bankId  题库 id
     * @param request 请求
     * @return 题目列表
     */
    @GetMapping("/question/bankId/list/page")
    public BaseResponse<List<QuestionVO>> listQuestionVOByBankId(long bankId, HttpServletRequest request) {
        ThrowUtils.throwIf(bankId <= 0, ErrorCode.PARAMS_ERROR);
        List<QuestionVO> questionVOList = questionService.getQuestionVOByBankId(bankId, request);
        // 返回脱敏信息
        return ResultUtils.success(questionVOList);
    }

    /**
     * 根据 id 获取题目提交信息（脱敏）
     *
     * @param questionSubmitId 题目提交 id
     * @param request          请求
     * @return 题目提交信息
     */
    @GetMapping("/question_submit/get")
    public BaseResponse<QuestionSubmitVO> getQuestionSubmitVO(long questionSubmitId, HttpServletRequest request) {
        ThrowUtils.throwIf(questionSubmitId <= 0, ErrorCode.PARAMS_ERROR);
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        ThrowUtils.throwIf(questionSubmit == null, ErrorCode.NOT_FOUND_ERROR);
        User loginUser = userFeignClient.getLoginUser(request);
        QuestionSubmitVO questionSubmitVO = questionSubmitService.getQuestionSubmitVO(questionSubmit, loginUser);
        return ResultUtils.success(questionSubmitVO);
    }


    /**
     * 分页获取管理题目列表
     *
     * @param questionQueryRequest 查询请求
     * @param request              请求
     * @return 分页管理题目列表
     */
    @PostMapping("/manage/question/list/page")
    public BaseResponse<Page<ManageQuestionVO>> listManageQuestionVOByPage(
            @RequestBody QuestionQueryRequest questionQueryRequest, HttpServletRequest request) {
        return ResultUtils.success(questionService.getQueryWrapperOfManageQuestionByPage(questionQueryRequest));
    }

    /**
     * 获取题库列表（下拉框）
     * @return 题库列表
     */
    @GetMapping("/manage/bank/list")
    public BaseResponse<List<ManageBankVO>> listManageBankVO() {
        List<BankVO> bankAll = bankService.getBankAll();
        return ResultUtils.success(BeanUtil.copyToList(bankAll, ManageBankVO.class));
    }
}
