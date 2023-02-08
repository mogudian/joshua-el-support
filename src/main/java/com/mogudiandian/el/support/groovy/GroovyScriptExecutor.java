package com.mogudiandian.el.support.groovy;

import com.google.common.collect.ImmutableMap;
import com.mogudiandian.el.support.ExpressionExecutor;
import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.Script;
import groovy.transform.ThreadInterrupt;
import groovy.transform.TimedInterrupt;
import lombok.SneakyThrows;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.codehaus.groovy.control.customizers.SecureASTCustomizer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Groovy脚本执行器
 * @author sunbo
 */
public final class GroovyScriptExecutor implements ExpressionExecutor {

    /**
     * 脚本编译后的类缓存
     */
    private static final Map<String, Class<?>> classMap = new ConcurrentHashMap<>();

    /**
     * 自定义的编译器配置
     */
    private static final CompilerConfiguration compilerConfiguration = new CompilerConfiguration();

    /**
     * 默认的导入
     */
    private static final List<String> defaultImports = new ArrayList<>();

    static {
        defaultImports.add("java.util.*");
        defaultImports.add("com.alibaba.fastjson.*");
        defaultImports.add("org.apache.commons.lang3.StringUtils");
        defaultImports.add("org.apache.commons.collections4.CollectionUtils");
        defaultImports.add("groovy.json.*");
    }

    static {
        // 安全的定制器
        SecureASTCustomizer secureASTCustomizer = new SecureASTCustomizer();
        // 允许闭包
        secureASTCustomizer.setClosuresAllowed(true);
        // 拦截非法关闭JVM
        secureASTCustomizer.addExpressionCheckers(x -> {
            if (x instanceof MethodCallExpression) {
                MethodCallExpression call = (MethodCallExpression) x;
                if ("exit".equals(call.getMethod().getText())) {
                    if (call.getObjectExpression() instanceof ClassExpression) {
                        if (System.class.getName().equals(call.getObjectExpression().getText())) {
                            // 这里拦截 System.exit(xxx);
                            return false;
                        }
                    } else if (call.getObjectExpression() instanceof MethodCallExpression) {
                        call = (MethodCallExpression) call.getObjectExpression();
                        if (call.getObjectExpression() instanceof ClassExpression && Runtime.class.getName().equals(call.getObjectExpression().getText())) {
                            if ("getRuntime".equals(call.getMethodAsString())) {
                                // 这里拦截 Runtime.getRuntime().exit(xxx);
                                return false;
                            }
                        }
                    } else if (call.getObjectExpression() instanceof VariableExpression) {
                        // TODO 这里本来想拦截 ((Runtime) rt).exit(xxx); 这种的，但是目前只能获取到变量名，无法获取类型是否为Runtime
                        //      先简单写了，只要变量名叫runtime就拦截，应该不会误杀
                        if (Runtime.class.getSimpleName().equalsIgnoreCase(call.getObjectExpression().getText())) {
                            return false;
                        }
                    }
                    return false;
                }
            }
            return true;
        });

        compilerConfiguration.addCompilationCustomizers(secureASTCustomizer);

        // 超时 脚本运行5秒自动结束
        compilerConfiguration.addCompilationCustomizers(new ASTTransformationCustomizer(ThreadInterrupt.class));
        compilerConfiguration.addCompilationCustomizers(new ASTTransformationCustomizer(ImmutableMap.of("value", 5L), TimedInterrupt.class));

        // 默认导入的工具
        ImportCustomizer importCustomizer = new ImportCustomizer();
        for (String anImport : defaultImports) {
            if (anImport.contains("*")) {
                importCustomizer.addStarImports(anImport.replace("*", ""));
            } else {
                importCustomizer.addImports(anImport);
            }
        }
        compilerConfiguration.addCompilationCustomizers(importCustomizer);
    }

    /**
     * 编译脚本
     * @param scriptText 脚本
     * @return 编译后的类
     */
    private Class<?> compile0(String scriptText) {
        // 需要当前的classloader来加载JVM已经加载的类
        ClassLoader currentClassLoader = Optional.ofNullable(Thread.currentThread().getContextClassLoader())
                                                 .orElse(GroovyScriptExecutor.class.getClassLoader());

        // groovy的类加载器
        GroovyClassLoader groovyLoader = new GroovyClassLoader(currentClassLoader, compilerConfiguration);

        return groovyLoader.parseClass(scriptText);
    }

    /**
     * 编译脚本 如果已编译则不进行编译 用来进行预编译
     * @param scriptText 脚本
     */
    public void compile(String scriptText) {
        if (classMap.get(scriptText) != null) {
            return;
        }
        classMap.computeIfAbsent(scriptText, this::compile0);
    }

    /**
     * 获取已编译的脚本或编译
     * @param scriptText 脚本
     * @return 编译后的类
     */
    private Class<?> getOrCompile(String scriptText) {
        Class<?> clazz = classMap.get(scriptText);
        if (clazz == null) {
            clazz = classMap.computeIfAbsent(scriptText, this::compile0);
        }
        return clazz;
    }

    /**
     * 执行脚本
     * @param expressionText 脚本
     * @param variables 变量
     * @return 执行后脚本的返回结果
     */
    @Override
    @SneakyThrows
    public Object execute(String expressionText, Map<String, Object> variables) {
        if (StringUtils.isBlank(expressionText)) {
            return null;
        }

        Class<?> clazz = getOrCompile(expressionText);
        Script script = (Script) clazz.newInstance();
        Binding binding = new Binding();
        if (MapUtils.isNotEmpty(variables)) {
            variables.forEach(binding::setVariable);
        }
        script.setBinding(binding);
        return script.run();
    }

}
