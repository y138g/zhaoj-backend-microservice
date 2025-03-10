package com.itgr.zhaojbackendmodel.model.dto.bank;

import lombok.Data;

import java.io.Serializable;

/**
 * 题库新增请求
 *
 * @author y138g
 * @from <a href="https://github.com/y138g">yg的开源仓库</a>
 */
@Data
public class BankAddRequest implements Serializable {

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
