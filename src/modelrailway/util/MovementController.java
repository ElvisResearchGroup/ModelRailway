package modelrailway.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import modelrailway.core.Controller;
import modelrailway.core.Event;
import modelrailway.core.Section;
import modelrailway.core.Event.Listener;
import modelrailway.core.Route;
import modelrailway.simulation.Switch;
import modelrailway.core.Train;
import modelrailway.simulation.BackSwitch;
import modelrailway.simulation.Crossing;
import modelrailway.simulation.ForwardSwitch;
import modelrailway.simulation.Simulator;
import modelrailway.simulation.Track;
/**
 * This test controller has no collision detection but just tests to make sure that a train can follow a route. in order to test the train simulator.
 * @author powleybenj
 *
 */
public class MovementController implements Controller, Listener {

	private Event.Listener trackController = null;
	private List<Listener> listeners = new ArrayList<Listener>();
	private Map<Integer,Route> trainRoutes = new ConcurrentHashMap<Integer,Route>();
	private Map<Integer,modelrailway.core.Train> trainOrientations ;
	private Map<Integer,Boolean> isMoving = new ConcurrentHashMap<Integer,Boolean>();
	private Map<Integer, Section> sections;
	private Track head;
	public class AlreadyHere extends RuntimeException{
		public Train tr;
		public AlreadyHere(Train tr){
			this.tr = tr;
		}


	}
	/**
	 * the trainOrientations method returns a mapping from trainID integers to modelrailway.core.Train objects.
	 * @return
	 */
	public Map<Integer, modelrailway.core.Train> trainOrientations(){
		return trainOrientations;
	}
	/**
	 * sections() returns a mapping from sectionID integers to Section objects.
	 * @return
	 */
	public Map<Integer, Section> sections(){
		return sections;
	}
	/**
	 * the routes() method returns a map from trainID integers to Route objects.
	 * @return
	 */
	public Map<Integer, Route> routes(){
		return trainRoutes;
	}
	/**
	 * The testController is produced using a mapping of trainIDs to trainorientations, a mapping of sectionID's  to Section objects, The head section of a train track,
	 * And a Controller, This TestController is listed as a listener to the controller provided.
	 *
	 * @param orientations
	 * @param intSecMap
	 * @param head
	 * @param trackController
	 */
	public MovementController( Map<Integer,modelrailway.core.Train> orientations, Map<Integer, Section> intSecMap , Track head, Event.Listener trackController){
		this.trackController = trackController;
		trainOrientations = orientations; // are the trains going backwards or fowards.
		this.sections = intSecMap;
		this.head = head;
	}

	public Integer calculateSectionNumber(Event.SectionChanged ev){
		return 1 + ((((Event.SectionChanged)ev).getSection()-1) * 2);
	}

	public Integer calculateEventNumber(int i){
		return ((i - 1)/2) +1;

	}
	/*
	 *  adjustSection returns a pair containing the section the train has just moved into and the train that has just moved.
	 *  adjustSection adjusts the train object to the section that the train has just moved into.
	 */
	public Train adjustSection(Event e, boolean moving){
		synchronized(isMoving){
			Pair<Integer,Train> pair = this.sectionTrainMovesInto((Event.SectionChanged)e, moving);

			if(pair == null) throw new AlreadyHere(null);//throw new RuntimeException("Fault in section movement");
			if(pair.fst == null && pair.snd != null) throw new AlreadyHere(pair.snd); // throw an exception containing the train that has moved in twice
			//System.out.println("pair.fst: "+pair.fst);
			pair.snd.setSection(pair.fst); // no exception has been thrown so now we set the section that the train is in.
			return pair.snd;
	    }
	}


	@Override
	public void notify(Event e) {
		Train tr = null;
		try{
		  if(e instanceof Event.SectionChanged ){ // when there is a section change into another section
			  tr = adjustSection(e,true);

		  }
		}catch(AlreadyHere h){
			tr = h.tr;
		}
		if(tr != null){
		  if(e instanceof Event.SectionChanged){
	       moveIntoSection(tr); // re-perform section movement when we move in twice.
		  }
		}
		for(Listener l : listeners){
			//System.out.println("Notify listeners: "+ e.getClass().toString());
			l.notify(e);
		}

	}

