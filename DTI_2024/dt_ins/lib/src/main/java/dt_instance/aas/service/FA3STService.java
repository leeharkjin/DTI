package dt_instance.aas.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.Reference;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import de.fraunhofer.iosb.ilt.faaast.service.Service;
import de.fraunhofer.iosb.ilt.faaast.service.assetconnection.AssetConnectionException;
import de.fraunhofer.iosb.ilt.faaast.service.assetconnection.AssetConnectionManager;
import de.fraunhofer.iosb.ilt.faaast.service.config.CoreConfig;
import de.fraunhofer.iosb.ilt.faaast.service.config.ServiceConfig;
import de.fraunhofer.iosb.ilt.faaast.service.config.ServiceConfig.Builder;
import de.fraunhofer.iosb.ilt.faaast.service.endpoint.http.HttpEndpointConfig;
import de.fraunhofer.iosb.ilt.faaast.service.exception.ConfigurationException;
import de.fraunhofer.iosb.ilt.faaast.service.exception.EndpointException;
import de.fraunhofer.iosb.ilt.faaast.service.exception.MessageBusException;
import de.fraunhofer.iosb.ilt.faaast.service.model.serialization.DataFormat;
import de.fraunhofer.iosb.ilt.faaast.service.persistence.Persistence;
import de.fraunhofer.iosb.ilt.faaast.service.persistence.file.PersistenceFileConfig;
import de.fraunhofer.iosb.ilt.faaast.service.persistence.memory.PersistenceInMemoryConfig;
import de.fraunhofer.iosb.ilt.faaast.service.messagebus.internal.MessageBusInternalConfig;
import de.fraunhofer.iosb.ilt.faaast.service.filestorage.FileStorageConfig;
import de.fraunhofer.iosb.ilt.faaast.service.filestorage.memory.FileStorageInMemoryConfig;
import de.fraunhofer.iosb.ilt.faaast.service.starter.util.ServiceConfigHelper;
import de.fraunhofer.iosb.ilt.faaast.service.typing.TypeInfo;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.Environment;
//import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShellEnvironment;
import org.eclipse.digitaltwin.aas4j.v3.model.Environment;
//import org.eclipse.digitaltwin.aas4j.v3.model.KeyType;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementCollection;
import dt_instance.dt_instance;
import dt_instance.aas.ksx9101.BindingAAS;
import dt_instance.aas.ksx9101.BindingSM;
import dt_instance.aas.ksx9101.BindingSMC_AI;
import dt_instance.aas.ksx9101.BindingTableSchema;
import dt_instance.aas.ksx9101.Database;
import dt_instance.aas.message.FA3STMessageBus;
import dt_instance.aas.message.FA3STMessageBusConfig;
import dt_instance.aas.subscription.BindingType;
import dt_instance.util.ConfigManager;
import dt_instance.util.SettingManager;



public class FA3STService {

	public static final String DEF_INSTANCEID = "fa3st.instanceid";
	public static final String DEF_INSTANCEID_PRIFIX = "FA3ST";
	
	public static final String DEF_CONFIG = "aas.fa3st.config";
	
	public static final String DEF_THREAD_POOL_SIZE = "fa3st.config.threadPoolSize";
	
	public static final String DEF_REGISTRY_HOST = "fa3st.config.registry.host";
	public static final String DEF_REGISTRY_PORT = "fa3st.config.registry.port";
	public static final String DEF_REGISTRY_AASBASEPATH = "fa3st.config.registry.aasBasePath";
	public static final String DEF_REGISTRY_SUBMODELBASEPATH = "fa3st.config.registry.submodelBasePath";
	
	public static final String DEF_ENDPOINT_HTTP_PORT = "fa3st.endpoint.http.port";
	public static final String DEF_ENDPOINT_HTTP_CORS = "fa3st.endpoint.http.cors";
	
