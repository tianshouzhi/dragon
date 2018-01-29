package com.tianshouzhi.dragon.shard.route;

import com.tianshouzhi.dragon.shard.route.clazz.JavaStringCompiler;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by tianshouzhi on 2018/1/29.
 */
public class JavaStringCompilerTest {

    JavaStringCompiler compiler;

    @Before
    public void setUp() throws Exception {
        compiler = new JavaStringCompiler();
    }

    static final String RULE_ENGINE =
            "  package com.tianshouzhi.dragon.shard.route;\n                                            "
            + "import com.tianshouzhi.dragon.shard.route.*;" +
               "import java.util.*;\n                            "
            + "public class CustomRuleEngine implements RuleEngine {\n "
            +"    public Object eval(Map<String,Object> params){\n"       +
            "      return ((Integer)params.get(\"id\"))%10;\n" +
                  "}"
            + "}";

    @Test
    public void testRuleEngine() throws Exception {
        Map<String, byte[]> compile = compiler.compile("CustomRuleEngine.java", RULE_ENGINE);
        Class<?> singleClass = compiler.loadClass("com.tianshouzhi.dragon.shard.route.CustomRuleEngine", compile);
        RuleEngine ruleEngine = (RuleEngine) singleClass.newInstance();
        HashMap<String, Object> params = new HashMap<>();
        params.put("id",5);
        System.out.println(ruleEngine.eval(params));
    }

    static final String SINGLE_JAVA = "/* a single java class to one file */  "
            + "package com.tianshouzhi.dragon.shard.route;                                            "
            + "import com.tianshouzhi.dragon.shard.route.*;                            "
            + "public class UserProxy extends User implements BeanProxy {     "
            + "    boolean _dirty = false;                                    "
            + "    public void setId(String id) {                             "
            + "        super.setId(id);                                       "
            + "        setDirty(true);                                        "
            + "    }                                                          "
            + "    public void setName(String name) {                         "
            + "        super.setName(name);                                   "
            + "        setDirty(true);                                        "
            + "    }                                                          "
            + "    public void setCreated(long created) {                     "
            + "        super.setCreated(created);                             "
            + "        setDirty(true);                                        "
            + "    }                                                          "
            + "    public void setDirty(boolean dirty) {                      "
            + "        this._dirty = dirty;                                   "
            + "    }                                                          "
            + "    public boolean isDirty() {                                 "
            + "        return this._dirty;                                    "
            + "    }                                                          "
            + "}                                                              ";

    @Test
    public void testCompileSingleClass() throws Exception {
        Map<String, byte[]> results = compiler.compile("UserProxy.java", SINGLE_JAVA);
        assertEquals(1, results.size());
        assertTrue(results.containsKey("com.tianshouzhi.dragon.shard.route.UserProxy"));
        Class<?> clazz = compiler.loadClass("com.tianshouzhi.dragon.shard.route.UserProxy", results);
        // get method:
        Method setId = clazz.getMethod("setId", String.class);
        Method setName = clazz.getMethod("setName", String.class);
        Method setCreated = clazz.getMethod("setCreated", long.class);
        // try instance:
        Object obj = clazz.newInstance();
        // get as proxy:
        BeanProxy proxy = (BeanProxy) obj;
        assertFalse(proxy.isDirty());
        // set:
        setId.invoke(obj, "A-123");
        setName.invoke(obj, "Fly");
        setCreated.invoke(obj, 123000999);
        // get as user:
        User user = (User) obj;
        assertEquals("A-123", user.getId());
        assertEquals("Fly", user.getName());
        assertEquals(123000999, user.getCreated());
        assertTrue(proxy.isDirty());
    }

    static final String MULTIPLE_JAVA = "/* a single class to many files */   "
            + "package com.tianshouzhi.dragon.shard.route;                                            "
            + "import java.util.*;                                            "
            + "public class Multiple {                                        "
            + "    List<Bird> list = new ArrayList<Bird>();                   "
            + "    public void add(String name) {                             "
            + "        Bird bird = new Bird();                                "
            + "        bird.name = name;                                      "
            + "        this.list.add(bird);                                   "
            + "    }                                                          "
            + "    public Bird getFirstBird() {                               "
            + "        return this.list.get(0);                               "
            + "    }                                                          "
            + "    public static class StaticBird {                           "
            + "        public int weight = 100;                               "
            + "    }                                                          "
            + "    class NestedBird {                                         "
            + "        NestedBird() {                                         "
            + "            System.out.println(list.size() + \" birds...\");   "
            + "        }                                                      "
            + "    }                                                          "
            + "}                                                              "
            + "/* package level */                                            "
            + "class Bird {                                                   "
            + "    String name = null;                                        "
            + "}                                                              ";

    @Test
    public void testCompileMultipleClasses() throws Exception {
        Map<String, byte[]> results = compiler.compile("Multiple.java", MULTIPLE_JAVA);
        assertEquals(4, results.size());
        assertTrue(results.containsKey("com.tianshouzhi.dragon.shard.route.Multiple"));
        assertTrue(results.containsKey("com.tianshouzhi.dragon.shard.route.Multiple$StaticBird"));
        assertTrue(results.containsKey("com.tianshouzhi.dragon.shard.route.Multiple$NestedBird"));
        assertTrue(results.containsKey("com.tianshouzhi.dragon.shard.route.Bird"));
        Class<?> clzMul = compiler.loadClass("com.tianshouzhi.dragon.shard.route.Multiple", results);
        // try instance:
        Object obj = clzMul.newInstance();
        assertNotNull(obj);
    }
}