	public Pair<Track,Track> getCurrentTrackSection(Section currentSection, Section previousSection, boolean movingForward){
		Track prev = null;
		Track curr = null;
		//if(currentSection == null) throw new RuntimeException("The section was null");
		//if(currentSection.size() == 0) throw new RuntimeException("No sections for track: ");
		for(Track t: currentSection){
			if(movingForward){
				//System.out.println("moving forward");
				if(t.getPrevious(true).getSection() == previousSection){
					prev = t.getPrevious(true);
					curr = t;

				}
                if(t.getPrevious(false).getSection() == previousSection){
                	prev = t.getPrevious(false);
                	curr = t;
                }
			    if(t.getPrevious(true).getAltSection() == previousSection){
			    	prev = t.getPrevious(true);
			    	curr = t;
			    }
			    if(t.getPrevious(false).getAltSection() == previousSection){
					prev = t.getPrevious(false);
					curr = t;
				}

			}
			else{
				//System.out.println("moving back");
				if(t.getNext(true).getSection() == previousSection){
					prev = t.getNext(true);
					curr = t;

				}
                if(t.getNext(false).getSection() == previousSection){
                	prev = t.getNext(false);
                	curr = t;
                }
			    if(t.getNext(true).getAltSection() == previousSection){
			    	prev = t.getNext(true);
			    	curr = t;
			    }
			    if(t.getNext(false).getAltSection() == previousSection){
					prev = t.getNext(false);
					curr = t;
				}
			}
		}
		return new Pair<Track,Track>(prev,curr);
	}

	public Pair<Integer,Train> sectionTrainMovesInto(Event.SectionChanged e, boolean moving){
		Integer eventsectionID =  this.calculateSectionNumber((Event.SectionChanged) e);

		if(((Event.SectionChanged) e).getInto()){ // if we are moving into a section,
			//System.out.println("Moving in");
			for(Map.Entry<Integer, Train> tr: trainOrientations.entrySet()){ // go through all the trains

			    Train trainObj = tr.getValue(); // for each train
			    if(isMoving.get(tr.getKey()) != null && isMoving.get(tr.getKey()) == moving){ // if the train is moving
			    	//System.out.println("tr.getKey(): " + tr.getKey());
			    	//System.out.println("trainObj.currentSection(): "+ trainObj.currentSection());
			    	//System.out.println("trainRoutes.get(tr.getKey()).nextSection(trainObj.currentSection()): "+ trainRoutes.get(tr.getKey()).nextSection(trainObj.currentSection()));
			    	//System.out.println("eventsectionID: "+ eventsectionID);
			    	Queue<Integer> reqCurrent = sections().get(eventsectionID).getEntryRequests();

			    	
			    	Route rt = trainRoutes.get(tr.getKey());

			    	Integer nextID = null;
			    	if(!(trainObj.currentSection() == rt.lastSection() && !rt.isALoop())){ // if we are not at the last section of a non loop.
			    	  nextID = trainRoutes.get(tr.getKey()).nextSection(trainObj.currentSection()) ;
			    	}
			    	Queue<Integer> reqNext = sections().get(nextID).getEntryRequests();

			    	if(!(!rt.isALoop()&& trainObj.currentSection() == rt.lastSection()) && nextID == eventsectionID){

			    		if( !reqNext.contains(tr.getKey()) || reqNext.peek() == tr.getKey() ){ // if we are running without locking, or if this is the next train on the list.
			    		  //System.out.println("Train: "+tr.getKey());
			    		  return new Pair<Integer,Train>(eventsectionID, trainObj);
			    		}
			    	} else if(trainObj.currentSection() == eventsectionID){ // if the train is already in the section
			    		if( !reqCurrent.contains(tr.getKey()) || reqCurrent.peek() == tr.getKey() ){
			    		  //System.out.println("Train: "+tr.getKey());
			    		  return new Pair<Integer,Train>(null,trainObj);
			    		}
			    	}
			    }
			}
		}
		else { // when we are moving out of a section.

			for(Map.Entry<Integer, modelrailway.core.Train> tr : trainOrientations.entrySet()){
				   Train trainObj = tr.getValue();
				   if(isMoving.get(tr.getKey()) != null && isMoving.get(tr.getKey()) == moving){
					   //Queue<Integer> reqCurrent = sections().get(eventsectionID).getEntryRequests();
					   //System.out.println("currentSec: "+trainObj.currentSection());
					  // System.out.println("prev: "+trainRoutes.get(tr.getKey()).prevSection(trainObj.currentSection()));
					   Route rt = trainRoutes.get(tr.getKey());

					   //if(!rt.isALoop() && rt.firstSection() == trainObj.currentSection()){


					   //}
					   Integer prev = null;
					   if(!(trainObj.currentSection() == rt.firstSection() && !rt.isALoop())){
					     prev= trainRoutes.get(tr.getKey()).prevSection(trainObj.currentSection()) ;
					   }
					  // Queue<Integer> reqPrev = sections().get(prev).getEntryRequests();
					   //System.out.println("currentSection: "+ trainObj.currentSection());
					   //System.out.println("eventsectionID: "+ eventsectionID);
					   if(trainObj.currentSection() == eventsectionID){

						  // if( (!reqCurrent.contains(tr.getKey())) || reqCurrent.peek() == tr.getKey() ){
						    Integer nextOne = trainRoutes.get(tr.getKey()).nextSection(trainObj.currentSection());
					     	eventsectionID = nextOne;
					     	//System.out.println("Train: "+tr.getKey());
					     	return new Pair<Integer,Train>(eventsectionID,trainObj);
						   //}
					   }
					   else if ( !(!rt.isALoop()&& trainObj.currentSection() == rt.firstSection())&&  prev == eventsectionID){ // if the train is already in the section, this should not happen on the first section
						   //if( (!reqPrev.contains(tr.getKey())) || reqPrev.peek() == tr.getKey() ){
							//System.out.println("Train: "+tr.getKey());
						    return new Pair<Integer,Train>(null,trainObj);
						   //}
					   }
				   }
			}
		}
		return null;
	}

