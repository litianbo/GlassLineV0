package gui.test;

import org.junit.Test;

import mocks.MockAnimation;
import mocks.MockConveyor;
import mocks.MockPopup;
import mocks.MockSensor;
import mocks.MockWorkStation;
import engine.agent.ConveyorAgent;
import engine.agent.Glass;
import engine.agent.PopupAgent;
import engine.agent.Recipe;
import engine.agent.SensorAgent;
import engine.agent.WorkStationAgent;
import engine.util.ConveyorFamily;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;
import transducer.TransducerDebugMode;
import junit.framework.TestCase;

public class BlackBoxTest extends TestCase {

	public void testOneGlassPassToConveyorFamily() {
		Transducer transducer = new Transducer();
		transducer.startTransducer();
		transducer.setDebugMode(TransducerDebugMode.EVENTS_AND_ACTIONS);
		MockAnimation animation = new MockAnimation(transducer);
		Recipe recipe = new Recipe();
		Recipe recipe2 = new Recipe(false, false, false, false, false, false,
				false, false, false);
		Glass glass1 = new Glass(recipe, "glass1");
		Glass glass2 = new Glass(recipe2, "glass2");
		// previous conveoyrfamily
		ConveyorFamily conveyorFamily1 = new ConveyorFamily();
		// this conveyor family
		ConveyorFamily conveyorFamily2 = new ConveyorFamily();
		// next conveyor family
		ConveyorFamily conveyorFamily3 = new ConveyorFamily();
		// create popup agent
		WorkStationAgent top = new WorkStationAgent("Top", transducer);
		WorkStationAgent bot = new WorkStationAgent("Bot", transducer);
		PopupAgent popup = new PopupAgent("This Popup", transducer,
				conveyorFamily2, conveyorFamily3, top, bot);
		// create conveyor agent
		ConveyorAgent conveyor = new ConveyorAgent("Conveyor", transducer,
				conveyorFamily2);
		// create sensors
		SensorAgent frontSensor = new SensorAgent("Sensor1", transducer,
				conveyorFamily2, conveyorFamily1);// end a popup, start a
													// conveyor
		SensorAgent backSensor = new SensorAgent("Sensor2", transducer,
				conveyorFamily2, conveyorFamily1);// end a conveoyr, start a
													// popup
		// set up components for conveyor family

		conveyorFamily2.setConveyor1(conveyor);
		conveyorFamily2.setSensor2(backSensor);
		conveyorFamily2.setSensor1(frontSensor);
		conveyorFamily2.setPopup(popup);
		// create mock conveyor familys
		MockSensor mockSensor = new MockSensor("Sensor2", transducer,
				conveyorFamily3, conveyorFamily2);
		MockPopup mockpopup = new MockPopup("Preivous Popup", transducer,
				conveyorFamily1, conveyorFamily2);
		conveyorFamily1.setPopup(mockpopup);
		conveyorFamily3.setSensor1(mockSensor);
		assertEquals(
				"Mock animation should have an empty event log now. Instead, the mock animation event log reads: "
						+ animation.log.toString(), 0, animation.log.size());
		// now, test the 1 case
		// glass1 and glass2 are default set to needs machining
		// now, suppose conveyorfamily1 pass glass to
		// conveyorfamily2, according to the prof's email, we assume we can pass
		// unlimited amount of glass to the conveyor, here, assume it is empty
		// at the inital time
		conveyorFamily2.msgHereIsGlass(mockpopup, glass1);
		// To match up with each other inside the team, we use the same msg only
		// between each
		// conveyor family, which are: msgHereIsGlass(), msgStopConveyor(),
		// msgStartConveyor()
		// here, inside msgHereIsGlass() is just a rename for my own
		// msgHereIsGlass().
		// now, front sensor in conveyorFamily2 should receive glass
		// run sensor scheduler
		// mock animation should receive GUI pressed because it is already
		// passing
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
		// clear the log to avoid duplicate messages
		animation.log.clear();
		frontSensor.pickAndExecuteAnAction();
		// now, conveyor should receive msgCanISendGlass()
		// run the scheduler
		assertEquals(
				"Mock animation should have an empty event log now. Instead, the mock animation event log reads: "
						+ animation.log.toString(), 0, animation.log.size());
		conveyor.pickAndExecuteAnAction();
		// now, animation received event do_Start, because conveyor havn't start
		// yet
		try {
			transducer.transducerThread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ animation.log.toString(),
				animation.log.containsString("CONVEYOR_DO_START"));
		animation.log.clear();
		// now front sensor received msgIAmEmpty(),
		// run scheduler, now, animation received event SENSOR_GUI_RELEASED,
		// because it is sending glass to conveyor
		assertEquals(
				"Mock animation should have an empty event log now. Instead, the mock animation event log reads: "
						+ animation.log.toString(), 0, animation.log.size());
		frontSensor.pickAndExecuteAnAction();
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
		// conveyor received msgHereIsGlass();
		// run scheduler
		conveyor.pickAndExecuteAnAction();
		// now sensor2 received msgCanISendGlass();
		// run scheduler
		backSensor.pickAndExecuteAnAction();
		// now, conveyor received msgIAmEmpty();
		// call scheduler
		assertEquals(
				"Mock animation should have an empty event log now. Instead, the mock animation event log reads: "
						+ animation.log.toString(), 0, animation.log.size());
		conveyor.pickAndExecuteAnAction();
		// now, back sensor received msgHereIsGlass();
		// animation will recieve SENSOR_GUI_PRESSED because glass is coming.
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
		// run scheduler
		backSensor.pickAndExecuteAnAction();
		// popup will receive msgCanISendGlass()
		popup.pickAndExecuteAnAction();
		// back sensor received msgIAmEmpty()
		// run scheduler
		assertEquals(
				"Mock animation should have an empty event log now. Instead, the mock animation event log reads: "
						+ animation.log.toString(), 0, animation.log.size());
		backSensor.pickAndExecuteAnAction();
		// sensor should pass glass to popup
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
		// now, popup received glass, run scheduler then
		popup.pickAndExecuteAnAction();
		// because this one needs maching, so popup will move up to workstation
		// state = WORKING_ON_GLASS
		try {
			transducer.transducerThread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ animation.log.toString(),
				animation.log.containsString("POPUP_DO_MOVE_UP"));
		animation.log.clear();
		// call scheduler again
		popup.pickAndExecuteAnAction();
		// now, top workstation received msgHereISGlass();
		assertEquals(
				"Mock animation should have an empty event log now. Instead, the mock animation event log reads: "
						+ animation.log.toString(), 0, animation.log.size());
		top.pickAndExecuteAnAction();
		// glass finished processing
		try {
			transducer.transducerThread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ animation.log.toString(),
				animation.log.containsString("WORKSTATION_GUI_ACTION_FINISHED"));
		animation.log.clear();
		// popup received msgGlassDone();
		// run scheduler
		assertEquals(
				"Mock sensor should have an empty event log now. Instead, the mock sensor event log reads: "
						+ mockSensor.log.toString(), 0, mockSensor.log.size());
		popup.pickAndExecuteAnAction();
		// now, popup is going to move down and send glass to next family
		try {
			transducer.transducerThread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ animation.log.toString(),
				animation.log.containsString("POPUP_DO_MOVE_DOWN"));
		animation.log.clear();
		// now, back to the mock front sensor in the next conveyor
		// family(conveyorFamily3)
		// it should receive msgCanISendGlass()

		assertTrue(
				"Mock sensor should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ mockSensor.log.toString(),
				mockSensor.log
						.containsString("I know that the popup from previous conveyor family are going to send glass"));
		mockSensor.log.clear();
		// suppose mock snesor seng msgIAmEmpty() to popup
		popup.msgIAmEmpty(mockSensor);
		popup.pickAndExecuteAnAction();
		// mock sensor should receive msgHereIsGlass();
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ mockSensor.log.toString(),
				mockSensor.log
						.containsString("I know that there is glass incoming"));
		mockSensor.log.clear();
		// now, test for 0 case
		/**********************************************************************************************/
		conveyorFamily2.msgHereIsGlass(mockpopup, glass2);
		// glass2 is the one doesn't need machining
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
		// clear the log to avoid duplicate messages
		animation.log.clear();
		frontSensor.pickAndExecuteAnAction();
		// now, conveyor should receive msgCanISendGlass()
		// run the scheduler
		assertEquals(
				"Mock animation should have an empty event log now. Instead, the mock animation event log reads: "
						+ animation.log.toString(), 0, animation.log.size());
		conveyor.pickAndExecuteAnAction();
		// now, animation SHOULDN'T received event do_Start, because conveyor
		// have already started
		// yet
		try {
			transducer.transducerThread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(
				"Mock animation should have an empty event log now. Instead, the mock animation event log reads: "
						+ animation.log.toString(), 0, animation.log.size());

		// now front sensor received msgIAmEmpty(),
		// run scheduler, now, animation received event SENSOR_GUI_RELEASED,
		// because it is sending glass to conveyor
		assertEquals(
				"Mock animation should have an empty event log now. Instead, the mock animation event log reads: "
						+ animation.log.toString(), 0, animation.log.size());
		frontSensor.pickAndExecuteAnAction();
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
		// conveyor received msgHereIsGlass();
		// run scheduler
		conveyor.pickAndExecuteAnAction();
		// now sensor2 received msgCanISendGlass();
		// run scheduler
		backSensor.pickAndExecuteAnAction();
		// now, conveyor received msgIAmEmpty();
		// call scheduler
		assertEquals(
				"Mock animation should have an empty event log now. Instead, the mock animation event log reads: "
						+ animation.log.toString(), 0, animation.log.size());
		conveyor.pickAndExecuteAnAction();
		// now, back sensor received msgHereIsGlass();
		// animation will recieve SENSOR_GUI_PRESSED because glass is coming.
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
		// run scheduler
		backSensor.pickAndExecuteAnAction();
		// popup will receive msgCanISendGlass()
		popup.pickAndExecuteAnAction();
		// back sensor received msgIAmEmpty()
		// run scheduler
		assertEquals(
				"Mock animation should have an empty event log now. Instead, the mock animation event log reads: "
						+ animation.log.toString(), 0, animation.log.size());
		backSensor.pickAndExecuteAnAction();
		// sensor should pass glass to popup
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
		// now, popup received glass, run scheduler then
		popup.pickAndExecuteAnAction();
		// because this one doesn't needs machining, so popup will NOT move up
		// to workstation
		// state = SENDING_GLASS_TO_SENSOR
		try {
			transducer.transducerThread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(
				"Mock animation should have an empty event log now. Instead, the mock animation event log reads: "
						+ animation.log.toString(), 0, animation.log.size());
		// call scheduler again
		popup.pickAndExecuteAnAction();
		// now, sensor received msgCanISendGlass()
		assertTrue(
				"Mock sensor should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ mockSensor.log.toString(),
				mockSensor.log
						.containsString("I know that the popup from previous conveyor family are going to send glass"));
		mockSensor.log.clear();
		// suppose mock snesor seng msgIAmEmpty() to popup
		popup.msgIAmEmpty(mockSensor);
		popup.pickAndExecuteAnAction();
		// mock sensor should receive msgHereIsGlass();
		assertTrue(
				"Mock sensor should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ mockSensor.log.toString(),
				mockSensor.log
						.containsString("I know that there is glass incoming"));
		mockSensor.log.clear();
		// finished two cases in one glass passing through conveyor family
	}

	/**
	 * TODO: test two glass pass to conveyor family
	 */
	public void testTwoGlassPassToConveyorFamily() {

		Transducer transducer = new Transducer();
		transducer.startTransducer();
		transducer.setDebugMode(TransducerDebugMode.EVENTS_AND_ACTIONS);
		MockAnimation animation = new MockAnimation(transducer);
		Recipe recipe = new Recipe();
		Recipe recipe2 = new Recipe(false, false, false, false, false, false,
				false, false, false);
		Glass glass1 = new Glass(recipe, "glass1");
		Glass glass2 = new Glass(recipe2, "glass2");
		// previous conveoyrfamily
		ConveyorFamily conveyorFamily1 = new ConveyorFamily();
		// this conveyor family
		ConveyorFamily conveyorFamily2 = new ConveyorFamily();
		// next conveyor family
		ConveyorFamily conveyorFamily3 = new ConveyorFamily();
		// create popup agent
		WorkStationAgent top = new WorkStationAgent("Top", transducer);
		WorkStationAgent bot = new WorkStationAgent("Bot", transducer);
		PopupAgent popup = new PopupAgent("This Popup", transducer,
				conveyorFamily2, conveyorFamily3, top, bot);
		// create conveyor agent
		ConveyorAgent conveyor = new ConveyorAgent("Conveyor", transducer,
				conveyorFamily2);
		// create sensors
		SensorAgent frontSensor = new SensorAgent("Sensor1", transducer,
				conveyorFamily2, conveyorFamily1);// end a popup, start a
													// conveyor
		SensorAgent backSensor = new SensorAgent("Sensor2", transducer,
				conveyorFamily2, conveyorFamily1);// end a conveoyr, start a
													// popup
		// set up components for conveyor family

		conveyorFamily2.setConveyor1(conveyor);
		conveyorFamily2.setSensor2(backSensor);
		conveyorFamily2.setSensor1(frontSensor);
		conveyorFamily2.setPopup(popup);
		// create mock conveyor familys
		MockSensor mockSensor = new MockSensor("Sensor2", transducer,
				conveyorFamily3, conveyorFamily2);
		MockPopup mockpopup = new MockPopup("Preivous Popup", transducer,
				conveyorFamily1, conveyorFamily2);
		conveyorFamily1.setPopup(mockpopup);
		conveyorFamily3.setSensor1(mockSensor);
		try {
			transducer.transducerThread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(
				"Mock animation should have an empty event log now. Instead, the mock animation event log reads: "
						+ animation.log.toString(), 0, animation.log.size());
		assertEquals(
				"Mock popup should have an empty event log now. Instead, the mock animation event log reads: "
						+ mockpopup.log.toString(), 0, mockpopup.log.size());
		// now, test 00 case
		conveyorFamily2.msgHereIsGlass(mockpopup, glass2);
		// since it is intial, no need to call msgCanISendGlass()
		frontSensor.pickAndExecuteAnAction();
		// now, conveyor should receive msgCanISendGlass()
		// now, suppose another glass wants to come
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
		// clear the log to avoid duplicate messages
		animation.log.clear();
		conveyorFamily2.msgCanISendGlass();
		frontSensor.pickAndExecuteAnAction();
		// mock popup should receive msgIAmOccupied()
		assertTrue(
				"Mock popup should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ mockpopup.log.toString(),
				mockpopup.log
						.containsString("I know that sensor is occupied from Sensor1"));
		// clear the log to avoid duplicate messages
		mockpopup.log.clear();
		// glass2 is the one doesn't need machining

		frontSensor.pickAndExecuteAnAction();
		// now, conveyor should receive msgCanISendGlass()
		// run the scheduler
		assertEquals(
				"Mock animation should have an empty event log now. Instead, the mock animation event log reads: "
						+ animation.log.toString(), 0, animation.log.size());
		conveyor.pickAndExecuteAnAction();
		// now, animation should received event do_Start, because conveyor
		// havn't already started
		// yet
		try {
			transducer.transducerThread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ animation.log.toString(),
				animation.log.containsString("CONVEYOR_DO_START"));
		animation.log.clear();
		// now front sensor received msgIAmEmpty(),
		// run scheduler, now, animation received event SENSOR_GUI_RELEASED,
		// because it is sending glass to conveyor
		frontSensor.pickAndExecuteAnAction();
		// conveyor received msgHereIsGlass();
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
		// run it again to notify popup empty
		frontSensor.pickAndExecuteAnAction();
		// now, popup is notified to send glass
		assertTrue(
				"Mock popup should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ mockpopup.log.toString(),
				mockpopup.log.containsString("I know that sensor is empty"));
		// clear the log to avoid duplicate messages
		mockpopup.log.clear();
		frontSensor.msgHereIsGlass(mockpopup, glass2);
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
		// send msgCanISendGlass to conveyor
		frontSensor.pickAndExecuteAnAction();
		// after run scheduler, conveyor should send msgIAmEmpty() to front
		// sensor
		conveyor.pickAndExecuteAnAction();
		// now front sensor received msgIAmEmpty();
		// run scheduler to pass glass
		frontSensor.pickAndExecuteAnAction();
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
		// now two glass passed to conveyor, send msgCanISendGlass() to back
		// sensor
		conveyor.pickAndExecuteAnAction();
		// msgIAmEmpty() to conveyor
		backSensor.pickAndExecuteAnAction();
		conveyor.pickAndExecuteAnAction();
		// now, back sensor received msgHereIsGlass();
		// animation will recieve SENSOR_GUI_PRESSED because glass is coming.
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
		// if another glass wants to pass to sensor, stop the conveyor
		// backSensor.pickAndExecuteAnAction();
		conveyor.pickAndExecuteAnAction();
		backSensor.pickAndExecuteAnAction();
		// stop the conveyor

		// fired event do_stop
		try {
			transducer.transducerThread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ animation.log.toString(),
				animation.log.containsString("CONVEYOR_DO_STOP"));
		animation.log.clear();
		// run scheduler to pass glass to popup
		backSensor.pickAndExecuteAnAction();
		// popup will receive msgCanISendGlass()
		popup.pickAndExecuteAnAction();
		// back sensor received msgIAmEmpty()

		assertEquals(
				"Mock animation should have an empty event log now. Instead, the mock animation event log reads: "
						+ animation.log.toString(), 0, animation.log.size());
		backSensor.pickAndExecuteAnAction();
		// sensor should pass glass to popup, state: empty
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
		try {
			transducer.transducerThread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ animation.log.toString(),
				animation.log.containsString("CONVEYOR_DO_START"));
		animation.log.clear();
		// send glass to back sensor
		conveyor.pickAndExecuteAnAction();
		backSensor.pickAndExecuteAnAction();
		// now, back sensor received glass, run scheduler then
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
		popup.pickAndExecuteAnAction();
		// because this one doesn't needs machining, so popup will NOT move up
		// to workstation
		// state = SENDING_GLASS_TO_SENSOR
		// call scheduler again
		popup.pickAndExecuteAnAction();
		// now, sensor received msgCanISendGlass()
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ mockSensor.log.toString(),
				mockSensor.log
						.containsString("I know that the popup from previous conveyor family are going to send glass"));
		mockSensor.log.clear();
		// suppose mock snesor seng msgIAmEmpty() to popup
		popup.msgIAmEmpty(mockSensor);
		popup.pickAndExecuteAnAction();
		// mock sensor should receive msgHereIsGlass();
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ mockSensor.log.toString(),
				mockSensor.log
						.containsString("I know that there is glass incoming"));
		mockSensor.log.clear();

		backSensor.pickAndExecuteAnAction();
		popup.pickAndExecuteAnAction();
		backSensor.pickAndExecuteAnAction();
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
		popup.pickAndExecuteAnAction();
		// because this one doesn't needs machining, so popup will NOT move up
		// to workstation
		// state = SENDING_GLASS_TO_SENSOR
		// call scheduler again
		popup.pickAndExecuteAnAction();
		// now, sensor received msgCanISendGlass()
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ mockSensor.log.toString(),
				mockSensor.log
						.containsString("I know that the popup from previous conveyor family are going to send glass"));
		mockSensor.log.clear();
		// suppose mock snesor seng msgIAmEmpty() to popup
		popup.msgIAmEmpty(mockSensor);
		popup.pickAndExecuteAnAction();
		// mock sensor should receive msgHereIsGlass();
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ mockSensor.log.toString(),
				mockSensor.log
						.containsString("I know that there is glass incoming"));
		mockSensor.log.clear();
		// now, test the case 01,10,11. Since there is no difference between
		// them in the procedure before popup, so I only test after the time
		// they reach
		// the popup
		popup.msgHereIsGlass(backSensor, glass2);
		popup.pickAndExecuteAnAction();
		try {
			transducer.transducerThread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(
				"Mock animation should have an empty event log now. Instead, the mock animation event log reads: "
						+ animation.log.toString(), 0, animation.log.size());
		animation.log.clear();
		popup.pickAndExecuteAnAction();
		// now, sensor received msgCanISendGlass()
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ mockSensor.log.toString(),
				mockSensor.log
						.containsString("I know that the popup from previous conveyor family are going to send glass"));
		mockSensor.log.clear();
		// suppose mock snesor seng msgIAmEmpty() to popup
		popup.msgIAmEmpty(mockSensor);
		popup.pickAndExecuteAnAction();
		// mock sensor should receive msgHereIsGlass();
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ mockSensor.log.toString(),
				mockSensor.log
						.containsString("I know that there is glass incoming"));
		mockSensor.log.clear();
		// since I proved in the 00 case part: code works properly when there is
		// one glass in the popup and one glass waiting outside, here, I just
		// wait until sending glass finished
		popup.msgHereIsGlass(backSensor, glass1);
		popup.pickAndExecuteAnAction();
		// because this one needs maching, so popup will move up to workstation
		// state = WORKING_ON_GLASS
		try {
			transducer.transducerThread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ animation.log.toString(),
				animation.log.containsString("POPUP_DO_MOVE_UP"));
		animation.log.clear();
		// call scheduler again
		popup.pickAndExecuteAnAction();
		// now, top workstation received msgHereISGlass();
		assertEquals(
				"Mock animation should have an empty event log now. Instead, the mock animation event log reads: "
						+ animation.log.toString(), 0, animation.log.size());
		top.pickAndExecuteAnAction();
		// glass finished processing
		try {
			transducer.transducerThread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ animation.log.toString(),
				animation.log.containsString("WORKSTATION_GUI_ACTION_FINISHED"));
		animation.log.clear();
		// popup received msgGlassDone();
		// run scheduler
		assertEquals(
				"Mock sensor should have an empty event log now. Instead, the mock sensor event log reads: "
						+ mockSensor.log.toString(), 0, mockSensor.log.size());
		popup.pickAndExecuteAnAction();
		// now, popup is going to move down and send glass to next family
		try {
			transducer.transducerThread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ animation.log.toString(),
				animation.log.containsString("POPUP_DO_MOVE_DOWN"));
		animation.log.clear();
		// now, back to the mock front sensor in the next conveyor
		// family(conveyorFamily3)
		// it should receive msgCanISendGlass()

		assertTrue(
				"Mock sensor should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ mockSensor.log.toString(),
				mockSensor.log
						.containsString("I know that the popup from previous conveyor family are going to send glass"));
		mockSensor.log.clear();
		// suppose mock snesor seng msgIAmEmpty() to popup
		popup.msgIAmEmpty(mockSensor);
		popup.pickAndExecuteAnAction();
		// mock sensor should receive msgHereIsGlass();
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ mockSensor.log.toString(),
				mockSensor.log
						.containsString("I know that there is glass incoming"));
		mockSensor.log.clear();
		// case 10
		popup.msgHereIsGlass(backSensor, glass1);
		popup.pickAndExecuteAnAction();
		// because this one needs maching, so popup will move up to workstation
		// state = WORKING_ON_GLASS
		try {
			transducer.transducerThread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ animation.log.toString(),
				animation.log.containsString("POPUP_DO_MOVE_UP"));
		animation.log.clear();
		// call scheduler again
		popup.pickAndExecuteAnAction();
		// now, top workstation received msgHereISGlass();

		// assume here, another glass coming in
		popup.msgHereIsGlass(backSensor, glass2);
		popup.pickAndExecuteAnAction();
		// popup should move down and pick up
		try {
			transducer.transducerThread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ animation.log.toString(),
				animation.log.containsString("POPUP_DO_MOVE_DOWN"));
		animation.log.clear();
		// now popup should send glass to next conveyor family
		popup.msgIAmEmpty(mockSensor);
		popup.pickAndExecuteAnAction();
		// mock sensor should receive msgHereIsGlass();
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ mockSensor.log.toString(),
				mockSensor.log
						.containsString("I know that there is glass incoming"));
		mockSensor.log.clear();
		top.pickAndExecuteAnAction();
		// glass finished processing, popup needs to move up to pick up

		try {
			transducer.transducerThread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ animation.log.toString(),
				animation.log.containsString("POPUP_DO_MOVE_UP"));

		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ animation.log.toString(),
				animation.log.containsString("WORKSTATION_GUI_ACTION_FINISHED"));
		animation.log.clear();
		// popup received msgGlassDone();
		// run scheduler
		assertEquals(
				"Mock sensor should have an empty event log now. Instead, the mock sensor event log reads: "
						+ mockSensor.log.toString(), 0, mockSensor.log.size());
		popup.pickAndExecuteAnAction();
		// now, popup is going to move down and send glass to next family
		try {
			transducer.transducerThread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ animation.log.toString(),
				animation.log.containsString("POPUP_DO_MOVE_DOWN"));
		animation.log.clear();
		// now, back to the mock front sensor in the next conveyor
		// family(conveyorFamily3)
		// it should receive msgCanISendGlass()

		assertTrue(
				"Mock sensor should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ mockSensor.log.toString(),
				mockSensor.log
						.containsString("I know that the popup from previous conveyor family are going to send glass"));
		mockSensor.log.clear();
		// suppose mock snesor seng msgIAmEmpty() to popup
		popup.msgIAmEmpty(mockSensor);
		popup.pickAndExecuteAnAction();
		// mock sensor should receive msgHereIsGlass();
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ mockSensor.log.toString(),
				mockSensor.log
						.containsString("I know that there is glass incoming"));
		mockSensor.log.clear();
		// case 11
		popup.msgHereIsGlass(backSensor, glass1);
		popup.pickAndExecuteAnAction();
		// because this one needs maching, so popup will move up to workstation
		// state = WORKING_ON_GLASS
		try {
			transducer.transducerThread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ animation.log.toString(),
				animation.log.containsString("POPUP_DO_MOVE_UP"));
		animation.log.clear();
		// call scheduler again
		popup.pickAndExecuteAnAction();
		// now, top workstation received msgHereISGlass();

		// assume here, another glass coming in
		popup.msgHereIsGlass(backSensor, glass1);

		// popup should move down and pick up
		try {
			transducer.transducerThread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ animation.log.toString(),
				animation.log.containsString("POPUP_DO_MOVE_DOWN"));
		animation.log.clear();
		// popup should move glass up to bot workstation;
		popup.pickAndExecuteAnAction();
		try {
			transducer.transducerThread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ animation.log.toString(),
				animation.log.containsString("POPUP_DO_MOVE_UP"));
		animation.log.clear();
		popup.pickAndExecuteAnAction();
		top.pickAndExecuteAnAction();
		// now, top workstation finished
		try {
			transducer.transducerThread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ animation.log.toString(),
				animation.log.containsString("WORKSTATION_GUI_ACTION_FINISHED"));

		animation.log.clear();
		// now popup should send glass to next conveyor family
		popup.pickAndExecuteAnAction();
		try {
			transducer.transducerThread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ animation.log.toString(),
				animation.log.containsString("POPUP_DO_MOVE_DOWN"));
		animation.log.clear();

		assertTrue(
				"Mock sensor should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ mockSensor.log.toString(),
				mockSensor.log
						.containsString("I know that the popup from previous conveyor family are going to send glass"));
		mockSensor.log.clear();
		popup.msgIAmEmpty(mockSensor);
		popup.pickAndExecuteAnAction();
		// mock sensor should receive msgHereIsGlass();

		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ mockSensor.log.toString(),
				mockSensor.log
						.containsString("I know that there is glass incoming"));
		mockSensor.log.clear();

		// glass finished processing, popup needs to move up to pick up

		// popup received msgGlassDone();
		// run scheduler
		assertEquals(
				"Mock sensor should have an empty event log now. Instead, the mock sensor event log reads: "
						+ mockSensor.log.toString(), 0, mockSensor.log.size());
		bot.pickAndExecuteAnAction();
		// now, popup is going to move down and send glass to next family
		try {
			transducer.transducerThread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ animation.log.toString(),
				animation.log.containsString("WORKSTATION_GUI_ACTION_FINISHED"));
		popup.pickAndExecuteAnAction();
		try {
			transducer.transducerThread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ animation.log.toString(),
				animation.log.containsString("POPUP_DO_MOVE_DOWN"));
		animation.log.clear();
		// now, back to the mock front sensor in the next conveyor
		// family(conveyorFamily3)
		// it should receive msgCanISendGlass()

		assertTrue(
				"Mock sensor should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ mockSensor.log.toString(),
				mockSensor.log
						.containsString("I know that the popup from previous conveyor family are going to send glass"));
		mockSensor.log.clear();
		// suppose mock snesor seng msgIAmEmpty() to popup
		popup.msgIAmEmpty(mockSensor);
		popup.pickAndExecuteAnAction();
		// mock sensor should receive msgHereIsGlass();
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ mockSensor.log.toString(),
				mockSensor.log
						.containsString("I know that there is glass incoming"));
		mockSensor.log.clear();
		// suppose mock sensor at next conveyor family have unlimited space
		// finished test for case 00,01,10,11
	}

	/**
	 * TODO: test three glass pass to conveyor family
	 */
	public void testThreeGlassPassToConveyorFamily() {

	}

}
