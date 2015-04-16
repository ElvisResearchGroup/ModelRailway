package modelrailway.simulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

		private Map<Integer, modelrailway.simulation.Train> modelTrains;
		private Map<Integer, Route> trainRoute;

		public TrainThread(Map<Integer, modelrailway.simulation.Train> modelTrains, Map<Integer, Train> trains){
			this.modelTrains = modelTrains;
		}

		public void run(){

		}

		public boolean startTrain(int trainID, Route route){
			if(modelTrains.containsKey(trainID)){
			    modelTrains.get(trainID).stop();
			    return true;
			}
			else {
				return false;
			}
		}

		public void stopTrain(int trainID){
			if(modelTrains.containsKey(trainID)) modelTrains.get(trainID);
		}
	}
	private ArrayList<Listener> listeners = new ArrayList<Listener>();
	private Map<Integer,Train> trainMap; // a map from train id's to trains.
	private TrainThread runningThread;


	public Simulator(Track track, Map<Integer,Train> map, Map<Integer,modelrailway.simulation.Train> trains){
		trainMap = map;
		runningThread = new TrainThread(trains,map);

	}
	@Override
	public void notify(Event e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void register(Listener listener) {
		listeners.add(listener);

	}

	@Override
	public boolean start(int trainID, Route route) {
		return runningThread.startTrain(trainID,route);
	}

	@Override
	public void stop(int trainID) {
		runningThread.stopTrain(trainID);

	}

	@Override
	public Train train(int trainID) {
		// TODO Auto-generated method stub
		return trainMap.get(trainID);
	}

}
