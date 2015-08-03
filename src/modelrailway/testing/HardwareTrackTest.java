package modelrailway.testing;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import modelrailway.Main;
import modelrailway.Main2;
import modelrailway.ModelRailway;
import modelrailway.core.Controller;
import modelrailway.core.Event;
import modelrailway.core.Route;
import modelrailway.core.Section;
import modelrailway.core.Train;
import modelrailway.core.Event.Listener;
import modelrailway.core.Event.SectionChanged;
import modelrailway.simulation.Locomotive;
import modelrailway.simulation.Movable;
import modelrailway.simulation.Simulator;
import modelrailway.simulation.Switch;
import modelrailway.simulation.Track;
import modelrailway.simulation.Straight.StraightDblRing;
import modelrailway.util.ControlerCollision;
import modelrailway.util.SimulationTrack;
import modelrailway.util.TrainController;

public class HardwareTrackTest extends Main{


	public HardwareTrackTest(ModelRailway railway, Controller controller) {
		super(railway, controller);
		Command[] cmd = this.getCommands();
		Command htest0 = this.new Command("hardwareTest0", getMethod("hardwareTest0"));
		Command htest1 = this.new Command("hardwareTest1", getMethod("hardwareTest1"));
		Command htest2 = this.new Command("hardwareTest2", getMethod("hardwareTest2"));
		Command htest3 = this.new Command("hardwareTest3", getMethod("hardwareTest3"));
		Command htest4 = this.new Command("hardwareTest4", getMethod("hardwareTest4"));
		Command htest5 = this.new Command("hardwareTest5", getMethod("hardwareTest5"));
		Command[] cmd2 = new Command[cmd.length+6];
		System.arraycopy(cmd, 0, cmd2, 0, cmd.length);
		cmd2[cmd2.length-6] = htest5;
		cmd2[cmd2.length-5] = htest4;
		cmd2[cmd2.length-4] = htest0;
		cmd2[cmd2.length-3] = htest1;
		cmd2[cmd2.length-2] = htest2;
		cmd2[cmd2.length-1] = htest3;
		this.setCommands(cmd2);

		// TODO Auto-generated constructor stub
	}
	public static Method getMethod(String name, Class... paramTypes) {
		try {
			return HardwareTrackTest.class.getMethod(name, paramTypes);
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

	private static SimulationTrack sim0 = new SimulationTrack();

	public static void main(String args[]) throws Exception {
		String port = args[0];
		System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyACM0");

		// Needed for connection on lab machines


		// Construct the model railway assuming the interface (i.e. USB Cable)
		// is on a given port. Likewise, we initialise it with three locomotives
		// whose addresses are 1,2 + 3. If more locomotives are to be used, this
		// needs to be updated accordingly.

		final ModelRailway railway = new ModelRailway(port,
				new int[] {1});
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
		Train[] trains = {
				new Train(1,true), // default config for train 0
		};
		Controller controller = new TrainController(trains,new Integer[]{1},rails, sim0);
		rails.register(controller);
		controller.register(rails);
		ctl = controller;
		System.out.println("got to print Loop");
		HardwareTrackTest tst = new HardwareTrackTest(rails,controller);
		tst.ctl = ctl;
		tst.rails = rails;
		tst.readEvaluatePrintLoop();
	}


	final String port="/dev/ttyACM0";
	public void hardwareTest0() throws Exception{


		final Controller controller = getCtl();
		// Enter Read, Evaluate, Print loop.

		StraightDblRing ring = sim0.getTrack();

		ring.getSectionNumberMap();

		((TrainController)controller).trainOrientations().get(0).setSection(1);
		final Route route = new Route(true, 2,3,4,5,6,7,8,1);



		final ArrayList<Integer> outputArray = new ArrayList<Integer>();
		final Thread th = Thread.currentThread();
		final Listener lst = new Listener(){
			public void notify(Event e){
 				System.out.println("event in unit test: "+e.toString());

 				if(e instanceof Event.SectionChanged && ((SectionChanged) e).getInto()){
 				  Integer i = ((((SectionChanged)e).getSection() -1)* 2) +1;
 				  outputArray.add(i);
 				  System.out.println("sectionchanged in unit test into section: "+ i);
 				  if(i == 1){
 					  System.out.println("stop triggered by unit test");
 					  controller.stop(0);
 					  th.interrupt();

 				  }
 				}
 				else if(e instanceof Event.SectionChanged && !((SectionChanged) e).getInto()){
 					Integer i = ((((SectionChanged)e).getSection() -1)* 2) +1;
					outputArray.add(route.nextSection(i));


 				}

 				if(e instanceof Event.EmergencyStop){
 					System.out.println("an emergency stop has been triggered by an event in the unit test");
 					controller.stop(0);
 					th.interrupt();
 				}
 			}

		};

		controller.register(lst);

 		controller.start(0, route);

		try{
			Thread.currentThread().join();
		}catch(InterruptedException e){
			System.out.println("hardwareTest0");
			System.out.println("route: "+route.toString());
			System.out.println("output: "+outputArray.toString());
		}
		assert(outputArray.get(0) == 2);
		assert(outputArray.get(1) == 3);
		assert(outputArray.get(2) == 4);
		assert(outputArray.get(3) == 5);
		assert(outputArray.get(4) == 6);
		assert(outputArray.get(5) == 7);
		assert(outputArray.get(6) == 8);
		assert(outputArray.get(7) == 1);

		((TrainController) controller).deregister(lst);

	}


	public void hardwareTest1(){

		final Controller controller = getCtl();
		// Enter Read, Evaluate, Print loop.

		StraightDblRing ring = sim0.getTrack();

		ring.getSectionNumberMap();
		((TrainController)controller).trainOrientations().get(0).setSection(1);

		final Route route = new Route(true,1,2,3,4,19,20);



		final ArrayList<Integer> outputArray = new ArrayList<Integer>();
		final Thread th = Thread.currentThread();

		final Listener lst = new Listener(){
			public void notify(Event e){
 				System.out.println("event in unit test: "+e.toString());

 				if(e instanceof Event.SectionChanged && ((SectionChanged) e).getInto()){
 				  Integer i = ((((SectionChanged)e).getSection() -1)* 2) +1;
 				  outputArray.add(i);
 				  System.out.println("sectionchanged in unit test into section: "+ i);
 				  if(i == 19){
 					  System.out.println("stop triggered by unit test");
 					  controller.stop(0);
 					  th.interrupt();

 				  }
 				}
 				else if(e instanceof Event.SectionChanged && !((SectionChanged) e).getInto()){
 					Integer i = ((((SectionChanged)e).getSection() -1)* 2) +1;
					outputArray.add(route.nextSection(i));
					if(route.nextSection(i) == 20){
						System.out.println("stop triggered by unit test");
	 					controller.stop(0);
	 					th.interrupt();
					}


 				}

 				if(e instanceof Event.EmergencyStop){
 					System.out.println("an emergency stop has been triggered by an event in the unit test");
 					controller.stop(0);
 					th.interrupt();
 				}
 			}

		};

		controller.register(lst);

 		controller.start(0, route);

		try{
			Thread.currentThread().join();
		}catch(InterruptedException e){
			System.out.println("hardwareTest1");
			System.out.println("route: "+route.toString());
			System.out.println("output: "+outputArray.toString());
		}
		assert(outputArray.get(0) == 2);
		assert(outputArray.get(1) == 3);
		assert(outputArray.get(2) == 4);
		assert(outputArray.get(3) == 19);
		//assert(outputArray.get(4) == 20);

		((TrainController) controller).deregister(lst);


	}

	public void hardwareTest2(){

		final Controller controller = getCtl();
		// Enter Read, Evaluate, Print loop.

		StraightDblRing ring = sim0.getTrack();

		ring.getSectionNumberMap();
		((TrainController)controller).trainOrientations().get(0).setSection(1);

		final Route route = new Route(true,1,2,3,4,5,6,7,8,9);

		ctl.set(((Switch)ring.getSectionNumberMap().get(16).get(0)).getSwitchID(), false);
		ctl.set(((Switch)ring.getSectionNumberMap().get(9).get(0)).getSwitchID(), true);


		final ArrayList<Integer> outputArray = new ArrayList<Integer>();
		final Thread th = Thread.currentThread();

		final Listener lst  = new Listener(){
			public void notify(Event e){
 				System.out.println("event in unit test: "+e.toString());

 				if(e instanceof Event.SectionChanged && ((SectionChanged) e).getInto()){
 				  Integer i = ((((SectionChanged)e).getSection() -1)* 2) +1;
 				  outputArray.add(i);
 				  System.out.println("sectionchanged in unit test into section: "+ i);
 				  if(i == 10){
 					  System.out.println("stop triggered by unit test");
 					  controller.stop(0);
 					  th.interrupt();

 				  }
 				}
 				else if(e instanceof Event.SectionChanged && !((SectionChanged) e).getInto()){
 					Integer i = ((((SectionChanged)e).getSection() -1)* 2) +1;
					outputArray.add(route.nextSection(i));
					if(route.nextSection(i) == 10){
						System.out.println("stop triggered by unit test");
	 					controller.stop(0);
	 					th.interrupt();
					}


 				}

 				if(e instanceof Event.EmergencyStop){
 					System.out.println("an emergency stop has been triggered by an event in the unit test");
 					controller.stop(0);
 					th.interrupt();
 				}
 			}

		};
		controller.register(lst);

 		controller.start(0, route);

		try{
			Thread.currentThread().join();
		}catch(InterruptedException e){
			System.out.println("hardwareTest2");
			System.out.println("route: "+route.toString());
			System.out.println("output: "+outputArray.toString());
		}
		assert(outputArray.get(0) == 2);
		assert(outputArray.get(1) == 3);
		assert(outputArray.get(2) == 4);
		assert(outputArray.get(3) == 5);
		assert(outputArray.get(3) == 6);
		assert(outputArray.get(3) == 7);
		assert(outputArray.get(3) == 8);
		assert(outputArray.get(3) == 9);

		((TrainController) controller).deregister(lst);

	}
	public void hardwareTest3(){

		final Controller controller = getCtl();
		// Enter Read, Evaluate, Print loop.

		StraightDblRing ring = sim0.getTrack();

		ring.getSectionNumberMap();
		((TrainController)controller).trainOrientations().get(0).setSection(9);

		final Route route = new Route(true,9,10,3,4,5,6,7,8,1);



		final ArrayList<Integer> outputArray = new ArrayList<Integer>();
		final Thread th = Thread.currentThread();
		final Listener lst = new Listener(){
			public void notify(Event e){
 				System.out.println("event in unit test: "+e.toString());

 				if(e instanceof Event.SectionChanged && ((SectionChanged) e).getInto()){
 				  Integer i = ((((SectionChanged)e).getSection() -1)* 2) +1;
 				  outputArray.add(i);
 				  System.out.println("sectionchanged in unit test into section: "+ i);
 				  if(i == 1){
 					  System.out.println("stop triggered by unit test");
 					  controller.stop(0);
 					  th.interrupt();

 				  }
 				}
 				else if(e instanceof Event.SectionChanged && !((SectionChanged) e).getInto()){
 					Integer i = ((((SectionChanged)e).getSection() -1)* 2) +1;
					outputArray.add(route.nextSection(i));
					if(route.nextSection(i) == 1){
						System.out.println("stop triggered by unit test");
	 					controller.stop(0);
	 					th.interrupt();
					}


 				}

 				if(e instanceof Event.EmergencyStop){
 					System.out.println("an emergency stop has been triggered by an event in the unit test");
 					controller.stop(0);
 					th.interrupt();
 				}
 			}

		};

		controller.register(lst);

 		controller.start(0, route);

		try{
			Thread.currentThread().join();
		}catch(InterruptedException e){
			System.out.println("hardwareTest2");
			System.out.println("route: "+route.toString());
			System.out.println("output: "+outputArray.toString());
		}
		assert(outputArray.get(0) == 10);
		assert(outputArray.get(1) == 3);
		assert(outputArray.get(2) == 4);
		assert(outputArray.get(3) == 5);
		assert(outputArray.get(3) == 6);
		assert(outputArray.get(3) == 7);
		assert(outputArray.get(3) == 8);
		assert(outputArray.get(3) == 1);

		((TrainController) controller) .deregister(lst);

	}

	public void hardwareTest4() throws Exception{
		hardwareTest2();
		Thread.sleep(1000);

		hardwareTest3();
	}

	public void hardwareTest5(){
		final Controller controller = getCtl();
		// Enter Read, Evaluate, Print loop.

		StraightDblRing ring = sim0.getTrack();

		ring.getSectionNumberMap();
		((TrainController)controller).trainOrientations().get(0).setSection(16);
		ctl.set(((Switch)ring.getSectionNumberMap().get(16).get(0)).getSwitchID(), false);
		ctl.set(((Switch)ring.getSectionNumberMap().get(9).get(0)).getSwitchID(), true);


		final Route route = new Route(true, 16 ,9,10,3,4,5,6,7,8,1);



		final ArrayList<Integer> outputArray = new ArrayList<Integer>();
		final Thread th = Thread.currentThread();
		final Listener lst = new Listener(){
			public void notify(Event e){
 				System.out.println("event in unit test: "+e.toString());

 				if(e instanceof Event.SectionChanged && ((SectionChanged) e).getInto()){
 				  Integer i = ((((SectionChanged)e).getSection() -1)* 2) +1;
 				  outputArray.add(i);
 				  System.out.println("sectionchanged in unit test into section: "+ i);
 				  if(i == 1){
 					  System.out.println("stop triggered by unit test");
 					  controller.stop(0);
 					  th.interrupt();

 				  }
 				}
 				else if(e instanceof Event.SectionChanged && !((SectionChanged) e).getInto()){
 					Integer i = ((((SectionChanged)e).getSection() -1)* 2) +1;
					outputArray.add(route.nextSection(i));
					if(route.nextSection(i) == 1){
						System.out.println("stop triggered by unit test");
	 					controller.stop(0);
	 					th.interrupt();
					}


 				}

 				if(e instanceof Event.EmergencyStop){
 					System.out.println("an emergency stop has been triggered by an event in the unit test");
 					controller.stop(0);
 					th.interrupt();
 				}
 			}

		};

		controller.register(lst);

 		controller.start(0, route);

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
