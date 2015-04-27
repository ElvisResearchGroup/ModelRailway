package modelrailway.testing;

import java.util.Map;

import modelrailway.core.Controller;
import modelrailway.core.Event;
import modelrailway.core.Route;
import modelrailway.simulation.Section;
import modelrailway.simulation.Track;
import modelrailway.simulation.Train;

public class TestControlerCollision extends TestController implements Controller{

	public TestControlerCollision(Map<Integer, Train> trains,
			Map<Integer, modelrailway.core.Train> orientations, Track head,
			Controller trackController) {
		super(trains, orientations, head, trackController);
		// TODO Auto-generated constructor stub
	}

	public void notify(Event e){
		super.notify(tryLocking(e));
	}
	/**
	 * Try to obtain the lock for the section ahead of where we are traveling into if we are traveling into a section.
	 * @param e
	 * @return
	 */
	private Event tryLocking(Event e){
		if((e instanceof Event.SectionChanged) && ((Event.SectionChanged) e).getInto()){ // when we are moving into a section

			Map.Entry<modelrailway.simulation.Train, Route> entry = super.getRoute(((Event.SectionChanged)e).getSection());
			Integer sec = entry.getValue().nextSection(((Event.SectionChanged) e).getSection());  // get section number the train changed into.
			Train train = entry.getKey(); // get the train
			Route trainRoute = entry.getValue(); // get the route that the train has planned.
			Integer nextSec = trainRoute.nextSection(sec);
			//reserve sections.
			if(train.isFowards()){
				Track front = train.getFront();
				Track notAltNext = front.getNext(false);
				Track altNext = front.getNext(true);
				boolean reserved = false ;
				if(notAltNext.getSection().getNumber() == nextSec){
					reserved = notAltNext.getSection().reserveSection(train);

				} if(notAltNext.getAltSection() != null && notAltNext.getAltSection().getNumber() == nextSec){
					reserved = notAltNext.getAltSection().reserveSection(train);

				} if(altNext.getSection().getNumber() == nextSec){
					reserved = altNext.getSection().reserveSection(train);

				} if(altNext.getAltSection() != null && altNext.getAltSection().getNumber() == nextSec){
					reserved = altNext.getAltSection().reserveSection(train);
				}

				if(reserved == false){ // we need to trigger an emergency stop
					Integer id = super.getID(train);
					this.stop(super.getID(train));
					super.notify(new Event.EmergencyStop(id));
				}
			}
			else{
				Track back = train.getBack();
				Track notAltPrev = back.getPrevious(false);
				Track altPrev = back.getPrevious(true);
				boolean reserved = false ;
				if(notAltPrev.getSection().getNumber() == nextSec){
					reserved = notAltPrev.getSection().reserveSection(train);

				} if(notAltPrev.getAltSection() != null && notAltPrev.getAltSection().getNumber() == nextSec){
					reserved =notAltPrev.getAltSection().reserveSection(train);

				} if(altPrev.getSection().getNumber() == nextSec){
					reserved = altPrev.getSection().reserveSection(train);

				} if(altPrev.getAltSection() != null && altPrev.getAltSection().getNumber() == nextSec){
					reserved = altPrev.getAltSection().reserveSection(train);
				}

				if(reserved == false){
					Integer id = super.getID(train);
					this.stop(id);
					super.notify(new Event.EmergencyStop(id));
				}
			}
		}
		return e;
	}

}
