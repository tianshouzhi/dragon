package com.tianshouzhi.dragon.real.jdbc;

import com.tianshouzhi.dragon.common.exception.DragonException;
import com.tianshouzhi.dragon.common.jdbc.datasource.DragonDataSource;
import com.tianshouzhi.dragon.common.jdbc.datasource.DragonDataSourceAdapter;
import com.tianshouzhi.dragon.common.util.BeanPropertyUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by tianshouzhi on 2017/10/14.
 */
public class RealDataSource<T extends DataSource> extends DragonDataSourceAdapter implements DragonDataSource {
	protected String index;

	protected Class<T> realDsClass;

	protected Properties realDsProperties;

	protected T dataSource;

	public RealDataSource(String index, Class<T> realDsClass, Properties realDsProperties) throws DragonException {
		this.index = index;
		this.realDsClass = realDsClass;
		this.realDsProperties = realDsProperties;
		if (realDsClass == null || realDsProperties == null) {
			throw new DragonException("'realDsClass' and 'realDsProperties' can't be null");
		}
	}

	@Override
	public void init() throws Exception {
        dataSource = realDsClass.newInstance();
        BeanPropertyUtil.populate(dataSource, realDsProperties);
	}

    @Override
    public void close() throws Exception {

    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        if (username == null && password == null) {
            return dataSource.getConnection();
        }
        return dataSource.getConnection(username, password);
    }

	// getters and setters
	public String getIndex() {
		return index;
	}
}
