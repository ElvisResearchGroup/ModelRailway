package modelrailway.testing;

import java.lang.reflect.Method;
import java.util.ArrayList;

import modelrailway.Main;
import modelrailway.ModelRailway;
import modelrailway.Main.Command;
import modelrailway.core.Controller;
import modelrailway.core.Event;
import modelrailway.core.Route;
import modelrailway.core.Train;
import modelrailway.core.Event.Listener;
import modelrailway.core.Event.SectionChanged;
import modelrailway.simulation.Straight.StraightDblRing;
import modelrailway.simulation.Switch;
import modelrailway.util.SimulationTrack;
import modelrailway.util.TrainController;

public class MultiTrainHardwareTest extends Main{

	public MultiTrainHardwareTest(ModelRailway railway, Controller controller) {
		super(railway, controller);
		Command[] cmd = this.getCommands();
		Command htest0 = this.new Command("hardwareTest0", MultiTrainHardwareTest.getMethod("hardwareTest0"));
		Command htest1 = this.new Command("hardwareTest1", MultiTrainHardwareTest.getMethod("hardwareTest1"));
		Command htest2 = this.new Command("hardwareTest2", MultiTrainHardwareTest.getMethod("hardwareTest2"));
		Command htest3 = this.new Command("hardwareTest3", MultiTrainHardwareTest.getMethod("hardwareTest3"));
		Command htest4 = this.new Command("hardwareTest4", MultiTrainHardwareTest.getMethod("hardwareTest4"));
		Command[] cmd2 = new Command[cmd.length+5];
		System.arraycopy(cmd, 0, cmd2, 0, cmd.length);
		cmd2[cmd2.length-5] = htest4;
		cmd2[cmd2.length-1] = htest0;
		cmd2[cmd2.length-2] = htest1;
		cmd2[cmd2.length-3] = htest2;
		cmd2[cmd2.length-4] = htest3;
		super.setCommands(cmd2);

		// TODO Auto-generated constructor stub
	}
	public static Method getMethod(String name, Class... paramTypes) {
		try {
			return MultiTrainHardwareTest.class.getMethod(name, paramTypes);
		} catch (Exception e) {
			throw new RuntimeException("No such method: " + name, e);
		}
	}

	public ModelRailway getRailway(){
		return rails;
	}
	public Controller getCtl(){
		return ctl;
	}
	private static ModelRailway rails;
	private static Controller ctl;

	public MultiTrainHardwareTest(){
		super(null,null);

	}

	private static final SimulationTrack sim0 = new SimulationTrack();

	private static final Train[] trains = {new Train(16,true), new Train(8,true)};

	public static void main(String args[]) throws Exception {
		///String port = args[0];
		String port = MultiTrainHardwareTest.port;
		System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyACM0");

		// Needed for connection on lab machines


		// Construct the model railway assuming the interface (i.e. USB Cable)
		// is on a given port. Likewise, we initialise it with three locomotives
		// whose addresses are 1,2 + 3. If more locomotives are to be used, this
		// needs to be updated accordingly.

		final ModelRailway railway = new ModelRailway(port,
				new int[] {1,2}); // 2 1
		rails = railway;
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

		// Enter Read, Evaluate, Print loop.
		Controller controller = new TrainController(trains,new Integer[]{2,1},rails, sim0);

		ctl = controller;
		System.out.println("got to print Loop");
		MultiTrainHardwareTest tst = new MultiTrainHardwareTest(rails,controller);
		tst.readEvaluatePrintLoop();
	}


	final static String port="/dev/ttyACM0";

	public void setRoute(int id, Route rt, StraightDblRing ring, TrainController ctl){
		//Integer fst = rt.firstSection();
		//Integer snd = rt.nextSection(rt.firstSection());
		ctl.trainOrientations().get(id).setSection(rt.firstSection());
		ring.getSectionNumberMap().get(rt.firstSection()).getEntryRequests().add(id);
		ring.getSectionNumberMap().get(rt.nextSection(rt.firstSection())).getEntryRequests().add(id);
	}
	
