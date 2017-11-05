package com.tianshouzhi.dragon.ha.jdbc.datasource;

import com.tianshouzhi.dragon.common.jdbc.datasource.DragonDataSource;
import com.tianshouzhi.dragon.common.jdbc.datasource.DragonDataSourceAdapter;
import com.tianshouzhi.dragon.ha.util.DatasourceUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by tianshouzhi on 2017/11/1.
 */
public class RealDataSourceWrapper extends DragonDataSourceAdapter implements DragonDataSource {
	private String haDSName;
	private String realDSName;
	private String fullName;
	private int readWeight;

	private int writeWeight;

	private Properties properties;

	private String clazz;

	private DataSource dataSource;

	private boolean available=true;

	public RealDataSourceWrapper(String haDSName,String realDSName, int readWeight, int writeWeight, Properties properties, String clazz) {
		this.haDSName = haDSName;
		this.realDSName = realDSName;
		this.fullName=haDSName+"."+realDSName;
		this.readWeight = readWeight;
		this.writeWeight = writeWeight;
		this.properties = properties;
		this.clazz = clazz;
	}


	public RealDataSourceWrapper(String realDSName, int readWeight, int writeWeight, DataSource dataSource) {
		this.realDSName = realDSName;
		this.readWeight = readWeight;
		this.writeWeight = writeWeight;
		this.dataSource = dataSource;
	}

	public String getRealDSName() {
		return realDSName;
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
			DatasourceUtil.close(haDSName,realDSName,dataSource);
		}
	}

	@Override
	protected Connection doGetConnection(String username, String password) throws SQLException {
		return dataSource.getConnection();
	}

	@Override
	protected void doInit() throws Exception {
		if (dataSource == null) {
			synchronized (this){
				if(dataSource==null){
					Class<? extends DataSource> dsClass = (Class<? extends DataSource>) Class.forName(clazz);
					dataSource=DatasourceUtil.createDataSource(dsClass,properties);
					DatasourceUtil.init(dataSource);
				}
			}
		}
	}

	public String getFullName() {
		return fullName;
	}

	public void disable(){
		this.available=false;
	}
	public void enable(){
		this.available=true;
	}

	public boolean isAvailable() {
		return available;
	}
}
