package dt_instance.aas.subscription;



public class FA3STSubscribeCreate{

	public void start() throws Exception {
		FA3STSubscribe.create();

	}

	public void stop() throws Exception {
		
		FA3STSubscribe.delete();
		
	}

}
