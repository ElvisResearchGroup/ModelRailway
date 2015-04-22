package modelrailway.simulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import modelrailway.core.Controller;
import modelrailway.core.Event;
import modelrailway.core.Event.Listener;
import modelrailway.core.Route;
import modelrailway.core.Train;
/**
 *
 * @author powleybenj
 *
 */
public class Simulator implements Controller{


	private class TrainThread extends Thread{

		private Track track;
		private Map<Integer, modelrailway.simulation.Train> modelTrains;
		private Map<Integer, Route> trainRoute;
		private boolean stopthread = false;

		public TrainThread(Map<Integer, modelrailway.simulation.Train> modelTrains, Map<Integer, Train> trains, Track track ){
			this.modelTrains = modelTrains;
			this.track = track;
		}

		/**
		 * the run method repeatedly calls move on all the trains and checks for section changes.
		 *
		 */
		public void run(){
			while(!stopthread){
			  for(Map.Entry<Integer, modelrailway.simulation.Train> entry: modelTrains.entrySet()){
				  synchronized(listeners){
				  modelrailway.simulation.Train train = entry.getValue();
				  List<Section> slist = Arrays.asList(new Section[]{train.getBack().getSection(), train.getFront().getSection()});
				  train.move();
				  List<Section> s2list = Arrays.asList(new Section[]{train.getBack().getSection(), train.getFront().getSection()});
				  System.out.println("checking For movement: "+train.getBack().getSection().getNumber()); //+" slist: "+slist+" s2list: "+s2list);
				  for(Section s : s2list){
					if(!slist.contains(s)){
						for(Listener l : listeners){
							System.out.println("trainSec: "+train.getBack().getSection().getNumber());
							System.out.println("listenerSize: "+listeners.size());
							Event ev = new Event.SectionChanged(s.getNumber(), true);
							l.notify(ev);

							//
						}
					}
				  }
				  for(Section s : slist){
					if(!s2list.contains(s)){
						for(Listener l : listeners){
							Event ev = new Event.SectionChanged(s.getNumber(), false);
							l.notify(ev);
						}
				  	}
				  }
				}
			  }
			}

		}
		/**
		 * Start the train going on the track.
		 * The route is ignored by the simulator, The controller needs to controll the route
		 * @param trainID
		 * @param route
		 * @return
		 */
		public boolean startTrain(int trainID, Route route){
			if(modelTrains.containsKey(trainID)){
			    modelTrains.get(trainID).start();
			    //trainRoute.put(trainID, route);
			    return true;
			}
			else {
				return false;
			}
		}
		/**
		 * Stop the train with the supplied id.
		 * @param trainID
		 */
		public void stopTrain(int trainID){
			if(modelTrains.containsKey(trainID)) modelTrains.get(trainID).stop();
		}

		public void stopThread(){
			stopthread = true;
		}
	}
	private List<Listener> listeners = new ArrayList<Listener>();
	private Map<Integer,Train> trainMap; // a map from train id's to trains.
	private TrainThread runningThread;
	private Map<Integer,modelrailway.simulation.Train> trains;
	private Track head;

	public Simulator(Track track, Map<Integer,Train> map, Map<Integer,modelrailway.simulation.Train> trains){
		head = track;
		trainMap = map;
		runningThread = new TrainThread(trains,map,track);
		this.trains = trains;
		register(this);
		runningThread.start();
	}

	@Override
	public void notify(Event e) {

		if(e instanceof Event.DirectionChanged){
			Event.EmergencyStop stp = (Event.EmergencyStop) e;
			Integer loco = stp.getLocomotive();
			trains.get(loco).toggleDirection();
		}
		else if (e instanceof Event.EmergencyStop){
			Event.EmergencyStop stopEvent = (Event.EmergencyStop) e;
			Integer loco = stopEvent.getLocomotive();
			//Train tr = trainMap.get(loco);
			for(Map.Entry<Integer, Train > entry : trainMap.entrySet()){
				// powered off
				runningThread.stopTrain(entry.getKey());
			}
			runningThread.stopTrain(loco);

		}
		else if (e instanceof Event.PowerChanged){
			for(Map.Entry<Integer, Train > entry : trainMap.entrySet()){
				// powered off
				runningThread.stopTrain(entry.getKey());
			}
			runningThread.stopThread();
		}

        //todo implement variable speed change
	}

	@Override
	public void register(Listener listener) {
		synchronized(listeners){
		   listeners.add(listener);
		}
	}

	/**
	 * This method starts the train with trainID as its id.
	 * The route is ignored as it is the responsibility of the controller with a track object to ensure that trains follow the route.
	 *
	 */
	public boolean start(int trainID, Route route) {

		for(Listener list: listeners){
			list.notify(new Event.SpeedChanged(trainID, trains.get(trainID).getCurrentSpeed()));
		}
		return runningThread.startTrain(trainID,route);
	}

	@Override
	public void stop(int trainID) {
		runningThread.stopTrain(trainID);

	}

	public void stop(){

		runningThread.stopThread();
	}

	@Override
	public Train train(int trainID) {
		return trainMap.get(trainID);
	}

	@Override
	public void set(int turnoutID, boolean thrown) {
		Track sw = head.getSwitchEntry(turnoutID);
		if(sw instanceof ForwardSwitch){
			boolean currNotThrown = ((ForwardSwitch) sw).isNext();
			if(currNotThrown == thrown) ((ForwardSwitch) sw).toggle();

		}
		else if (sw instanceof BackSwitch){
			boolean currNotThrown = ((BackSwitch) sw).isPrev();
			if(currNotThrown  == thrown) ((BackSwitch) sw).toggle();
		}
		else{
			throw new RuntimeException("unknown switch encountered in TestControler");
		}

	}

}
