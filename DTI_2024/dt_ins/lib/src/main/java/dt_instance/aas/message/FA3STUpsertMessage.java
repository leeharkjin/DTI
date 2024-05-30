package dt_instance.aas.message;

import de.fraunhofer.iosb.ilt.faaast.service.model.messagebus.EventMessage;
import de.fraunhofer.iosb.ilt.faaast.service.model.messagebus.event.change.ElementChangeEventMessage;
import org.eclipse.digitaltwin.aas4j.v3.model.Referable;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FA3STUpsertMessage {

	public static final Logger log = LoggerFactory.getLogger(FA3STUpsertMessage.class);
	private EventMessage _msg = null;
	
	public FA3STUpsertMessage(EventMessage msg) {
		this._msg = msg;
	}
	
	public DefaultReference getElement() {
		return (DefaultReference)_msg.getElement();
	}
	
	public Referable getValue() {
		if (_msg == null)
			return null;
		
		try {
			return ((ElementChangeEventMessage)_msg).getValue();
		} catch (Exception e) {
			log.error("FA3ST.message.value : Cast fail", e);
			return null;
		}
	}
	
}