	public Listener registerListenerNoStop(Controller controller, StraightDblRing ring , final ArrayList<Integer> outputArray){
		
		///final Thread th = Thread.currentThread();
		final Listener lst = new Listener(){
			public void notify(Event e){
 				System.out.println("event in unit test: "+e.toString());

 				if(e instanceof Event.SectionChanged && ((SectionChanged) e).getInto()){
 				  Integer i = ((((SectionChanged)e).getSection() -1)* 2) +1;
 				  outputArray.add(i);
 				  System.out.println("sectionchanged in unit test into section: "+ i);

 				}
 				else if(e instanceof Event.SpeedChanged){
 				   System.out.println("=================================================================");
 				   System.out.println("speed changed in test: "+((Event.SpeedChanged) e).getLocomotive());
 				   System.out.println("==================================================================");
 				}
 				System.out.println(" 1: "+ring.getSectionNumberMap().get(1).getEntryRequests().toString());
 				System.out.println(" 2: "+ring.getSectionNumberMap().get(2).getEntryRequests().toString());
 				System.out.println(" 3: "+ring.getSectionNumberMap().get(3).getEntryRequests().toString());
 				System.out.println(" 4: "+ring.getSectionNumberMap().get(4).getEntryRequests().toString());
 				System.out.println(" 5: "+ring.getSectionNumberMap().get(5).getEntryRequests().toString());
 				System.out.println(" 6: "+ring.getSectionNumberMap().get(6).getEntryRequests().toString());
 				System.out.println(" 7: "+ring.getSectionNumberMap().get(7).getEntryRequests().toString());
 				System.out.println(" 8: "+ring.getSectionNumberMap().get(8).getEntryRequests().toString());
 				System.out.println(" 9: "+ring.getSectionNumberMap().get(9).getEntryRequests().toString());
 				System.out.println(" 10: "+ring.getSectionNumberMap().get(10).getEntryRequests().toString());
 				System.out.println(" 11: "+ring.getSectionNumberMap().get(11).getEntryRequests().toString());
 				System.out.println(" 12: "+ring.getSectionNumberMap().get(12).getEntryRequests().toString());
 				System.out.println(" 13: "+ring.getSectionNumberMap().get(13).getEntryRequests().toString());
 				System.out.println(" 14: "+ring.getSectionNumberMap().get(14).getEntryRequests().toString());
 				System.out.println(" 15: "+ring.getSectionNumberMap().get(15).getEntryRequests().toString());
 				System.out.println(" 16: "+ring.getSectionNumberMap().get(16).getEntryRequests().toString());
 			}};

		controller.register(lst);
		return lst;
		
	}
	
	public Listener registerListenerWithStop(Controller controller, StraightDblRing ring, final ArrayList<Integer> outputArray){
		final Thread th = Thread.currentThread();
		final Listener lst = new Listener(){
			public void notify(Event e){
 				System.out.println("event in unit test: "+e.toString());

 				if(e instanceof Event.SectionChanged && ((SectionChanged) e).getInto()){
 				  Integer i = ((((SectionChanged)e).getSection() -1)* 2) +1;
 				  outputArray.add(i);
 				  System.out.println("sectionchanged in unit test into section: "+ i);
 				}
 				else if(e instanceof Event.SpeedChanged){

 				   System.out.println("speed changed in test: "+((Event.SpeedChanged) e).getLocomotive());
 				}

 				if(controller.train(0).currentSection() == 3){
 					controller.stop(0);
 					controller.stop(1);
 					th.interrupt();
 				}
 			}

		};

		controller.register(lst);
		return lst;
	}

