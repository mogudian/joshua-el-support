package com.mogudiandian.el.support.mvel;

import com.mogudiandian.el.support.ExpressionExecutor;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRuntime;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MVEL模板执行器
 *
 * @author joshua
 */
public final class MvelTemplateExecutor implements ExpressionExecutor {

    /**
     * 脚本编译后的类缓存
     */
    private static final Map<String, CompiledTemplate> compiledTemplateMap = new ConcurrentHashMap<>();

    /**
     * 编译脚本 如果已编译则不进行编译 用来进行预编译
     * @param scriptText 脚本
     */
    public void compile(String scriptText) {
        getOrCompile(scriptText);
    }

    /**
     * 获取已编译的脚本或编译
     * @param scriptText 脚本
     * @return 编译后的对象
     */
    private CompiledTemplate getOrCompile(String scriptText) {
        CompiledTemplate compiledTemplate = compiledTemplateMap.get(scriptText);
        if (compiledTemplate == null) {
            compiledTemplate = compiledTemplateMap.computeIfAbsent(scriptText, TemplateCompiler::compileTemplate);
        }
        return compiledTemplate;
    }

    /**
     * 执行脚本
     * @param expressionText 脚本
     * @param variables 变量
     * @return 执行后脚本的返回结果
     */
    @Override
    public Object execute(String expressionText, Map<String, Object> variables) {
        return TemplateRuntime.execute(getOrCompile(expressionText), variables);
    }

}
