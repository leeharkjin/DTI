package dt_instance.aas.message;

import de.fraunhofer.iosb.ilt.faaast.service.model.messagebus.EventMessage;

public interface IFA3STMessageHandler {

	void subscribe(EventMessage message);
	
}
