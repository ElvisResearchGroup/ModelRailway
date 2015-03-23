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
		connection.setCommandStationType("DCS51");
		connection.configure();
		connection.openPort(portName, "JMRI app");
	}

	/**
	 * Destroy this railway connection, dropping all resources.
	 */
	public void destroy() {
		connection.dispose();
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
		int opcode = arg0.getOpCode();
		switch(opcode) {
		case LnConstants.OPC_GPON:
		case LnConstants.OPC_GPOFF:
			event = new Event.PowerChanged(opcode == LnConstants.OPC_GPON);
			break;
		case LnConstants.OPC_LOCO_DIRF:
			boolean isForward = (arg0.getElement(2) & LnConstants.DIRF_DIR) == LnConstants.DIRF_DIR;
			event = new Event.DirectionChanged(arg0.getElement(1), isForward);
			break;
		case LnConstants.OPC_LOCO_SPD:
			int speed = arg0.getElement(2);
			if(speed == 1) {
				speed = -1;
			} else if(speed > 1) {
				speed = speed - 1;
			}
			event = new Event.SpeedChanged(arg0.getElement(1), speed, 0x7F-1);
			break;
		case LnConstants.OPC_LOCO_ADR:
			System.out.println("GOT ADDRESS - " + arg0.getElement(1) + ","  + arg0.getElement(2));
			break;
		default:
			// this is an unrecognised message, which we'll just silently ignore
			// for now.
		}
		
		System.out.println("RECEIVED EVENT: " + event);
		
		// Second, dispatch message as event (if understood)
		if(event != null) {
			for(Event.Listener listener : eventListeners) {
				listener.notify(event);
			}
		}		
	}
	
	public void notify(Event event) {
		int[] message;
		if (event instanceof Event.PowerChanged) {
			Event.PowerChanged ep = (Event.PowerChanged) event;
			message = new int[] { ep.isPowerOn() ? LnConstants.OPC_GPON
					: LnConstants.OPC_GPOFF };
		} else if (event instanceof Event.SpeedChanged) {
			Event.SpeedChanged ep = (Event.SpeedChanged) event;
			int slot = getLocomotiveSlot(ep.getLocomotive());
			message = new int[] { LnConstants.OPC_LOCO_SPD, slot,
					ep.getSpeed() };
		} else if (event instanceof Event.DirectionChanged) {
			Event.DirectionChanged ep = (Event.DirectionChanged) event;
			int cmd = ep.getDirection() ? LnConstants.DIRF_DIR : 0;
			message = new int[] { LnConstants.OPC_LOCO_SPD, ep.getLocomotive(),
					cmd };
		} else {
			throw new IllegalArgumentException("Unknown event encountered: "
					+ event.getClass().getName());
		}
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
}
