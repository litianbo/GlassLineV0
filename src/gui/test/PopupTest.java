package gui.test;

import mocks.MockAnimation;
import mocks.MockSensor;
import mocks.MockWorkStation;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;
import transducer.TransducerDebugMode;
import engine.agent.Glass;
import engine.agent.PopupAgent;
import engine.agent.Recipe;
import engine.util.ConveyorFamily;
import junit.framework.TestCase;

public class PopupTest extends TestCase {
	public void testPopupInteractWithSensor() {// popup is at the end of
		// conveyor
		// create a transducer
		Transducer transducer = new Transducer();
		transducer.startTransducer();
		transducer.setDebugMode(TransducerDebugMode.EVENTS_AND_ACTIONS);
		// create glass
		Glass glass1 = new Glass(new Recipe(), "Glass1");
		Glass glass2 = new Glass(new Recipe(false, false, false, false, false,
				false, false, false, false), "Glass2");

		Glass glass3 = new Glass(new Recipe(), "Glass3");
		// create a conveyor family
		ConveyorFamily conveyorFamily1 = new ConveyorFamily();
		// next conveyor family
		ConveyorFamily conveyorFamily2 = new ConveyorFamily();
		// create mock workstation agents
		MockWorkStation top = new MockWorkStation("Top", transducer);
		MockWorkStation bot = new MockWorkStation("Bot", transducer);
		// create a popup agent
		PopupAgent popup = new PopupAgent("Popup1", transducer,
				conveyorFamily1, conveyorFamily2, top, bot);
		// create mocks
		// this sensor is on the front of the popup
		MockSensor sensor2 = new MockSensor("Sensor2", transducer,
				conveyorFamily1);
		// this sensor is behind the popup (belong to the next family)
		MockSensor sensor1 = new MockSensor("Sensor1", transducer,
				conveyorFamily2, conveyorFamily1);
		// create a mock animation
		MockAnimation animation = new MockAnimation(transducer);

		// set mocks for the families
		conveyorFamily1.setSensor2(sensor2);
		conveyorFamily2.setSensor1(sensor1);
		// neither sensor1 or sensor2 should have message right now, so use
		// 'equal' to check it
		assertEquals(
				"Mock sensor1 should have an empty event log now. Instead, the mock sensor1 event log reads: "
						+ sensor1.log.toString(), 0, sensor1.log.size());
		assertEquals(
				"Mock sensor2 should have an empty event log now. Instead, the mock sensor2 event log reads: "
						+ sensor2.log.toString(), 0, sensor2.log.size());
		assertEquals(
				"Mock animation should have an empty event log now. Instead, the mock animation event log reads: "
						+ animation.log.toString(), 0, animation.log.size());
		// suppose sensor2 sending glass to popup, then popup will receive
		// msgHereIsGlass, I will test sensors later, assume it works properly
		// right now
		popup.msgCanISendGlass(sensor2, glass1);

		// run scheduler to call the function checkstationstate()
		popup.pickAndExecuteAnAction();
		// now, sensor should receive msgIAmEmpty(),test it!

		assertTrue(
				"Mock sensor2 should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ sensor2.log.toString(),
				sensor2.log.containsString("I know that popup is empty"));
		assertEquals(
				"1 message should have been sent to the workstation. Event log: "
						+ sensor2.log.toString(), 1, sensor2.log.size());
		assertEquals(
				"Mock animation should have an empty event log now. Instead, the mock animation event log reads: "
						+ animation.log.toString(), 0, animation.log.size());
		// clear the log here, so I don't get duplicate message that mislead the
		// test
		sensor2.log.clear();
		sensor1.log.clear();
		animation.log.clear();
		// now, suppose sensor send the msghereisglass to popup properly
		popup.msgHereIsGlass(sensor2, glass1);
		// now, can popup pass the glass to next conveyor family successfully?
		// run scheduler
		popup.pickAndExecuteAnAction();
		// now, state is changed to working, popup is raising!
		// animation should receive DO_MOVE_UP
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
		popup.pickAndExecuteAnAction();
		// now, popup is raised, and sensor2(in this conveyor) should receive
		// msgIAmOccupied

		assertTrue(
				"Mock sensor should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ sensor2.log.toString(),
				sensor2.log.containsString("I know that popup is occupied"));
		assertEquals(
				"1 message should have been sent to the workstation. Event log: "
						+ sensor2.log.toString(), 1, sensor2.log.size());

		sensor2.log.clear();
		sensor1.log.clear();
		animation.log.clear();
		// suppose popup finished raising,
		Object[] args = new Object[1];
		args[0] = new Long(0);
		transducer.fireEvent(TChannel.POPUP, TEvent.POPUP_GUI_MOVED_UP, args);
		try {
			transducer.transducerThread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ animation.log.toString(),
				animation.log.containsString("POPUP_GUI_MOVED_UP"));
		// animation.log.clear();
		// now, washer should receive event do load glass
		try {
			transducer.transducerThread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ animation.log.toString(),
				animation.log.containsString("DO_LOAD_GLASS"));
		animation.log.clear();
		// after it is done, fire release glass(workstation is not reqiured, so
		// here, assume it works properly right now)
		transducer.fireEvent(TChannel.POPUP,
				TEvent.WORKSTATION_GUI_ACTION_FINISHED, args);
		// if the popup is not raised, then, go raising, else, the popup just
		// fire the event
		try {
			transducer.transducerThread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ animation.log.toString(),
				animation.log.containsString("WORKSTATION_RELEASE_FINISHED "));
		animation.log.clear();
		assertEquals(
				"Mock sensor1 should have an empty event log now. Instead, the mock sensor1 event log reads: "
						+ sensor1.log.toString(), 0, sensor1.log.size());
		assertEquals(
				"Mock sensor2 should have an empty event log now. Instead, the mock sensor2 event log reads: "
						+ sensor2.log.toString(), 0, sensor2.log.size());

		// now run scheduler
		popup.pickAndExecuteAnAction();
		// popup sent msgCanISendGlass to next conveyor family
		// test it!
		assertTrue(
				"Mock sensor1 should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ sensor1.log.toString(),
				sensor1.log
						.containsString("I know that the popup from previous conveyor family are going to send glass"));
		assertEquals(
				"1 message should have been sent to the workstation. Event log: "
						+ sensor1.log.toString(), 1, sensor1.log.size());

		sensor1.log.clear();

		// now, next conveyor family received incoming glass message
		// Again, here, suppose sensor works properly(test sensor later), and it
		// sends the msgIAmEmpty back to the previous popup agent
		popup.msgIAmEmpty(sensor1);
		// suppse the workstation sent the finished glass to popup
		popup.msgGlassDone(top, glass1);
		// set the correct stage, then run the scheduler
		popup.pickAndExecuteAnAction();
		// now, the glass should push to next conveyor family,
		// About the animation, there is no such event similar to
		// PUSH_GLASS_TO_SENSOR, so I leave it for V1
		assertTrue(
				"Mock sensor1 should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ sensor1.log.toString(),
				sensor1.log
						.containsString("I know that there is glass incoming"));
		assertEquals(
				"1 message should have been sent to the workstation. Event log: "
						+ sensor1.log.toString(), 1, sensor1.log.size());
		sensor1.log.clear();
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ animation.log.toString(),
				animation.log.containsString("POPUP_DO_MOVE_DOWN "));
		animation.log.clear();
		// if there is glass whose recipe doesn't need to process with the
		// workstation
		// suppose popup is empty, lowered(After the operations above, of course
		// the popup should lowered and send glass to next conveyor family. I
		// proved above, the msgCanISendGlass works
		// properly, so here, I just send msgHereIsGlass() to set the stage
		// quickly)
		popup.msgHereIsGlass(sensor2, glass2);
		// run the scheduler.
		popup.pickAndExecuteAnAction();
		// now, state should change to SENDING_GLASS_TO_SENSOR
		// run the scheduler again
		popup.pickAndExecuteAnAction();
		// now, next conveyor family get notified if popup can send glass to it
		// or not
		
