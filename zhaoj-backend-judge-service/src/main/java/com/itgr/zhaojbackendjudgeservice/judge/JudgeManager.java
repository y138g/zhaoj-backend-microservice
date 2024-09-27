package com.itgr.zhaojbackendjudgeservice.judge;

import com.itgr.zhaojbackendjudgeservice.judge.strategy.DefaultJudgeStrategy;
import com.itgr.zhaojbackendjudgeservice.judge.strategy.JavaLanguageJudgeStrategy;
import com.itgr.zhaojbackendjudgeservice.judge.strategy.JudgeContext;
import com.itgr.zhaojbackendjudgeservice.judge.strategy.JudgeStrategy;
import com.itgr.zhaojbackendmodel.model.codesandbox.JudgeInfo;
import com.itgr.zhaojbackendmodel.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

/**
 * 判题管理（简化调用）
 */
@Service
public class JudgeManager {

    /**
     * 执行判题
     *
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext) {
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if ("java".equals(language)) {
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }

}
