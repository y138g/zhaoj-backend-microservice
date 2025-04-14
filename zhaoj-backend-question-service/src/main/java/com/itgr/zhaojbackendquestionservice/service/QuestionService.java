package com.itgr.zhaojbackendquestionservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itgr.zhaojbackendmodel.model.dto.question.QuestionQueryRequest;
import com.itgr.zhaojbackendmodel.model.entity.Question;
import com.itgr.zhaojbackendmodel.model.vo.ManageQuestionVO;
import com.itgr.zhaojbackendmodel.model.vo.QuestionVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author y138g
 * @description 针对表【question(题目)】的数据库操作Service
 * @createDate 2023-08-07 20:58:00
 */
public interface QuestionService extends IService<Question> {


    /**
     * 校验题目参数信息
     *
     * @param question 题目信息
     * @param add      是否为新建
     */
    void validQuestion(Question question, boolean add);

    /**
     * 获取查询包装类（用户根据哪些字段查询，根据前端传来的请求对象，得到 mybatis 框架支持的查询 QueryWrapper 类）
     *
     * @param questionQueryRequest 题目信息查询包装类
     * @return 返回题目实体包装类
     */
    QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest);

    /**
     * 获取题目管理分页
     *
     * @param questionQueryRequest 题目信息查询包装类
     * @return 返回题目管理分页
     */
    Page<ManageQuestionVO> getQueryWrapperOfManageQuestionByPage(QuestionQueryRequest questionQueryRequest);

    /**
     * 获取题目封装
     *
     * @param question 题目实体
     * @param request  请求
     * @return 题目封装
     */
    QuestionVO getQuestionVO(Question question, HttpServletRequest request);

    /**
     * 分页获取题目封装
     *
     * @param questionPage 题目分页
     * @param request      请求
     * @return 分页后的题目封装
     */
    Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request);

    /**
     * 根据题库 id 获取题目封装列表
     *
     * @param bankId  题库 id
     * @param request 请求
     * @return 题目封装列表
     */
    List<QuestionVO> getQuestionVOByBankId(long bankId, HttpServletRequest request);

}
