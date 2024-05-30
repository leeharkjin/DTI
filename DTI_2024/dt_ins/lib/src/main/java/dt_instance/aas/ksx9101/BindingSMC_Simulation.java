package dt_instance.aas.ksx9101;





import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dt_instance.aas.subscription.BindingType;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.Key;
import org.eclipse.digitaltwin.aas4j.v3.model.Referable;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementCollection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
public class BindingSMC_Simulation {
	
	public static final Logger log = LoggerFactory.getLogger(BindingSMC_Simulation.class);


	private static final String DEF_TBL_COL_HISTORYKEY = "HISTORYKEY";
	
	private static final String DEF_TBL_COL_CREATIONDATETIME = "CREATIONDATETIME";
	private static final String DEF_TBL_COL_CREATORID = "CREATORID";
	
//	private static final String DEF_TBL_COL_MODIFICATIONDATETIME = "MODIFICATIONDATETIME";
//	private static final String DEF_TBL_COL_MODIFIERID = "MODIFIERID";
	
	private static final String DEF_TBL_COL_DEFAULTUSER = "FA3ST-Service";
	private static final String DEF_TBL_COL_DEFAULTDATE = "NOW()";
	private static final String TABLE_PREFIX = "ETRI_TB_";
	private static final String TABLE_SUBPREFIX = "SIMULATIONINFO_";
	
	DefaultSubmodelElementCollection _value = null;
	DefaultReference _DefaultReference = null;
	
	String _refelement[] = new String[10];
	String _submodelinfo = null;
	BindingType _type = null;
//	ISQLData _data = null;
//	ISQLRepository _repo = null;
	String _tableName;
	Map<String, Object> _data;
//	
	BindingSMC_Simulation() {

	}
	
	public enum CRUDType {

		INSERT, UPDATE, DELETE
		
	}

	
	public BindingSMC_Simulation(DefaultSubmodelElementCollection value, BindingType type, DefaultReference refelement) {
		this();
		this._value = value;
		this._type = type;
		this._DefaultReference = refelement;
		List<Key> tmp = refelement.getKeys();
		
	
		for(int i =0 ; i < tmp.size(); i++)
		{			
			this._refelement[i] = tmp.get(i).getValue();
		}
		if(tmp.size()>1)
			this._submodelinfo = _refelement[1];
		else
		{
			this._submodelinfo = value.getIdShort();
		}
		
		
		
		this._data = new HashMap<>();
	}

	private void initHistoryKey() {
//		if (!_repo.isColumn(DEF_TBL_COL_HISTORYKEY))
//			_repo.addColumn(DEF_TBL_COL_HISTORYKEY);
//		
//		if (!_repo.isKey(DEF_TBL_COL_HISTORYKEY))
//			_repo.addKey(DEF_TBL_COL_HISTORYKEY);
//		
//		_repo.set(DEF_TBL_COL_HISTORYKEY, Generate.createID());
	}
	
	// Message.eventdate 로 변경 검토!!!
	private void initCreationValue() {
//		if (!_repo.isColumn(DEF_TBL_COL_CREATIONDATETIME))
//			_repo.addColumn(DEF_TBL_COL_CREATIONDATETIME);
//		
//		if (!_repo.isColumn(DEF_TBL_COL_CREATORID))
//			_repo.addColumn(DEF_TBL_COL_CREATORID);
//		
//		_repo.addFunction(DEF_TBL_COL_CREATIONDATETIME, DEF_TBL_COL_DEFAULTDATE);
//		_repo.set(DEF_TBL_COL_CREATORID, DEF_TBL_COL_DEFAULTUSER);
	}
	
	// Message.eventdate 로 변경 검토!!!
//	private void initModificationValue() {
//		if (!_repo.isColumn(DEF_TBL_COL_MODIFICATIONDATETIME))
//			_repo.addColumn(DEF_TBL_COL_MODIFICATIONDATETIME);
//		
//		if (!_repo.isColumn(DEF_TBL_COL_MODIFIERID))
//			_repo.addColumn(DEF_TBL_COL_MODIFIERID);
//		
//		_repo.addFunction(DEF_TBL_COL_MODIFICATIONDATETIME, DEF_TBL_COL_DEFAULTDATE);
//		_repo.set(DEF_TBL_COL_MODIFIERID, DEF_TBL_COL_DEFAULTUSER);
//	}
	
