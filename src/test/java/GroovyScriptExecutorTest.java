import com.mogudiandian.el.support.groovy.GroovyScriptExecutor;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试groovy脚本
 *
 * @author sunbo
 */
public class GroovyScriptExecutorTest {

    public static void main(String[] args) {

        char[] separators = new char[] {'+', '-', '*', '/', '%'};

        Map<String, Object> vars = new HashMap<>();
        vars.put("a", 1);
        vars.put("b", 2);

        GroovyScriptExecutor groovyScriptExecutor = new GroovyScriptExecutor();

        for (char separator : separators) {
            String script = "a " + separator + " b";

            for (int i = 0; i < 10; i++) {
                long l1 = System.nanoTime();
                System.out.println(groovyScriptExecutor.execute(script, vars));
                System.out.println(((System.nanoTime() - l1) / 1000000.00) + "ms");
            }
        }

    }

}
