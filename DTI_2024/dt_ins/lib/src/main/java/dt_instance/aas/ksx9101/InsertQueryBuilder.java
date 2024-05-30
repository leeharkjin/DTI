package dt_instance.aas.ksx9101;

import java.security.SecureRandom;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.ColumnName;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.jdbi.v3.sqlobject.config.RegisterColumnMapper;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.customizer.BindBeanList;
import org.jdbi.v3.sqlobject.customizer.BindMap;
import org.jdbi.v3.sqlobject.customizer.BindMethods;
import org.jdbi.v3.sqlobject.customizer.Define;
import org.jdbi.v3.sqlobject.statement.SqlBatch;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlScript;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public class InsertQueryBuilder {
    private static Jdbi _jdbi;

    private static Map<String, String> insertquerylist;
    
    
	static {
		insertquerylist = new HashMap<String, String>();
		
	}
	
	
    public InsertQueryBuilder() {
    
    }
    
    public static void init(Jdbi jdbi) {
       	_jdbi = jdbi;
		
       	Map<String, Set<String>> allcolumn = BindingTableSchema.getAllColumnNames();
       	
       	Set<String> key = allcolumn.keySet();
       	
        for (Entry<String, Set<String>> entry : allcolumn.entrySet()) {
        	
        	
        	String sql = null;
        	sql = generateInsertlist(entry.getKey(), entry.getValue());
			
        	insertquerylist.put(entry.getKey(), sql.toUpperCase());
            
        }
       	
       	
        
        return;
    }

    public static String generateInsertlist(String tableName, Set<String> data)
    {
        Map<String, Object> test = new HashMap<>();
        test.put("INSTANCENAME", "test1");
        test.put("AASX", "asdf1");
        
        
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("INSERT INTO ")
                    .append(tableName.toUpperCase())
                    .append(" (");
        
        // 데이터의 key 값을 이용하여 INSERT 문의 컬럼 목록 작성
        List<String> columns = new ArrayList<>(data);
        for (int i = 0; i < columns.size(); i++) {
            queryBuilder.append(columns.get(i).toUpperCase());
            if (i < columns.size() - 1) {
                queryBuilder.append(", ");
            }
        }
        
        queryBuilder.append(") VALUES (");
        
        // 데이터의 value 값을 이용하여 INSERT 문의 값 목록 작성
        for (int i = 0; i < columns.size(); i++) {
            queryBuilder.append(":")
                        .append(columns.get(i));
            if (i < columns.size() - 1) {
                queryBuilder.append(", ");
            }
        }
        
        queryBuilder.append(")");
        return queryBuilder.toString();
    }
    public static String generateInsertQuery(String tableName) {
        // 테이블의 컬럼 정보 조회
        List<ColumnMetadata> columns = _jdbi.withHandle(handle ->
                handle.createQuery("SELECT COLUMN_NAME, DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = :tableName")
                        .bind("tableName", tableName)
                        .mapTo(ColumnMetadata.class)
                        .list());

        // INSERT 쿼리 생성
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("INSERT INTO ").append(tableName).append(" (");

        for (int i = 0; i < columns.size(); i++) {
            ColumnMetadata column = columns.get(i);
            queryBuilder.append(column.getColumnName());
            if (i < columns.size() - 1) {
                queryBuilder.append(", ");
            }
        }

        queryBuilder.append(") VALUES (");

        for (int i = 0; i < columns.size(); i++) {
            queryBuilder.append(":").append(columns.get(i).getColumnName());
            if (i < columns.size() - 1) {
                queryBuilder.append(", ");
            }
        }

        queryBuilder.append(")");

        return queryBuilder.toString();
    }

    // ColumnMetadata 클래스
    public class ColumnMetadata {
        @ColumnName("COLUMN_NAME")
        private String columnName;

        @ColumnName("DATA_TYPE")
        private String dataType;

        // getter, setter 생략
        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }
    }
    
    public void insertData(String tableName, Map<String, Object> data) throws SQLException {
        // 마리아DB에 연결하는 코드
        
        // 데이터를 추가하는 SQL 쿼리 작성
    	
        
    	String Query = insertquerylist.get(tableName.toUpperCase());
        
//    	GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
//    	Long generatedKey = keyHolder.getKey(); 
//    	data.put("HISTORYKEY", ())
    	

    	
        // PreparedStatement를 사용하여 SQL 쿼리 실행
        Handle handle = _jdbi.open();
        
        SecureRandom random = new SecureRandom();
        byte[] randomBytes = new byte[16];
        random.nextBytes(randomBytes);
    	
        long randomKey = generateRandomLongKey();
    	data.put("HISTORYKEY", bytesToHexString(randomBytes));
    	data.put("ENTERPRISEID", "");
    	data.put("SITEID", "");
    	data.put("PLANTID", "");
    	data.put("NOTE", "");
    	data.put("CREATORID", "gausslee");
    	ZoneId seoulZone = ZoneId.of("Asia/Seoul");
    	ZonedDateTime now = ZonedDateTime.now(seoulZone);
    	String currentTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    	data.put("CREATIONDATETIME", currentTime);
    	data.put("MODIFIERID", "gausslee");
    	data.put("MODIFICATIONDATETIME", currentTime);
    	
       handle.createUpdate(Query)
        		.bindMap(data)
        		.execute();        
        handle.close();
        // 데이터베이스 연결 해제하는 코드
    }
    
    public static long generateRandomLongKey() {
        Random random = new Random();
        long randomKey = random.nextLong();
        return randomKey;
    }
    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    
    
    
    
}
