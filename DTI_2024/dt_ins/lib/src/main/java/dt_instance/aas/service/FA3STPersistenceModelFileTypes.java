package dt_instance.aas.service;



public enum FA3STPersistenceModelFileTypes {

	Memory,
	File;
	
	public static FA3STPersistenceModelFileTypes of(String value) {
		if (value.isEmpty())
			return Memory;
		
		switch (value.toUpperCase()) {
			case "MEMORY":
			default:
				return Memory;
			case "FILE":
				return File;
		}
	}
	
}
