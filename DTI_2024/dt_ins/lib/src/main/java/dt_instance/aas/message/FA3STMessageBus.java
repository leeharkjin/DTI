package dt_instance.aas.message;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import de.fraunhofer.iosb.ilt.faaast.service.ServiceContext;
import de.fraunhofer.iosb.ilt.faaast.service.config.CoreConfig;
import de.fraunhofer.iosb.ilt.faaast.service.exception.MessageBusException;
import de.fraunhofer.iosb.ilt.faaast.service.messagebus.MessageBus;
import de.fraunhofer.iosb.ilt.faaast.service.model.messagebus.EventMessage;
import de.fraunhofer.iosb.ilt.faaast.service.model.messagebus.SubscriptionId;
import de.fraunhofer.iosb.ilt.faaast.service.model.messagebus.SubscriptionInfo;
import de.fraunhofer.iosb.ilt.faaast.service.util.Ensure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MessageBusInternal: Implements the internal MessageBus interface
 * subscribe/unsubscribe and publishes/dispatches EventMessages to subscribers.
 */
public class FA3STMessageBus implements MessageBus<FA3STMessageBusConfig> {

	private static final Logger LOGGER = LoggerFactory.getLogger(FA3STMessageBus.class);
	
	private final BlockingQueue<EventMessage> messageQueue;

	private final AtomicBoolean running;
	private final Map<SubscriptionId, SubscriptionInfo> subscriptions;
	private final ExecutorService executor;
	private FA3STMessageBusConfig config;

	public FA3STMessageBus() {
		running = new AtomicBoolean(false);
		subscriptions = new ConcurrentHashMap<>();
		messageQueue = new LinkedBlockingDeque<>();
		executor = Executors.newSingleThreadExecutor();
	}

	@Override
	public FA3STMessageBusConfig asConfig() {
		return config;
	}

	@Override
	public void init(CoreConfig coreConfig, FA3STMessageBusConfig config, ServiceContext serviceContext) {
		this.config = config;
		running.set(false);
	}

	@Override
	public void publish(EventMessage message) throws MessageBusException {
		if (message != null) {
			try {
				messageQueue.put(message);
				System.out.println("EventMessage : " + message.getClass().getName());
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new MessageBusException("adding message to queue failed", e);
			}
		}
	}

	private void run() {
		running.set(true);
		
		while (running.get()) {
			try {
				
				EventMessage message = messageQueue.take();

				Thread.sleep(100);
				/// 땜빵 코드 두개의 이벤트가 동시에 들어왔을때 value change 걸러내기
				LOGGER.info("FA3ST.subscribe queue size : {}", messageQueue.size());
				if(messageQueue.size()>0)
				{
					message = messageQueue.take();
				}	
				
				Class<? extends EventMessage> messageType = message.getClass();
				for (SubscriptionInfo subscription : subscriptions.values()) {
					if (subscription.getSubscribedEvents().stream().anyMatch(x -> x.isAssignableFrom(messageType))
							&& subscription.getFilter().test(message.getElement())) {
						subscription.getHandler().accept(message);
						LOGGER.info("FA3ST.subscribe call : {}", message);
					}
				}
			} catch (InterruptedException e) {
				LOGGER.error("FA3ST.subscribe : Fail", e);
			}
		}
	}

	@Override
	public void start() {
		executor.submit(() -> run());
	}

	@Override
	public void stop() {
        running.set(false);
        executor.shutdown();
        try {
            executor.awaitTermination(2, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            LOGGER.error("interrupted while waiting for shutdown.", e);
            Thread.currentThread().interrupt();
        }
	}

	@Override
	public SubscriptionId subscribe(SubscriptionInfo subscriptionInfo) {
		Ensure.requireNonNull(subscriptionInfo, "subscriptionInfo must be non-null");
		SubscriptionId subscriptionId = new SubscriptionId();
		subscriptions.put(subscriptionId, subscriptionInfo);
		return subscriptionId;
	}

	@Override
	public void unsubscribe(SubscriptionId id) {
		subscriptions.remove(id);
	}

}