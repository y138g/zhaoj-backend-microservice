package com.itgr.zhaojbackendquestionservice.controller.inner;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.itgr.zhaojbackendmodel.model.entity.Question;
import com.itgr.zhaojbackendmodel.model.entity.QuestionSubmit;
import com.itgr.zhaojbackendquestionservice.service.QuestionService;
import com.itgr.zhaojbackendquestionservice.service.QuestionSubmitService;
import com.itgr.zhaojbackendserviceclient.service.QuestionFeignClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 该服务仅内部调用，不是给前端的
 */
@RestController
@RequestMapping("/inner")
public class QuestionInnerController implements QuestionFeignClient {

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    /**
     * 根据题目 id 获取题目
     *
     * @param questionId 题目id
     * @return 获取的题目
     */
    @GetMapping("/get/id")
    @Override
    public Question getQuestionById(@RequestParam("questionId") long questionId) {
        return questionService.getById(questionId);
    }

    /**
     * 根据题目提交 id 获取题目提交信息
     *
     * @param questionSubmitId 题目提交 id
     * @return 题目提交信息
     */
    @GetMapping("/question_submit/get/id")
    @Override
    public QuestionSubmit getQuestionSubmitById(@RequestParam("questionId") long questionSubmitId) {
        return questionSubmitService.getById(questionSubmitId);
    }

    /**
     * 更新题目提交信息
     *
     * @param questionSubmit 题目提交信息
     * @return 返回结果
     */
    @PostMapping("/question_submit/update")
    @Override
    public boolean updateQuestionSubmitById(@RequestBody QuestionSubmit questionSubmit) {
        return questionSubmitService.updateById(questionSubmit);
    }

    /**
     * 更新题目通过数
     *
     * @param questionId 题目 id
     * @return 返回结果
     */
    @PostMapping("/question/updateAcceptedNum")
    @Override
    public boolean updateAcceptedNum(@RequestBody long questionId) {
        UpdateWrapper<Question> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", questionId).setSql("accepted = accepted + 1");
        return questionService.update(null, updateWrapper);
    }
}
