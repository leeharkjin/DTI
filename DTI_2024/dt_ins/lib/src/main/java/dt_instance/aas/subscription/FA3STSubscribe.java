package dt_instance.aas.subscription;

import de.fraunhofer.iosb.ilt.faaast.service.model.messagebus.SubscriptionId;
import de.fraunhofer.iosb.ilt.faaast.service.model.messagebus.event.change.ElementCreateEventMessage;
import de.fraunhofer.iosb.ilt.faaast.service.model.messagebus.event.change.ElementDeleteEventMessage;
import de.fraunhofer.iosb.ilt.faaast.service.model.messagebus.event.change.ElementUpdateEventMessage;
import de.fraunhofer.iosb.ilt.faaast.service.model.messagebus.event.change.ValueChangeEventMessage;
import de.fraunhofer.iosb.ilt.faaast.service.model.value.ElementValue;
import dt_instance.aas.ksx9101.BindingAAS;
import dt_instance.aas.ksx9101.BindingEV;
import dt_instance.aas.ksx9101.BindingSM;
import dt_instance.aas.ksx9101.BindingSMC;
import dt_instance.aas.ksx9101.FA3STNoTableException;
import dt_instance.aas.message.FA3STChangeMessage;
import dt_instance.aas.message.FA3STUpsertMessage;


import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.Referable;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FA3STSubscribe {
	
	static SubscriptionId _read;
	static SubscriptionId _create;
	static SubscriptionId _update;
	static SubscriptionId _delete;
	static SubscriptionId _change;
	
	static int doublebinding=0;
	
	public static final Logger log = LoggerFactory.getLogger(FA3STSubscribe.class);
	
	private static void binding(Referable ref, BindingType type, DefaultReference refelement) {
		doublebinding=1;
		log.debug("FA3ST.doublebinding start: ", doublebinding);
		try {
			if (ref instanceof AssetAdministrationShell) {
				new BindingAAS(
					(DefaultAssetAdministrationShell)ref,
					type
				).execute();
			}
			
			else if (ref instanceof Submodel) {
				new BindingSM(
					(DefaultSubmodel)ref,
					type,
					refelement
				).execute();
			}
			
			else if (ref instanceof SubmodelElementCollection) {
				new BindingSMC(
					(DefaultSubmodelElementCollection)ref,
					type,
					refelement
				).execute();
			}
		} catch (Exception e) {
			if (e.toString().contains("no table")) {
				log.debug("FA3ST.db : No table", e);
			} else {
				log.error("FA3ST.db : Execute Fail", e);
			}
		}
		doublebinding=0;
		log.debug("FA3ST.doublebinding end : ", doublebinding);
	}
	
	private static void bindingElement(ElementValue ref, BindingType type, DefaultReference refelement) {
		
		int test = 1;
		test= 3;
		log.debug("FA3ST.doublebinding valuechange : ", doublebinding);
		if(doublebinding==1)
			return;
		
		try {
			if (ref instanceof ElementValue) {
				new BindingEV(
					ref,
					type,
					refelement
				).execute();
			}
		} catch (Exception e) {
			if (e.toString().contains("no table")) {
				log.debug("FA3ST.db : No table", e);
			} else {
				log.error("FA3ST.db : Execute Fail", e);
			}
		}
	}

	public static void create() {
//		_read = FA3STMessageAdapter.subscribe(
//			ElementReadEventMessage.class,
//			x -> {
//				FA3STReadMessage msg = new FA3STReadMessage(x);
//				Referable ref = msg.getValue();
//				try {
//					binding(ref, BindingType.Read);
//				} catch (DatabaseException e) {
//					Log.error(FA3STSubscribe.class, "FA3ST.subscribe : Fail", e);
//				}
//			}
//		);
		
		_change = FA3STMessageAdapter.subscribe(
				ValueChangeEventMessage.class,
				x -> {
					FA3STChangeMessage msg = new FA3STChangeMessage(x);
					ElementValue ref = msg.getValue();
					DefaultReference refsubmodelInfo = msg.getElement();

					try {
						bindingElement(ref, BindingType.Update, refsubmodelInfo);
					} catch (Exception e) {
						log.error("FA3ST.subscribe : Fail", e);
					}
				}
			);
		_create = FA3STMessageAdapter.subscribe(
			ElementCreateEventMessage.class,
			x -> {
				FA3STUpsertMessage msg = new FA3STUpsertMessage(x);
				Referable ref = msg.getValue();
				DefaultReference refsubmodelInfo = msg.getElement();
				
				try {
					binding(ref, BindingType.Create, refsubmodelInfo);
				} catch (Exception e) {
					log.error("FA3ST.subscribe : Fail", e);
				}
			}
		);
		_update = FA3STMessageAdapter.subscribe(
			ElementUpdateEventMessage.class,
			x -> {
				FA3STUpsertMessage msg = new FA3STUpsertMessage(x);
				Referable ref = msg.getValue();
				DefaultReference refsubmodelInfo = msg.getElement();
				
				try {
					binding(ref, BindingType.Update, refsubmodelInfo);
				} catch (Exception e) {
					log.error( "FA3ST.subscribe : Fail", e);
				}
			}
		);
		_delete = FA3STMessageAdapter.subscribe(
			ElementDeleteEventMessage.class,
			x -> {
				FA3STUpsertMessage msg = new FA3STUpsertMessage(x);
				Referable ref = msg.getValue();
				DefaultReference refsubmodelInfo = msg.getElement();

				try {
					//binding(ref, BindingType.Delete, refsubmodelInfo);
				} catch (Exception e) {
					log.error("FA3ST.subscribe : Fail", e);
				}
			}
		);

			
		
		log.info("FA3ST.subscribe : Created");
	}
	
	public static void delete() {
	//	FA3STMessageAdapter.unsubscribe(_read);
		FA3STMessageAdapter.unsubscribe(_create);
		FA3STMessageAdapter.unsubscribe(_update);
		FA3STMessageAdapter.unsubscribe(_delete);
		FA3STMessageAdapter.unsubscribe(_change);
		
		log.info("FA3ST.subscribe : Deleted");
	}
	
}
