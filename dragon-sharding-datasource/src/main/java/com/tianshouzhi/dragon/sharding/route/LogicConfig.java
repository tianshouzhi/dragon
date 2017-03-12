package com.tianshouzhi.dragon.sharding.route;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by TIANSHOUZHI336 on 2017/2/23.
 */
public abstract class LogicConfig {
    //route rule中变量的命名规则
    public static final Pattern routeRuleVariablePattern =Pattern.compile("(\\$\\{.+?\\})",Pattern.CASE_INSENSITIVE);
    private String nameFormat;
    protected MessageFormat messageFormat;//eg table_{00}
    protected List<RouteRule> routeRuleList;//eg:${user_id}.toLong().intdiv(100)%100

    /**db 和tb 使用到的所有分区字段*/
    private Set<String> mergedShardColumns;


    public LogicConfig(String nameFormat, List<String> routeRuleStrList) {
        if(StringUtils.isBlank(nameFormat)){
            throw new RuntimeException("nameFormat can't be blank!!!");
        }
        if(CollectionUtils.isEmpty(routeRuleStrList)){
            throw new RuntimeException("tbRouteRuleStrList can't be empty!!!");
        }
        this.nameFormat=nameFormat;
        this.messageFormat = new MessageFormat(nameFormat);
        routeRuleList =new ArrayList<RouteRule>();
        for (String tbRuleStr : routeRuleStrList) {
            routeRuleList.add(new RouteRule(tbRuleStr));
        }
        mergedShardColumns=new HashSet<String>();
        for (RouteRule routeRule : routeRuleList) {
            mergedShardColumns.addAll(routeRule.shardColumns);
        }
    }

    public String getNameFormat(){
        return nameFormat;
    }

    protected String getRouteIndex(Map<String,Object> params) {
        if(params==null){
            throw new NullPointerException();
        }
        RouteRule selectedRouteRule=null;
        for (RouteRule routeRule : routeRuleList) {
            if(params.keySet().containsAll(routeRule.getShardColumns())){
                selectedRouteRule=routeRule;
                break;
            }
        }
        if(selectedRouteRule==null){
            throw new RuntimeException("no matched route rule found !!!");
        }

        String caculatedIndex = DragonGroovyEngine.eval(selectedRouteRule.getReplacedRouteRuleStr(), params).toString();
        return messageFormat.format(new Object[]{NumberUtils.toLong(caculatedIndex)});
    }

    public Set<String> getMergedShardColumns() {
        return mergedShardColumns;
    }

    protected static class RouteRule{
        private String originRouteRuleStr;//eg:${user_id}.toLong().intdiv(100)%100
        private String replacedRouteRuleStr;//eg:user_id.toLong().intdiv(100)%100
        private List<String> shardColumns;

        public RouteRule(String originRouteRuleStr) {
            if(StringUtils.isBlank(originRouteRuleStr)){
                throw new IllegalArgumentException("'originRouteRuleStr' can't be blank");
            }

            this.originRouteRuleStr = originRouteRuleStr;
            this.shardColumns = new ArrayList<String>();
            Matcher matcher = routeRuleVariablePattern.matcher(originRouteRuleStr);
            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                String varible = matcher.group(1);// 脚本中的变量名${xxx}
                String column = varible.substring(varible.indexOf("{")+1,varible.indexOf("}"));//变量名：xxx
                shardColumns.add(column);
                matcher.appendReplacement(sb,column);
            }
            if(CollectionUtils.isEmpty(shardColumns)){
                throw new IllegalArgumentException("'originRouteRuleStr' must contains shard column!!!");
            }
            matcher.appendTail(sb);
            this. replacedRouteRuleStr=sb.toString();
        }

        public String getOriginRouteRuleStr() {
            return originRouteRuleStr;
        }

        public String getReplacedRouteRuleStr() {
            return replacedRouteRuleStr;
        }

        public List<String> getShardColumns() {
            return shardColumns;
        }
    }
}
