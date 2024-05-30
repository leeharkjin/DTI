package dt_instance.aas.ksx9101;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.CaseStrategy;
import org.jdbi.v3.core.mapper.MapMapper;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import dt_instance.util.SettingManager;



public class Database {

    
    private static Jdbi jdbi;
    
    private static SettingManager _setter = null;

    public static boolean Init() {
      //JDBI 인스턴스 생성
    	
    	String uri = "jdbc:mariadb://129.254.89.150:23306/KSX9101?autoReconnect=true&useUnicode=true&characterEncoding=utf8";
    	String id = "root";
    	String passwd = "12345PW!@";
    	boolean enable = false;
    	
		_setter = new SettingManager();
		if (_setter != null) {
			uri = _setter.getString("fa3st.database.uri");
			id = _setter.getString("fa3st.database.id");
			passwd = _setter.getString("fa3st.database.passwd");
			enable = _setter.getBoolean("fa3st.database.enable");					
		}
		if(enable)
		{
			jdbi = Jdbi.create(uri, id, passwd);
       
	        // SQL Object 플러그인 등록
	        jdbi.installPlugin(new SqlObjectPlugin());
		}
		else
		{
			
		}
		return enable;
    }

    public static Jdbi getJdbi(){
        return jdbi;
    }
	
	public static List<Map<String, Object>> selectMapList(String query)
	{		
		List<Map<String, Object>> result = jdbi.withHandle(handle ->
        handle.createQuery(query)
                .mapToMap()
                .list()
				);
		
		return result;
		
	}
	
	
    public void insertData(String tableName, Map<String, Object> data) {
        // 마리아DB에 연결하는 코드
        
        // 데이터를 추가하는 SQL 쿼리 작성
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("INSERT INTO ")
                    .append(tableName)
                    .append(" (");
        
        // 데이터의 key 값을 이용하여 INSERT 문의 컬럼 목록 작성
        List<String> columns = new ArrayList<>(data.keySet());
        for (int i = 0; i < columns.size(); i++) {
            queryBuilder.append(columns.get(i));
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
        
        // PreparedStatement를 사용하여 SQL 쿼리 실행
        
        // 데이터베이스 연결 해제하는 코드
    }
	
//    public static void main(String[] args) {
//        // JDBI 인스턴스 생성
//        Jdbi jdbi = Jdbi.create("jdbc:mysql://localhost:3306/mydatabase", "username", "password");
//
//        // SQL Object 플러그인 등록
//        jdbi.installPlugin(new SqlObjectPlugin());
//
//        // RowMapper 설정
//        RowMapper<MyEntity> rowMapper = BeanMapper.of(MyEntity.class);
//
//        // 데이터 조회
//        List<MyEntity> entities = jdbi.withHandle(handle ->
//                handle.createQuery("SELECT * FROM my_table")
//                        .map(rowMapper)
//                        .list());
//
//        // 조회 결과 출력
//        for (MyEntity entity : entities) {
//            System.out.println(entity);
//        }
//    }
//
//    // 조회할 테이블의 엔티티 클래스
//    public static class MyEntity {
//        private int id;
//        private String name;
//
//        // Getter, Setter 생략
//
//        @Override
//        public String toString() {
//            return "MyEntity [id=" + id + ", name=" + name + "]";
//        }
//    }
}



