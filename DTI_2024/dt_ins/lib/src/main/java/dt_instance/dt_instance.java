///*
// * This Java source file was generated by the Gradle 'init' task.
// */
//package dt_ins;
//
//
//
//public class Library {
//    public boolean someLibraryMethod() {
//    	  System.out.println("hell");
//        return true;
//    }
//}
/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package dt_instance;

import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dt_instance.aas.ksx9101.BindingTableSchema;
import dt_instance.aas.ksx9101.Database;
import dt_instance.aas.ksx9101.InsertQueryBuilder;
import dt_instance.aas.service.FA3STService;
import dt_instance.aas.subscription.FA3STSubscribe;
import dt_instance.aas.subscription.FA3STSubscribeCreate;
import picocli.CommandLine;

public class dt_instance {
	
	public static final Logger log = LoggerFactory.getLogger(dt_instance.class);
    private static final CountDownLatch SHUTDOWN_FINISHED = new CountDownLatch(1);
    private static final CountDownLatch SHUTDOWN_REQUESTED = new CountDownLatch(1);
    public boolean someLibraryMethod() {
    	
    	Logger logger = LoggerFactory.getLogger("dt_instance");
        
        return true;
    }
    private static int exitCode = -1;
    public static void main(String[] args) throws Exception {
        System.out.println("안녕하세요! 애플리케이션입니다.");

        start();
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
            	service.stop();
                if (exitCode == CommandLine.ExitCode.OK) {
                    SHUTDOWN_REQUESTED.countDown();
                    try {
                        SHUTDOWN_FINISHED.await();
                    }
                    catch (InterruptedException ex) {
                        log.error("Error while waiting for FA³ST Service to gracefully shutdown");
                        Thread.currentThread().interrupt();
                    }
                    finally {
                        log.info("Goodbye!");
                    }
                }
            }
        });
        
    }
    
	
	private static FA3STService service;

	
	public static FA3STService getService() {
		return service;
	}

	public static void start() throws Exception {
		
		boolean dbenable=false;
		
		dbenable = Database.Init();
				
		service = FA3STService.Instance();
		//ComponentService.add(service);
		
		service.start();


		if(dbenable)
		{
		
			FA3STSubscribe.create();
				
			BindingTableSchema.init();
			
			InsertQueryBuilder.init(Database.getJdbi());
			
			service.initinsertdb();
		}
		
	}

	public static void stop() throws Exception {
		if (service != null)
			service.stop();
		
		FA3STSubscribe.delete();
	}
}

