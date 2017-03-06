package com.tianshouzhi.dragon.sharding;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.util.JdbcConstants;
import org.apache.commons.collections.CollectionUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by TIANSHOUZHI336 on 2017/2/20.
 */
public class DragonSqlParserUtil {
    public void parse(String sql) throws SQLException {
        SQLStatementParser sqlStatementParser = SQLParserUtils.createSQLStatementParser(sql, JdbcConstants.MYSQL);
        List<SQLStatement> sqlStatements = sqlStatementParser.parseStatementList();
        if(sqlStatements.size()==1){
            SQLStatement sqlStatement = sqlStatements.get(0);
            if(sqlStatement instanceof SQLInsertStatement){
//                parseInsertInfo((SQLInsertStatement)sqlStatement,sql);
            }else if(sqlStatement instanceof SQLUpdateStatement){
                parseUpdateInfo((SQLUpdateStatement)sqlStatement);
            }else if(sqlStatement instanceof SQLDeleteStatement){
                parseDeleteInfo((SQLDeleteStatement)sqlStatement);
            }else if(sqlStatement instanceof SQLSelectStatement){
                parseSelectInfo((SQLSelectStatement)sqlStatement);
            }
        }else{

        }
    }
    private void parseInsertInfo(SQLInsertStatement insertStatement,String sql,List<String> partitionColumns) throws SQLException{
        String tableName = insertStatement.getTableName().getSimpleName();
        List<SQLExpr> columns = insertStatement.getColumns();
        // insert 没有带列名：insert into t values(xxx,xxx)
        if(CollectionUtils.isEmpty(columns)){
            throw new SQLException("insert sql("+sql+") must contains column names!!!");
        }else{
            StringBuilder sb=new StringBuilder();
            sb.append("insert into ")
                    .append(tableName);
            sb.append("(");
            //分区字段以及其在sql中的位置映射
            Map<String,Integer> patitionColumnIndexMap=new HashMap<String, Integer>();
            for(int i = 0; i < columns.size(); i++) {
                String columnName = columns.get(i).toString();
                if(i < columns.size() - 1)
                    sb.append(columnName).append(",");
                else
                    sb.append(columnName);
                if(partitionColumns.contains(columnName)){
                    patitionColumnIndexMap.put(columnName,i);
                }
            }
            if(patitionColumnIndexMap.isEmpty()){
                throw new SQLException("insert sql("+sql+") must contains partition column!!!");
            }
            sb.append(")");
            sb.append(" values");
            List<SQLInsertStatement.ValuesClause> valuesList = insertStatement.getValuesList();
            // 批量insert：insert into tab(id,name) values(1,'a'),(2,'b'),(3,'c');
            if(valuesList != null && valuesList.size() > 1){
                for(int j=0; j<valuesList.size(); j++){
                    List<SQLExpr> columnValues = valuesList.get(j).getValues();
                    if(j != valuesList.size() - 1)
                        appendValues(columnValues, sb).append(",");
                    else
                        appendValues(columnValues, sb);
                }
            }else{  // 非批量 insert:insert：insert into tab(id,name) values(1,'a');
                List<SQLExpr> valuse = insertStatement.getValues().getValues();
                appendValues(valuse, sb);
            }
            //on duplicate key update语法
            if(insertStatement instanceof MySqlInsertStatement){
                List<SQLExpr> duplicateKeyUpdate = ((MySqlInsertStatement)insertStatement).getDuplicateKeyUpdate();
                if(duplicateKeyUpdate != null && duplicateKeyUpdate.size() > 0){
                    sb.append(" on duplicate key update ");
                    for(int i=0; i<duplicateKeyUpdate.size(); i++){
                        SQLExpr exp = duplicateKeyUpdate.get(i);
                        if(exp != null){
                            if(i < duplicateKeyUpdate.size() - 1)
                                sb.append(exp.toString()).append(",");
                            else
                                sb.append(exp.toString());
                        }
                    }
                }
                System.out.println(sb);
            }
        }
    }
    private static StringBuilder appendValues(List<SQLExpr> valuse, StringBuilder sb){
        int size = valuse.size();
        sb.append("(");
        for(int i = 0; i < size; i++) {
            if(i < size - 1){
                sb.append(valuse.get(i).toString()).append(",");
            }else{
                sb.append(valuse.get(i).toString());
            }
        }
        return sb.append(")");
    }

    private void parseSelectInfo(SQLSelectStatement sqlStatement) {

    }

    private void parseDeleteInfo(SQLDeleteStatement sqlStatement) {

    }

    private void parseUpdateInfo(SQLUpdateStatement sqlStatement) {

    }


}
