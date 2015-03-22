package modelrailway;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.slf4j.LoggerFactory;

import jmri.InstanceManager;
import jmri.jmrix.loconet.*;
import jmri.jmrix.loconet.pr3.PR3Adapter;
import jmri.util.Log4JUtil;
import jmri.web.server.WebServerManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements LocoNetListener {
	// The PR3 Adaptor is the physical interface to the railway. It contains a
	// USB port through which all loconet traffic is routed
	private PR3Adapter connection;
	private LnPacketizer trafficController;
	
	private LocoNetSlot[] trains;
	private LocoNetThrottle[] throttles;

	/**
	 * Constructor starts the JMRI application running, and then returns.
	 * @throws Exception 
	 */
	public Main(String portName, int numTrains) throws Exception {
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
	
	public void sendMessage(int... message) {
		trafficController.sendLocoNetMessage(new LocoNetMessage(message));
	}
	
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
	// LocoNet Message Listener
	// ===============================================================
	
	/**
	 * The message listener which is called for all broadcasted loconet
	 * messages.
	 */
	@Override
	public void message(LocoNetMessage arg0) {		
		System.out.println("RECEIVED MESSAGED");
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

	static Logger log = LoggerFactory.getLogger(Main.class.getName());

	// ===============================================================
	// Main entry point
	// ===============================================================
	public static void main(String args[]) throws Exception {
		Main railway = new Main(args[0], 3);
		float speed = 0.1f;
		boolean direction = true;
//		
//		while (true) {
//			Thread.currentThread().sleep(1000);
//			railway.setSpeedAndDirection(1, true, speed);
//			if (speed > 0.9f) {
//				direction = false;
//			} else if (speed < 0.0f) {
//				direction = true;
//			}
//			if (direction) {
//				speed = Math.min(1.0f, speed + 0.25f);
//			} else {
//				speed = Math.max(0.0f, speed - 0.1f);
//			}
//		}
		while(true) {
			Thread.currentThread().sleep(100);
			railway.sendMessage(0x83);
		}
	}
}
