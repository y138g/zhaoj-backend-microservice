package com.itgr.zhaojbackendcommon.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * SQL 工具
 *
 * @author y138g
 * @from <a href="https://github.com/y138g">yg的开源仓库</a>
 */
public class SqlUtils {

    /**
     * 校验排序字段是否合法（防止 SQL 注入）
     *
     * @param sortField
     * @return
     */
    public static boolean validSortField(String sortField) {
        if (StringUtils.isBlank(sortField)) {
            return false;
        }
        return !StringUtils.containsAny(sortField, "=", "(", ")", " ");
    }
}
