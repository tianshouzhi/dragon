package com.tianshouzhi.dragon.sharding.pipeline.handler.sqlrewrite;

import com.tianshouzhi.dragon.sharding.route.LogicTable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *表示主维度表的路由参数:<br/>
 * 所谓主维度表，指一条sql中存在多个表，用于确定路由规则的那个表，其他表不能指定分区维度。例如:<br/>
 * SELECT user.id,user.name,user_account.account_no FROM user,user_account WHERE user.id=user_account.user_id AND user.id in (?,?,?,?);<br/>
 * 上例中假设user.id是分区维度，那么user_account则不能指定分区条件，但是可以指定分区之外的其他条件，例如加上条件:<br/>
 * user_account.money>0<br/>
 *
 * 在sql重写时，主维度表确定的分表，也是其他表对应的分表，例如user确定分表为user_0000，则user_account表对应的分表就是user_account_0000
 */
public class SqlRouteParams {//优化，只允许存在一个主维度表
    private LogicTable primaryLogicTable;
    /**二元操作符路由参数 例如 id=？*/
    private Map<String, Object> binaryRouteParamsMap = null;
    /**in 操作符路由参数，例如 id in(?,?,?)*/
    private Map<String, List<Object>> sqlInListRouteParamsMap = null;

    public void putBinaryRouteParams(LogicTable primaryLogicTable, String column, Object value){
        if(this.primaryLogicTable==null){
            this.primaryLogicTable=primaryLogicTable;
        }else{
            if(!primaryLogicTable.equals(primaryLogicTable)){//主维度表不匹配
                throw new IllegalArgumentException("primary logic table not match!!!");
            }
        }
        if(binaryRouteParamsMap ==null){
            binaryRouteParamsMap=new HashMap<String, Object>();
            binaryRouteParamsMap.put(column,value);
        }

    }
    public void putInListRouteParams(LogicTable primaryLogicTable,String column,List<Object> value){
        if(this.primaryLogicTable==null){
            this.primaryLogicTable=primaryLogicTable;
        }else{
            if(!primaryLogicTable.equals(primaryLogicTable)){//主维度表不匹配
                throw new IllegalArgumentException("primary logic table not match!!!");
            }
        }
        if(sqlInListRouteParamsMap ==null) {
            sqlInListRouteParamsMap = new HashMap<String, List<Object>>();
            sqlInListRouteParamsMap.put(column, value);
        }
    }

    public LogicTable getPrimaryLogicTable() {
        return primaryLogicTable;
    }

    public Map<String, Object> getBinaryRouteParamsMap() {
        return binaryRouteParamsMap;
    }

    public Map<String, List<Object>> getSqlInListRouteParamsMap() {
        return sqlInListRouteParamsMap;
    }
}
