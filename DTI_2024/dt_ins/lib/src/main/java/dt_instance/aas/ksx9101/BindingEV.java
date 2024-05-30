package dt_instance.aas.ksx9101;





import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import dt_instance.dt_instance;
import dt_instance.aas.service.FA3STService;
import dt_instance.aas.subscription.BindingType;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.Environment;
import org.eclipse.digitaltwin.aas4j.v3.model.Key;
import org.eclipse.digitaltwin.aas4j.v3.model.Referable;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementCollection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.iosb.ilt.faaast.service.model.IdShortPath;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.Request;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.Response;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.modifier.Level;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.modifier.OutputModifier;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.modifier.QueryModifier;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.request.submodel.GetSubmodelElementByPathRequest;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.request.submodel.PutSubmodelElementByPathRequest;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.response.submodel.GetSubmodelElementByPathResponse;
import de.fraunhofer.iosb.ilt.faaast.service.model.api.response.submodel.PutSubmodelElementByPathResponse;
import de.fraunhofer.iosb.ilt.faaast.service.model.exception.ResourceNotFoundException;
import de.fraunhofer.iosb.ilt.faaast.service.model.value.ElementValue;
import de.fraunhofer.iosb.ilt.faaast.service.persistence.memory.PersistenceInMemory;
import de.fraunhofer.iosb.ilt.faaast.service.util.ReferenceBuilder;

import java.util.List;
public class BindingEV {
	
	public static final Logger log = LoggerFactory.getLogger(BindingEV.class);


	private static final String DEF_TBL_COL_HISTORYKEY = "HISTORYKEY";
	
	private static final String DEF_TBL_COL_CREATIONDATETIME = "CREATIONDATETIME";
	private static final String DEF_TBL_COL_CREATORID = "CREATORID";
	
//	private static final String DEF_TBL_COL_MODIFICATIONDATETIME = "MODIFICATIONDATETIME";
//	private static final String DEF_TBL_COL_MODIFIERID = "MODIFIERID";
	
	private static final String DEF_TBL_COL_DEFAULTUSER = "FA3ST-Service";
	private static final String DEF_TBL_COL_DEFAULTDATE = "NOW()";
	private static final String TABLE_PREFIX = "ETRI_TB_";
	private static final String TABLE_SUBPREFIX = "MDTINFO_";
	
	private PersistenceInMemory persistence;
	
	ElementValue _value = null;
	DefaultSubmodelElementCollection _savevalue = null;
	DefaultReference _DefaultReference = null;
	
	String _refelement[] = new String[10];
	String _submodelinfo = null;
	BindingType _type = null;
//	ISQLData _data = null;
//	ISQLRepository _repo = null;
	String _tableName;
	Map<String, Object> _data;
//	
	
	
    public static Submodel getFAAASTSubmodel(Environment environment, String submodelid) {
        return environment.getSubmodels().stream()
                .filter(x -> Objects.equals(x.getId(), submodelid))
                .findFirst()
                .orElseThrow();
    }
    public static SubmodelElement getFAAASTSubmodelColloction(Submodel environment, String submodelid) {
        return environment.getSubmodelElements().stream()
                .filter(x -> Objects.equals(x.getIdShort(), submodelid))
                .findFirst()
                .orElseThrow();
    }
    public static SubmodelElement getFAAASTSubmodelColloction(Submodel environment, String submodelid, String submodelid2) {
    	SubmodelElement tmp = environment.getSubmodelElements().stream()
                .filter(x -> Objects.equals(x.getIdShort(), submodelid))
                .findFirst()                
                .orElseThrow();
    	
		if (!(tmp instanceof Property))
			return null;
    	
    	List<SubmodelElement> submods = new ArrayList<>(((DefaultSubmodelElementCollection) tmp).getValue());
    	
    	String test = tmp.getCategory();
    	
  
        return tmp; 
    }


    
	BindingEV() {

	}
	
	public enum CRUDType {

		INSERT, UPDATE, DELETE
		
	}

	
	public BindingEV(ElementValue value, BindingType type, DefaultReference refelement) {
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
			
		}		
		