	public static final String DEF_VALIDATE_CONSTRAINTS = "fa3st.config.validate.constraints";
	public static final String DEF_VALIDATE_IDENTIFIER_UNIQUENESS = "fa3st.config.validate.identifier.uniqueness";
	public static final String DEF_VALIDATE_IDSHORT_UNIQUENESS = "fa3st.config.validate.idshort.uniqueness";
	public static final String DEF_VALIDATE_VALUE_TYPES = "fa3st.config.validate.value.types";
	
	public static final String DEF_ASSET_CONNECTION_RETRY_INTERVAL = "fa3st.config.asset.connection.retry.interval";
	
	public static final String DEF_PERSISTENCE_MODEL_PATH = "fa3st.persistence.model.path";
	public static final String DEF_PERSISTENCE_MODEL_TYPE = "fa3st.persistence.model.type";
	public static final String DEF_PERSISTENCE_MODEL_DIR = "fa3st.persistence.model.file.dir";
	public static final String DEF_PERSISTENCE_MODEL_DATAFORMAT = "fa3st.persistence.model.file.dataformat";
	public static final String DEF_PERSISTENCE_MODEL_KEEPINITIAL = "fa3st.persistence.model.file.keepinitial";
	
	public static final String DEF_DATABASE_MODEL_INSTANCE = "fa3st.database.model.instancename";
	
	public static final String DEF_CONFIG_PATH = "fa3st.configfile";
	
	public static final Logger log = LoggerFactory.getLogger(FA3STService.class);
	
	private static final FA3STService _instance = new FA3STService();
	public static FA3STService Instance() {
		return _instance;
	}
	public File configFile;
	private String _instanceid = null;
	private ConfigManager _cfg = new ConfigManager();
	private Service _service = null;
	private int _threadPoolSize = 50;
	private int _port = 10101;
	private boolean _cors = false;
	private String _aasxFilePath = "config/test.json";
	private FA3STPersistenceModelFileTypes _aasxFileType = FA3STPersistenceModelFileTypes.Memory;
	private SettingManager _setter = null;
	private ServiceConfig _servicConfig = null;
	private String _servicConfigfile = null;
	private static Persistence persistence;
	private boolean _dbenable = false;
	
	FA3STService() {
		
	}
	
	public String getInstanceID() {
		return _instanceid;
	}
	
	public String name() {
		return "FA3ST-v0.6-SNAPSHOT";
	}

	public ConfigManager configuration() {
		return _cfg;
	}
	
	public SettingManager setter() {
		return _setter;
	}
	
	public Service getService() {
		return _service;
	}
	
	public FA3STMessageBus getMessageBus() {
		if (_service == null)
			return null;
		
		return (FA3STMessageBus)_service.getMessageBus();
	}
	
	public Environment getEnv() {
		
		return _service.getAASEnvironment();
	}
	
	public AssetConnectionManager getConnectionManager() {
		return _service.getAssetConnectionManager();
	}
	
    public void testAasUpdate() throws Exception {
        AssetAdministrationShell aas = _service.getAASEnvironment().getAssetAdministrationShells().get(0);
        String oldIdShort = aas.getIdShort();
        aas.setIdShort("Changed Id Short");
        
//        stubFor(put(coreConfig.getAasRegistryBasePath() + "/" + getEncodedIdentifier(aas))
//                .willReturn(ok()));
//
//        MESSAGE_BUS.publish(ElementUpdateEventMessage.builder()
//                .element(aas).build());
//
//        verify(putRequestedFor(urlEqualTo(coreConfig.getAasRegistryBasePath() + "/" + getEncodedIdentifier(aas)))
//                .withRequestBody(equalToJson(getAasDescriptorBody(aas))));

        aas.setIdShort(oldIdShort);
    }

	

