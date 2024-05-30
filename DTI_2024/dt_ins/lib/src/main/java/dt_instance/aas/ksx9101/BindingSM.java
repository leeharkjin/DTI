package dt_instance.aas.ksx9101;

import java.sql.SQLException;
import java.util.List;

import dt_instance.aas.subscription.BindingType;
import org.eclipse.digitaltwin.aas4j.v3.model.Key;
import org.eclipse.digitaltwin.aas4j.v3.model.Referable;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementCollection;


public class BindingSM {

	DefaultSubmodel _value = null;
	BindingType _type = null;
	String _submodelinfo = null;
	String _refelement[] = new String[10];
	DefaultReference _DefaultReference= null;
	
	BindingSM() {

	}
	
	public BindingSM(DefaultSubmodel value, BindingType type, DefaultReference refinfo) {
		this();
		this._value = value;
		this._type = type;
		this._DefaultReference = refinfo;
		List<Key> tmp = refinfo.getKeys();
		
		for(int i =0 ; i < tmp.size(); i++)
		{			
			this._refelement[i] = tmp.get(i).getValue();
		}
		
		
	}
	
	public void execute() throws SQLException {
		executeModel(_value);
	}
	
	private void executeModel(DefaultSubmodel model) throws SQLException {
		List<SubmodelElement> submods = model.getSubmodelElements();
		SubmodelElement se = null;
		
		for (int i=0,l=submods.size(); i<l; i++) {
			se = submods.get(i);
			String tmpString = se.getIdShort();
			
			
			
			if (se instanceof SubmodelElementCollection) {
				new BindingSMC(
					(DefaultSubmodelElementCollection)se,
					_type,
					_DefaultReference
				).execute();
			}
		}
	}	
}
