package com.tianshouzhi.dragon.sharding.pipeline.handler;


import org.junit.Test;

/**
 * Created by TIANSHOUZHI336 on 2017/2/22.
 */
public class MysqlInsertStatementRewriterTest {
    @Test
    public void rewrite() throws Exception {
      /*  String sql="insert into user(id,name) values(?,?),(?,?),(?,?),(?,?),(?,?),(?,?),(?,?),(?,?)";
        SQLStatementParser sqlStatementParser = SQLParserUtils.createSQLStatementParser(sql, JdbcConstants.MYSQL);
        SQLStatement sqlStatement = sqlStatementParser.parseStatement();
        MysqlInsertStatementRewriter mysqlInsertStatementRewriter=new MysqlInsertStatementRewriter();
        DragonShardingPrepareStatement dragonShardingStatement=new DragonShardingPrepareStatement(null);
        dragonShardingStatement.setInt(1,10000);
        dragonShardingStatement.setString(2,"tianshouzhi");
        dragonShardingStatement.setInt(3,10000);
        dragonShardingStatement.setString(4,"wangxiaoxiao");
        dragonShardingStatement.setInt(5,10001);
        dragonShardingStatement.setString(6,"huhuamin");
        dragonShardingStatement.setInt(7,10001);
        dragonShardingStatement.setString(8,"wanghanao");
        dragonShardingStatement.setInt(9,10100);
        dragonShardingStatement.setString(10,"luyang");
        dragonShardingStatement.setInt(11,10100);
        dragonShardingStatement.setString(12,"chengkun");
        dragonShardingStatement.setInt(13,10101);
        dragonShardingStatement.setString(14,"tianhui");
        dragonShardingStatement.setInt(15,10101);
        dragonShardingStatement.setString(16,"tianmin");
//        HandlerContext handlerContext=new HandlerContext(handler, dragonShardingStatement);
       *//* LogicTable logicTable = new LogicTable();
        List<String> shardColumns = Arrays.asList("id");
        logicTable.setShardColumns(shardColumns);
        handlerContext.setLogicTable(logicTable);
        mysqlInsertStatementRewriter.rewrite((MySqlInsertStatement) sqlStatement,handlerContext);*/
    }
}