	private void initEnv() {
		_setter = new SettingManager();
		if (_setter != null) {
			_threadPoolSize = _setter.getInteger(DEF_THREAD_POOL_SIZE);
			if (_threadPoolSize <= 0)
				_threadPoolSize = 2;
			
			log.info("FA3ST.env : Init thread pool size. {}", _threadPoolSize);
			
			_port = _setter.getInteger(DEF_ENDPOINT_HTTP_PORT);
			if (_port <= 0)
				_port = 10101;
			
			log.info("FA3ST.env : Init port. {}", _port);
			
			_cors = _setter.getBoolean(DEF_ENDPOINT_HTTP_CORS);
			log.info("FA3ST.env : Init CORS. {}", _cors);
			
			_aasxFilePath = _setter.getString(DEF_PERSISTENCE_MODEL_PATH);
			log.info("FA3ST.env : Init AASX File Path. {}", _aasxFilePath);
			
			_aasxFileType = FA3STPersistenceModelFileTypes.of(_setter.getString(DEF_PERSISTENCE_MODEL_TYPE));
			log.info("FA3ST.env : Init AASX File Type. {}", _aasxFileType);
			
			_servicConfigfile = _setter.getString(DEF_CONFIG_PATH);
			
			_dbenable = _setter.getBoolean("fa3st.database.enable");	
			
			
			
			String value = System.getenv("ENV_DATABASE_MODEL_NAME");
			//value = "QualityInspectionEquipment";
			if(value == null)
			{
				log.info("FA3ST.env : Init Database model empty. ");
			}else
			{				
				if (_setter.containsKey(DEF_DATABASE_MODEL_INSTANCE))
					_setter.setProperty(DEF_DATABASE_MODEL_INSTANCE, value);		
				log.info("FA3ST.env : Init Database model name =  {}", value);
			}
			
		}
	}
	private int DBAASmodeluse(File aasxFile)
	{
		/// hark db 내용 읽어서 모델 변경 하기 기능 추가
				String modeldbname = "";
				
				if (_setter.containsKey(DEF_DATABASE_MODEL_INSTANCE))
					modeldbname = _setter.getString(DEF_DATABASE_MODEL_INSTANCE);
				
				String query = "SELECT * FROM ETRI_C_TB_AASXFILE A WHERE A.instancename = '"+ modeldbname +"';";
				String retur = "";
				
				List<Map<String, Object>> list = Database.selectMapList(query);

				for (Map<String, Object> item : list) {
					
					retur = item.get("aasx").toString();
				}
				if(retur.isBlank())
				{
					System.out.println("디비에 없는 모델 입니다.....");
				}else
				{
					if (aasxFile.exists()) {
			            // 파일 내용 삭제
						aasxFile.delete();
			            
			            try {
			                // 파일에 새로운 내용 쓰기
			                FileWriter writer = new FileWriter(aasxFile);
			                writer.write(retur);
			                writer.close();
			                System.out.println("파일 내용이 성공적으로 업데이트되었습니다.");
			            } catch (IOException e) {
			                e.printStackTrace();
			            }
			        } else {
			            try {
			                // 파일에 새로운 내용 쓰기
			                FileWriter writer = new FileWriter(aasxFile);
			                writer.write(retur);
			                writer.close();
			                System.out.println("파일 내용이 성공적으로 업데이트되었습니다.");
			            } catch (IOException e) {
			                e.printStackTrace();
			            }
			            System.out.println("파일을 찾을 수 없습니다.");
			        }
				}
		return 1;
	}
	private Service newService() throws ConfigurationException, AssetConnectionException, SQLException, IOException {
		File aasxFile = new File(_aasxFilePath);
		if (aasxFile == null || !aasxFile.exists())
		{
			log.error("No aasx file. {}", aasxFile.getAbsolutePath());
			return null;
		}
		
		if(_dbenable)
			DBAASmodeluse(aasxFile);
		
		
//		String newAAS=null;
//        try {
//            InetAddress localHost = InetAddress.getLocalHost();
//            String ipAddress = localHost.getHostAddress();
//            
//            System.out.println("내 IP 주소: " + ipAddress);
//            
//    		newAAS = retur.replace("setmyaddress", ipAddress+":"+_port);
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
//		
//		///사이값으로 변경하면 좋다 ip를 가져와서 설정하기
//
//		// db에도 써야할까? db는 특정값으로 세팅해놓고 그걸 찾아와서 변경해줄까?
		

				
		
		CoreConfig.Builder cc = CoreConfig
			.builder()
			.requestHandlerThreadPoolSize(_threadPoolSize);

		
//		if (_setter.containsKey(DEF_REGISTRY_HOST)) {
//			cc.registryHost(_setter.getString(DEF_REGISTRY_HOST));
//			
//			int rport = _setter.getInteger(DEF_REGISTRY_PORT);
//			if (rport <= 0)
//				log.info("No register port");
//			
//			cc.registryPort(rport);
			
//			if (_setter.containsKey(DEF_REGISTRY_AASBASEPATH))
//				cc.aasRegistryBasePath(DEF_REGISTRY_AASBASEPATH);
//			
//			if (_setter.containsKey(DEF_REGISTRY_SUBMODELBASEPATH))
//				cc.submodelRegistryBasePath(DEF_REGISTRY_SUBMODELBASEPATH);
//		}
		
		if (_setter.containsKey(DEF_VALIDATE_CONSTRAINTS))
			cc.validateConstraints(_setter.getBoolean(DEF_VALIDATE_CONSTRAINTS));
		
		if (_setter.containsKey(DEF_VALIDATE_IDENTIFIER_UNIQUENESS))
			cc.validateIdentifierUniqueness(_setter.getBoolean(DEF_VALIDATE_IDENTIFIER_UNIQUENESS));
		
		if (_setter.containsKey(DEF_VALIDATE_IDSHORT_UNIQUENESS))
			cc.validateIdShortUniqueness(_setter.getBoolean(DEF_VALIDATE_IDSHORT_UNIQUENESS));
		
//		if (_setter.containsKey(DEF_VALIDATE_VALUE_TYPES))
//			cc.validateValueTypes(_setter.getBoolean(DEF_VALIDATE_VALUE_TYPES));
		
		if (_setter.containsKey(DEF_ASSET_CONNECTION_RETRY_INTERVAL))
			cc.assetConnectionRetryInterval(_setter.getInteger(DEF_ASSET_CONNECTION_RETRY_INTERVAL));
		
		
		
		HttpEndpointConfig.Builder endcc = HttpEndpointConfig
				.builder();
				
		endcc.port(_port);
		endcc.cors(_cors);	

		
		
        if (new File(_servicConfigfile).exists()) {
            configFile = new File(_servicConfigfile);
            try {
            	log.info("Config: {} (default location)", configFile.getCanonicalFile());
            }
            catch (IOException e) {
            	log.info("Retrieving path of config file failed with {}", e.getMessage());
            }
            _servicConfig = ServiceConfigHelper.load(configFile);
        }
        
        
		
		Builder cfg = ServiceConfig
			.builder()
				.core(
					cc.build()
				)
				.endpoint(
					endcc.build()
				)
				.persistence(PersistenceInMemoryConfig.builder()
						.initialModelFile(new File(_aasxFilePath))
						.build())
				.fileStorage(new FileStorageInMemoryConfig())
				.assetConnections(_servicConfig.getAssetConnections())
				.messageBus(
						FA3STMessageBusConfig
							.builder()
							.build()
					);
		
		///나중에 위에처렁 파일로드해서 퍼시스턴트 설정 해줘야 한다
		
//		switch (_aasxFileType) {
//			case File:
//				String modelDir = getConfigStringFromSetter(DEF_PERSISTENCE_MODEL_DIR);
//				if (!isValidPath(modelDir))
//					log.info("FA3ST.persistence : Invalid model dir");
//				else if (modelDir.isEmpty())
//					modelDir =("Tmp/Fa3st");
//				else {
//					File modelPath = new File(modelDir);
//					if (!modelPath.exists()) {
//						modelPath.mkdirs();
//						log.info("FA3ST.persistence : Create Dir. {}", modelPath.getAbsolutePath());
//					}
//				}
//				log.info("FA3ST.env : Init Model DIR. {}", modelDir);
//				
//				String modelFormat = getConfigStringFromSetter(DEF_PERSISTENCE_MODEL_DATAFORMAT);
//				if (modelFormat.isEmpty())
//					modelFormat = "JSON";
//				
//				log.info("FA3ST.env : Init Model Format. {}", modelFormat);
//				
//				boolean modelKeepInit = getConfigBooleanFromSetter(DEF_PERSISTENCE_MODEL_KEEPINITIAL);
//				log.info("FA3ST.env : Init Model Keep initial. {}", modelKeepInit);
//				
//				environment = AASFull.createEnvironment();
//				
//				cfg
//					.persistence(
//						PersistenceFileConfig
//							.builder()
//							.initialModelFile(aasxFile)
//							.dataDir(modelDir)
//							.dataformat(
//								DataFormat.valueOf(modelFormat)
//							)
//							.keepInitial(modelKeepInit)
//							.build()
//					);
//				;
//				break;
//			case Memory:
//			default:
//				cfg
//					.persistence(
//						PersistenceInMemoryConfig
//							.builder()
//							.initialModelFile(aasxFile)
//							.build()
//					);
//				break;
//		}
		
		
		
		
		return new Service(
			cfg.build()
		);
	}
	
