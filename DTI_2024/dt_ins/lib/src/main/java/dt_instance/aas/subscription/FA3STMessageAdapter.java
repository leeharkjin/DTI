package dt_instance.aas.subscription;

import de.fraunhofer.iosb.ilt.faaast.service.model.messagebus.EventMessage;
import de.fraunhofer.iosb.ilt.faaast.service.model.messagebus.SubscriptionId;
import de.fraunhofer.iosb.ilt.faaast.service.model.messagebus.SubscriptionInfo;
import dt_instance.dt_instance;
import dt_instance.aas.message.FA3STMessageBus;
import dt_instance.aas.message.IFA3STMessageHandler;
import dt_instance.aas.service.FA3STService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class FA3STMessageAdapter {

	
	public static final Logger log = LoggerFactory.getLogger(FA3STMessageAdapter.class);
	
	
	private static FA3STMessageBus getMessageBus() {
		FA3STService service = dt_instance.getService();
		if (service == null)
			return null;
		
		return service.getMessageBus();
	}
	
	public static void subscribe(IFA3STMessageHandler handler) {
		subscribe(EventMessage.class, handler);
	}
	
	public static SubscriptionId subscribe(Class<? extends EventMessage> messageClass, IFA3STMessageHandler handler) {
		try {
			return getMessageBus()
				.subscribe(
					SubscriptionInfo.create(
						messageClass,
				        x -> {
				            if (handler == null)
				            	return;
				            
				            try {
				            	handler.subscribe(x);
							} catch (Exception e) {
								log.error("FA3ST.subscribe.handler : Fail", e);
							}
				        }
			        )
				);
		} catch (Exception e) {
			log.error("FA3ST.subscribe.handler : Fail", e);
		}
		return null;
	}
	
	public static void unsubscribe(SubscriptionId id) {
		getMessageBus()
			.unsubscribe(id);
	}
	
}
