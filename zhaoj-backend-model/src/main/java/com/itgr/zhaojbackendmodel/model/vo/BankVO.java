package com.itgr.zhaojbackendmodel.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 题库视图
 */
@Data
public class BankVO implements Serializable {

    /**
     * 题库id
     */
    private Long id;

    /**
     * 题库题目数量
     */
    private Integer questionNum;

    /**
     * 题库标题
     */
    private String title;

    /**
     * 题库描述
     */
    private String content;

    private static final long serialVersionUID = 1L;

}
