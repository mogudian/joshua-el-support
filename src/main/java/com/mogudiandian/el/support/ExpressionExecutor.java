package com.mogudiandian.el.support;

import com.alibaba.fastjson.util.TypeUtils;

import java.util.Map;

/**
 * 表达式执行器
 *
 * @author joshua
 */
public interface ExpressionExecutor {

    /**
     * 执行表达式
     * @param expressionText 表达式文本
     * @param variables 变量
     * @return 执行后脚本的返回结果
     */
    Object execute(String expressionText, Map<String, Object> variables);

    /**
     * 执行表达式
     * @param expressionText 表达式文本
     * @param variables 变量
     * @param resultType 结果类型
     * @param <T> 结果类型
     * @return 执行后脚本的返回结果
     */
    default <T> T execute(String expressionText, Map<String, Object> variables, Class<T> resultType) {
        return TypeUtils.cast(execute(expressionText, variables), resultType, null);
    }

}
