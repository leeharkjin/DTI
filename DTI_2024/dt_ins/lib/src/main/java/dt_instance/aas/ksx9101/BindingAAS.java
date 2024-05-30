package dt_instance.aas.ksx9101;

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.Identifiable;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dt_instance.aas.subscription.BindingType;

public class BindingAAS {

	public static final Logger log = LoggerFactory.getLogger(BindingAAS.class);
	DefaultAssetAdministrationShell _value = null;
	BindingType _type = null;
	
	public BindingAAS(DefaultAssetAdministrationShell value, BindingType type) {
		this._value = value;
		this._type = type;
	}
	
	public void execute() {
		log.info("FA3ST.info : {}", toString());
	}
	
	@Override
	public String toString() {
		String id = _value.getId();
		
		List<Reference> submods = _value.getSubmodels();
		
		StringBuilder sb = new StringBuilder();
		sb.append("\n==================================================================\n");
		sb.append("=== Type : " + _type);
		sb.append("\n");
		sb.append("=== IdShort : " + _value.getIdShort());
		sb.append("\n");
		sb.append("=== Id : " + id);
		sb.append("\n");
		sb.append("=== Submodels : " + (submods == null ? 0 : submods.size()) + "/ea");
		sb.append("\n==================================================================\n");
		
		return sb.toString();
	}
	
}
