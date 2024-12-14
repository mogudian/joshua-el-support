package com.mogudiandian.el.support.aviator;

import com.mogudiandian.el.support.ExpressionExecutor;
import com.googlecode.aviator.AviatorEvaluator;

import java.util.Map;

/**
 * Aviator脚本执行器
 *
 * @author joshua
 */
public final class AviatorScriptExecutor implements ExpressionExecutor {

    /**
     * 校验脚本 如果校验失败会抛出异常
     * @param scriptText 脚本
     */
    public void validate(String scriptText) {
        AviatorEvaluator.validate(scriptText);
    }

    /**
     * 编译脚本
     * @param scriptText 脚本
     */
    public void compile(String scriptText) {
        AviatorEvaluator.compile(scriptText, true);
    }

    /**
     * 执行脚本
     * @param expressionText 脚本
     * @param variables 变量
     * @return 执行后脚本的返回结果
     */
    @Override
    public Object execute(String expressionText, Map<String, Object> variables) {
        return AviatorEvaluator.execute(expressionText, variables, true);
    }

}
