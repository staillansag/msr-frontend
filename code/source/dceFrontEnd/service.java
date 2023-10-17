package dceFrontEnd;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
// --- <<IS-END-IMPORTS>> ---

public final class service

{
	// ---( internal utility methods )---

	final static service _instance = new service();

	static service _newInstance() { return new service(); }

	static service _cast(Object o) { return (service)o; }

	// ---( server methods )---




	public static final void alloueMemoire (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(alloueMemoire)>> ---
		// @sigtype java 3.5
		// [i] field:0:required quantite
		// [i] field:0:required duree
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
			String	quantite = IDataUtil.getString( pipelineCursor, "quantite" );
			String	duree = IDataUtil.getString( pipelineCursor, "duree" );
		pipelineCursor.destroy();
		// pipeline
		
		allocateMemory(Integer.parseInt(quantite), Integer.parseInt(duree));
		// --- <<IS-END>> ---

                
	}



	public static final void calculeSuiteFibonacci (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(calculeSuiteFibonacci)>> ---
		// @sigtype java 3.5
		// [i] field:0:required n
		// [o] field:0:required resultat
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		String	n = IDataUtil.getString( pipelineCursor, "n" );
		pipelineCursor.destroy();
		
		long resultat = fibonacci(Integer.parseInt(n));
		
		// pipeline
		IDataCursor pipelineCursor_1 = pipeline.getCursor();
		IDataUtil.put( pipelineCursor_1, "resultat", Long.toString(resultat) );
		pipelineCursor_1.destroy();
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
	public static long fibonacci(int n) {
	    if (n <= 1) return n;
	    return fibonacci(n-1) + fibonacci(n-2);
	}
	
	public static void allocateMemory(int n, int s) throws ServiceException {
	    List<byte[]> list = new ArrayList<>();
	    byte[] b = new byte[n * 1024 * 1024];  
	    new Random().nextBytes(b);  
	    list.add(b); 
	 
	    try {
	        Thread.sleep(s * 1000); 
	    } catch (InterruptedException e) {
	        throw new ServiceException(e);
	    }
	 
	    list.clear(); 
	}
	// --- <<IS-END-SHARED>> ---
}

