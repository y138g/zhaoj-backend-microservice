package com.itgr.zhaojbackendmodel.model.dto.question;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 编辑请求
 *
 * @author y138g
 * @from <a href="https://github.com/y138g">yg的开源仓库</a>
 */
@Data
public class QuestionEditRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 题目答案
     */
    private String answer;

    /**
     * 题库
     */
    private List<Long> bankIds;

    /**
     * 判题用例
     */
    private List<JudgeCase> judgeCase;

    /**
     * 判题配置
     */
    private JudgeConfig judgeConfig;

    private static final long serialVersionUID = 1L;
}
