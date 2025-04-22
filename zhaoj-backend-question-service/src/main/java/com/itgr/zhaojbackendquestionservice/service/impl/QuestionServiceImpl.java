package com.itgr.zhaojbackendquestionservice.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itgr.zhaojbackendcommon.common.ErrorCode;
import com.itgr.zhaojbackendcommon.constant.CommonConstant;
import com.itgr.zhaojbackendcommon.exception.BusinessException;
import com.itgr.zhaojbackendcommon.exception.ThrowUtils;
import com.itgr.zhaojbackendcommon.utils.SqlUtils;
import com.itgr.zhaojbackendmodel.model.dto.question.QuestionQueryRequest;
import com.itgr.zhaojbackendmodel.model.entity.Bank;
import com.itgr.zhaojbackendmodel.model.entity.BankQuestion;
import com.itgr.zhaojbackendmodel.model.entity.Question;
import com.itgr.zhaojbackendmodel.model.entity.User;
import com.itgr.zhaojbackendmodel.model.vo.ManageQuestionVO;
import com.itgr.zhaojbackendmodel.model.vo.QuestionVO;
import com.itgr.zhaojbackendmodel.model.vo.UserVO;
import com.itgr.zhaojbackendquestionservice.mapper.BankQuestionMapper;
import com.itgr.zhaojbackendquestionservice.mapper.QuestionMapper;
import com.itgr.zhaojbackendquestionservice.service.QuestionService;
import com.itgr.zhaojbackendquestionservice.mapper.BankMapper;
import com.itgr.zhaojbackendserviceclient.service.UserFeignClient;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author y138g
 * @description 针对表【question(题目)】的数据库操作Service实现
 * @createDate 2023-08-07 20:58:00
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
        implements QuestionService {


    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private BankQuestionMapper bankQuestionMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private BankMapper bankMapper;

    @Override
    public void validQuestion(Question question, boolean add) {
        if (question == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String title = question.getTitle();
        String content = question.getContent();
        String tags = question.getTags();
        String answer = question.getAnswer();
        String judgeCase = question.getJudgeCase();
        String judgeConfig = question.getJudgeConfig();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(title, content, tags), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(title) && title.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
        if (StringUtils.isNotBlank(answer) && answer.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "答案过长");
        }
        if (StringUtils.isNotBlank(judgeCase) && judgeCase.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题用例过长");
        }
        if (StringUtils.isNotBlank(judgeConfig) && judgeConfig.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题配置过长");
        }
    }

    @Override
    public QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        if (questionQueryRequest == null) {
            return queryWrapper;
        }
        Long id = questionQueryRequest.getId();
        String title = questionQueryRequest.getTitle();
        String content = questionQueryRequest.getContent();
        List<String> tags = questionQueryRequest.getTags();
        String answer = questionQueryRequest.getAnswer();
        Long userId = questionQueryRequest.getUserId();
        String sortField = questionQueryRequest.getSortField();
        String sortOrder = questionQueryRequest.getSortOrder();

        // 拼接查询条件
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        queryWrapper.like(StringUtils.isNotBlank(answer), "answer", answer);
        if (CollectionUtils.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public Page<ManageQuestionVO> getQueryWrapperOfManageQuestionByPage(
            QuestionQueryRequest questionQueryRequest) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();

        // 构建查询条件（保持不变）
        Long id = questionQueryRequest.getId();
        String title = questionQueryRequest.getTitle();
        List<Long> bankIds = questionQueryRequest.getBankIds();
        List<String> tags = questionQueryRequest.getTags();
        Integer difficulty = questionQueryRequest.getDifficulty();
        String sortField = questionQueryRequest.getSortField();
        String sortOrder = questionQueryRequest.getSortOrder();

        List<Long> ids = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(bankIds)) {
            QueryWrapper<BankQuestion> bankQuestionQueryWrapper = new QueryWrapper<>();
            bankQuestionQueryWrapper.in("bankId", bankIds);
            List<Object> questionIds = bankQuestionMapper
                    .selectObjs(bankQuestionQueryWrapper.select("questionId"));
            ids = questionIds.stream().map(questionId -> (Long) questionId).collect(Collectors.toList());
        }

        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.in(ObjectUtils.isNotEmpty(ids), "id", ids);
        queryWrapper.eq(ObjectUtils.isNotEmpty(difficulty), "difficulty", difficulty);
        if (CollectionUtils.isNotEmpty(tags)) {
            tags.forEach(tag -> queryWrapper.like("tags", "\"" + tag + "\""));
        }
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);

        // 使用MyBatis-Plus分页查询
        Page<Question> questionPage = new Page<>(questionQueryRequest.getCurrent(), questionQueryRequest.getPageSize());
        questionMapper.selectPage(questionPage, queryWrapper);

        List<ManageQuestionVO> manageQuestionVOS = BeanUtil
                .copyToList(questionPage.getRecords(), ManageQuestionVO.class);

        // 批量处理题库名称查询
        if (!manageQuestionVOS.isEmpty()) {
            List<Long> questionIds = manageQuestionVOS.stream()
                    .map(ManageQuestionVO::getId)
                    .collect(Collectors.toList());

            // 批量查询bankId
            QueryWrapper<BankQuestion> bankQuestionQueryWrapper = new QueryWrapper<>();
            bankQuestionQueryWrapper.in("questionId", questionIds);
            List<BankQuestion> bankQuestions = bankQuestionMapper.selectList(bankQuestionQueryWrapper);
            Map<Long, List<Long>> questionToBankIds = bankQuestions.stream()
                    .collect(Collectors.groupingBy(BankQuestion::getQuestionId,
                            Collectors.mapping(BankQuestion::getBankId, Collectors.toList())));

            // 批量查询bankTitle
            Set<Long> uniqueBankIds = bankQuestions.stream()
                    .map(BankQuestion::getBankId)
                    .collect(Collectors.toSet());
            Map<Long, String> bankIdToTitle;
            if (!uniqueBankIds.isEmpty()) {
                List<Bank> banks = bankMapper.selectBatchIds(uniqueBankIds);
                bankIdToTitle = banks.stream()
                        .collect(Collectors.toMap(Bank::getId, Bank::getTitle));
            } else {
                bankIdToTitle = new HashMap<>();
            }

            // 设置bankTitle
            manageQuestionVOS.forEach(vo -> {
                List<Long> voBankIds = questionToBankIds.getOrDefault(vo.getId(), Collections.emptyList());
                List<String> titles = voBankIds.stream()
                        .map(bankIdToTitle::get)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                vo.setBankTitle(titles);
            });
        }

        // 构建分页结果
        Page<ManageQuestionVO> resultPage = new Page<>();
        resultPage.setRecords(manageQuestionVOS);
        resultPage.setTotal(questionPage.getTotal());
        resultPage.setCurrent(questionPage.getCurrent());
        resultPage.setSize(questionPage.getSize());
        resultPage.setPages(questionPage.getPages());

        return resultPage;
    }

    @Override
    public QuestionVO getQuestionVO(Question question, HttpServletRequest request) {
        QuestionVO questionVO = QuestionVO.objToVo(question);
        // 1. 关联查询用户信息
        Long userId = question.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userFeignClient.getById(userId);
        }
        UserVO userVO = userFeignClient.getUserVO(user);
        questionVO.setUserVO(userVO);
        return questionVO;
    }

    @Override
    public Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request) {
        List<Question> questionList = questionPage.getRecords();
        Page<QuestionVO> questionVOPage = new Page<>(questionPage.getCurrent(), questionPage.getSize(),
                questionPage.getTotal());
        if (CollectionUtils.isEmpty(questionList)) {
            return questionVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = questionList.stream().map(Question::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userFeignClient.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        List<QuestionVO> questionVOList = questionList.stream().map(question -> {
            QuestionVO questionVO = QuestionVO.objToVo(question);
            Long userId = question.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionVO.setUserVO(userFeignClient.getUserVO(user));
            return questionVO;
        }).collect(Collectors.toList());
        questionVOPage.setRecords(questionVOList);
        return questionVOPage;
    }

    @Override
    public List<QuestionVO> getQuestionVOByBankId(long bankId, HttpServletRequest request) {
        // 根据bankId查询题库题目关联表
        QueryWrapper<BankQuestion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjectUtils.isNotEmpty(bankId), "bankId", bankId);
        List<BankQuestion> bankQuestionList = bankQuestionMapper.selectList(queryWrapper);
        // 找出所有questionId
        List<Long> questionIds = bankQuestionList.stream()
                .map(BankQuestion::getQuestionId).collect(Collectors.toList());
        // 再根据questionId获取所有题目
        return questionIds.stream()
                .map(questionId -> QuestionVO.objToVo(this.getById(questionId))).collect(Collectors.toList());
    }


}




