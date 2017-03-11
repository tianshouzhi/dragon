package com.tianshouzhi.dragon.ha.jdbc.statement;

import com.tianshouzhi.dragon.common.exception.DragonException;
import com.tianshouzhi.dragon.common.exception.ExceptionSorter;
import com.tianshouzhi.dragon.common.jdbc.statement.DragonStatement;
import com.tianshouzhi.dragon.common.util.SqlTypeUtil;
import com.tianshouzhi.dragon.ha.jdbc.connection.DragonHAConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import static com.tianshouzhi.dragon.common.jdbc.statement.DragonStatement.ExecuteType.EXECUTE_BATCH;

/**
 * Created by TIANSHOUZHI336 on 2016/12/3.
 */
public class DragonHAStatement extends DragonStatement implements Statement {
    private static final Logger LOGGER= LoggerFactory.getLogger(DragonHAStatement.class);
    protected DragonHAConnection dragonHAConnection;
    protected Statement realStatement;
    public DragonHAStatement(DragonHAConnection dragonConnection) {
        super();
        this.dragonHAConnection=dragonConnection;
    }

    public DragonHAStatement(Integer resultSetType, Integer resultSetConcurrency, DragonHAConnection dragonConnection) {
        super(resultSetType, resultSetConcurrency);
        this.dragonHAConnection=dragonConnection;
    }

    public DragonHAStatement(Integer resultSetType, Integer resultSetConcurrency, Integer resultSetHoldability, DragonHAConnection dragonConnection){
        super(resultSetType, resultSetConcurrency, resultSetHoldability);
        this.dragonHAConnection=dragonConnection;
    }

    //只有单条sql的时候，应该调用这个方法，batch和callablestatemnt自行处理
    public boolean doExecute() throws SQLException {
        if(resultSet!=null){//jdbc规范规定，每次执行的时候，如果当前ResultSet不为空，需要显示关闭，因此最好一个Statement执行一个sql
            resultSet.close();
        }
        int errorCount = 0;
        boolean isResultSet = false;
        int maxRetryTimes = 3;
        Set<String> excludes = null;
        for (int i = 0; i < maxRetryTimes; i++) {
            Connection realConnection=null;
            try {
                if(executeType !=EXECUTE_BATCH){
                    realConnection = dragonHAConnection.getRealConnection(sql,useSqlTypeCache());
                }else{//批处理操作，没有sql可以判断
                    realConnection= dragonHAConnection.buildNewWriteConnectionIfNeed();
                }
                createRealStatement(realConnection);
                isResultSet = doExecuteByType();
                setExecuteResult(isResultSet);
                return isResultSet; //正常执行完成，跳出循环，不进行重试
            } catch (SQLException e) {
                //出现异常
                String dataSourceIndex = dragonHAConnection.getCurrentDBIndex();
                ExceptionSorter exceptionSorter=dragonHAConnection.getExceptionSorter();
                if (exceptionSorter.isExceptionFatal(e)) {//如果是致命异常
                    LOGGER.error("fatal exception,sqlstate:{},error code:{},sql:{}", e.getSQLState(), e.getErrorCode(), sql);
                    dragonHAConnection.getHAConnectionManager().invalid(dataSourceIndex);
                    throw e;
                } else {
                    //不是致命异常,没有开启事务，其sql是查询，重试
                    if (failRetry()) {
                        LOGGER.warn("query failed for the " + (errorCount++) + "st try,sql state:{},error code:{},sql is:'{}'", e.getSQLState(), e.getErrorCode(), sql);
                        if (errorCount == maxRetryTimes) {
                            throw new SQLException("query failed after try 3 times,sql is:" + sql, e);
                        }
                        if(excludes==null){
                            excludes=new HashSet<String>();
                        }
                        excludes.add(dataSourceIndex);
                        //选择一个新的数据源，创建connection，并且重新创建statement
                        Connection newConnection=dragonHAConnection.buildNewReadConnectionExclue(excludes);
                        if(newConnection==null){
                            LOGGER.error("no more datasource can be used to retry,have tried:{}", excludes);
                            throw e;
                        }
                        createRealStatement(newConnection);
                    } else {//如果开启了事务，或者是写操作，或者是致命异常，不重试
                        throw new DragonException("sql '" + sql + "' execute fail, no retry", e);
                    }
                }
            }
        }
        return isResultSet;
    }
    protected void createRealStatement(Connection realConnection) throws SQLException {
        switch (createType) {
            case NONE:
                realStatement = realConnection.createStatement();
                break;
            case RESULTSET_TYPE_CONCURRENCY:
                realStatement = realConnection.createStatement(resultSetType, resultSetConcurrency);
                break;
            case RESULTSET_TYPE_CONCURRENCY_HOLDABILITY:
                realStatement = realConnection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
                break;
        }
        setStatementParams(realStatement);
    }
    protected void setStatementParams(Statement realStatement) throws SQLException {
        realStatement.setQueryTimeout(queryTimeout);
        realStatement.setFetchSize(fetchSize);
        realStatement.setFetchSize(fetchDirection);
        realStatement.setPoolable(poolable);
        realStatement.setMaxFieldSize(maxFieldSize);
        realStatement.setMaxRows(maxRows);
        realStatement.setEscapeProcessing(enableEscapeProcessing);
        //如果是批处理，需要设置sql
        if(executeType == ExecuteType.EXECUTE_BATCH&&batchExecuteInfoList.size()>0){
            setBatchExecuteParams();
        }
    }

