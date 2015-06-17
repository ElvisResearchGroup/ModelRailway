package modelrailway.simulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import modelrailway.core.Controller;
import modelrailway.core.Event;
import modelrailway.core.Section;
import modelrailway.core.Event.Listener;
import modelrailway.core.Route;
import modelrailway.core.Train;
/**
 *
 * @author powleybenj
 *
 */
public class Simulator implements Event.Listener{

	private final Listener thisListener = this;
	private List<Listener> listeners = new ArrayList<Listener>();
	private TrainThread runningThread;
	private Map<Integer,modelrailway.simulation.Train> trains;
	private Track head;

	public Simulator(Track track, Map<Integer,Train> map, Map<Integer,modelrailway.simulation.Train> trains){
		head = track;
		runningThread = new TrainThread(trains,track);
		this.trains = trains;
		register(this);
		runningThread.start();
	}

	public void clockTick(Map.Entry<Integer, modelrailway.simulation.Train> entry ){

			  modelrailway.simulation.Train train = entry.getValue();
			  List<Section> slist = Arrays.asList(new Section[]{train.getBack().getSection(), train.getFront().getSection()});
			  train.move(thisListener);
			  List<Section> s2list = Arrays.asList(new Section[]{train.getBack().getSection(), train.getFront().getSection()});
			  //System.out.println("checking For movement: "+train.getBack().getSection().getNumber()); //+" slist: "+slist+" s2list: "+s2list);
			  for(Section s : s2list){
				if(!slist.contains(s)){
					for(Listener l : listeners){

						//s.reserveSection(train);
						if(s.getNumber() % 2 == 1){ // only send section changed events for the odd numbered sections, also adjust the section number to be consistent with the track.
						  Event ev = new Event.SectionChanged(((s.getNumber()-1)/2)+1, true);
						  //if(train.getParts()[0].getTrainOb() != null) train.getParts()[0].getTrainOb().setSection(s.getNumber());
						  l.notify(ev); // notify all the listeners about the change in the section
						}
					}
				}
			  }
			  for(Section s : slist){
				if(!s2list.contains(s)){
					for(Listener l : listeners){

						//s.reserveSection(train);
						if(s.getNumber() %2 == 1){
						  Event ev = new Event.SectionChanged(((s.getNumber()-1)/2)+1, false);
						  l.notify(ev);
						}
					}
			  	}
			  }
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
			for(Map.Entry<Integer, modelrailway.simulation.Train > entry : trains.entrySet()){
				// powered off
				runningThread.stopTrain(entry.getKey());
			}
			runningThread.stopTrain(loco);

		}
		else if (e instanceof Event.PowerChanged){
			for(Map.Entry<Integer, modelrailway.simulation.Train > entry : trains.entrySet()){
				// powered off
				runningThread.stopTrain(entry.getKey());
			}
			runningThread.stopThread();
		}
		else if (e instanceof Event.SpeedChanged){
			int loco =((Event.SpeedChanged) e).getLocomotive();
			if(trains.get(loco).getCurrentSpeed() == 0){ // start the train
				runningThread.startTrain(loco);

			}
		}
		else if (e instanceof Event.TurnoutChanged){

			this.set(((Event.TurnoutChanged)e).getTurnout(), ((Event.TurnoutChanged)e).getThrown());
		}


        //todo implement variable speed change
	}
	/**
	 * Register a listener
	 * @param listener
	 */
	public void register(Listener listener) {
		synchronized(listeners){
		   listeners.add(listener);
		}
	}

	/**
	 * Stop the simulation
	 */
	public void stop(){
		runningThread.stopThread();
	}

	/**
	 * A private method for setting switches.
	 * @param turnoutID
	 * @param thrown
	 */
	private void set(int turnoutID, boolean thrown) {
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

	/**
	 * This class is for running the train simulation.
	 * @author powleybenj
	 *
	 */
	private class TrainThread extends Thread{
		private Track track;
		private Map<Integer, modelrailway.simulation.Train> modelTrains;
		private volatile boolean stopthread = false;

		public TrainThread(Map<Integer, modelrailway.simulation.Train> modelTrains, Track track ){
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
					clockTick(entry);
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
		public boolean startTrain(int trainID){
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

		public boolean resumeTrain(int trainID){
			if(modelTrains.containsKey(trainID)){
				modelTrains.get(trainID).start();
				return true;
			}
			return false;
		}

		public void stopThread(){
			stopthread = true;
		}
	}
}
