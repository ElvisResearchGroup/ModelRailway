package modelrailway.testing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

	private Controller trackController;
	private List<Listener> listeners = new ArrayList<Listener>();
	private Map<Integer,Route> trainRoutes = new HashMap<Integer,Route>();
	private Map<Integer,modelrailway.core.Train> trainOrientations ;
	private Map<Integer, Section> sections;
	private Track head;

	public Map<Integer, modelrailway.core.Train> trainOrientations(){
		return trainOrientations;
	}
	public Map<Integer, Section> sections(){
		return sections;
	}

	public Map<Integer, Route> routes(){
		return trainRoutes;
	}

	public TestController( Map<Integer,modelrailway.core.Train> orientations, Map<Integer, Section> intSecMap , Track head, Controller trackController){
		this.trackController = trackController;
		trainOrientations = orientations; // are the trains going backwards or fowards.
		this.sections = intSecMap;
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
	/**
	 * Fix a foward switch to point in the direction that we want the train to go in.
	 * @param trainID
	 * @param track
	 * @param section
	 * @param goingFoward
	 */
	private void fixPointsFowards(Integer trainID, ForwardSwitch track,Section section ,boolean goingFoward){
		//System.out.println("fix Points Forwards"+section.getNumber());
		if(goingFoward){ // entering the points going in the foward direction.
		    Route rt = trainRoutes.get(trainID);
		    Integer nextSec = rt.nextSection(section.getNumber());
		   // System.out.println("currentSec: "+section.getNumber()+" nextSec: "+nextSec);
		    Track next = track.getNext(false);
		    Track alt = track.getNext(true);
		    //System.out.println("track.getSwitchID(): "+track.getSwitchID());
		    if(alt.getSection().getNumber() == nextSec){
		    	// we intend to travel down the alt path
		        this.set(track.getSwitchID(), true);

		    } else if (alt.getAltSection() != null && alt.getAltSection().getNumber() == nextSec){
		    	// we intend to travel down the alt path
		    	this.set(track.getSwitchID(), true);

		    } else if (next.getSection().getNumber() == nextSec){
		    	this.set(track.getSwitchID(), false);
		    } else if (next.getAltSection() != null && next.getAltSection().getNumber() == nextSec){
		    	this.set(track.getSwitchID(), false);

		    }
		    else{ // a mistake.
		    	throw new RuntimeException("invalid route at fixPointsFowards");
		    }

		}else{ // entering the points going in the backward direction
			// switch the points to point to where we are coming from
			Route rt = trainRoutes.get(trainID);
			Integer prevSec = rt.prevSection(section.getNumber()); // the section we came from

			Section previous = sections.get(prevSec);

			// are the next sections in the track segment.
			//System.out.println("sec: "+section.getNumber());
			//System.out.println("previous: "+previous.getNumber());
			//System.out.println("PrevSec: "+ prevSec);
			//System.out.println("PrevAlt: "+ track.getNext(true).getSection().getNumber());
		//	System.out.println("Prev: "+ track.getNext(false).getSection().getNumber());

			if(prevSec == track.getNext(true).getSection().getNumber()){
				this.set(track.getSwitchID(), true);
			} else if (track.getNext(true).getAltSection() != null && prevSec == track.getNext(true).getAltSection().getNumber()){
				this.set(track.getSwitchID(), true);
			}
			else if (prevSec == track.getNext(false).getSection().getNumber()){
			    this.set(track.getSwitchID(), false);
            } else if (track.getNext(false).getAltSection() != null && prevSec == track.getNext(false).getAltSection().getNumber()){
            	this.set(track.getSwitchID(), false);
			}else{
		    	throw new RuntimeException("invalid route at fixPointsFowards");
		    }

		}

	}
	/**
	 * Fix a backwards switch to point in the direction that we want to go in.
	 * @param trainID
	 * @param track
	 * @param section
	 * @param goingFoward
	 */
	private void fixPointsBackwards(Integer trainID, BackSwitch track, Section section,boolean goingFoward){
		if(goingFoward){ // entering the points going in the foward direction.

		   Route rt = trainRoutes.get(trainID);
		   Integer prevSec = rt.prevSection(section.getNumber()); // the section we have just come from
		   Track prev = track.getPrevious(false);
		   Track prevAlt = track.getPrevious(true);

		   if(prevAlt.getSection().getNumber() == prevSec){
		    	// we intend to travel down the alt path
		        this.set(track.getSwitchID(), true);

		    } else if (prevAlt.getAltSection() != null && prevAlt.getAltSection().getNumber() == prevSec){

		    	// we intend to travel down the alt path
		    	this.set(track.getSwitchID(), true);

		    } else if (prev.getSection().getNumber() == prevSec){
		    	this.set(track.getSwitchID(), false);
		    } else if (prev.getAltSection() != null && prev.getAltSection().getNumber() == prevSec){
		    	this.set(track.getSwitchID(), false);

		    }
		    else{
		    	throw new RuntimeException("invalid route at fixPointsFowards");
		    }

		}else{ // entering the points going in the backward direction
			// switch the points to point to where we are coming from
			//modelrailway.simulation.Train tr = trains.get(trainID); // get train.
		   Route rt = trainRoutes.get(trainID);
		   Integer nextSec = rt.nextSection(section.getNumber());
		   Track prev = track.getPrevious(false);
		   Track alt = track.getPrevious(true);
		   //System.out.println("sec: "+section.getNumber());
			//System.out.println("nextSec: "+ nextSec);
			//System.out.println("alt: "+ track.getPrevious(true).getSection().getNumber());
			//System.out.println("prev: "+ track.getPrevious(false).getSection().getNumber());

		   if(alt.getSection().getNumber() == nextSec){
			    	// we intend to travel down the alt path
			   this.set(track.getSwitchID(), true);

		   } else if (alt.getAltSection() != null && alt.getAltSection().getNumber() == nextSec){
			    	// we intend to travel down the alt path
			   this.set(track.getSwitchID(),true);

		   } else if (prev.getSection().getNumber() == nextSec){
			   this.set(track.getSwitchID(),false);
		   } else if (prev.getAltSection() != null && prev.getAltSection().getNumber() == nextSec){
			   this.set(track.getSwitchID(),false);
		   }
		   else{ // a mistake.
			   throw new RuntimeException("invalid route at fixPointsFowards");
	       }

		}
		//System.out.println("fix Points Backwards"+section.getNumber());
	}
	/**
	 * We have moved into a section. Do what is necissary to configure the section that we have just moved into.
	 * note find correct train does not support diamond crossing.
	 * @param e
	 * @return
	 */
	private void moveIntoSection(Event e){
		for(Entry<Integer, Train> trainOrientation: trainOrientations.entrySet()){ // for all the trains on the track

			Integer section = trainOrientation.getValue().currentSection();
			//System.out.println("Section: "+section);
			//System.out.println("e.getSection(): "+((Event.SectionChanged) e).getSection());
	    	if(section ==  ((Event.SectionChanged) e).getSection()){ // check that the front of the train is in the section
	    		Integer trainSection = ((Event.SectionChanged) e).getSection(); // store the section number in a variable
	    		Section sec = sections.get(section);
	    		Track thisTrack = sec.get(0);
	    		//System.out.println("Section: "+section);
	    		//System.out.println("Track: "+thisTrack);
	    		//System.out.println("TrackSection: "+thisTrack.getSection().getNumber());
	    	   // train has traveled fowards into a section. we check weather the next section is a point or a diamond crossing
	    	    if(thisTrack instanceof ForwardSwitch){
	    	    	//System.out.println("orientation: "+trainOrientation.getValue().currentOrientation());
	    	    	fixPointsFowards(trainOrientation.getKey() ,((ForwardSwitch)thisTrack), sec,trainOrientation.getValue().currentOrientation());

	    	    } else if (thisTrack instanceof BackSwitch){
	    	    	fixPointsBackwards(trainOrientation.getKey(), ((BackSwitch) thisTrack), sec, trainOrientation.getValue().currentOrientation());
	    	    } else if (thisTrack instanceof Crossing){
	    	    	// check diamond crossing.
	    	    	throw new RuntimeException("Diamond crossing is not supported yet");
	    	    }

	    	}
       }
	}

	@Override
	public void register(Listener listener) {

		listeners.add(listener);


	}

	@Override
	public boolean start(int trainID, Route route) {

		trainRoutes.put(trainID,route);
		return trackController.start(trainID, route);

	}

	public boolean resumeTrain(int trainID){

		return trackController.start(trainID, trainRoutes.get(trainID));

	}

	@Override
	public void stop(int trainID) {
		trackController.stop(trainID);

	}


	@Override
	public modelrailway.core.Train train(int trainID) {
		return trainOrientations.get(trainID);
	}

	@Override
	/**
	 * Set the switch at the given turnoutId, thrown is true if we are setting the switch to the alternate section.
	 */
	public void set(int turnoutID, boolean thrown) {
		trackController.set(turnoutID, thrown);

	}
	/**
	 * Takes a section id and returns the route of the first Train object that is currently in that section.
	 * @param section
	 */
	public Map.Entry<Integer,Route> getRoute(int section) {
		for(Map.Entry<Integer, Route> trainRoute : trainRoutes.entrySet()){
			for(Map.Entry<Integer,Train> trainOrientation : trainOrientations.entrySet()){
				if (trainOrientation.getKey() == trainRoute.getKey()) return trainRoute;
			}

		}
		return null; // no train was on the section that we provided.
	}


}
