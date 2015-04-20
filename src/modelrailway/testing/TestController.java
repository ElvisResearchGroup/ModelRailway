package modelrailway.testing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import modelrailway.core.Controller;
import modelrailway.core.Event;
import modelrailway.core.Event.Listener;
import modelrailway.core.Route;
import modelrailway.core.Train;
import modelrailway.simulation.BackSwitch;
import modelrailway.simulation.ForwardSwitch;
import modelrailway.simulation.Section;
import modelrailway.simulation.Track;
/**
 * This test controller has no collision detection but just tests to make sure that a train can follow a route. in order to test the train simulator.
 * @author powleybenj
 *
 */
public class TestController implements Controller, Listener {

	private Map<Integer,modelrailway.simulation.Train> trains;
	private Controller trackController;
	private List<Listener> listeners = new ArrayList<Listener>();
	
	private Map<Integer,Route> trainRoutes = new HashMap<Integer,Route>();
	private Map<Integer,modelrailway.core.Train> trainOrientations ;
	private Track head;
	
	public TestController(Map<Integer,modelrailway.simulation.Train> trains, Map<Integer,modelrailway.core.Train> orientations,Track head, Controller trackController){
		this.trackController = trackController;
		this.trains = trains;		
		trainOrientations = orientations; // are the trains going backwards or fowards.
		this.head = head;
	}
	
	@Override
	public void notify(Event e) {
		// TODO Auto-generated method stub
		if(e instanceof Event.SectionChanged && ((Event.SectionChanged) e).getInto()){ // when there is a section change into another section
		    moveIntoSection(e);
		}		
		
	}
	
	private void fixPointsFowards(modelrailway.simulation.Train train, Section section){
		
	}
	
	private void fixPointsBackwards(modelrailway.simulation.Train train, Section section){
		
	}
	/**
	 * note find correct train does not support diamond crossing.
	 * @param e
	 * @return
	 */
	private void moveIntoSection(Event e){
		for(Map.Entry<Integer, modelrailway.simulation.Train> trainEntry: trains.entrySet()){ // for all the trains on the track
			
			Section secFnt = trainEntry.getValue().getFront().getSection();
			Section altSecFnt = trainEntry.getValue().getFront().getAltSection();
			Section secBack = trainEntry.getValue().getBack().getSection();
			Section altSecBack = trainEntry.getValue().getBack().getAltSection();
			
	    	if(secFnt.getNumber() ==  ((Event.SectionChanged) e).getSection()){ // check that the front of the train is in the section
	    		Integer trainSection = ((Event.SectionChanged) e).getSection(); // store the section number in a variable
	    		Track thisTrack = trainEntry.getValue().getFront();
	    	    if(trainEntry.getValue().isFowards() ){ // if the train is traveling in the fowards orientation.
	    	    	
	    	    }
	    	    else { // if the train is traveling in the backwards orientation.
	    	    	
	    	    }
	    	}
	    	else if (secBack.getNumber() ==  ((Event.SectionChanged) e).getSection()){ // check that the back of the train is in the section.
	    		
	    	}
	    	else if (altSecFnt != null && altSecFnt.getNumber() == ((Event.SectionChanged) e).getSection()){ 
	    		
	    	}
	    	else if (altSecBack !=null && altSecBack.getNumber() == ((Event.SectionChanged) e).getSection()){
	    		
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
	public modelrailway.core.Train train(int trainID) {
		// TODO Auto-generated method stub
		if(trains.containsKey(trainID)) return trainOrientations.get(trainID);
		return null;
	}
}
