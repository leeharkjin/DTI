package dt_instance.aas.subscription;

import java.io.File;
import java.io.IOException;

import dt_instance.aas.service.FA3STService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FA3STModels {

	public static final Logger log = LoggerFactory.getLogger(FA3STModels.class);
	
	public void event() throws IOException {
		String path = FA3STService.Instance().setter().getString("fa3st.persistence.model.path");
		if ((path.isEmpty()))
			return;
		
		File file = new File(path);
		if (!file.isFile())
			log.info("No model file. {}", file.getAbsolutePath());
		
//		IData data = _m.get();
//		IData result = data.get(MessageFormat.Result);
//		result.set(MessageFormat.Result_Data, FileUtils.readFileToString(file));
	}
	
}
