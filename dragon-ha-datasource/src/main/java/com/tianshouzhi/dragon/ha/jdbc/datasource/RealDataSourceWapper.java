package com.tianshouzhi.dragon.ha.jdbc.datasource;

import com.tianshouzhi.dragon.common.jdbc.datasource.DragonDataSource;
import com.tianshouzhi.dragon.common.jdbc.datasource.DragonDataSourceAdapter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by tianshouzhi on 2017/11/1.
 */
public class RealDataSourceWapper extends DragonDataSourceAdapter implements DragonDataSource {
	private String index;

	private int readWeight;

	private int writeWeight;

	private Properties properties;

	private Class<? extends DataSource> clazz;

	private DataSource dataSource;


	public RealDataSourceWapper(String index, int readWeight, int writeWeight, Properties properties, Class<? extends DataSource> clazz) {
		this.index = index;
		this.readWeight = readWeight;
		this.writeWeight = writeWeight;
		this.properties = properties;
		this.clazz = clazz;
	}


	public RealDataSourceWapper(String index, int readWeight, int writeWeight, DataSource dataSource) {
		this.index = index;
		this.readWeight = readWeight;
		this.writeWeight = writeWeight;
		this.dataSource = dataSource;
	}

	public String getIndex() {
		return index;
	}

	public int getReadWeight() {
		return readWeight;
	}

	public int getWriteWeight() {
		return writeWeight;
	}

	public DataSource getRealDataSource() {
		return dataSource;
	}

	@Override
	public void close() throws Exception {
		if (dataSource != null) {
			DatasourceUtil.close(dataSource);
		}
	}

	@Override
	protected Connection doGetConnection(String username, String password) throws SQLException {
		return dataSource.getConnection();
	}

	@Override
	protected void doInit() throws Exception {
		if (dataSource == null) {
			dataSource=DatasourceUtil.createDataSource(clazz,properties);
		}
		DatasourceUtil.init(dataSource);
	}
}
