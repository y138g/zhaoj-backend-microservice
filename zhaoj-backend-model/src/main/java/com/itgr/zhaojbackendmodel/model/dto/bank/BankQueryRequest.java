package com.itgr.zhaojbackendmodel.model.dto.bank;

import com.itgr.zhaojbackendcommon.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 题库查询请求
 *
 * @author y138g
 * @from <a href="https://github.com/y138g">yg的开源仓库</a>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BankQueryRequest extends PageRequest implements Serializable {

    /**
     * 题库标题
     */
    private String title;

    private static final long serialVersionUID = 1L;

}
