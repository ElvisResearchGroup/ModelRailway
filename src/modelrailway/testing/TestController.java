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
import modelrailway.simulation.Crossing;
import modelrailway.simulation.ForwardSwitch;
import modelrailway.simulation.Section;
import modelrailway.simulation.Simulator;
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
		trackController.register(this);
	}

	@Override
	public void notify(Event e) {
		if(e instanceof Event.SectionChanged && ((Event.SectionChanged) e).getInto()){ // when there is a section change into another section
			//System.out.println("section change event."+((Event.SectionChanged) e).getSection());
		    moveIntoSection(e);
		}

		for(Listener l : listeners){

			l.notify(e);
		}
	}

	private void fixPointsFowards(Integer trainID, ForwardSwitch track,Section section ,boolean goingFoward){
		//System.out.println("fix Points Forwards"+section.getNumber());
		if(goingFoward){ // entering the points going in the foward direction.
		    Route rt = trainRoutes.get(trainID);
		    Integer nextSec = rt.nextSection(section.getNumber());
		   // System.out.println("currentSec: "+section.getNumber()+" nextSec: "+nextSec);
		    Track next = track.getNext(false);
		    Track alt = track.getNext(true);
		    if(alt.getSection().getNumber() == nextSec){
		    	// we intend to travel down the alt path
		    	if(track.isNext()){
		    		track.toggle();
		  //  		System.out.println("foward points toggled");
		    	}

		    } else if (alt.getAltSection() != null && alt.getAltSection().getNumber() == nextSec){
		    	// we intend to travel down the alt path
		    	if(track.isNext()) track.toggle();

		    } else if (next.getSection().getNumber() == nextSec){
		    	if(!track.isNext()) track.toggle();
		    } else if (next.getAltSection() != null && next.getAltSection().getNumber() == nextSec){
		    	if(!track.isNext()) track.toggle();
		    }
		    else{ // a mistake.
		    	throw new RuntimeException("invalid route at fixPointsFowards");
		    }

		}else{ // entering the points going in the backward direction
			// switch the points to point to where we are coming from
			modelrailway.simulation.Train tr = trains.get(trainID); // get train.
			boolean isAlt = tr.getBackAlt();
			if(isAlt && track.isNext()) track.toggle();
			else if(!isAlt && !track.isNext()) track.toggle();

		}

	}

	private void fixPointsBackwards(Integer trainID, BackSwitch track, Section section,boolean goingFoward){
		if(goingFoward){ // entering the points going in the foward direction.
		   boolean isAlt = track.getCurrentAlt(trains.get(trainID));
		   if(isAlt && track.isPrev()) track.toggle();
		   if(!isAlt && !track.isPrev()) track.toggle();

		}else{ // entering the points going in the backward direction
			// switch the points to point to where we are coming from
			//modelrailway.simulation.Train tr = trains.get(trainID); // get train.
		   Route rt = trainRoutes.get(trainID);
		   Integer nextSec = rt.nextSection(section.getNumber());
		   Track prev = track.getPrevious(false);
		   Track alt = track.getPrevious(true);

		   if(alt.getSection().getNumber() == nextSec){
			    	// we intend to travel down the alt path
			   if(track.isPrev()) track.toggle();

		   } else if (alt.getAltSection() != null && alt.getAltSection().getNumber() == nextSec){
			    	// we intend to travel down the alt path
			   if(track.isPrev()) track.toggle();

		   } else if (prev.getSection().getNumber() == nextSec){
			   if(!track.isPrev()) track.toggle();
		   } else if (prev.getAltSection() != null && prev.getAltSection().getNumber() == nextSec){
			   if(!track.isPrev()) track.toggle();
		   }
		   else{ // a mistake.
			   throw new RuntimeException("invalid route at fixPointsFowards");
	       }

		}
		//System.out.println("fix Points Backwards"+section.getNumber());
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
	    		Track thisTrack = trainEntry.getValue().getFront(); //get the front of the train
	    	   // train has traveled fowards into a section. we check weather the next section is a point or a diamond crossing
	    	    if(thisTrack instanceof ForwardSwitch){
	    	    	fixPointsFowards(trainEntry.getKey() ,((ForwardSwitch)thisTrack), secFnt,trainEntry.getValue().isFowards());

	    	    } else if (thisTrack instanceof BackSwitch){
	    	    	fixPointsBackwards(trainEntry.getKey(), ((BackSwitch) thisTrack), secFnt, trainEntry.getValue().isFowards());
	    	    } else if (thisTrack instanceof Crossing){
	    	    	// check diamond crossing.
	    	    	throw new RuntimeException("Diamond crossing is not supported yet");
	    	    }

	    	}
	    	else if (secBack.getNumber() ==  ((Event.SectionChanged) e).getSection()){ // check that the back of the train is in the section.
	    		Integer trainSection = ((Event.SectionChanged) e).getSection(); // store the section number in a variable
	    		Track thisTrack = trainEntry.getValue().getBack(); //get the front of the train
	    	   // train has traveled fowards into a section. we check weather the next section is a point or a diamond crossing
	    	    if(thisTrack instanceof ForwardSwitch){
	    	    	fixPointsFowards(trainEntry.getKey() ,((ForwardSwitch)thisTrack), secBack,trainEntry.getValue().isFowards());

	    	    } else if (thisTrack instanceof BackSwitch){
	    	    	fixPointsBackwards(trainEntry.getKey(), ((BackSwitch) thisTrack), secBack, trainEntry.getValue().isFowards());
	    	    } else if (thisTrack instanceof Crossing){
	    	    	// check diamond crossing.
	    	    	throw new RuntimeException("Diamond crossing is not supported yet");
	    	    }
	    	}
	    	else if (altSecFnt != null && altSecFnt.getNumber() == ((Event.SectionChanged) e).getSection()){
	    		//if the track piece has an alternate section check to see if the train is in the alternate section. at the front.
	    		Integer trainSection = ((Event.SectionChanged) e).getSection(); // store the section number in a variable
	    		Track thisTrack = trainEntry.getValue().getFront(); //get the front of the train
	    	   // train has traveled fowards into a section. we check weather the next section is a point or a diamond crossing
	    	    if(thisTrack instanceof ForwardSwitch){
	    	    	fixPointsFowards(trainEntry.getKey() ,((ForwardSwitch)thisTrack), altSecFnt,trainEntry.getValue().isFowards());

	    	    } else if (thisTrack instanceof BackSwitch){
	    	    	fixPointsBackwards(trainEntry.getKey(), ((BackSwitch) thisTrack), altSecFnt, trainEntry.getValue().isFowards());
	    	    } else if (thisTrack instanceof Crossing){
	    	    	// check diamond crossing.
	    	    	throw new RuntimeException("Diamond crossing is not supported yet");
	    	    }
	    	}
	    	else if (altSecBack !=null && altSecBack.getNumber() == ((Event.SectionChanged) e).getSection()){
	    		//if the track piece has an alternate section check to see if the train is in the alternate section at the back
	    		Integer trainSection = ((Event.SectionChanged) e).getSection(); // store the section number in a variable
	    		Track thisTrack = trainEntry.getValue().getBack(); //get the front of the train
	    	   // train has traveled fowards into a section. we check weather the next section is a point or a diamond crossing
	    	    if(thisTrack instanceof ForwardSwitch){
	    	    	fixPointsFowards(trainEntry.getKey() ,((ForwardSwitch)thisTrack), altSecBack,trainEntry.getValue().isFowards());

	    	    } else if (thisTrack instanceof BackSwitch){
	    	    	fixPointsBackwards(trainEntry.getKey(), ((BackSwitch) thisTrack), altSecBack, trainEntry.getValue().isFowards());

	    	    } else if (thisTrack instanceof Crossing){
	    	    	// check diamond crossing.
	    	    	throw new RuntimeException("Diamond crossing is not supported yet");
	    	    }
	    	}
	    //	System.out.println("Find Section for train: "+trainEntry.getKey()+" Section: "+((Event.SectionChanged) e).getSection());
	    }
	}

	@Override
	public void register(Listener listener) {
		//trackController.register(listener);
		listeners.add(listener);

	}

	@Override
	public boolean start(int trainID, Route route) {
		if(this.trains.containsKey(trainID)){
			trainRoutes.put(trainID,route);
			return trackController.start(trainID, route);
		}
		return false;
	}

	@Override
	public void stop(int trainID) {
		if(this.trains.containsKey(trainID)) trackController.stop(trainID);

	}


	@Override
	public modelrailway.core.Train train(int trainID) {
		if(trains.containsKey(trainID)) return trainOrientations.get(trainID);
		return null;
	}

	@Override
	/**
	 * Set the switch at the given turnoutId, thrown is true if we are setting the switch to the alternate section.
	 */
	public void set(int turnoutID, boolean thrown) {
		trackController.set(turnoutID, thrown);

	}
}