	/**
	 * We have moved into a section. Do what is necissary to configure the section that we have just moved into.
	 *
	 * @param train
	 * @return
	 */
	protected void moveIntoSection( Train train){
		//System.out.println("In Movement Move.");
		Integer eventsectionID = train.currentSection(); // The train object has already been adjusted
		System.out.println("The section of the train: "+ eventsectionID);

		for(Entry<Integer, Train> trainOrientation: trainOrientations.entrySet()){ // for all the trains on the track, we need to find the train id that matches the train we have.

			Integer section = trainOrientation.getValue().currentSection(); // get the section of the train

			// we compare sections to see that we are working with the same train as has been passed in.
			// so trainOrientation.getKey() will have the train id of the train object
	    	if(section ==  eventsectionID && trainRoutes.get(trainOrientation.getKey()) != null){ // check that the front of the train is in the section
	    		// first work out which track segments we are currently dealing with.
	    		Integer prevSection = trainRoutes.get(trainOrientation.getKey()).prevSection(section); // the previous section that we came from
	    		Section previous = sections.get(prevSection); // the previous section object matching the prevSection id.

	    		Section thisSection = sections.get(section); // the section object matching the id of the current section that the train is in

	    		Track thisTrack = sections.get(eventsectionID).get(0);
	    		//System.out.println("Previous: "+previous.getNumber());
	    		//System.out.println("");

	    		// this is where unlocking should be inserted in the collision controller.

	    		//System.out.println("Switch"+thisTrack);
	    		//System.out.println("trackSection: "+thisTrack.getSection().getNumber());

	    		if(thisTrack instanceof Switch){ // move switches
	    			Integer trainID = trainOrientation.getKey();
	    			Train trainObj = trainOrientation.getValue();
	    			moveSwitches(trainID, section, trainObj, thisTrack);
	    		}
	    	}
       }
	}