	private String getConfigStringFromSetter(String key) {
		return _setter.getString(key);
	}
	
	private boolean getConfigBooleanFromSetter(String key) {
		return _setter
			.getBoolean(key);
	}
	
	private void initInstance()  {
		if (!_setter.containsKey(DEF_INSTANCEID)) {
			_setter.setProperty(DEF_INSTANCEID, "harktest");
		}
			
		this._instanceid = _setter.getString(DEF_INSTANCEID);
	}
	
	public void start() {//throws ConfigurationException, AssetConnectionException, SQLException, MessageBusException, EndpointException, IOException  {
		
		try {
		initEnv();
		initInstance();
		
		File aasxFile = new File(_aasxFilePath);
		if (aasxFile == null || !aasxFile.exists())
			log.info("No aasx file. {}", aasxFile.getAbsolutePath());

		_service = newService();
		log.info("FA3ST.service.configured : {}, {}", _port, _threadPoolSize);
		
		log.info("FA3ST.service.starting : {}, {}", _port, _threadPoolSize);
		_service.start();
		
		
		
		log.info("FA3ST.service.started : {}, {}", _port, _threadPoolSize);
		
		
		} catch (Exception e) {
			log.error("FA3ST.service.exception : {}, {}", _port, e);
			//throw new IOException(e);
		}
	}