	public void execute() throws SQLException  {
		
		
		//테이블이 있는지 확인하고
		
		
		DefaultSubmodelElementCollection models = _value;
		String submodelinfo = _submodelinfo;
		
//		//info 모델들 중복되는거 제거해야하는데... 아니면 다른 루틴을 타아햠 바로 smc 를 태운다면...
//		switch (_submodelinfo) {
//
//			case "MDTlInfo":
//				break;
//			case "SubModelInfo":
//				break;
//			case "DataInfo":
//				break;
//			case "AIInfo":
//				break;
//			case "SimulationInfo":
//				break;
//			case "BehaviorInfo":
//				break;		
//				
//				
//			default:	
//			
//			
//		}
		
		/// hark updatev 여기에서 타입별 행동을 분리 시켜야함 datainfo, datainfo.element, operation, equiption 등등
		String modelName = models.getIdShort();
		if (modelName.contains("EquipmentParameter"))
			modelName = "EquipmentParameter";
		else if (modelName.contains("OperationParameter"))
			modelName = "OperationParameter";
		else if (modelName.contains("Input"))
			modelName = "Input";
		else if (modelName.contains("Output"))
			modelName = "Output";
		else
			log.debug("FA3ST.submodel : Execute String -> \n{}", modelName);
		
		
		String tableName = TABLE_PREFIX + TABLE_SUBPREFIX + modelName.toUpperCase();
	
		this._tableName = tableName;
		
		if (!BindingTableSchema.isTableFromSchema(tableName))
		{
			log.error("No table({}) information is available.", tableName);
			if(!submodelinfo.equals("DataInfo"))
				return;
		}
		
		
		//테이블이 없으면 왜 없는지 검사하고
		
		//정말 필요없는 테이블이라면 스킵
		
		//그래도 필요한 테이블이라면 처리
		
		// 어떻게 처리할것인가? datainfo ?? aiinfo
		
		
		
		if(executeModels(_value, _tableName) == 0)
		{
			log.warn("FA3ST.submodel : dose not management table -> \n{}", _value.getIdShort());
			
			log.warn("FA3ST.submodel : dose not management table -> \n{}", _value.getIdShort());
			log.warn("FA3ST.submodel : dose not management table -> \n{}", _value.getIdShort());
			return;
		}
		
		
		initHistoryKey();
		
		String sql = null;
		
		switch (_type) {
			default:
			case Create:
//				sql = _data.getInsertQuery();
//				initCreationValue();
//				executeQuery(INSERT);
				break;
			case Update:
//				sql = _data.getUpdateQuery();
//				initModificationValue();
//				executeQuery(SQLUpsertType.UPDATE);
				
				initCreationValue();
//				executeQuery(INSERT);
				InsertQueryBuilder tmp = new InsertQueryBuilder();
				tmp.insertData(_tableName, _data);
				break;
			case Delete:
//				sql = _data.getDeleteQuery();
//				initModificationValue();
//				executeQuery(SQLUpsertType.DELETE);
//				break;
				return;
		}
			
//		log.debug("FA3ST.submodel : Execute String -> \n{}, {}", sql, _data.toMap());
	}
	
//	private void executeQuery(SQLUpsertType type) throws DatabaseException {
//		ITransactionContext tc = SQLService.createTransactionContext();
//		SQLService.beginTransaction(tc);
//		try {
//			switch (type) {
//				case INSERT:
//					_data.insert();
//					break;
//				case UPDATE:
//					_data.update();
//					break;
//				case DELETE:
//					_data.delete();
//					break;
//				default:
//					return;
//			}
//			
//			SQLService.commitTransaction(tc);
//		} catch (Exception e) {
//			SQLService.rollbackTransaction(tc);
//			throw e;
//		}
//	}
	
	private int executeModels(DefaultSubmodelElementCollection models, String tableName) throws SQLException{
		
		

		
//		_repo.setTableName(BindingTableSchema.getTableName(tableName));
//		
//		BindingTableSchema.appendKeysFromSchema(tableName, _data);
		
		List<SubmodelElement> submods = new ArrayList<>(models.getValue());
		SubmodelElement se = null;
		DefaultProperty pp = null;
		String columnName = null;
		
		for (int i=0,l=submods.size(); i<l; i++) {
			se = submods.get(i);
			
			if (se instanceof SubmodelElementCollection) {
				new BindingSMC_Simulation(
					(DefaultSubmodelElementCollection)se,
					_type,
					this._DefaultReference // 여기에는 모델타입과 아이디가 들어가야됨
				).execute();
				continue;
			}
			
			if (!(se instanceof Property))
				continue;
			
			pp = (DefaultProperty)se;
			columnName = se.getIdShort();
			columnName = columnName.toUpperCase();
			
			if (!BindingTableSchema.isColumnFromSchema(tableName, columnName))
				log.error("No column({}->{}) information is available.", tableName, columnName);
			
//			_repo.addColumn(se.getIdShort());
//			_repo.set(se.getIdShort(), pp.getValue());
			_data.put(se.getIdShort().toUpperCase(), pp.getValue());
		}
		
//		_repo.addColumn("SUBMODELID");
//		_repo.set("SUBMODELID", _submodelid);
		_data.put("SUBMODELID", _refelement[0]);
		
		return 1;
	}
	
}