	public void hardwareTest0(){
		final TrainController controller =(TrainController) ctl;
		final StraightDblRing ring = sim0.getTrack();
		ring.getSectionNumberMap();
		final Route route = new Route(true, 8,1,2,3,4,5,6,7,8,1,2);
		final Route route2 = new Route(true, 16,9,10,3,4,5,6,7,8,9,10);
		setRoute(1, route2, ring, (TrainController) controller);
		setRoute(2, route,  ring, (TrainController) controller);

		rails.register(controller);
		controller.register(rails);

		// configure switches on tracks for the starting sections.

		ctl.set(((Switch)ring.getSectionNumberMap().get(16).get(0)).getSwitchID(), false);
		ctl.set(((Switch)ring.getSectionNumberMap().get(9).get(0)).getSwitchID(), true);
		ctl.set(((Switch)ring.getSectionNumberMap().get(8).get(0)).getSwitchID(), false);
		final ArrayList<Integer> outputArray = new ArrayList<Integer>();
		Listener lst = registerListenerNoStop(controller, ring , outputArray);
		
		controller.start(2, route2);
 		controller.start(1, route);

		try{
			Thread.currentThread().join();
		}catch(InterruptedException e){
			System.out.println("hardwareTest0");
			//System.out.println("route: "+route.toString());
			System.out.println("output: "+outputArray.toString());
		}

		((TrainController) controller).deregister(lst);

	}

	/**
	 * When a fast train is behind a slow train, check that the fast train will not run into the back of the slow one.
	 */
	public void hardwareTest1(){
		final Controller controller = getCtl();
		// Enter Read, Evaluate, Print loop.

		StraightDblRing ring = sim0.getTrack();
		ring.getSectionNumberMap();

		((TrainController)controller).trainOrientations().get(1).setSection(1);
		final Route route = new Route(true, 1,2,3,4,5,6,7,8,1,2,3);
		final Route route2 = new Route(true, 4,5,6,7,8,1,2,3,4,5);

		((TrainController)controller).trainOrientations().get(0).setSection(4);
		System.out.println("Route 1: "+ sim0.getSections().get(1).getEntryRequests().toString());
		System.out.println("Route 2: "+ sim0.getSections().get(2).getEntryRequests().toString());

		final ArrayList<Integer> outputArray = new ArrayList<Integer>();
		
		Listener lst = registerListenerWithStop(controller,  ring, outputArray);
		controller.start(0, route2);
 		controller.start(1, route);

		try{
			Thread.currentThread().join();
		}catch(InterruptedException e){

			System.out.println("1: "+sim0.getSections().get(1).getEntryRequests().toString());
			System.out.println("2: "+sim0.getSections().get(2).getEntryRequests().toString());
			System.out.println("3: "+sim0.getSections().get(3).getEntryRequests().toString());
			System.out.println("4: "+sim0.getSections().get(4).getEntryRequests().toString());
			System.out.println("5: "+sim0.getSections().get(5).getEntryRequests().toString());
			System.out.println("6: "+sim0.getSections().get(6).getEntryRequests().toString());
			System.out.println("7: "+sim0.getSections().get(7).getEntryRequests().toString());
			System.out.println("8: "+sim0.getSections().get(8).getEntryRequests().toString());

		}
		((TrainController) controller).deregister(lst);

	}
	/**
	 *  Check that the controller can control a second train.
	 */
	public void hardwareTest2(){
		final Controller controller = getCtl();
		// Enter Read, Evaluate, Print loop.

		StraightDblRing ring = sim0.getTrack();

		ring.getSectionNumberMap();
		((TrainController)controller).trainOrientations().get(0).setSection(16);

		ctl.set(((Switch)ring.getSectionNumberMap().get(16).get(0)).getSwitchID(), false);
		ctl.set(((Switch)ring.getSectionNumberMap().get(9).get(0)).getSwitchID(), true);
		ctl.set(((Switch)ring.getSectionNumberMap().get(8).get(0)).getSwitchID(), false);

		final Route route = new Route(true, 8,1,2,3,4,5,6,7,8,1);



		final ArrayList<Integer> outputArray = new ArrayList<Integer>();
		Listener lst = this.registerListenerWithStop(controller, ring, outputArray);
 		controller.start(1, route);
		try{
			Thread.currentThread().join();
		}catch(InterruptedException e){
			System.out.println("hardwareTest2");
			System.out.println("route: "+route.toString());
			System.out.println("output: "+outputArray.toString());
		}
		((TrainController) controller) .deregister(lst);
	}
	/**
	 *  Check that the train can stop when a section is occupied.
	 */
	public void hardwareTest3(){
		final Controller controller = getCtl();
		if(controller == null) System.out.println("There is no controller supplied");
		// Enter Read, Evaluate, Print loop.
		StraightDblRing ring = sim0.getTrack();

		ring.getSectionNumberMap();
		rails.register(controller);
		controller.register(rails);
		System.out.println("trainOrientations: "+((TrainController) controller).trainOrientations().toString());
		System.out.println("trainOrientation2: "+ ((TrainController) controller).trainOrientations().get(2));

		((TrainController)controller).trainOrientations().get(2).setSection(5); // make train 0 placed in section 5
		ring.getSectionNumberMap().get(5).getMovableSet().add(0);

		ctl.set(((Switch)ring.getSectionNumberMap().get(16).get(0)).getSwitchID(), false);
		ctl.set(((Switch)ring.getSectionNumberMap().get(9).get(0)).getSwitchID(), true);
		ctl.set(((Switch)ring.getSectionNumberMap().get(8).get(0)).getSwitchID(), false);

		final Route route = new Route(true, 8,1,2,3,4,5,6,7,8,1);
		this.setRoute(1, route, ring, (TrainController) ctl);
		
		final ArrayList<Integer> outputArray = new ArrayList<Integer>();
		Listener lst = this.registerListenerWithStop(controller, ring, outputArray);

 		controller.start(1, route);

		try{
			Thread.currentThread().join();
		}catch(InterruptedException e){
			System.out.println("hardwareTest2");
			System.out.println("route: "+route.toString());
			System.out.println("output: "+outputArray.toString());
		}


		((TrainController) controller) .deregister(lst);



	}