		this._data = new HashMap<>();
	}
	
	public void execute() throws SQLException  {
		
		
		//테이블이 있는지 확인하고
		
		
		ElementValue models = _value;
		String submodelinfo = _submodelinfo;
		
		//info 모델들 중복되는거 제거해야하는데... 아니면 다른 루틴을 타아햠 바로 smc 를 태운다면...
		switch (_submodelinfo) {

			case "MDTlInfo":
				break;
			case "SubModelInfo":
				break;
			case "DataInfo":
				break;
			case "AIInfo":
				break;
			case "SimulationInfo":
				break;
			case "BehaviorInfo":
				break;		
				
				
			default:	
			
			
		}
		
		String tableName="temp";
		int i=0;
		List<Key> defaultkey = _DefaultReference.getKeys();
		for( i =_DefaultReference.getKeys().size()-1 ; i >= 0; i--)
		{			
			if(_refelement[i].contentEquals("EventDateTime"))
			{
				return;	
			}
			tableName = TABLE_PREFIX + submodelinfo.toUpperCase() +"_" +_refelement[i];
			if (BindingTableSchema.isTableFromSchema(tableName))
			{
				log.info("====table({}) information is available.", tableName);
				break;
			}
			if(_refelement[i].length()>1)
				defaultkey.remove(i);
			
		}

		FA3STService service = dt_instance.getService();
		if (service == null)
			return;
		
        Reference expectedref = new DefaultReference.Builder().build();
        expectedref.setKeys(defaultkey);
		
        Request expected = GetSubmodelElementByPathRequest.builder()
                .submodelId(_refelement[0])
                .path(IdShortPath.fromReference(_DefaultReference).toString())
                .outputModifier(new OutputModifier.Builder()
                        .level(Level.DEEP)
                        .build())
                .build();        
		
        GetSubmodelElementByPathResponse res = (GetSubmodelElementByPathResponse) service.getService().execute(expected);
		
		_savevalue = (DefaultSubmodelElementCollection) res.getPayload();
//		
//		String test1 = _DefaultReference.toString();
//		
//		Environment initaasenv = service.getEnv();
//
//		Submodel submodel = getFAAASTSubmodel(initaasenv, _refelement[0]);
		
		
//		getFAAASTSubmodelColloction(submodel, _refelement[1], _refelement[2]);
//		SubmodelElement submodele;
//		for(int k = 1; k <= i; k++)
//		{
//			submodele = getFAAASTSubmodelColloction(submodel, _refelement[k]);
//		}
//				
		this._tableName = tableName;
		
		if (!BindingTableSchema.isTableFromSchema(tableName))
		{
			log.error("No table({}) information is available.", tableName);
			if(!submodelinfo.equals("DataInfo"))
				return;
		}

		
		if(executeModels(_savevalue, _tableName) == 0)
		{
			return;
		}
		
//		무한루프로 빠지게 됨 sub 이벤트가 계속 발생		
//		res.setPayload(_savevalue);
//		
//        Request expected2 = PutSubmodelElementByPathRequest.builder()
//                .submodelId(_refelement[0])
//                .path(IdShortPath.fromReference(_DefaultReference).toString())
//                .submodelElement((SubmodelElement)_savevalue)
//                .build();
//        PutSubmodelElementByPathResponse res1 = (PutSubmodelElementByPathResponse) service.getService().execute(expected2);
		
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

	private int executeModels(DefaultSubmodelElementCollection models, String tableName) throws SQLException{
		
		List<SubmodelElement> submods = new ArrayList<>(models.getValue());
		SubmodelElement se = null;
		DefaultProperty pp = null;
		String columnName = null;
		
    	ZoneId seoulZone = ZoneId.of("Asia/Seoul");
    	ZonedDateTime now = ZonedDateTime.now(seoulZone);
    	String currentTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		
		for (int i=0,l=submods.size(); i<l; i++) {
			se = submods.get(i);
			
			if (!(se instanceof Property))
				continue;
			
			pp = (DefaultProperty)se;
			columnName = se.getIdShort();
			columnName = columnName.toUpperCase();
			
			if (!BindingTableSchema.isColumnFromSchema(tableName, columnName))
				log.error("No column({}->{}) information is available.", tableName, columnName);
			
//			_repo.addColumn(se.getIdShort());
//			_repo.set(se.getIdShort(), pp.getValue());
			if(se.getIdShort().contentEquals("EventDateTime"))
			{				
				pp.setValue(currentTime);
				models.setValue(submods);
				
			}
			
			_data.put(se.getIdShort().toUpperCase(), pp.getValue());
		}
		
//		_repo.addColumn("SUBMODELID");
//		_repo.set("SUBMODELID", _submodelid);
		_data.put("SUBMODELID", _refelement[0]);
		
		

		//_data.put("EVENTDATETIME", currentTime);
		
		
		return 1;
	}
	
}
