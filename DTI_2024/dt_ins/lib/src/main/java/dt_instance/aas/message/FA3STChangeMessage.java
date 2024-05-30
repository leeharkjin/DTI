package dt_instance.aas.message;

import de.fraunhofer.iosb.ilt.faaast.service.model.messagebus.EventMessage;
import de.fraunhofer.iosb.ilt.faaast.service.model.messagebus.event.change.ValueChangeEventMessage;
import de.fraunhofer.iosb.ilt.faaast.service.model.value.ElementValue;

import org.eclipse.digitaltwin.aas4j.v3.model.Referable;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FA3STChangeMessage {

	public static final Logger log = LoggerFactory.getLogger(FA3STChangeMessage.class);
	private EventMessage _msg = null;
	
	public FA3STChangeMessage(EventMessage msg) {
		this._msg = msg;
	}
	
	public DefaultReference getElement() {
		return (DefaultReference)_msg.getElement();
	}
	
	public ElementValue getValue() {
		if (_msg == null)
			return null;
		
		try {
			return ((ValueChangeEventMessage)_msg).getNewValue();
		} catch (Exception e) {
			log.error("FA3ST.message.value : Cast fail", e);
			return null;
		}
	}
	
}