	/**
	 *  Test that a train can restart after stopping
	 *
	 */
	public void hardwareTest4(){
		final Controller controller = getCtl();
		StraightDblRing ring = sim0.getTrack();
		ring.getSectionNumberMap();
		((TrainController)controller).trainOrientations().get(0).setSection(5); // make train 0 placed in section 5
		ring.getSectionNumberMap().get(5).getMovableSet().add(0);
		ring.getSectionNumberMap().get(5).getEntryRequests().offer(0);
		ctl.set(((Switch)ring.getSectionNumberMap().get(16).get(0)).getSwitchID(), false);
		ctl.set(((Switch)ring.getSectionNumberMap().get(9).get(0)).getSwitchID(), true);
		ctl.set(((Switch)ring.getSectionNumberMap().get(8).get(0)).getSwitchID(), false);
		final Route route = new Route(true, 8,1,2,3,4,5,6,7,8,1);
		final Route route2= new Route(true, 5,6,7,8,1,2,3,4);
		final ArrayList<Integer> outputArray = new ArrayList<Integer>();
		final Thread th = Thread.currentThread();
		final Listener lst = new Listener(){
			public void notify(Event e){
 				System.out.println("event in unit test: "+e.toString());
 				if(e instanceof Event.SectionChanged && ((SectionChanged) e).getInto()){
 				  Integer i = ((((SectionChanged)e).getSection() -1)* 2) +1;
 				  outputArray.add(i);
 				  System.out.println("sectionchanged in unit test into section: "+ i);
 				}
 				else if(e instanceof Event.SectionChanged && !((SectionChanged) e).getInto()){
 					Integer i = ((((SectionChanged)e).getSection() -1)* 2) +1;
					outputArray.add(route.nextSection(i));
 				}
 				if(e instanceof Event.SpeedChanged){
 					System.out.println("LOCO: "+ ((Event.SpeedChanged) e).getLocomotive());
 				    System.out.println("Speed: "+ ((Event.SpeedChanged)e).getSpeed());
 					if(((Event.SpeedChanged) e).getLocomotive() == 1 && ((Event.SpeedChanged)e).getSpeed() == 0){
 						controller.start(0, route2);
 						System.out.println("starting it");
 					}
 				}
 			}
		};
		controller.register(lst);
 		controller.start(1, route);
		try{
			Thread.currentThread().join();
		}catch(InterruptedException e){
			System.out.println("hardwareTest2");
			System.out.println("route: "+route.toString());
			System.out.println("output: "+outputArray.toString());
		}
		((TrainController) controller) .deregister(lst);


	}

}
