package modelrailway.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
	/*
	 *  adjustSection returns a pair containing the section the train has just moved into and the train that has just moved.
	 *  adjustSection adjusts the train object to the section that the train has just moved into.
	 */
	public Train adjustSection(Event e){
		synchronized(isMoving){
			Pair<Integer,Train> pair = this.sectionTrainMovesInto((Event.SectionChanged)e);

			if(pair == null) throw new AlreadyHere(null);//throw new RuntimeException("Fault in section movement");
			if(pair.fst == null && pair.snd != null) throw new AlreadyHere(pair.snd); // throw an exception containing the train that has moved in twice
			pair.snd.setSection(pair.fst); // no exception has been thrown so now we set the section that the train is in.
			return pair.snd;
	    }
	}
	@Override
	public void notify(Event e) {
		Train tr = null;
		try{
		  if(e instanceof Event.SectionChanged ){ // when there is a section change into another section
			  tr = adjustSection(e);

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
			System.out.println("Notify listeners: "+ e.getClass().toString());
			l.notify(e);
		}

	}

	public Pair<Track,Track> getCurrentTrackSection(Section currentSection, Section previousSection, boolean movingForward){
		Track prev = null;
		Track curr = null;
		if(currentSection == null) throw new RuntimeException("The section was null");
		if(currentSection.size() == 0) throw new RuntimeException("No sections for track: ");
		for(Track t: currentSection){
			if(movingForward){
				System.out.println("moving forward");
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
				System.out.println("moving back");
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

	public Pair<Integer,Train> sectionTrainMovesInto(Event.SectionChanged e){
		Integer eventsectionID =  this.calculateSectionNumber((Event.SectionChanged) e);

		if(((Event.SectionChanged) e).getInto()){ // if we are moving into a section,
			System.out.println("Moving in");;
			for(Map.Entry<Integer, Train> tr: trainOrientations.entrySet()){ // go through all the trains

			    Train trainObj = tr.getValue(); // for each train
			    if(isMoving.get(tr.getKey()) != null && isMoving.get(tr.getKey())){ // if the train is moving
			    	System.out.println("tr.getKey(): " + tr.getKey());
			    	System.out.println("trainObj.currentSection(): "+ trainObj.currentSection());
			    	System.out.println("trainRoutes.get(tr.getKey()).nextSection(trainObj.currentSection()): "+ trainRoutes.get(tr.getKey()).nextSection(trainObj.currentSection()));
			    	System.out.println("eventsectionID: "+ eventsectionID);
			    	if(trainRoutes.get(tr.getKey()).nextSection(trainObj.currentSection()) == eventsectionID){
			    		System.out.println("return the pair.");
			    		return new Pair<Integer,Train>(eventsectionID, trainObj);
			    	} else if(trainObj.currentSection() == eventsectionID){ // if the train is already in the section
			    		return new Pair<Integer,Train>(null,trainObj);
			    	}
			    }
			}
		}
		else { // when we are moving out of a section.

			for(Map.Entry<Integer, modelrailway.core.Train> tr : trainOrientations.entrySet()){
				   Train trainObj = tr.getValue();
				   if(isMoving.get(tr.getKey()) != null && isMoving.get(tr.getKey())){
					   if(trainObj.currentSection() == eventsectionID){
						    Integer nextOne = trainRoutes.get(tr.getKey()).nextSection(trainObj.currentSection());
					     	eventsectionID = nextOne;
					     	return new Pair<Integer,Train>(eventsectionID,trainObj);
					   }
					   else if (trainRoutes.get(tr.getKey()).prevSection(trainObj.currentSection()) == eventsectionID){ // if the train is already in the section
						    return new Pair<Integer,Train>(null,trainObj);
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
	private void moveIntoSection( Train train){

		Integer eventsectionID = train.currentSection(); // The train object has already been adjusted


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
	    		System.out.println("Previous: "+previous.getNumber());
	    		System.out.println("");

	    		unlockSection(previous, trainOrientation); // unlock the section we just came from
	    		try{
	    			Track tr = previous.get(0);
	    			if(tr.getNext(false).getSection() == thisSection|| tr.getNext(false).getAltSection() == thisSection){
	    				unlockSection(tr.getNext(true).getSection(), trainOrientation);
	    				unlockSection(tr.getNext(true).getAltSection(),trainOrientation);

	    			} else if (tr.getNext(true).getSection() == thisSection || tr.getNext(true).getAltSection()== thisSection){
	    				unlockSection(tr.getNext(false).getSection(), trainOrientation);
	    				unlockSection(tr.getNext(false).getAltSection(),trainOrientation);

	    			} else if (tr.getPrevious(false).getSection() == thisSection || tr.getPrevious(false).getAltSection() == thisSection){
	    				unlockSection(tr.getPrevious(true).getSection(), trainOrientation);
	    				unlockSection(tr.getPrevious(true).getAltSection(),trainOrientation);
	    			} else if (tr.getPrevious(true).getSection() == thisSection|| tr.getPrevious (false).getAltSection() == thisSection){
	    				unlockSection(tr.getPrevious(false).getSection(), trainOrientation);
	    				unlockSection(tr.getPrevious(false).getAltSection(),trainOrientation);
	    			}

	    		} catch(RuntimeException ex){}

	    		//System.out.println("Switch"+thisTrack);
	    		System.out.println("trackSection: "+thisTrack.getSection().getNumber());

	    		if(thisTrack instanceof Switch){ // move switches
	    			Integer trainID = trainOrientation.getKey();
	    			Train trainObj = trainOrientation.getValue();
	    			moveSwitches(trainID, section, trainObj, thisTrack);
	    		}
	    	}
       }
	}
	/**
	 * unlock the provided section, in order for a train to unlock the section. The train must have just moved out of the section.
	 * and removed itself from the queue of trains waiting to leave the section.
	 * @param previous
	 * @param trainOrientation
	 */
	private void unlockSection(Section previous, Map.Entry<Integer,Train> trainOrientation){
		if(previous == null) return;
		Track t = previous.get(0); // for the track segment.
	    boolean trainMoved = false; // trainMoved = false,
		System.out.println("Key: "+ trainOrientation.getKey()+", "+t.getSection().getNumber());
		Section tracSec = t.getSection(); // get the section
		Section trackAltSec = t.getAltSection(); // get the alternate section
		if(tracSec != null){
			Pair<Boolean,Integer> pair = null;
			if(!tracSec.isQueueEmpty() && trainOrientation.getKey() != null){
				pair = tracSec.removeFromQueue(trainOrientation.getKey());
				System.out.println("result from removing from queue: element removed: "+pair.fst +", trainonQueue: "+pair.snd );
			}
			if(pair != null){ //
				  if(pair.fst != null && pair.fst){ // instruct next train to move.
					  if(!trainMoved && pair.snd != null) {
						  System.out.println("Resuming the train : "+ pair.snd);
						  this.resumeTrain(pair.snd);
						  trainMoved = true;
					  }
				  }
			}
		}
		Pair<Boolean,Integer> pair = null;
		if((trackAltSec != null) && (!trackAltSec.isQueueEmpty()) && (trainOrientation.getKey() != null)){
			pair = trackAltSec.removeFromQueue(trainOrientation.getKey());
		}
		if(pair != null && pair.snd != null){
			if(pair.fst != null && pair.fst){
				if(!trainMoved && pair.snd!= null) {
					this.resumeTrain(pair.snd);
					trainMoved = true;
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
		Pair<Integer, Integer> sectionPair = new Pair<Integer,Integer>(prevSec,nextSec);
	    // for sectionPair
	    List<Boolean> switchingOrder = sectionS.retrieveSwitchingOrder(sectionPair);
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
		isMoving.put(trainID, false);

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
		System.out.println("set a turnout");
		((Listener) trackController).notify(new Event.TurnoutChanged(turnoutID, thrown));

	}
	/**
	 * Takes a section id and returns the route of the first Train object that is currently in that section.
	 * @param section
	 */
	public Map.Entry<Integer,Route> getRoute(int section){
		for(Map.Entry<Integer, Route> trainRoute : trainRoutes.entrySet()){
			for(Map.Entry<Integer,Train> trainOrientation : trainOrientations.entrySet()){
				if (trainOrientation.getKey() == trainRoute.getKey()) return trainRoute;
			}

		}
		return null; // no train was on the section that we provided.
	}


}