		assertTrue(
				"Mock sensor1 should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ sensor1.log.toString(),
				sensor1.log
						.containsString("I know that the popup from previous conveyor family are going to send glass"));
		assertEquals(
				"1 message should have been sent to the workstation. Event log: "
						+ sensor1.log.toString(), 1, sensor1.log.size());
		assertEquals(
				"Mock sensor2 should have an empty event log now. Instead, the mock sensor2 event log reads: "
						+ sensor2.log.toString(), 0, sensor2.log.size());
		
		sensor1.log.clear();
		sensor2.log.clear();
		animation.log.clear();
		// again, suppose the sensor is occupied
		popup.msgIAmOccupied(sensor1);
		popup.pickAndExecuteAnAction();
		// sensor 1 should receive nothing
		assertEquals(
				"Mock sensor1 should have an empty event log now. Instead, the mock sensor1 event log reads: "
						+ sensor1.log.toString(), 0, sensor1.log.size());
		assertEquals(
				"Mock sensor2 should have an empty event log now. Instead, the mock sensor2 event log reads: "
						+ sensor2.log.toString(), 0, sensor2.log.size());
		assertEquals(
				"Mock animation should have an empty event log now. Instead, the mock animation event log reads: "
						+ animation.log.toString(), 0, animation.log.size());
		sensor1.log.clear();
		sensor2.log.clear();
		// now, suppose 1 year later, the sensor is empty
		popup.msgIAmEmpty(sensor1);
		popup.pickAndExecuteAnAction();
		assertTrue(
				"Mock sensor1 should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ sensor1.log.toString(),
				sensor1.log
						.containsString("I know that there is glass incoming"));
		assertEquals(
				"1 message should have been sent to the workstation. Event log: "
						+ sensor1.log.toString(), 1, sensor1.log.size());
		assertEquals(
				"Mock sensor2 should have an empty event log now. Instead, the mock sensor2 event log reads: "
						+ sensor2.log.toString(), 0, sensor2.log.size());
		assertEquals(
				"Mock animation should have an empty event log now. Instead, the mock animation event log reads: "
						+ animation.log.toString(), 0, animation.log.size());
		sensor1.log.clear();
		sensor2.log.clear();
		animation.log.clear();
	}

	public void testPopupAndTwoWorkStations() {
		Object[] args = new Object[1];
		args[0] = new Long(0);
		Transducer transducer = new Transducer();
		transducer.startTransducer();
		MockAnimation animation = new MockAnimation(transducer);
		// create glass
		Glass glass1 = new Glass(new Recipe(), "Glass1");
		Glass glass2 = new Glass(new Recipe(), "Glass2");
		Glass glass3 = new Glass(new Recipe(), "Glass3");
		// create a conveyor family
		ConveyorFamily conveyorFamily1 = new ConveyorFamily();
		// next conveyor family
		ConveyorFamily conveyorFamily2 = new ConveyorFamily();
		// create mock workstation agents
		MockWorkStation top = new MockWorkStation("Top", transducer);
		MockWorkStation bot = new MockWorkStation("Bot", transducer);
		// create a popup agent
		PopupAgent popup = new PopupAgent("Popup2", transducer,
				conveyorFamily1, conveyorFamily2, top, bot);
		// create mocks
		// this sensor is on the front of the popup
		MockSensor sensor2 = new MockSensor("Sensor2", transducer,
				conveyorFamily1);
		// this sensor is behind the popup (belong to the next family)
		MockSensor sensor1 = new MockSensor("Sensor1", transducer,
				conveyorFamily2, conveyorFamily1);
		// set mocks for the families
		conveyorFamily1.setSensor2(sensor2);
		conveyorFamily2.setSensor1(sensor1);
		// pre-condition
		assertEquals(
				"Mock sensor1 should have an empty event log now. Instead, the mock sensor1 event log reads: "
						+ sensor1.log.toString(), 0, sensor1.log.size());
		assertEquals(
				"Mock sensor2 should have an empty event log now. Instead, the mock sensor2 event log reads: "
						+ sensor2.log.toString(), 0, sensor2.log.size());
		assertEquals(
				"Mock animation should have an empty event log now. Instead, the mock animation event log reads: "
						+ animation.log.toString(), 0, animation.log.size());
		// now, there are two workstations at popup, now, let's see how do they
		// interact with popup
		// firstly, there is glass coming to the popup
		popup.msgCanISendGlass(sensor2, glass1);
		// run scheduler to call the function checkstationstate()
		popup.pickAndExecuteAnAction();
		// now, sensor should receive msgIAmEmpty(),test it!

		assertTrue(
				"Mock sensor2 should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ sensor2.log.toString(),
				sensor2.log.containsString("I know that popup is empty"));
		assertEquals(
				"1 message should have been sent to the workstation. Event log: "
						+ sensor2.log.toString(), 1, sensor2.log.size());
		assertEquals(
				"Mock animation should have an empty event log now. Instead, the mock animation event log reads: "
						+ animation.log.toString(), 0, animation.log.size());
		assertEquals(
				"Mock sensor1 should have an empty event log now. Instead, the mock sensor1 event log reads: "
						+ sensor1.log.toString(), 0, sensor1.log.size());
		// I have to clear the log here, because it would make no sense to use
		// containString() to test it over and over
		sensor2.log.clear();
		// now, suppose sensor send the msghereisglass to popup properly
		popup.msgHereIsGlass(sensor2, glass1);
		popup.pickAndExecuteAnAction();
		// now, state is changed to working, popup is ready to raise!
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
		// now, top workstation should receive msg come from popup, test it!
		// ignore transducer fire events about workstation, because I did in the
		// testPopupInteractWithSensor
		assertTrue(
				"Mock workstation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ top.log.toString(),
				top.log.containsString("I know that there is glass coming to Top"));
		assertEquals(
				"1 message should have been sent to the workstation. Event log: "
						+ top.log.toString(), 1, top.log.size());
		top.log.clear();
		// then, sensor should receive msgIAmOccupied
		assertTrue(
				"Mock sensor2 should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ sensor2.log.toString(),
				sensor2.log.containsString("I know that popup is occupied"));
		assertEquals(
				"1 message should have been sent to the workstation. Event log: "
						+ sensor2.log.toString(), 1, sensor2.log.size());
		sensor2.log.clear();
		// now, popup is raised, suppose another glass is coming to the popup

		popup.msgCanISendGlass(sensor2, glass2);
		// run scheduler to call the function doLowerPopup()
		popup.pickAndExecuteAnAction();
		assertTrue(
				"Mock sensor2 should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ sensor2.log.toString(),
				sensor2.log.containsString("I know that popup is empty"));
		assertEquals(
				"1 message should have been sent to the workstation. Event log: "
						+ sensor2.log.toString(), 1, sensor2.log.size());
		try {
			transducer.transducerThread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ animation.log.toString(),
				animation.log.containsString("POPUP_DO_MOVE_DOWN"));// move down
																	// when pick
																	// up a
																	// glass
		assertEquals(
				"Mock sensor1 should have an empty event log now. Instead, the mock sensor1 event log reads: "
						+ sensor1.log.toString(), 0, sensor1.log.size());
		sensor2.log.clear();
		animation.log.clear();

		popup.msgHereIsGlass(sensor2, glass2);
		// run the sceduler to call glassArrived()
		popup.pickAndExecuteAnAction();
		// now, run the scheduler to raise the popup
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

		// and sensor2 should receive I am occupied now;
		assertTrue(
				"Mock sensor2 should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ sensor2.log.toString(),
				sensor2.log.containsString("I know that popup is occupied"));
		assertEquals(
				"1 message should have been sent to the workstation. Event log: "
						+ sensor2.log.toString(), 1, sensor2.log.size());
		assertEquals(
				"Mock sensor1 should have an empty event log now. Instead, the mock sensor1 event log reads: "
						+ sensor1.log.toString(), 0, sensor1.log.size());

		sensor2.log.clear();

		// now, bot workstation should receive msg come from popup, test it!

		assertTrue(
				"Mock workstation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ bot.log.toString(),
				bot.log.containsString("I know that there is glass coming to Bot"));
		assertEquals(
				"1 message should have been sent to the workstation. Event log: "
						+ bot.log.toString(), 1, bot.log.size());
		assertEquals(
				"Mock animation should have an empty event log now. Instead, the mock animation event log reads: "
						+ animation.log.toString(), 0, animation.log.size());
		bot.log.clear();

		// now, the workstation of popup should set to occupied in both
		// suppose sensor wants to sent another glass right now,
		popup.msgCanISendGlass(sensor2, glass3);
		// run scheduler to call the function checkstationstate()
		popup.pickAndExecuteAnAction();
		// now, sensor should receive msgIAmOccupied(),test it!

		assertTrue(
				"Mock sensor2 should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ sensor2.log.toString(),
				sensor2.log.containsString("I know that popup is occupied"));
		assertEquals(
				"1 message should have been sent to the sensor2. Event log: "
						+ sensor2.log.toString(), 1, sensor2.log.size());
		sensor2.log.clear();
		// can they release from workstation successfully?

		// then, suppose workstation work (not requirement in V0), it will send
		// msgGlassDone() to popup, it can't be tested since popup is not a mock
		// here
		// as an alternative way, we go further and see what happen?
		// Suppose the workstation sent the finished glass to popup
		popup.msgGlassDone(top, glass1);
		// now, next conveyor family received incoming glass message
		// Again, here, suppose sensor works properly(test sensor later), and it
		// sends the msgIAmEmpty back to the previous popup agent
		popup.msgIAmEmpty(sensor2);
		// it totally depend on the GUI, hence, call eventFired() is a easy way
		// to test it
		transducer.fireEvent(TChannel.WASHER, TEvent.WORKSTATION_RELEASE_GLASS,
				args);
		try {
			transducer.transducerThread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ animation.log.toString(),
				animation.log.containsString("WORKSTATION_RELEASE_GLASS"));

		animation.log.clear();

		// set the state to sending_glass_to_sensor
		transducer.fireEvent(TChannel.POPUP,
				TEvent.WORKSTATION_RELEASE_FINISHED, args);

		// now run scheduler and next conveyor family will get notified;
		popup.pickAndExecuteAnAction();
		assertTrue(
				"Mock sensor1 should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ sensor1.log.toString(),
				sensor1.log
						.containsString("I know that there is glass incoming"));
		assertEquals(
				"1 message should have been sent to the sensor1. Event log: "
						+ sensor1.log.toString(), 1, sensor1.log.size());
		sensor1.log.clear();
		// popup receive the same event

		// of course animation will receive WORKSTATION_RELEASE_FINISHED, so
		// just delete it
		animation.log.clear();
		// next, test the second glass finished
		popup.msgGlassDone(bot, glass2);
		// skip the steps asking around whether sensor has space or not.
		// Again, here, suppose sensor works properly(test sensor later), and it
		// sends the msgIAmOccupied back to the previous popup agent
		popup.msgIAmOccupied(sensor2);
		transducer.fireEvent(TChannel.WASHER, TEvent.WORKSTATION_RELEASE_GLASS,
				args);
		try {
			transducer.transducerThread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ animation.log.toString(),
				animation.log.containsString("WORKSTATION_RELEASE_GLASS"));

		animation.log.clear();

		// set the state to sending_glass_to_sensor
		transducer.fireEvent(TChannel.POPUP,
				TEvent.WORKSTATION_RELEASE_FINISHED, args);
		popup.pickAndExecuteAnAction();
		// here is important!!! the sensor1 should not receive anything because
		// we are not passing glass due to the capacity of the sensor
		assertEquals(
				"0 message should have been sent to the sensor1. Event log: "
						+ sensor1.log.toString(), 0, sensor1.log.size());
		assertEquals(
				"Mock animation should have an empty event log now. Instead, the mock animation event log reads: "
						+ animation.log.toString(), 0, animation.log.size());
		sensor1.log.clear();
		// assume 1 year later, the sensor sent the msgIAmEmpty() to the popup
		popup.msgIAmEmpty(sensor2);
		popup.pickAndExecuteAnAction();

		try {
			transducer.transducerThread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// maybe you will see a fail in the test here? it depends on how many
		// times you run
		// I just add a sleep here, try to make the run longer
		assertTrue(
				"Mock sensor1 should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ sensor1.log.toString(),
				sensor1.log
						.containsString("I know that there is glass incoming"));
		sensor1.log.clear();

		// now, can a third one raise to the workstation?

		/*
		 * popup.msgCanISendGlass(sensor2, glass3); // run scheduler to call the
		 * function doLowerPopup() if needs
		 * 
		 * popup.pickAndExecuteAnAction();
		 * 
		 * assertTrue(
		 * "Mock sensor2 should have received the msg after the pickAndExecuteAnAction. Event log: "
		 * + sensor2.log.toString(),
		 * sensor2.log.containsString("I know that popup is empty"));
		 * assertEquals(
		 * "1 message should have been sent to the workstation. Event log: " +
		 * sensor2.log.toString(), 1, sensor2.log.size()); sensor2.log.clear();
		 */
		popup.msgHereIsGlass(sensor2, glass3);
		// run the sceduler to call glassArrived()
		popup.pickAndExecuteAnAction();
		// now, run the scheduler to raise the popup
		popup.pickAndExecuteAnAction();
		// and sensor2 should receive I am occupied now;
		assertTrue(
				"Mock sensor2 should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ sensor2.log.toString(),
				sensor2.log.containsString("I know that popup is occupied"));
		assertEquals(
				"1 message should have been sent to the workstation. Event log: "
						+ sensor2.log.toString(), 1, sensor2.log.size());
		sensor2.log.clear();
		// animation received do_move_up
		assertTrue(
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ animation.log.toString(),
				animation.log.containsString("POPUP_DO_MOVE_UP"));
		animation.log.clear();
		// now, top workstation should receive msg come from popup,

		assertTrue(
				"Mock workstation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ top.log.toString(),
				top.log.containsString("I know that there is glass coming to Top"));
		assertEquals(
				"1 message should have been sent to the workstation. Event log: "
						+ top.log.toString(), 1, top.log.size());
		top.log.clear();
		// no more about the popup to test at this point
		// finished test for msgGlassDone(), msgHereIsGlass, msgCanISendGlass,
		// msgIAmOccupied, msgIAmEmpty, test done
	}
}
