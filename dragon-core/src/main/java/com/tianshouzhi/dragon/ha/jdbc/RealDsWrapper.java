package com.tianshouzhi.dragon.ha.jdbc;

import com.tianshouzhi.dragon.common.exception.DragonException;
import com.tianshouzhi.dragon.common.jdbc.datasource.DataSourceAdapter;
import com.tianshouzhi.dragon.common.log.Log;
import com.tianshouzhi.dragon.common.log.LoggerFactory;
import com.tianshouzhi.dragon.ha.config.RealDsWrapperConfig;
import com.tianshouzhi.dragon.ha.exception.HASQLException;
import com.tianshouzhi.dragon.ha.util.DatasourceSpiUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by tianshouzhi on 2017/11/1.
 */
public class RealDsWrapper extends DataSourceAdapter implements DataSource, AutoCloseable {

	private static final Log LOGGER = LoggerFactory.getLogger(RealDsWrapper.class);

	private DataSource realDataSource;

	private boolean available = true;

	private RealDsWrapperConfig realDsConfig;

	private String parentHaDSName;

	public RealDsWrapper(RealDsWrapperConfig realDsConfig, String parentHaDSName) {
		this.realDsConfig = realDsConfig;
		this.parentHaDSName = parentHaDSName;
	}

	@Override
	protected void doInit() throws SQLException {
		try{
			if (realDataSource == null) {
				synchronized (this) {
					if (realDataSource == null) {
						LOGGER.info(" init real datasource(" + getRealDSName() + ")...");
						Class<? extends DataSource> dsClass = (Class<? extends DataSource>) Class.forName(realDsConfig.getRealDsClass());
						realDataSource = DatasourceSpiUtil.createDataSource(dsClass, realDsConfig.getRealDsProperties());
						DatasourceSpiUtil.init(realDataSource);
					}
				}
			}
		}catch (Exception e){
			throw new HASQLException("init physical ds ["+getRealDSName()+"] error",e);
		}
	}

	@Override
	public void close() throws DragonException {
		if (realDataSource != null) {
			LOGGER.info(" close real datasource(" + getRealDSName() + ")...");
			DatasourceSpiUtil.close(realDataSource);
		}
	}

	@Override
	protected Connection doGetConnection(String username, String password) throws SQLException {
		return realDataSource.getConnection();
	}

	public String getRealDSName() {
		return realDsConfig.getRealDsName();
	}

	public int getReadWeight() {
		return realDsConfig.getReadWeight();
	}

	public int getWriteWeight() {
		return realDsConfig.getWriteWeight();
	}

	public void disable() {
		this.available = false;
	}

	public void enable() {
		this.available = true;
	}

	public boolean isAvailable() {
		return available;
	}

	public String getParentHaDSName() {
		return parentHaDSName;
	}
}
