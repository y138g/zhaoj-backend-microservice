package com.itgr.zhaojbackendmodel.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 题库视图（仅用于选择题库）
 */
@Data
public class ManageBankVO implements Serializable {

    /**
     * 题库id
     */
    private Long id;

    /**
     * 题库标题
     */
    private String title;

    private static final long serialVersionUID = 1L;

}
