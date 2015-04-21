package modelrailway.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import modelrailway.core.Event.Listener;
import modelrailway.simulation.Track;

public class TrainController implements Controller, Listener {

	private Map<Integer,modelrailway.simulation.Train> trains;
	private Controller trackController;
	private List<Listener> listeners = new ArrayList<Listener>();
	
	private Map<Integer,Route> trainRoutes = new HashMap<Integer,Route>();
	private Map<Integer,modelrailway.core.Train> trainOrientations ;
	private Track head;
	
	public TrainController(Map<Integer,modelrailway.simulation.Train> trains, Map<Integer,modelrailway.core.Train> orientations,Track head, Controller trackController){
		this.trackController = trackController;
		this.trains = trains;		
		trainOrientations = orientations; // are the trains going backwards or fowards.
		this.head = head;
	}
	@Override
	public void notify(Event e) {
		// TODO Auto-generated method stub
		if(e instanceof Event.SectionChanged){
		     Integer section = ((Event.SectionChanged) e).getSection();
		     for(Map.Entry<Integer, modelrailway.simulation.Train> tr : trains.entrySet()){
		    	 if(tr.getValue().getFront().getSection().getNumber() == section){
		    		 throw new RuntimeException("notify in TrainControler not implemented");
		    	 }
		    	 if (tr.getValue().getBack().getSection().getNumber() == section){
		    		 throw new RuntimeException("notify in TrainControler not implemented");
		    	 }
		     }
		}		
		
	}

	@Override
	public void register(Listener listener) {
		// TODO Auto-generated method stub
		//trackController.register(listener);
		listeners.add(listener);
		
	}

	@Override
	public boolean start(int trainID, Route route) {
		// TODO Auto-generated method stub
		if(this.trains.containsKey(trainID)){
			trainRoutes.put(trainID,route);
			return trackController.start(trainID, route);
		}
		return false;
	}

	@Override
	public void stop(int trainID) {
		// TODO Auto-generated method stub
		if(this.trains.containsKey(trainID)) trackController.stop(trainID);
		
	}

	@Override
	public Train train(int trainID) {
		// TODO Auto-generated method stub
		if(trains.containsKey(trainID)) return trainOrientations.get(trainID);
		return null;
	}

}
