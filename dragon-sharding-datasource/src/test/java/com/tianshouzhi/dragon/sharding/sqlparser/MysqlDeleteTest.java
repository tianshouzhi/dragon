package com.tianshouzhi.dragon.sharding.sqlparser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;

/**
 * Created by TIANSHOUZHI336 on 2017/2/28.
 */
public class MysqlDeleteTest {
   @Test
    public void testParseDelete(){
       String sql="select max(id),address from user group by address";
       SQLStatementParser sqlStatementParser = SQLParserUtils.createSQLStatementParser(sql, JdbcConstants.MYSQL);
       SQLSelectStatement sqlStatement = (SQLSelectStatement) sqlStatementParser.parseStatement();
       MySqlSelectQueryBlock query = (MySqlSelectQueryBlock) sqlStatement.getSelect().getQuery();
       List<SQLSelectItem> selectList = query.getSelectList();
       for (SQLSelectItem sqlSelectItem : selectList) {
           SQLExpr expr = sqlSelectItem.getExpr();
           HashSet<String> supportAggregateFunctions = new HashSet<String>();
           supportAggregateFunctions.add("MAX");
           supportAggregateFunctions.add("MIN");
           supportAggregateFunctions.add("COUNT");
           SQLSelectGroupByClause groupBy = query.getGroupBy();
           if(expr instanceof SQLAggregateExpr){
               String methodName = ((SQLAggregateExpr) expr).getMethodName();
               if(!supportAggregateFunctions.contains(methodName)){
                   throw new UnsupportedOperationException("use unsupport function:"+methodName+",sql:");
               }
               List<SQLExpr> arguments = ((SQLAggregateExpr) expr).getArguments();
               SQLExpr sqlExpr = arguments.get(0);//这些函数都只支持一个参数
               SQLOrderBy withinGroup = ((SQLAggregateExpr) expr).getWithinGroup();
//               option.
           }
       }
       System.out.println("selectList = " + selectList);
   }
}
