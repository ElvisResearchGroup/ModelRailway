package modelrailway.util;

import java.util.Map;

import modelrailway.core.Controller;
import modelrailway.core.Event;
import modelrailway.core.Route;
import modelrailway.core.Section;
import modelrailway.simulation.Simulator;
import modelrailway.simulation.Switch;
import modelrailway.simulation.Track;
import modelrailway.simulation.Train;

public class ControlerCollision extends MovementController implements Controller{

	public ControlerCollision(Map<Integer, modelrailway.core.Train> trains,
			Map<Integer, Section> sections, Track head, Event.Listener trackControler) {
		super(trains, sections, head, trackControler);
		// TODO Auto-generated constructor stub
	}

	public void notify(Event e){
		Integer train = tryLocking(e);
		if(train == null){ // when it is null do not do the movement
		  super.notify(e);
		}else{
		  super.notify(e);
		  super.notify(new Event.EmergencyStop(train));
		}
	}
	/**
	 * Try to obtain the lock for the section ahead of where we are traveling into if we are traveling into a section.
	 * @param e
	 * @return
	 */
	private Integer tryLocking(Event e){
		//assumes that we have already adjusted for the section change in Train.
		if((e instanceof Event.SectionChanged)){ // when we are moving into a section
			try{
				adjustSection(e); // adjust section for the next section
			}catch(AlreadyHere ex){
				//System.out.println("already Here in tryLocking");
				//return e;

			}

			Integer sectionID = 1 + ((((Event.SectionChanged)e).getSection()-1) * 2);

			//System.out.println("sectionChanged: "+e +" sectionID: "+sectionID);
			Map.Entry<Integer, Route> entry = super.getRoute(sectionID); //

			//System.out.println("is entry null: "+(entry == null));
			Integer train = entry.getKey(); // get the train
			Route trainRoute = entry.getValue(); // get the route that the train has planned.

			Integer nextSec = entry.getValue().nextSection(this.trainOrientations().get(train).currentSection());  // get section number the train changed into.
			//System.out.println("nextSec: "+nextSec +", thisSec: "+ sectionID);



			//System.out.println("entryValue: "+entry.getValue());

			if(this.trainOrientations().get(train).currentOrientation() == true){
				System.out.println("Orientation: "+this.trainOrientations().get(train).currentOrientation());
				Section thisSec = this.sections().get(this.trainOrientations().get(train).currentSection());
				//System.out.println("currentSection: "+this.trainOrientations().get(train).currentSection());

				Track front = thisSec.get(0);
				Track notAltNext = front.getNext(false);
				Track altNext = front.getNext(true);
				boolean reserved = false ;
				//System.out.println("reserve sections: notalt: "+ notAltNext.getSection().getNumber() + " alt: "+altNext.getSection().getNumber() +" section: "+ nextSec);
				//System.out.println("nextSec: "+nextSec);
				//System.out.println("notAltNext: "+notAltNext.getSection().getNumber());
				//System.out.println("altNext: "+altNext.getSection().getNumber());

				//System.out.println("thisSec: "+thisSec.getNumber());
				if(notAltNext.getSection().getNumber() == nextSec){

					System.out.println("notalt next, not alt sec number: "+notAltNext.getSection().getNumber());
					boolean reserved1 = notAltNext.getSection().reserveSection(train);
					System.out.println("reserved1: "+reserved1);
					System.out.println("Movables In Section: "+ notAltNext.getSection().getMovableSet());
					System.out.println("Movables In Queue:"+notAltNext.getSection().getEntryRequests());
					boolean reserved2 = false;
					if(notAltNext.getAltSection() != null){ reserved2 = notAltNext.getAltSection().reserveSection(train);}
					else{ reserved2 = true;}
					//System.out.println("reserved:" +reserved);
					reserved = reserved1 && reserved2;
					// fix switches in reserved section.
					if(notAltNext.getSection().get(0) instanceof Switch){
					  super.moveSwitches(train, notAltNext.getSection().getNumber(), trainOrientations().get(train), notAltNext.getSection().get(0));
					}



				}else  if(notAltNext.getAltSection() != null && notAltNext.getAltSection().getNumber() == nextSec){
					System.out.println("notalt next, alt sec number: "+notAltNext.getSection().getNumber());
					boolean reserved1 = notAltNext.getAltSection().reserveSection(train);
					boolean reserved2 = notAltNext.getSection().reserveSection(train);
					reserved = reserved1 && reserved2;

					if(notAltNext.getAltSection().get(0) instanceof Switch){
					  super.moveSwitches(train, notAltNext.getAltSection().getNumber(), trainOrientations().get(train), notAltNext.getAltSection().get(0));
					}

				}else if(altNext.getSection().getNumber() == nextSec){
					//System.out.println();
					System.out.println("alt next, not alt sec number: "+altNext.getSection().getNumber());
					boolean reserved1 = altNext.getSection().reserveSection(train);
					boolean reserved2 = false;
					if(altNext.getAltSection() != null){reserved2 = altNext.getAltSection().reserveSection(train);}
					else {reserved2 = true;}
					reserved = reserved1 && reserved2;
					if(altNext.getSection().get(0) instanceof Switch){
				      super.moveSwitches(train, altNext.getSection().getNumber(), trainOrientations().get(train), altNext.getSection().get(0));
					}


				}else if(altNext.getAltSection() != null && altNext.getAltSection().getNumber() == nextSec){
					System.out.println("alt next, alt sec number: "+altNext.getAltSection().getNumber());
					boolean reserved1 = altNext.getAltSection().reserveSection(train);
					boolean reserved2 = altNext.getSection().reserveSection(train);
					reserved = reserved1 && reserved2;
					if(altNext.getAltSection().get(0) instanceof Switch){
				      super.moveSwitches(train, altNext.getAltSection().getNumber(), trainOrientations().get(train), altNext.getAltSection().get(0));
				    }
				}
				//System.out.println("reserved: "+reserved);
				if(reserved == false){ // we need to trigger an emergency stop
					System.out.println("========train is being stoped as reserved is false");

					System.out.println("altNext, section: "+altNext.getSection().getNumber() + "next, section "+notAltNext.getSection().getNumber() +" thisSec: " +thisSec.getNumber());

					this.stop(train);
					System.out.println("sending emergency stop");

					return train; // dont to the movement
				}
			}
			else{
				System.out.println("Orientation: "+this.trainOrientations().get(train).currentOrientation());
				Section thisSec = this.sections().get(this.trainOrientations().get(train).currentSection());
				Track back = thisSec.get(0); // length is not supported.
				Track notAltPrev = back.getPrevious(false);
				Track altPrev = back.getPrevious(true);
				boolean reserved = false ;
				//System.out.println("backend: ");
				if(notAltPrev.getSection().getNumber() == nextSec){
					boolean reserved1 = notAltPrev.getSection().reserveSection(train);
					boolean reserved2 = false;
					if(notAltPrev.getAltSection() != null){reserved2 = notAltPrev.getAltSection().reserveSection(train);}
					else {reserved2 = true;}
					reserved = reserved1 && reserved2;
					if(notAltPrev.getSection().get(notAltPrev.getSection().size()-1) instanceof Switch){
					  super.moveSwitches(train, notAltPrev.getSection().getNumber(), trainOrientations().get(train), notAltPrev.getSection().get(notAltPrev.getSection().size()-1));
				    }

				} if(notAltPrev.getAltSection() != null && notAltPrev.getAltSection().getNumber() == nextSec){
					boolean reserved1 = notAltPrev.getSection().reserveSection(train);
					boolean reserved2 =notAltPrev.getAltSection().reserveSection(train);
					reserved = reserved1 && reserved2;
					if(notAltPrev.getAltSection().get(notAltPrev.getAltSection().size()-1) instanceof Switch){
						 super.moveSwitches(train, notAltPrev.getAltSection().getNumber(), trainOrientations().get(train), notAltPrev.getAltSection().get(notAltPrev.getAltSection().size()-1));
					}

				} if(altPrev.getSection().getNumber() == nextSec){
					boolean reserved1 = altPrev.getSection().reserveSection(train);
					boolean reserved2 = false;
					if(altPrev.getAltSection() != null) {reserved2 = altPrev.getAltSection().reserveSection(train);}
					else{reserved2 = true;}
					reserved = reserved1 && reserved2;
					if(altPrev.getAltSection().get(altPrev.getAltSection().size()-1) instanceof Switch){
						 super.moveSwitches(train, altPrev.getAltSection().getNumber(), trainOrientations().get(train), altPrev.getAltSection().get(altPrev.getAltSection().size()-1));
					}

				} if(altPrev.getAltSection() != null && altPrev.getAltSection().getNumber() == nextSec){
					boolean reserved1 = altPrev.getAltSection().reserveSection(train);
					boolean reserved2 = altPrev.getSection().reserveSection(train);
					reserved = reserved1 && reserved2;
					if(altPrev.getAltSection().get(notAltPrev.getAltSection().size()-1) instanceof Switch){
						 super.moveSwitches(train, altPrev.getAltSection().getNumber(), trainOrientations().get(train), altPrev.getAltSection().get(altPrev.getAltSection().size()-1));
					}
				}

				if(reserved == false){
					//System.out.println("========train is being stoped as reserved is false");
					this.stop(train);
					//super.notify(new Event.EmergencyStop(train));
					return train; // dont do the movement.
				}
			}
		}
		return null;
	}

}
