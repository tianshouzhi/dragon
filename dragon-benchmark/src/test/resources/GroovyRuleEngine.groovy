import com.tianshouzhi.dragon.benchmark.RuleEngine

class GroovyRuleEngine implements RuleEngine{

    @Override
    public Object eval(Map<String, Object> params) {
        String id =  params.get("id");
        return id.substring(id.length()-4).toLong().intdiv(100)%100;
    }
}