    protected void setBatchExecuteParams() throws SQLException {
        for (Object o : batchExecuteInfoList) {
            if(o instanceof String){
                realStatement.addBatch((String) o);
            }
        }
    }


    /**
     * 判断是否要使用sqlTypeCache，默认不使用cache，因为缓存的key是sql，对于Statament而言，sql的参数是直接是写在语句中的，无法命中的可能性大
     * Preparement应该对此进行覆盖，因为preparent对参数进行了剥离
     * @return
     */
    protected boolean useSqlTypeCache() {
        return false;
    }

    /**
     * 是否需要失败重试
     * @return
     * @throws SQLException
     */
    protected boolean failRetry() throws SQLException {
        return dragonHAConnection.getCurrentRealConnection().getAutoCommit() == true
                && executeType !=EXECUTE_BATCH //batchexecute的情况下，不判断sql
               && SqlTypeUtil.isQuery(sql,useSqlTypeCache());
    }

    protected void setExecuteResult(boolean isResultSet) throws SQLException {
        if(isResultSet){
            this.resultSet = realStatement.getResultSet();
        }else {
            this.updateCount = realStatement.getUpdateCount();
            if(Statement.RETURN_GENERATED_KEYS==autoGeneratedKeys){
                  this.generatedKeys = realStatement.getGeneratedKeys();
            }
        }
    }

    protected boolean doExecuteByType() throws SQLException {
        boolean isResultSet=false;
        switch (executeType) {
            case EXECUTE_QUERY:
                realStatement.executeQuery(sql);
                isResultSet = true;
                break;
            case EXECUTE_UPDATE:
                realStatement.executeUpdate(sql);
                break;
            case EXECUTE_UPDATE_WITH_AUTOGENERATEDKEYS:
                realStatement.executeUpdate(sql, autoGeneratedKeys);
                break;
            case EXECUTE_UPDATE_WITH_COLUMNINDEXES:
                realStatement.executeUpdate(sql, columnIndexes);
                break;
            case EXECUTE_UPDATE_WITH_COLUMNNAMES:
                realStatement.executeUpdate(sql, columnNames);
                break;
            case EXECUTE:
                isResultSet = realStatement.execute(sql);
                break;
            case EXECUTE_WITH_AUTOGENERATEDKEYS:
                isResultSet = realStatement.execute(sql, autoGeneratedKeys);
                break;
            case EXECUTE_WITH_COLUMNINDEXES:
                isResultSet = realStatement.execute(sql, columnIndexes);
                break;
            case EXECUTE_WITH_COLUMNNAMES:
                isResultSet = realStatement.execute(sql, columnNames);
                break;
            case EXECUTE_BATCH:
                batchExecuteResult= realStatement.executeBatch();
                break;
            default:
                throw new DragonException("unkown excute type "+executeType+",sql is "+sql);
        }
        return isResultSet;
    }

    //============================批处理操作，JDBC规范规定只能是更新语句，因此总是获取写connection==========
    @Override
    public void clearBatch() throws SQLException {
        checkClosed();
        batchExecuteInfoList.clear();
        if(realStatement!=null){
            realStatement.clearBatch();
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        checkClosed();
        return dragonHAConnection;
    }

    /**
     * JDBC规范规定，关闭statement的时候，需要关闭当前resultSet
     * @throws SQLException
     */
    @Override
    public void close() throws SQLException {
        if(resultSet!=null){
            resultSet.close();
            resultSet=null;
        }
        if(generatedKeys!=null){
            generatedKeys.close();
            generatedKeys=null;
        }
        if(realStatement!=null){
            realStatement.close();
            realStatement=null;
        }
        isClosed = true;
    }

    /**
     * 如果执行的是存储过程的话，并且其中有多个查询语句，那么可能会返回多个ResultSet，JDBC规范规定，默认只需要返回第一个
     * 如果需要获取更多的ResultSet，通过getMoreResults来判断
     * @return
     * @throws SQLException
     */
    @Override
    public boolean getMoreResults() throws SQLException {
        checkClosed();
        return realStatement.getMoreResults();
    }
    @Override
    public boolean getMoreResults(int current) throws SQLException {
        checkClosed();
        return realStatement.getMoreResults(current);
    }

    @Override
    public void cancel() throws SQLException {
        checkClosed();
        if(realStatement!=null){
            realStatement.cancel();
        }
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        checkClosed();
        if(realStatement!=null){
            return realStatement.getWarnings();
        }
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {
        checkClosed();
        if(realStatement!=null){
            realStatement.clearWarnings();
        }
    }

    @Override
    public void setCursorName(String name) throws SQLException {
        checkClosed();
        if(realStatement!=null){
            realStatement.setCursorName(name);
        }
    }
}
