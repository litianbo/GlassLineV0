package gui.test;

import mocks.MockAnimation;
import mocks.MockConveyor;
import mocks.MockPopup;
import transducer.Transducer;
import transducer.TransducerDebugMode;
import engine.agent.Glass;
import engine.agent.Recipe;
import engine.agent.SensorAgent;
import engine.util.ConveyorFamily;
import junit.framework.TestCase;

/**
 * BackSensor: sensor2, the one whose left is conveyor, right is popup
 * FrontSensor: sensor1, the one whose left is popup, right conveyor
 * 
 * @author lenovo
 * 
 */
public class SensorTest extends TestCase {
	public void testSensor() {
		Transducer transducer = new Transducer();
		transducer.startTransducer();
		transducer.setDebugMode(TransducerDebugMode.EVENTS_AND_ACTIONS);
		Glass glass1 = new Glass(new Recipe(), "glass1");
		Glass glass2 = new Glass(new Recipe(), "glass2");
		Glass glass3 = new Glass(new Recipe(), "glass3");
		// last conveoyrfamily
		ConveyorFamily conveyorFamily1 = new ConveyorFamily();
		// this conveyor family
		ConveyorFamily conveyorFamily2 = new ConveyorFamily();
		// create mock popup
		MockPopup popup = new MockPopup("Preivous Popup", transducer,
				conveyorFamily2, conveyorFamily1);
		// create mock conveyor
		MockConveyor conveyor = new MockConveyor("Conveyor", transducer,
				conveyorFamily2);
		// createa mock animation
		MockAnimation animation = new MockAnimation(transducer);
		// create sensors
		SensorAgent frontSensor = new SensorAgent("Sensor1", transducer,
				conveyorFamily2, conveyorFamily1);// end a popup, start a
													// conveyor
		SensorAgent backSensor = new SensorAgent("Sensor2", transducer,
				conveyorFamily2, conveyorFamily1);// end a conveoyr, start a
													// popup

		conveyorFamily1.setPopup(popup);
		conveyorFamily2.setConveyor1(conveyor);
		conveyorFamily2.setSensor1(backSensor);
		conveyorFamily2.setSensor1(frontSensor);
		// since popup has been tested above, here, suppose popup sent a glass
		// to the frontSensor
		// preconditions
		assertEquals(
				"0 message should have been sent to the conveyor. Event log: "
						+ conveyor.log.toString(), 0, conveyor.log.size());
		assertEquals(
				"0 message should have been sent to the popup. Event log: "
						+ popup.log.toString(), 0, popup.log.size());
		assertEquals(
				"0 message should have been sent to the animation. Event log: "
						+ animation.log.toString(), 0, animation.log.size());

		frontSensor.msgCanISendGlass();
		// now, since at initial, the state of the sensor is empty, after
		// running the scheduler, it should notify popup to send glass
		frontSensor.pickAndExecuteAnAction();
		// test if popup receive the msg or not
		assertTrue(
				"Mock popup should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ popup.log.toString(),
				popup.log.containsString("I know that sensor is empty"));
		assertEquals(
				"1 message should have been sent to the popup. Event log: "
						+ popup.log.toString(), 1, popup.log.size());
		assertTrue(
				"Message should be sent from " + frontSensor.toString(),
				popup.log.getLastLoggedEvent().getMessage()
						.contains(frontSensor.getName()));
		popup.log.clear();
		assertEquals(
				"0 message should have been sent to the animation. Event log: "
						+ animation.log.toString(), 0, animation.log.size());
		assertEquals(
				"0 message should have been sent to the conveyor. Event log: "
						+ conveyor.log.toString(), 0, conveyor.log.size());
		// we test popup earlier, now, it should send msgHereIsGlass() to
		// sensor2
		frontSensor.msgHereIsGlass(popup, glass1);
		try {
			transducer.transducerThread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ animation.log.toString(),
				animation.log.containsString("SENSOR_GUI_PRESSED"));
		animation.log.clear();
		// now, this sensor is occupied, what if another glass coming?
		frontSensor.msgCanISendGlass();
		// run scheduler to call method to send correct msg
		frontSensor.pickAndExecuteAnAction();
		// test if popup receive the msg or not
		assertTrue(
				"Mock popup should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ popup.log.toString(),
				popup.log.containsString("I know that sensor is occupied"));
		assertEquals(
				"1 message should have been sent to the popup. Event log: "
						+ popup.log.toString(), 1, popup.log.size());
		assertTrue(
				"Message should be sent from " + frontSensor.toString(),
				popup.log.getLastLoggedEvent().getMessage()
						.contains(frontSensor.getName()));
		popup.log.clear();
		assertEquals(
				"0 message should have been sent to the animation. Event log: "
						+ animation.log.toString(), 0, animation.log.size());
		assertEquals(
				"0 message should have been sent to the conveyor. Event log: "
						+ conveyor.log.toString(), 0, conveyor.log.size());
		// after run the schduler, sensor pass to the conveyor
		frontSensor.pickAndExecuteAnAction();
		// mock conveyor should notified by msgCanISendGlass()
		assertTrue(
				"Mock conveyor should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ conveyor.log.toString(),
				conveyor.log
						.containsString("I know that sensor is going to send glass"));
		assertEquals(
				"1 message should have been sent to the conveyor. Event log: "
						+ conveyor.log.toString(), 1, conveyor.log.size());
		assertTrue(
				"Message should be sent from " + frontSensor.toString(),
				conveyor.log.getLastLoggedEvent().getMessage()
						.contains(frontSensor.getName()));
		conveyor.log.clear();
		// then, conveyor send back msgIAmEmpty() to sensor
		frontSensor.msgIAmEmpty(conveyor);
		// now, run the schduler,
		frontSensor.pickAndExecuteAnAction();
		// now, conveyor should receive msgHereIsGlass()
		assertTrue(
				"Mock conveyor should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ conveyor.log.toString(),
				conveyor.log
						.containsString("I know that there is glass coming"));
		assertEquals(
				"1 message should have been sent to the conveyor. Event log: "
						+ conveyor.log.toString(), 1, conveyor.log.size());
		assertTrue(
				"Message should be sent from " + frontSensor.toString(),
				conveyor.log.getLastLoggedEvent().getMessage()
						.contains(frontSensor.getName()));
		conveyor.log.clear();
		try {
			transducer.transducerThread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// animation received event sensor_gui_released
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ animation.log.toString(),
				animation.log.containsString("SENSOR_GUI_RELEASED"));
		animation.log.clear();
		assertEquals(
				"0 message should have been sent to the popup. Event log: "
						+ popup.log.toString(), 0, popup.log.size());
		// now, test if the sensor respond correctly when the conveyor has too
		// much glass on it
		// suppose popup give it a new glass glass2
		frontSensor.msgHereIsGlass(popup, glass2);
		// now, run scheduler to send glass to conveyor
		frontSensor.pickAndExecuteAnAction();
		// now, sensor sent msgCanISendGlass() to conveyor
		assertTrue(
				"Mock conveyor should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ conveyor.log.toString(),
				conveyor.log
						.containsString("I know that sensor is going to send glass"));
		assertEquals(
				"1 message should have been sent to the workstation. Event log: "
						+ conveyor.log.toString(), 1, conveyor.log.size());
		assertTrue(
				"Message should be sent from " + frontSensor.toString(),
				conveyor.log.getLastLoggedEvent().getMessage()
						.contains(frontSensor.getName()));
		conveyor.log.clear();
		try {
			transducer.transducerThread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ animation.log.toString(),
				animation.log.containsString("SENSOR_GUI_PRESSED"));
		animation.log.clear();
		assertEquals(
				"0 message should have been sent to the popup. Event log: "
						+ popup.log.toString(), 0, popup.log.size());

		// suppose conveyor is stopped due to the capacity of the back end
		// sensor
		// it will give frontSensor msgIAmOccupied();
		frontSensor.msgIAmOccupied(conveyor);
		// nothing should conveyor receive
		frontSensor.pickAndExecuteAnAction();
		assertEquals(
				"0 message should have been sent to the workstation. Event log: "
						+ conveyor.log.toString(), 0, conveyor.log.size());
		conveyor.log.clear();
		// popup from previous family should notified to stop sending glass
		// actually this one is not important, since before popup send glass, it
		// will send msgCanISendGlass() to sensor1 anyway
		assertTrue(
				"Mock popup should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ popup.log.toString(),
				popup.log.containsString("I know that sensor is occupied"));
		assertEquals(
				"1 message should have been sent to the workstation. Event log: "
						+ popup.log.toString(), 1, popup.log.size());
		assertTrue(
				"Message should be sent from " + frontSensor.toString(),
				popup.log.getLastLoggedEvent().getMessage()
						.contains(frontSensor.getName()));
		popup.log.clear();
		assertEquals(
				"0 message should have been sent to the animation. Event log: "
						+ animation.log.toString(), 0, animation.log.size());

		// after conveyor starts, test if sensor can send glass to conveyor and
		// notify popup
		frontSensor.msgIAmEmpty(conveyor);
		frontSensor.pickAndExecuteAnAction();
		// now, conveyor should receive msgHereIsGlass();
		assertTrue(
				"Mock conveyor should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ conveyor.log.toString(),
				conveyor.log
						.containsString("I know that there is glass coming"));
		assertEquals(
				"1 message should have been sent to the workstation. Event log: "
						+ conveyor.log.toString(), 1, conveyor.log.size());
		assertTrue(
				"Message should be sent from " + frontSensor.toString(),
				conveyor.log.getLastLoggedEvent().getMessage()
						.contains(frontSensor.getName()));
		conveyor.log.clear();
		try {
			transducer.transducerThread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ animation.log.toString(),
				animation.log.containsString("SENSOR_GUI_RELEASED"));
		animation.log.clear();

		// now, the state of front sensor is empty, test if popup working
		// properly
		// suppose it sends msgCanISendGlass
		frontSensor.msgCanISendGlass();
		frontSensor.pickAndExecuteAnAction();
		// now, popup should receive msgIAmEmpty()
		assertTrue(
				"Mock popip should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ popup.log.toString(),
				popup.log.containsString("I know that sensor is empty"));
		assertEquals(
				"1 message should have been sent to the workstation. Event log: "
						+ popup.log.toString(), 1, popup.log.size());
		assertTrue(
				"Message should be sent from " + frontSensor.toString(),
				popup.log.getLastLoggedEvent().getMessage()
						.contains(frontSensor.getName()));
		popup.log.clear();
		// done for front sensor test
		// now testing the back sensor
		// can it receive glass properly?
		backSensor.msgCanISendGlass(conveyor);
		backSensor.pickAndExecuteAnAction();
		// now, conveyor should receive msgIAmEmpty();
		assertTrue(
				"Mock conveyor should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ conveyor.log.toString(),
				conveyor.log.containsString("I know that sensor is empty"));
		assertEquals(
				"1 message should have been sent to the workstation. Event log: "
						+ conveyor.log.toString(), 1, conveyor.log.size());

		conveyor.log.clear();
		assertEquals(
				"0 message should have been sent to the animation. Event log: "
						+ animation.log.toString(), 0, animation.log.size());
		assertEquals(
				"0 message should have been sent to the popup. Event log: "
						+ popup.log.toString(), 0, popup.log.size());
		// again, suppose conveyor sends msgHereIsGlass() to the backsensor
		backSensor.msgHereIsGlass(conveyor, glass1);
		// set the popup to this conveyor family
		try {
			transducer.transducerThread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ animation.log.toString(),
				animation.log.containsString("SENSOR_GUI_PRESSED"));
		animation.log.clear();
		conveyorFamily2.setPopup(popup);
		// if the popup is empty, backSensor push glass to popup
		backSensor.pickAndExecuteAnAction();
		// popup should receive msgCanISendGlass()
		assertTrue(
				"Mock popup should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ popup.log.toString(),
				popup.log
						.containsString("I know that sensor is going to send glass"));
		assertEquals(
				"1 message should have been sent to the workstation. Event log: "
						+ popup.log.toString(), 1, popup.log.size());
		assertTrue(
				"Message should be sent from " + backSensor.toString(),
				popup.log.getLastLoggedEvent().getMessage()
						.contains(backSensor.getName()));
		popup.log.clear();
		assertEquals(
				"0 message should have been sent to the animation. Event log: "
						+ animation.log.toString(), 0, animation.log.size());

		// after popup said ok, it send msgIAmEmpty() to back sensor
		backSensor.msgIAmEmpty();
		backSensor.pickAndExecuteAnAction();
		// popup should receive msgHereIsGlass();
		assertTrue(
				"Mock popup should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ popup.log.toString(),
				popup.log.containsString("Popup received glass"));
		assertEquals(
				"1 message should have been sent to the workstation. Event log: "
						+ popup.log.toString(), 1, popup.log.size());
		assertTrue(
				"Message should be sent from " + backSensor.toString(),
				popup.log.getLastLoggedEvent().getMessage()
						.contains(backSensor.getName()));
		popup.log.clear();
		try {
			transducer.transducerThread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ animation.log.toString(),
				animation.log.containsString("SENSOR_GUI_RELEASED"));
		animation.log.clear();
		assertEquals(
				"0 message should have been sent to the conveyor. Event log: "
						+ conveyor.log.toString(), 0, conveyor.log.size());
		// now, if the popup is full or raising, sensor should wait and notify
		// the conveyor
		// popup respond to it as msgIAmOccupied
		backSensor.msgIAmOccupied();
		// now the state is changed to: EMPTY_BUT_POPUP_IS_NOT
		// conveyor shouldn't receive any message right now
		assertEquals(
				"0 message should have been sent to the workstation. Event log: "
						+ conveyor.log.toString(), 0, conveyor.log.size());
		// now, suppose conveyor sends glass to sensor
		backSensor.msgCanISendGlass(conveyor);
		backSensor.pickAndExecuteAnAction();
		// now, conveyor should receive message msgIAmEmpty() because sensor is
		// empty right now. After it is occupied by this glass, if the conveyor
		// wants to send one more glass (which means that glass is close to
		// sensor), stop the whole conveyor instead of crashing
		assertTrue(
				"Mock conveyor should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ conveyor.log.toString(),
				conveyor.log.containsString("I know that sensor is empty"));
		assertEquals(
				"1 message should have been sent to the workstation. Event log: "
						+ conveyor.log.toString(), 1, conveyor.log.size());

		conveyor.log.clear();
		assertEquals(
				"0 message should have been sent to the animation. Event log: "
						+ animation.log.toString(), 0, animation.log.size());

		// after conveyor sent glass to it, it is totally full(I want to test
		// the case when popup is also full)
		backSensor.msgHereIsGlass(conveyor, glass2);
		// popup is occupied
		backSensor.msgIAmOccupied();
		// if conveyor ask to send one more glass, it will get stopped;
		backSensor.msgCanISendGlass(conveyor);
		// now the state is changed to: OCCUPIED AND SO DOES POPUP
		// run scheduler
		backSensor.pickAndExecuteAnAction();
		assertTrue(
				"Mock conveyor should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ conveyor.log.toString(),
				conveyor.log.containsString("conveyor stopped"));
		assertEquals(
				"1 message should have been sent to the workstation. Event log: "
						+ conveyor.log.toString(), 1, conveyor.log.size());
		conveyor.log.clear();
		try {
			transducer.transducerThread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ animation.log.toString(),
				animation.log.containsString("SENSOR_GUI_PRESSED"));
		animation.log.clear();
		// after popup sent msgIAmEmpty(), sensor should continue send glass to
		// popup and make conveyor run again
		backSensor.msgIAmEmpty();
		backSensor.pickAndExecuteAnAction();
		// now, popup should receive msgHereIsGlass();
		assertTrue(
				"Mock popup should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ popup.log.toString(),
				popup.log.containsString("Popup received glass"));
		assertEquals(
				"1 message should have been sent to the workstation. Event log: "
						+ popup.log.toString(), 1, popup.log.size());

		popup.log.clear();
		// now, conveyor should also started
		assertTrue(
				"Mock popup should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ conveyor.log.toString(),
				conveyor.log.containsString("conveyor started"));
		assertEquals(
				"1 message should have been sent to the workstation. Event log: "
						+ conveyor.log.toString(), 1, conveyor.log.size());

		conveyor.log.clear();
		try {
			transducer.transducerThread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ animation.log.toString(),
				animation.log.containsString("SENSOR_GUI_RELEASED"));
		animation.log.clear();
		// msgGlassIsWaiting is only for testing the conveyor, doesn't have real
		// things to do, make the test more readable and easy to understand!
		// finished test for msgCanISendGlass, msgCanISendGlass (one for
		// conveyor, the other one for popup), msgIAmOccupied,msgIAmOccupied
		// (one for conveyor, the other one for popup), msgIAmEmpty,
		// msgIAmEmpty (one for conveyor, the other one for popup),
		// msgHereIsGlass,msgHereIsGlass (one for conveyor, the other one for
		// popup),
		// finished testing for Sensors
	}
}
