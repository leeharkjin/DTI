package dt_instance.aas.ksx9101;

public class FA3STNoTableException  {

	private static final long serialVersionUID = 1533240743560925804L;
	
	private static final String DEF_PRIFIX = "FA3ST.";
	private static final String DEF_END = " - ";

	/**
	 * Instantiates a new invalid data exception.
	 */
	public FA3STNoTableException() {
		super();
	}

//	public FA3STNoTableException(String v) {
//		super(code(v));
//	}
//	
//
//	public FA3STNoTableException(String v, Object ... args) {
//		super(code(v), args);
//	}
//
//	public FA3STNoTableException(Throwable v) {
//		super(v);
//	}
	
	public static String code(String code) {
		if (code.isEmpty())
			code = "N/A";
		
		return DEF_PRIFIX + code + DEF_END;
	}
	
}
