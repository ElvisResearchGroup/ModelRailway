package modelrailway;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import modelrailway.core.Event;

import org.slf4j.LoggerFactory;

import jmri.InstanceManager;
import jmri.jmrix.loconet.*;
import jmri.jmrix.loconet.pr3.PR3Adapter;
import jmri.util.Log4JUtil;
import jmri.web.server.WebServerManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModelRailway implements LocoNetListener, Event.Listener {
	// The PR3 Adaptor is the physical interface to the railway. It contains a
	// USB port through which all loconet traffic is routed
	private PR3Adapter connection;
	private LnPacketizer trafficController;
	
	private LocoNetSlot[] trains;
	private LocoNetThrottle[] throttles;
	
	private ArrayList<Event.Listener> eventListeners = new ArrayList<Event.Listener>();

	/**
	 * Constructor starts the JMRI application running, and then returns.
	 * @throws Exception 
	 */
	public ModelRailway(String portName, int numTrains) throws Exception {
		// Configure Log4J
		initLog4J();
		log.info(Log4JUtil.startupInfo("Main"));

		// ============================================================
		// Create the loconet connection
		// ============================================================
		connection = new PR3Adapter();
		connection.setPort(portName);
		connection.configureBaudRate("57600");
		connection.setCommandStationType("DCS51");
		// connection.configureBaudRate("9600");
		connection.openPort(portName, "JMRI app");
		connection.configure();
		//connection.connect();

		// ============================================================
		// Create the loconet traffic controller
		// ============================================================
		trafficController = new LnPacketizer();		
		trafficController.startThreads();
		trafficController.connectPort(connection);
		trafficController.addLocoNetListener(LocoNetInterface.ALL,this);
		
		//
		trains = new LocoNetSlot[numTrains];
		throttles = new LocoNetThrottle[numTrains];
		for(int i=0;i!=numTrains;++i) {
			trains[i] = new LocoNetSlot(i);
		}
	}

	

	// ===============================================================
	// Train and Switch Controls
	// ===============================================================
	
	/**
	 * Set the speed and direction of a given locomotive.
	 * 
	 * @param id
	 * @param direction
	 *            true indicates forward, false is backwards.
	 * @param speed
	 *            Number between 0 and 1
	 */
	public void setSpeedAndDirection(int id, boolean direction, float speed) {
		System.out.println("SETTING SPEED: " + id + " : " + speed);
		LocoNetThrottle throttle = throttles[id];
		if (throttle == null) {
			throttle = new LocoNetThrottle(
					(LocoNetSystemConnectionMemo) connection
							.getSystemConnectionMemo(),
					trains[id]);
			throttles[id] = throttle;
		}
		throttle.setIsForward(direction);
		throttle.setSpeedSetting(speed);
		throttle.dispatch();
	}
		
	// ===============================================================
	// Message Listeners and Handlers
	// ===============================================================
	

	public void addEventListener(Event.Listener listener) {
		this.eventListeners.add(listener);
	}
	
	/**
	 * The message listener which is called for all broadcasted loconet
	 * messages. This processes each message and turns it into an appropriate
	 * instance of Event.
	 */
	@Override
	public void message(LocoNetMessage arg0) {
		Event event = null;
		
		// First, process loconet message
		int opcode = arg0.get(0);
		switch(opcode) {
		case OPC_GPON:
		case OPC_GPOFF:
			event = new Event.PowerChanged(opcode == OPC_GPON);
			break;
		case OPC_LOCO_DIRF:
			event = new Event.DirectionChanged(arg0.get(1), (arg0.get(2) & SL_DIR) == SL_DIR);
			break;
		case OPC_LOCO_SPD:
			int speed = arg0.get(2);
			if(speed == 1) {
				speed = -1;
			} else if(speed > 1) {
				speed = speed - 1;
			}
			event = new Event.SpeedChanged(arg0.get(1), speed, 0x7F-1);
			break;
		default:
			// this is an unrecognised message, which we'll just silently ignore
			// for now.
		}
		
		// Second, dispatch message as event (if understood)
		if(event != null) {
			for(Event.Listener listener : eventListeners) {
				listener.notify(event);
			}
		}		
	}
	
	public void notify(Event event) {
		trafficController.sendLocoNetMessage(new LocoNetMessage(message));
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

	static Logger log = LoggerFactory.getLogger(ModelRailway.class.getName());

	// Opcodes
	private static final int OPC_IDLE = 0x85;
	private static final int OPC_GPON = 0x83;
	private static final int OPC_GPOFF = 0x82;
	private static final int OPC_LOCO_DIRF = 0xA1;
	private static final int OPC_LOCO_SPD = 0xA0;

	// Direction masks
	private static final int SL_DIR = 32;
	private static final int SL_F0 = 16; // directional lighting
	private static final int SL_F4 = 8;  
	private static final int SL_F3 = 4;  
	private static final int SL_F2 = 2;  
	private static final int SL_F1 = 1;  
}
