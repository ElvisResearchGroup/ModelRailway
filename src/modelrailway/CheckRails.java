package modelrailway;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import modelrailway.core.Controller;
import modelrailway.core.Event;
import modelrailway.core.Train;
import modelrailway.util.SimpleController;
import jmri.DccLocoAddress;
import jmri.DccThrottle;
import jmri.ThrottleListener;
import jmri.ThrottleManager;
import jmri.Turnout;
import jmri.jmrix.loconet.LnTrafficController;
import jmri.jmrix.loconet.LnTurnoutManager;
import jmri.jmrix.loconet.LocoNetListener;
import jmri.jmrix.loconet.LocoNetMessage;
import jmri.jmrix.loconet.locomon.Llnmon;
import jmri.jmrix.loconet.pr3.PR3Adapter;
import jmri.jmrix.loconet.pr3.PR3SystemConnectionMemo;
import jmri.util.Log4JUtil;

public class CheckRails implements LocoNetListener, ThrottleListener, Event.Listener {
	/**
	 * The PR3 Adaptor is the physical interface to the railway. It contains a
	 * USB port through which all loconet traffic is routed
	 */
	private PR3Adapter connection;

	/**
	 * The SystemConnectionMemo provides access to all the components of the
	 * LocoNet interface.
	 */
	private PR3SystemConnectionMemo memo;

	/**
	 * The list of locomotives on the system.
	 */
	private DccLocoAddress[] locomotives;

	/**
	 * The list of active locomotive throttles
	 */
	private DccThrottle[] throttles;

	/**
	 * The list of active turnouts
	 */
	private Turnout[] turnouts;

	private ArrayList<Event.Listener> eventListeners = new ArrayList<Event.Listener>();

	/**
	 * The linmonitor is useful for decoding loconet messages.
	 */
	private Llnmon linmon;

	/**
	 * verbose mode means dump out more debugging information.
	 */
	private volatile boolean verbose = true;
	
