package dt_instance.aas.message;

import de.fraunhofer.iosb.ilt.faaast.service.messagebus.MessageBusConfig;
import de.fraunhofer.iosb.ilt.faaast.service.messagebus.internal.MessageBusInternal;

/**
 * Configuration class for {@link MessageBusInternal}.
 */
public class FA3STMessageBusConfig extends MessageBusConfig<FA3STMessageBus> {

	public static Builder builder() {
		return new Builder();
	}

	private abstract static class AbstractBuilder<T extends FA3STMessageBusConfig, B extends AbstractBuilder<T, B>>
			extends MessageBusConfig.AbstractBuilder<FA3STMessageBus, T, B> {

	}

	public static class Builder extends AbstractBuilder<FA3STMessageBusConfig, Builder> {

		@Override
		protected Builder getSelf() {
			return this;
		}

		@Override
		protected FA3STMessageBusConfig newBuildingInstance() {
			return new FA3STMessageBusConfig();
		}
	}
}