	/**
	 * trainID , the ID of the train moving through, section: the section number that we are switching.
	 * trainObj : the Train object for the train with trainID as its identification number,
	 * thisTrack: the track that contains the switch we are moving first.
	 * @param trainID
	 * @param section
	 * @param trainObj
	 * @param thisTrack
	 */
	public void moveSwitches(Integer trainID, Integer section, Train trainObj, Track  thisTrack){


		Route rt = trainRoutes.get(trainID);
		Section sectionS = sections.get(section);
	    Integer nextSec = rt.nextSection(section);
		Integer prevSec = rt.prevSection(section); // the section we came from
		//System.out.println("sectionS: "+ section +", nextSec: "+ nextSec+ ", prevSec: "+ prevSec);
		if(section == rt.firstSection() || section == rt.lastSection() || prevSec == null || nextSec == null) return; // dont change first and last section switches.
		Pair<Integer, Integer> sectionPair = new Pair<Integer,Integer>(prevSec,nextSec);
	    // for sectionPair
	    List<Boolean> switchingOrder = sectionS.retrieveSwitchingOrder(sectionPair);
	    //System.out.println("moveSwitches, pair: "+sectionPair.toString());
		if(!trainObj.currentOrientation()){
		  Track tr = thisTrack;
    	  for(Boolean bl: switchingOrder){ // for each switch
    		  if(!(tr instanceof Switch)) throw new RuntimeException("An invalid Section has been encountered as the section has multiple pieces and a piece is not a switch");
    		  this.set(((Switch)tr).getSwitchID() -1 , bl);
    		  tr = thisTrack.getPrevious(bl); // follow track
    	  }
		}
		else{
		  Track tr = thisTrack;

	      for(Boolean bl: switchingOrder){ // for each switch//
	    	 if(!(tr instanceof Switch)) throw new RuntimeException("An invalid Section has been encountered as the section has multiple pieces and a piece is not a switch");
	    	 this.set(((Switch)tr).getSwitchID()-1, bl); // minus 1 to adjust for counting from zero in turnout array
	    	 tr = thisTrack.getNext(bl); // follow track
	      }
		}

	}

	@Override
	public void register(Listener listener) {

		listeners.add(listener);


	}

	public void deregister(Listener listener){
		listeners.remove(listener);

	}

	@Override
	public boolean start(int trainID, Route route) {
		synchronized(isMoving){

		isMoving.put(trainID, true);
		trainRoutes.put(trainID,route);
		if(trackController == null) throw new RuntimeException("track controller was null in start train");
	    trackController.notify((Event)new Event.SpeedChanged(trainID, 6));
		return true;
		}


	}

	public boolean resumeTrain(int trainID){
		synchronized(isMoving){

		isMoving.put(trainID, true);
		((Listener) trackController).notify((Event)new Event.SpeedChanged(trainID, 6));
		return true;
		}

	}

	@Override
	public void stop(int trainID) {
		synchronized(isMoving){
		//System.out.println("Stopping: "+ trainID);
		//System.out.println(isMoving.values().toString());
		isMoving.put(trainID, false);
		((Listener) trackController).notify((Event) new Event.SpeedChanged(trainID, 0));
		((Listener) trackController).notify((Event)new Event.EmergencyStop(trainID));
		}
	}


	@Override
	public modelrailway.core.Train train(int trainID) {
		return trainOrientations.get(trainID); // change to notification
	}

	@Override
	/**
	 * Set the switch at the given turnoutId, thrown is true if we are setting the switch to the alternate section.
	 */
	public void set(int turnoutID, boolean thrown) {
		//System.out.println("set a turnout");
		((Listener) trackController).notify(new Event.TurnoutChanged(turnoutID, thrown));

	}
	/**
	 * Takes a section id and returns the route of the first Train object that is currently in that section.
	 * @param section
	 */
	public Map.Entry<Integer,Route> getRoute(int section){
		for(Map.Entry<Integer, Route> trainRoute : trainRoutes.entrySet()){
			for(Map.Entry<Integer,Train> trainOrientation : trainOrientations.entrySet()){
				if (trainOrientation.getKey() == trainRoute.getKey()){
					if(trainOrientation.getValue().currentSection() == section)return trainRoute;
				}
			}

		}
		return null; // no train was on the section that we provided.
	}


}