	public static void main(String[] args){
		try {
		   String port = args[0];

		   // Needed for connection on lab machines
		   System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyACM0");

		   // Construct the model railway assuming the interface (i.e. USB Cable)
		   // is on a given port. Likewise, we initialise it with three locomotives
		   // whose addresses are 1,2 + 3. If more locomotives are to be used, this
		   // needs to be updated accordingly.
		   System.out.println("Port: "+port);
		   final  CheckRails railway = new CheckRails(port,
					new int[] { 1});
		

		   // Add shutdown hook to make sure resources are released when quiting
		   // the application, even if the application is quit in a non-standard
		   // fashion.
		   Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			   @Override
			   public void run() {
				   System.out.println("Disconnecting from railway...");
				   railway.destroy();
			   }
		   }));
		   railway.readEvaluatePrintLoop();
		   
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

	/**
	 * Constructor starts the JMRI application running, and then returns.
	 * @throws Exception
	 */
	public CheckRails(String portName, int... locomotives) throws Exception {
		// Configure Log4J
		initLog4J();
		log.info(Log4JUtil.startupInfo("Main"));

		this.locomotives = new DccLocoAddress[locomotives.length];
		this.throttles = new DccThrottle[locomotives.length];
		for(int i = 0;i!=locomotives.length;++i) {
			this.locomotives[i] = new DccLocoAddress(locomotives[i],false);

		}
		// ============================================================
		// Create the loconet connection
		// ============================================================
		connection = new PR3Adapter();
		connection.openPort(portName, "Modelrailway App");
		// Is following line necessary?
		connection.setCommandStationType("DCS51 (Zephyr Xtra)");
		// The following line is extremely important. Without this line the PR3
		// assumes there is no command station and defaults to the programming
		// mode.
		connection.setOptionState("CommandStation", "false");
		connection.connect();
		connection.configure();
		memo = (PR3SystemConnectionMemo) connection.getSystemConnectionMemo();
		// Is following line necessary?
		memo.configureCommandStation(true, true, "DCS51 (Zephyr Xtra)", false, false);
		memo.getLnTrafficController().addLocoNetListener(LnTrafficController.ALL, this);
		requestThrottles();
		setupTurnouts(8);
	}

	@Override
	public void message(LocoNetMessage arg0) {
		if(linmon == null) {
			linmon = new Llnmon();
		}
		System.out.println("MESSAGE: " + linmon.displayMessage(arg0));

	}

	@Override
	public void notify(Event e) {
		// TODO Auto-generated method stub
		System.out.println("NOTIFICATION RECIEVED: "+e.toString());
		
	}

	/**
	 * Static method to get Log4J working before the rest of JMRI starts up.
	 */
	static protected void initLog4J() {
		// initialize log4j - from logging control file (lcf) only
		// if can find it!
		String logFile = "default.lcf";
		try {
			if (new java.io.File(logFile).canRead()) {
				org.apache.log4j.PropertyConfigurator.configure(logFile);
			} else {
				org.apache.log4j.BasicConfigurator.configure();
				org.apache.log4j.Logger.getRootLogger().setLevel(
						org.apache.log4j.Level.ERROR);
			}
		} catch (java.lang.NoSuchMethodError e) {
			log.error("Exception starting logging: " + e);
		}
		// install default exception handlers
		System.setProperty("sun.awt.exception.handler",
				jmri.util.exceptionhandler.AwtHandler.class.getName());
		Thread.setDefaultUncaughtExceptionHandler(new jmri.util.exceptionhandler.UncaughtExceptionHandler());
	}

	static private String getTurnoutString(int i) {
		String r = Integer.toString(i);
		while (r.length() < 3) {
			r = "0" + r;
		}
		return "LT" + r;
	}

	static Logger log = LoggerFactory.getLogger(ModelRailway.class.getName());
	

	/**
	 * Destroy this railway connection, dropping all resources.
	 */
	public void destroy() {
		connection.dispose();
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	/**
	 * Request throttles for all locomotives
	 */
	private void requestThrottles() {
		ThrottleManager manager = memo.getThrottleManager();
		for (DccLocoAddress loco : locomotives) {
			System.out.println("Requesting throttle for locomotive: " + loco);
			manager.requestThrottle(loco,this);
		}
	}

	/**
	 * Set up the turnouts on the railway. These are assumed to be numbered
	 * consecutively from 1.
	 *
	 * @param nTurnOuts
	 */
	private void setupTurnouts(int nTurnOuts) {
		LnTurnoutManager manager = memo.getTurnoutManager();
		this.turnouts = new Turnout[nTurnOuts];
		for(int i=0;i!=nTurnOuts;++i) {
			turnouts[i] = manager.provideTurnout(getTurnoutString(i+1));
		}
	}

	@Override
	public void notifyFailedThrottleRequest(DccLocoAddress arg0, String arg1) {
		System.out.println("FAILED REQUESTING THROTTLE: " + arg0);
	}

	@Override
	public void notifyThrottleFound(DccThrottle arg0) {
		System.out.println("OBTAINED THROTTLE: " + arg0);
		for(int i=0;i!=locomotives.length;++i) {
			if(locomotives[i].equals(arg0.getLocoAddress())) {
				System.out.println("MATCHED THROTTLE: " + arg0.getLocoAddress());
				throttles[i] = arg0;

			}
		}
	}
	public void readEvaluatePrintLoop() {
		final BufferedReader input = new BufferedReader(new InputStreamReader(
				System.in));

		try {
			System.out.println("Welcome to the Model Railway!");
			while (true) {
				System.out.print("> ");
				// Read the input line
				String line = input.readLine();
				if(line == null) continue;
				// Attempt to execute the input line
				boolean isOK = true; // fake it., ignore the input.
				if(!isOK) {
					// If we get here, then it means that the command was not
					// recognised. Therefore, print error!
					System.out.println("Error: command not recognised");
				}
			}
		} catch (IOException e) {
			System.err.println("I/O Error - " + e.getMessage());
		}
	}
}
