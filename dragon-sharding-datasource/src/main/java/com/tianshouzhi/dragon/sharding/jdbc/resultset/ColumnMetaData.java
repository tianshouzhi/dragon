package com.tianshouzhi.dragon.sharding.jdbc.resultset;

/**
 * 表示一行记录中每一列的属性
 */
public class ColumnMetaData {

	private boolean autoIncrement;

	private boolean caseSensitive;

	private boolean searchable;

	private boolean currency;

	private int nullable;

	private boolean signed;

	private int columnDisplaySize;

	private String columnLabel;

	private String columnName;

	private String schemaName;

	private int precision;

	private int scale;

	private String tableName;

	private String catalogName;

	private int columnType;

	private String columnTypeName;

	private boolean readOnly;

	private boolean writable;

	private boolean definitelyWritable;

	private String columnClassName;

	public boolean isAutoIncrement() {
		return autoIncrement;
	}

	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	public boolean isSearchable() {
		return searchable;
	}

	public boolean isCurrency() {
		return currency;
	}

	public int isNullable() {
		return nullable;
	}

	public boolean isSigned() {
		return signed;
	}

	public int getColumnDisplaySize() {
		return columnDisplaySize;
	}

	public String getColumnLabel() {
		return columnLabel;
	}

	public String getColumnName() {
		return columnName;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public int getPrecision() {
		return precision;
	}

	public int getScale() {
		return scale;
	}

	public String getTableName() {
		return tableName;
	}

	public String getCatalogName() {
		return catalogName;
	}

	public int getColumnType() {
		return columnType;
	}

	public String getColumnTypeName() {
		return columnTypeName;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public boolean isWritable() {
		return writable;
	}

	public boolean isDefinitelyWritable() {
		return definitelyWritable;
	}

	public String getColumnClassName() {
		return columnClassName;
	}

	public void setAutoIncrement(boolean autoIncrement) {
		this.autoIncrement = autoIncrement;
	}

	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	public void setSearchable(boolean searchable) {
		this.searchable = searchable;
	}

	public void setCurrency(boolean currency) {
		this.currency = currency;
	}

	public void setNullable(int nullable) {
		this.nullable = nullable;
	}

	public void setSigned(boolean signed) {
		this.signed = signed;
	}

	public void setColumnDisplaySize(int columnDisplaySize) {
		this.columnDisplaySize = columnDisplaySize;
	}

	public void setColumnLabel(String columnLabel) {
		this.columnLabel = columnLabel;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setCatalogName(String catalogName) {
		this.catalogName = catalogName;
	}

	public void setColumnType(int columnType) {
		this.columnType = columnType;
	}

	public void setColumnTypeName(String columnTypeName) {
		this.columnTypeName = columnTypeName;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public void setWritable(boolean writable) {
		this.writable = writable;
	}

	public void setDefinitelyWritable(boolean definitelyWritable) {
		this.definitelyWritable = definitelyWritable;
	}

	public void setColumnClassName(String columnClassName) {
		this.columnClassName = columnClassName;
	}
}