	public void stop(){
		if (_service == null)
			return;
		
		_service.stop();
		
		log.info("FA3ST.service.stopped : {}, {}", _port, _threadPoolSize);
	}
	
	public static boolean isValidPath(String path) {
		try {
			Paths.get(path);
		} catch (InvalidPathException | NullPointerException ex) {
			return false;
		}
		return true;
	}

	public void initinsertdb() throws SQLException {
		// TODO Auto-generated method stub
		////////////// 할수 있으까? 가져와서 db 넣기
		
		Environment initaasenv = _service.getAASEnvironment();
		AssetAdministrationShell aas = initaasenv.getAssetAdministrationShells().get(0);
		List<Submodel> submods = initaasenv.getSubmodels();
		BindingAAS initbindaas = new BindingAAS((DefaultAssetAdministrationShell)aas,BindingType.Update);
		
		Submodel se = null;
		DefaultProperty pp = null;
		String columnName = null;
		
		for (int i=0,l=submods.size(); i<l; i++) {
			se = submods.get(i);
			
			DefaultReference newReference = new DefaultReference.Builder().keys(new DefaultKey.Builder().value(se.getId()).build()).build();
					
							
			
			
			
			
			//TypeInfo test = _service.getTypeInfo(newReference);
			
			if (se instanceof Submodel) {
				new BindingSM((DefaultSubmodel)se, BindingType.Update, newReference).execute();
				continue;
			}

		}
		
		
		///////////////////값 찾아오기 테스트
		
		
	}
	public void initassertconnect() throws SQLException {
		// TODO Auto-generated method stub
		////////////// 할수 있으까? 가져와서 db 넣기
		

		
	}

}
