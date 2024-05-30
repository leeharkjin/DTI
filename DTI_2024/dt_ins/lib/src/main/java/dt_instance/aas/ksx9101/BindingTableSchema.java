package dt_instance.aas.ksx9101;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dt_instance.aas.ksx9101.InsertQueryBuilder.ColumnMetadata;
import dt_instance.aas.service.FA3STService;
import dt_instance.util.SettingManager;

import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dt_instance.aas.subscription.BindingType;


public class BindingTableSchema {

	public static final Logger log = LoggerFactory.getLogger(BindingTableSchema.class);

	
	private static Map<String, Set<String>> _pkm = null;
	private static Map<String, Set<String>> _colm = null;
	private static SettingManager _setter = FA3STService.Instance().setter();
	
	private static final String DEF_CFG_DATABASE_NAME = "fa3st.database.name";
	private static final String DEF_CFG_DATABASE_TABLE_PRIFIX = "fa3st.database.table.prifix";
	
	private static final String DEF_SO_TABLE_NAME = "table_name";//"TABLE_NAME";
//	private static final String DEF_SO_DATA_TYPE = "DATA_TYPE";
	private static final String DEF_SO_ATTR_COLUMN_NAME = "column_name";//"COLUMN_NAME";
	private static final String DEF_SO_ATTR_COLUMN_KEY = "column_key";//"COLUMN_KEY";
	private static final String DEF_SO_ATTR_PRI_KEY = "PRI";
	
	private static Jdbi jdbi;
	
	static {
		_pkm = new HashMap<String, Set<String>>();
		_colm = new HashMap<String, Set<String>>();
	}
	
	public static String getTableName(String name) {
		return _setter.getString(DEF_CFG_DATABASE_TABLE_PRIFIX) + name;
	}
	
	public static Set<String> getPrimaryKeys(String tableName) {
		if (tableName.isEmpty())
			return null;
		
		//tableName = getTableName(tableName);
		return _pkm.get(tableName.toUpperCase());
	}
	
	public static Set<String> getColumnNames(String tableName) {
		if (tableName.isEmpty())
			return null;
		
		//tableName = getTableName(tableName);
		return _colm.get(tableName.toUpperCase());
	}
	
	public static Map<String, Set<String>> getAllColumnNames() {
		
		return _colm;
	}
	
	public static boolean isTableFromSchema(String tableName) {
		Set<String> pks = getPrimaryKeys(tableName);
		if (pks == null)
			return false;
		
		if (pks.size() == 0)
			return false;
		
		return true;
	}
	
	public static boolean isPrimaryKeyFromSchema(String tableName, String columnName) {
		if (columnName.isEmpty())
			return false;

		Set<String> pks = getPrimaryKeys(tableName);
		if (pks == null)
			return false;
		
		if (pks.size() == 0)
			return false;
		
		return pks.contains(columnName.toUpperCase());
	}
	
	public static boolean isColumnFromSchema(String tableName, String columnName) {
		if (columnName.isEmpty())
			return false;
		
		Set<String> cols = getColumnNames(tableName);
		if (cols == null)
			return false;
		
		if (cols.size() == 0)
			return false;
		
		return cols.contains(columnName.toUpperCase());
	}
	
//	public static void appendKeysFromSchema(String tableName, ISQLData data){
//		if (data == null)
//			return;
//
//		ISQLRepository repo = data.repository();
//
//		Set<String> pks = getPrimaryKeys(tableName);
//		if (pks == null)
//			log.info("No primary keys. {}", tableName);
//		
//		if (pks.size() == 0)
//			log.info("No primary keys. {}", tableName);
//		
//		for (String item : pks)
//			repo.addKey(item);
//	}
	
	public static void init() {
		String query = "SELECT TABLE_NAME, COLUMN_NAME, COLUMN_KEY, DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '${OWNER}' AND TABLE_NAME LIKE 'ETRI_TB_%' ORDER BY TABLE_NAME;";
		if (query.isEmpty())
			log.error("No query");
			
		String owner = _setter.getString(DEF_CFG_DATABASE_NAME);
		if (owner.isEmpty())
			log.error("No database owner");
		
		Map<String, Object> bindVar = new HashMap<String, Object>();
		bindVar.put("OWNER", owner);
		query = "SELECT TABLE_NAME, COLUMN_NAME, COLUMN_KEY, DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'KSX9101' AND TABLE_NAME LIKE 'ETRI_TB_%' ORDER BY TABLE_NAME;";
		
		
		List<Map<String, Object>> list = Database.selectMapList(query);
		String tableName, columnName, columnKey;//, dataType;
		Set<String> pks;
		Set<String> cols;
		
		Map<String, Set<String>> pkm = new HashMap<String, Set<String>>();
		Map<String, Set<String>> colm = new HashMap<String, Set<String>>();
		
		for (Map<String, Object> item : list) {
			
			columnKey = item
				.get(
					DEF_SO_ATTR_COLUMN_KEY
				)
				.toString()
			;
			
			tableName = item
				.get(
					DEF_SO_TABLE_NAME
				)
				.toString()
				.toUpperCase()
			;
			
			columnName = item
				.get(
					DEF_SO_ATTR_COLUMN_NAME
				)
				.toString()
				.toUpperCase()
			;
			
			if (!colm.containsKey(tableName)) {
				cols =  new HashSet<String>();
				colm.put(tableName, cols);
			} else {
				cols = colm.get(tableName);
			}
			
			cols.add(columnName);
			
			if (!columnKey.equals(DEF_SO_ATTR_PRI_KEY))
				continue;
			
			if (!pkm.containsKey(tableName)) {
				pks =  new HashSet<String>();
				pkm.put(tableName, pks);
			} else {
				pks = pkm.get(tableName);
			}
			
			pks.add(columnName);
		}
		
		_pkm = pkm;
		_colm = colm;
		
		log.info( "FA3ST.schema : Init. {}", _pkm.size());
	}
	
	public static void clear() {
		_colm.clear();
		_colm = null;
		_pkm.clear();
		_pkm = null;
		
		log.info("FA3ST.schema : Clear.");
	}
	
}
