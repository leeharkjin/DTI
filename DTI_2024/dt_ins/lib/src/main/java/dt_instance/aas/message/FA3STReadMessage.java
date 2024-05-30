package dt_instance.aas.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import de.fraunhofer.iosb.ilt.faaast.service.model.messagebus.EventMessage;
import de.fraunhofer.iosb.ilt.faaast.service.model.messagebus.event.access.ReadEventMessage;
import dt_instance.dt_instance;
import org.eclipse.digitaltwin.aas4j.v3.model.Referable;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;

public class FA3STReadMessage {

	public static final Logger log = LoggerFactory.getLogger(FA3STReadMessage.class);
	private EventMessage _msg = null;
	
	public FA3STReadMessage(EventMessage msg) {
		this._msg = msg;
	}
	
	public DefaultReference getElement() {
		return (DefaultReference)_msg.getElement();
	}
	
	@SuppressWarnings("unchecked")
	public Referable getValue() {
		if (_msg == null)
			return null;
		
		try {
			return ((ReadEventMessage<Referable>)_msg).getValue();
		} catch (Exception e) {
			log.error("FA3ST.message.value : Cast fail", e);
			return null;
		}
	}
	
}
