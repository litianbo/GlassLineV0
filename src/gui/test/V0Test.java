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

public class V0Test extends TestCase {
	/**
	 * test the conveyor in the front of sensor, the main goal for the front
	 * conveyor is to pass the glass to the sensor if it is empty, or wait until
	 * it is empty
	 */
	public void testConveyor1() {
		// create a transducer
		Transducer transducer = new Transducer();
		transducer.startTransducer();
		transducer.setDebugMode(TransducerDebugMode.EVENTS_AND_ACTIONS);
		// create a glass
		Glass glass1 = new Glass(new Recipe(), "glass1");
		Glass glass2 = new Glass(new Recipe(), "glass2");
		Glass glass3 = new Glass(new Recipe(), "glass3");
		// create a conveyor family
		ConveyorFamily conveyorFamily1 = new ConveyorFamily();
		// create a conveyor agent for testing purpose
		ConveyorAgent conveyor = new ConveyorAgent("Conveyor1", transducer,
				conveyorFamily1);
		conveyorFamily1.setConveyor1(conveyor);

		// create a mock sensor
		MockSensor sensor1 = new MockSensor("Sensor1", transducer,
				conveyorFamily1);
		MockSensor sensor2 = new MockSensor("Sensor2", transducer,
				conveyorFamily1);
		// create a mock animation
		MockAnimation animation = new MockAnimation(transducer);
		// replace the agents with the mocks for the testing

		conveyorFamily1.setSensor1(sensor1);
		conveyorFamily1.setSensor2(sensor2);
		// now, test precondition of both sensors, to check if the conveyor send
		// any messages to them before calling the scheduler
		// neither sensor1 or sensor2 should have message right now, so use
		// 'equal' to check it
		assertEquals(
				"Mock sensor should have an empty event log now. Instead, the mock sensor event log reads: "
						+ sensor1.log.toString(), 0, sensor1.log.size());
		assertEquals(
				"Mock sensor should have an empty event log now. Instead, the mock sensor event log reads: "
						+ sensor2.log.toString(), 0, sensor2.log.size());
		assertEquals(
				"Mock animation should have an empty event log now. Instead, the mock animation event log reads: "
						+ animation.log.toString(), 0, animation.log.size());
		// now, give the conveyor the right state in order to test,
		// assume front sensor send the msgCanISendGlass() to conveyor (the
		// working of the sensor agent doesn't need to test here, beacuse
		// this sensor is only a mock here, I tested it in another class)
		conveyor.msgCanISendGlass(sensor1, glass1);// change to right state:
													// FRONT_SENSOR_CAN_SEND_GLASS
		// now, neither sensor1 or sensor2 should still have message right now,
		// so, use 'equal' to check it,precondition
		assertEquals(
				"Mock sensor should have an empty event log now. Instead, the mock sensor event log reads: "
						+ sensor1.log.toString(), 0, sensor1.log.size());
		assertEquals(
				"Mock sensor should have an empty event log now. Instead, the mock sensor event log reads: "
						+ sensor2.log.toString(), 0, sensor2.log.size());
		assertEquals(
				"Mock animation should have an empty event log now. Instead, the mock animation event log reads: "
						+ animation.log.toString(), 0, animation.log.size());
		// then, run scheduler to call notifySensor method
		conveyor.pickAndExecuteAnAction();
		// now, the mock sensor should receive message: msgIAmEmpty();
		assertTrue(
				"Mock sensor should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ sensor1.log.toString(),
				sensor1.log.containsString("I know that conveyor is empty"));
		assertEquals(
				"1 message should have been sent to the sensor. Event log: "
						+ sensor1.log.toString(), 1, sensor1.log.size());
		assertTrue(
				"Message should be sent from " + conveyor.toString(),
				sensor1.log.getLastLoggedEvent().getMessage()
						.contains(conveyor.getName()));
		assertEquals(
				"Mock sensor should have an empty event log now. Instead, the mock sensor event log reads: "
						+ sensor2.log.toString(), 0, sensor2.log.size());
		// after sensor echo the msgIAmEmpty back, conveyor needs to start it as
		// soon as possible
		assertTrue(
				"Mock sensor should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ animation.log.toString(),
				animation.log.containsString("CONVEYOR_DO_START"));

		// clear the log here, so we can use the containsString method without
		// worrying about the duplicate message received before
		animation.log.clear();
		sensor1.log.clear();
		sensor2.log.clear();
		// again, suppose the sensor works properly, and it will send the
		// msgHereIsGlass to conveyor
		conveyor.msgHereIsGlass(sensor1, glass1);
		// now, neither sensor1 or sensor2 should still have message right now,
		// so, use 'equal' to check it, precondition
		assertEquals(
				"Mock sensor should have an empty event log now. Instead, the mock sensor event log reads: "
						+ sensor1.log.toString(), 0, sensor1.log.size());
		assertEquals(
				"Mock sensor should have an empty event log now. Instead, the mock sensor event log reads: "
						+ sensor2.log.toString(), 0, sensor2.log.size());
		assertEquals(
				"Mock animation should have an empty event log now. Instead, the mock animation event log reads: "
						+ animation.log.toString(), 0, animation.log.size());
		// after I run the scheduler, sensor2 will receive the msg:
		// msgCanISendGlass;
		conveyor.pickAndExecuteAnAction();
		assertTrue(
				"Mock sensor should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ sensor2.log.toString(),
				sensor2.log
						.containsString("I know that conveyor is going to send glass from:"));
		assertEquals(
				"1 message should have been sent to the sensor. Event log: "
						+ sensor2.log.toString(), 1, sensor2.log.size());
		assertTrue(
				"Message should be sent from " + conveyor.toString(),
				sensor2.log.getLastLoggedEvent().getMessage()
						.contains(conveyor.getName()));
		assertEquals(
				"Mock sensor should have an empty event log now. Instead, the mock sensor event log reads: "
						+ sensor1.log.toString(), 0, sensor1.log.size());
		assertEquals(
				"Mock animation should have an empty event log now. Instead, the mock animation event log reads: "
						+ animation.log.toString(), 0, animation.log.size());
		// clear the log here, so we can use the containsString method without
		// worrying about the duplicate message received before
		sensor1.log.clear();
		sensor2.log.clear();
		animation.log.clear();
		// Again, suppose sensor works properly, it will sent msgIAmEmpty() to
		// conveyor
		conveyor.msgIAmEmpty();
		// now, neither sensor1 or sensor2 should still have message right now,
		// so, use 'equal' to check it, precondition
		assertEquals(
				"Mock sensor should have an empty event log now. Instead, the mock sensor event log reads: "
						+ sensor1.log.toString(), 0, sensor1.log.size());
		assertEquals(
				"Mock sensor should have an empty event log now. Instead, the mock sensor event log reads: "
						+ sensor2.log.toString(), 0, sensor2.log.size());
		assertEquals(
				"Mock animation should have an empty event log now. Instead, the mock animation event log reads: "
						+ animation.log.toString(), 0, animation.log.size());

		// run the schduler, then it will send glass to the sensor
		conveyor.pickAndExecuteAnAction();
		// now, sensor2 shoould receive glass from conveyor
		assertTrue(
				"Mock sensor should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ sensor2.log.toString(),
				sensor2.log.containsString("Sensor2 received glass "));
		assertEquals(
				"1 message should have been sent to the sensor. Event log: "
						+ sensor2.log.toString(), 1, sensor2.log.size());
		assertTrue(
				"Message should be sent from " + conveyor.toString(),
				sensor2.log.getLastLoggedEvent().getMessage()
						.contains(conveyor.getName()));
		assertEquals(
				"Mock animation should have an empty event log now. Instead, the mock animation event log reads: "
						+ animation.log.toString(), 0, animation.log.size());
		// transducer fires nothing because it already started;
		animation.log.clear();
		sensor1.log.clear();
		sensor2.log.clear();
		// now, suppose front sensor give one more piece of glass to the
		// conveyor, note: I tested all of the following, so I just save
		// the testing for only the final purpose of this
		conveyor.msgCanISendGlass(sensor1, glass2);
		conveyor.pickAndExecuteAnAction();
		conveyor.msgHereIsGlass(sensor1, glass2);
		animation.log.clear();
		sensor1.log.clear();
		sensor2.log.clear();// clear all of the above log to avoid duplicate
							// messages
		conveyor.pickAndExecuteAnAction();
		// now, back end sensor should receive msgCanISendGlass();
		assertTrue(
				"Mock sensor should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ sensor2.log.toString(),
				sensor2.log
						.containsString("I know that conveyor is going to send glass from:"));
		assertEquals(
				"1 message should have been sent to the sensor. Event log: "
						+ sensor2.log.toString(), 1, sensor2.log.size());
		assertTrue(
				"Message should be sent from " + conveyor.toString(),
				sensor2.log.getLastLoggedEvent().getMessage()
						.contains(conveyor.getName()));
		assertEquals(
				"Mock sensor should have an empty event log now. Instead, the mock sensor event log reads: "
						+ sensor1.log.toString(), 0, sensor1.log.size());
		// transducer fires event one more time here, because there is a chance
		// the conveyor stop running in some scenario
		assertTrue(
				"Mock sensor should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ animation.log.toString(),
				animation.log.containsString("CONVEYOR_DO_START"));

		animation.log.clear();
		sensor1.log.clear();
		sensor2.log.clear();
		// now, suppose back sensor give msgIAmOccupied to conveyor
		conveyor.msgIAmOccupied();
		// pre-conditions:
		assertEquals(
				"Mock sensor should have an empty event log now. Instead, the mock sensor event log reads: "
						+ sensor1.log.toString(), 0, sensor1.log.size());
		assertEquals(
				"Mock sensor should have an empty event log now. Instead, the mock sensor event log reads: "
						+ sensor2.log.toString(), 0, sensor2.log.size());
		assertEquals(
				"Mock animation should have an empty event log now. Instead, the mock animation event log reads: "
						+ animation.log.toString(), 0, animation.log.size());

		conveyor.pickAndExecuteAnAction();
		// now state change to WAITING_FOR_SENSOR
		// now, run scheduler again to send msgGlassWaiting in mock sensor
		// this msg doesn't do anything in the real agent, it created only for
		// the test purpose, I created the waiting list in the conveyor, if the
		// back end sensor is empty, it will send msgIAmEmpty() to the conveyor,
		// then, the agent will flip the blooean sensorOccupied, and will keep
		// running
		conveyor.pickAndExecuteAnAction();
		assertTrue(
				"Mock sensor should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ sensor2.log.toString(),
				sensor2.log
						.containsString("I know that there is glass waiting on the conveyor"));
		assertEquals(
				"1 message should have been sent to the sensor. Event log: "
						+ sensor2.log.toString(), 1, sensor2.log.size());
		assertTrue(
				"Message should be sent from " + conveyor.toString(),
				sensor2.log.getLastLoggedEvent().getMessage()
						.contains(conveyor.getName()));
		assertEquals(
				"Mock sensor should have an empty event log now. Instead, the mock sensor event log reads: "
						+ sensor1.log.toString(), 0, sensor1.log.size());
		assertTrue(
				"Mock sensor should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ animation.log.toString(),
				animation.log.containsString("CONVEYOR_DO_STOP"));
		// transducer fires nothing because it already started;
		animation.log.clear();
		sensor1.log.clear();
		sensor2.log.clear();
		// suppose one year after, the sensor is empty and it will send the
		// msgIAmEmpty() to conveyor
		conveyor.msgIAmEmpty();
		// test precondition
		assertEquals(
				"Mock sensor should have an empty event log now. Instead, the mock sensor event log reads: "
						+ sensor1.log.toString(), 0, sensor1.log.size());
		assertEquals(
				"Mock sensor should have an empty event log now. Instead, the mock sensor event log reads: "
						+ sensor2.log.toString(), 0, sensor2.log.size());
		assertEquals(
				"Mock animation should have an empty event log now. Instead, the mock animation event log reads: "
						+ animation.log.toString(), 0, animation.log.size());
		// now, run the scheduler
		conveyor.pickAndExecuteAnAction();
		// sensor2 suppose to receive msgCanISendGlass()
		assertTrue(
				"Mock sensor should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ sensor2.log.toString(),
				sensor2.log
						.containsString("I know that conveyor is going to send glass from:"));
		assertEquals(
				"1 message should have been sent to the sensor. Event log: "
						+ sensor2.log.toString(), 1, sensor2.log.size());
		assertTrue(
				"Message should be sent from " + conveyor.toString(),
				sensor2.log.getLastLoggedEvent().getMessage()
						.contains(conveyor.getName()));
		assertEquals(
				"Mock sensor should have an empty event log now. Instead, the mock sensor event log reads: "
						+ sensor1.log.toString(), 0, sensor1.log.size());
		// transducer starts again because he needs to send glass to the
		// recently emptied sensor
		assertTrue(
				"Mock sensor should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ animation.log.toString(),
				animation.log.containsString("CONVEYOR_DO_START"));

		animation.log.clear();
		// clear the log here, so we can use the containsString method without
		// worrying about the duplicate message received before
		sensor1.log.clear();
		sensor2.log.clear();
		// now, suppose the conveyor needs to stop due to the the other conveyor
		// family told it to do soF
		conveyor.msgStop();
		// precondiction
		assertEquals(
				"Mock sensor should have an empty event log now. Instead, the mock sensor event log reads: "
						+ sensor1.log.toString(), 0, sensor1.log.size());
		assertEquals(
				"Mock sensor should have an empty event log now. Instead, the mock sensor event log reads: "
						+ sensor2.log.toString(), 0, sensor2.log.size());
		assertEquals(
				"Mock animation should have an empty event log now. Instead, the mock animation event log reads: "
						+ animation.log.toString(), 0, animation.log.size());
		// transducer fires nothing because it already started and will be stop
		// after the scheduler runs;
		animation.log.clear();
		// run scheduler
		conveyor.pickAndExecuteAnAction();
		// now, front sensor should receive msgIAmOccupied() for a
		// simplification of msgIAmStoped(), these two have the same functin,
		// but different names
		assertTrue(
				"Mock sensor should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ sensor1.log.toString(),
				sensor1.log.containsString("I know that conveyor is occupied"));
		assertEquals(
				"1 message should have been sent to the sensor. Event log: "
						+ sensor1.log.toString(), 1, sensor1.log.size());
		assertTrue(
				"Message should be sent from " + conveyor.toString(),
				sensor1.log.getLastLoggedEvent().getMessage()
						.contains(conveyor.getName()));
		assertEquals(
				"Mock sensor should have an empty event log now. Instead, the mock sensor event log reads: "
						+ sensor2.log.toString(), 0, sensor2.log.size());
		assertTrue(
				"Mock sensor should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ animation.log.toString(),
				animation.log.containsString("CONVEYOR_DO_STOP"));
		animation.log.clear();
		sensor1.log.clear();
		sensor2.log.clear();
		// now, conveyor is at stop state, no matter what the front sensor send
		// messages to it, it shouldn't allow sensor to send any messages
		conveyor.msgCanISendGlass(sensor1, glass3);
		conveyor.pickAndExecuteAnAction();
		assertTrue(
				"Mock sensor should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ sensor1.log.toString(),
				sensor1.log.containsString("I know that conveyor is occupied"));
		assertEquals(
				"1 message should have been sent to the sensor. Event log: "
						+ sensor1.log.toString(), 1, sensor1.log.size());
		assertTrue(
				"Message should be sent from " + conveyor.toString(),
				sensor1.log.getLastLoggedEvent().getMessage()
						.contains(conveyor.getName()));
		assertEquals(
				"Mock sensor should have an empty event log now. Instead, the mock sensor event log reads: "
						+ sensor2.log.toString(), 0, sensor2.log.size());
		assertTrue(
				"Mock sensor should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ animation.log.toString(),
				animation.log.containsString("CONVEYOR_DO_STOP"));
		// because it stoped
		animation.log.clear();
		sensor1.log.clear();
		sensor2.log.clear();
		// now, suppose I want the conveyor to start
		// it will go back to previous state before stop, which is ususally
		// FRONT_SENSOR_CAN_SEND_GLASS, in other cases: NULL(which means glass
		// is empty)
		conveyor.msgStart();
		// Precondition
		assertEquals(
				"Mock sensor should have an empty event log now. Instead, the mock sensor event log reads: "
						+ sensor1.log.toString(), 0, sensor1.log.size());
		assertEquals(
				"Mock sensor should have an empty event log now. Instead, the mock sensor event log reads: "
						+ sensor2.log.toString(), 0, sensor2.log.size());
		assertEquals(
				"Mock animation should have an empty event log now. Instead, the mock animation event log reads: "
						+ animation.log.toString(), 0, animation.log.size());
		// because it stoped, after calling the scheduler, conveyor will run
		// will run
		animation.log.clear();
		// run the scheduler
		conveyor.pickAndExecuteAnAction();
		// now, sensor1 should receive msgIAmEmpty()
		assertTrue(
				"Mock sensor should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ sensor1.log.toString(),
				sensor1.log.containsString("I know that conveyor is empty"));
		assertEquals(
				"1 message should have been sent to the sensor. Event log: "
						+ sensor1.log.toString(), 1, sensor1.log.size());
		assertTrue(
				"Message should be sent from " + conveyor.toString(),
				sensor1.log.getLastLoggedEvent().getMessage()
						.contains(conveyor.getName()));
		assertEquals(
				"Mock sensor should have an empty event log now. Instead, the mock sensor event log reads: "
						+ sensor2.log.toString(), 0, sensor2.log.size());
		assertTrue(
				"Mock sensor should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ animation.log.toString(),
				animation.log.containsString("CONVEYOR_DO_START"));
		animation.log.clear();
		sensor1.log.clear();
		sensor2.log.clear();
	}

	/**
	 * TODO: test the conveyor behind the popup, nothing to do now, let's see
	 * what Prof. say in class
	 */
	public void testConveyor2() {

	}

	/**
	 * TODO: test the popup
	 */
	public void testPopupInteractWithSensor() {// popup is at the end of
												// conveyor
		// create a transducer
		Transducer transducer = new Transducer();
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
		PopupAgent popup = new PopupAgent("Popup1", transducer,
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
		// neither sensor1 or sensor2 should have message right now, so use
		// 'equal' to check it
		assertEquals(
				"Mock sensor1 should have an empty event log now. Instead, the mock sensor1 event log reads: "
						+ sensor1.log.toString(), 0, sensor1.log.size());
		assertEquals(
				"Mock sensor2 should have an empty event log now. Instead, the mock sensor2 event log reads: "
						+ sensor2.log.toString(), 0, sensor2.log.size());
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
		// now, suppose sensor send the msghereisglass to popup properly
		popup.msgHereIsGlass(sensor2, glass1);
		// now, can popup pass the glass to next conveyor family successfully?
		// run scheduler
		popup.pickAndExecuteAnAction();
		// now, state is changed to working, popup is ready to raise!
		popup.pickAndExecuteAnAction();
		// now, popup is raised, and sensor2(in this conveyor) should receive
		// msgIAmOccupied
		assertTrue(
				"Mock sensor should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ sensor2.log.toString(),
				sensor2.log.containsString("I know that popup is occupied"));
		assertEquals(
				"2 message should have been sent to the workstation. Event log: "
						+ sensor2.log.toString(), 2, sensor2.log.size());
		// since the time of working on popup is totally rely on animation,
		// need to use eventfired here to set the correct state
		Object[] args = new Object[1];
		args[0] = new Long(0);
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_RELEASE_FINISHED,
				args);
		// now run scheduler
		popup.pickAndExecuteAnAction();
		// popup sent msgCanISendGlass to next conveyor family
		// test it!
		assertTrue(
				"Mock sensor1 should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ sensor1.log.toString(),
				sensor1.log
						.containsString("I know that the popup from previous conveyor family are going to send glass"));
		// now, next conveyor family received incoming glass message
		// Again, here, suppose sensor works properly(test sensor later), and it
		// sends the msgIAmEmpty back to the previous popup agent
		popup.msgIAmEmpty(sensor2);
		// suppse the workstation sent the finished glass to popup
		popup.msgGlassDone(top, glass1);
		// set the correct stage, then run the scheduler
		popup.pickAndExecuteAnAction();
		// now, the glass should push to next conveyor family, Test it!!!
		assertTrue(
				"Mock sensor1 should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ sensor1.log.toString(),
				sensor1.log
						.containsString("I know that there is glass incoming"));

		/*
		 * assertTrue( "Message should be sent from " + popup.toString(),
		 * sensor2.log.getLastLoggedEvent().getMessage()
		 * .contains(popup.toString()));
		 */

	}

	public void testPopupAndTwoWorkStations() {
		Object[] args = new Object[1];
		args[0] = new Long(0);
		Transducer transducer = new Transducer();
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
		PopupAgent popup = new PopupAgent("Popup1", transducer,
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
		// I have to clear the log here, because it would make no sense to use
		// containString() to test it over and over
		sensor2.log.clear();
		// now, suppose sensor send the msghereisglass to popup properly
		popup.msgHereIsGlass(sensor2, glass1);
		popup.pickAndExecuteAnAction();
		// now, state is changed to working, popup is ready to raise!

		popup.pickAndExecuteAnAction();
		// now, top workstation should receive msg come from popup, test it!

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
		// now, popup is raised, suppose another glass is coming to the sensor

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
		sensor2.log.clear();
		popup.msgHereIsGlass(sensor2, glass2);
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

		// now, bot workstation should receive msg come from popup, test it!

		assertTrue(
				"Mock workstation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ bot.log.toString(),
				bot.log.containsString("I know that there is glass coming to Bot"));
		assertEquals(
				"1 message should have been sent to the workstation. Event log: "
						+ bot.log.toString(), 1, bot.log.size());
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
		top.eventFired(TChannel.POPUP, TEvent.WORKSTATION_RELEASE_GLASS, args);
		assertTrue(
				"Mock workstation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ top.log.toString(),
				top.log.containsString("fired an event:"));
		assertEquals(
				"1 message should have been sent to the workstation. Event log: "
						+ top.log.toString(), 1, top.log.size());

		top.log.clear();

		// set the state to sending_glass_to_sensor
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_RELEASE_FINISHED,
				args);
		// now run scheduler and next conveyor family will get notified;
		popup.pickAndExecuteAnAction();
		assertTrue(
				"Mock sensor1 should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ sensor1.log.toString(),
				sensor1.log
						.containsString("I know that the popup from previous "
								+ "conveyor family are going to send glass"));
		assertEquals(
				"1 message should have been sent to the sensor1. Event log: "
						+ sensor1.log.toString(), 1, sensor1.log.size());
		sensor1.log.clear();
		// again, suppose the sensor works, and send the msgIAmEmpty() to popup
		popup.msgIAmEmpty(sensor2);
		// run the scheduler, it will sent the glass to sensor
		popup.pickAndExecuteAnAction();
		assertTrue(
				"Mock sensor1 should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ sensor1.log.toString(),
				sensor1.log
						.containsString("I know that there is glass incoming"));
		sensor1.log.clear();
		// next, test the second glass finished
		popup.msgGlassDone(bot, glass2);
		bot.eventFired(TChannel.POPUP, TEvent.WORKSTATION_RELEASE_GLASS, args);
		assertTrue(
				"Mock workstation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ bot.log.toString(),
				bot.log.containsString("fired an event:"));
		assertEquals(
				"1 message should have been sent to the workstation. Event log: "
						+ bot.log.toString(), 1, bot.log.size());
		bot.log.clear();
		popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_RELEASE_FINISHED,
				args);
		// Again, here, suppose sensor works properly(test sensor later), and it
		// sends the msgIAmOccupied back to the previous popup agent
		popup.msgIAmOccupied(sensor2);
		popup.pickAndExecuteAnAction();
		// here is important!!! the sensor1 should not receive anything because
		// we are not passing glass due to the capacity of the sensor
		assertEquals(
				"0 message should have been sent to the sensor1. Event log: "
						+ sensor1.log.toString(), 0, sensor1.log.size());
		sensor1.log.clear();
		// assume 1 year later, the sensor sent the msgIAmEmpty() to the popup
		popup.msgIAmEmpty(sensor2);
		popup.pickAndExecuteAnAction();
		assertTrue(
				"Mock sensor1 should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ sensor1.log.toString(),
				sensor1.log
						.containsString("I know that there is glass incoming"));
		sensor1.log.clear();
		// now, can a third one raise to the workstation?
		popup.msgCanISendGlass(sensor2, glass3);
		// run scheduler to call the function doLowerPopup()
		popup.pickAndExecuteAnAction();
		assertTrue(
				"Mock sensor2 should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ sensor2.log.toString(),
				sensor2.log.containsString("I know that popup is empty"));
		assertEquals(
				"1 message should have been sent to the workstation. Event log: "
						+ sensor2.log.toString(), 1, sensor2.log.size());
		sensor2.log.clear();
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
		// now, bot workstation should receive msg come from popup, test it!

		assertTrue(
				"Mock workstation should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ top.log.toString(),
				top.log.containsString("I know that there is glass coming to Top"));
		assertEquals(
				"1 message should have been sent to the workstation. Event log: "
						+ top.log.toString(), 1, top.log.size());
		top.log.clear();
		// no more about the popup to test at this point
	}

	/**
	 * TODO: test the sensors
	 */
	public void testSensor() {
		Transducer transducer = new Transducer();
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
				"1 message should have been sent to the workstation. Event log: "
						+ popup.log.toString(), 1, popup.log.size());
		assertTrue(
				"Message should be sent from " + frontSensor.toString(),
				popup.log.getLastLoggedEvent().getMessage()
						.contains(frontSensor.getName()));
		popup.log.clear();

		// we test popup earlier, now, it should send msgHereIsGlass() to
		// sensor2
		frontSensor.msgHereIsGlass(popup, glass1);
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
				"1 message should have been sent to the workstation. Event log: "
						+ popup.log.toString(), 1, popup.log.size());
		assertTrue(
				"Message should be sent from " + frontSensor.toString(),
				popup.log.getLastLoggedEvent().getMessage()
						.contains(frontSensor.getName()));
		popup.log.clear();

		// after run the schduler, sensor pass to the conveyor
		frontSensor.pickAndExecuteAnAction();
		// mock conveyor should notified by msgCanISendGlass()
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
				"1 message should have been sent to the workstation. Event log: "
						+ conveyor.log.toString(), 1, conveyor.log.size());
		assertTrue(
				"Message should be sent from " + frontSensor.toString(),
				conveyor.log.getLastLoggedEvent().getMessage()
						.contains(frontSensor.getName()));
		conveyor.log.clear();

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

		// again, suppose conveyor sends msgHereIsGlass() to the backsensor
		backSensor.msgHereIsGlass(conveyor, glass1);
		// set the popup to this conveyor family
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
		// finished testing for Sensors
	}

	public void testTwoGlassPassToConveyorFamily() {
		Transducer transducer = new Transducer();
		Recipe recipe = new Recipe();
		Glass glass1 = new Glass(recipe, "glass1");
		Glass glass2 = new Glass(recipe, "glass2");
		// previous conveoyrfamily
		ConveyorFamily conveyorFamily1 = new ConveyorFamily();
		// this conveyor family
		ConveyorFamily conveyorFamily2 = new ConveyorFamily();
		// next conveyor family
		ConveyorFamily conveyorFamily3 = new ConveyorFamily();
		// create popup agent

		PopupAgent popup2 = new PopupAgent("This Popup", transducer,
				conveyorFamily2, conveyorFamily3);
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
		conveyorFamily2.setSensor1(backSensor);
		conveyorFamily2.setSensor1(frontSensor);
		conveyorFamily2.setPopup(popup2);
		// create mock conveyor familys
		MockSensor nextSensor = new MockSensor("Sensor2", transducer,
				conveyorFamily3, conveyorFamily2);
		MockPopup mockpopup = new MockPopup("Preivous Popup", transducer,
				conveyorFamily1, conveyorFamily2);
		conveyorFamily1.setPopup(mockpopup);
		conveyorFamily3.setSensor1(nextSensor);
		// now, test the 11 case
		// glass1 and glass2 are default set to needs machining
		// now, suppose previous conveyorfamily() pass glass to this
		// conveyorfamily2

	}

	/**
	 * TODO: test three glass pass to conveyor family
	 */
	public void testThreeGlassPassToConveyorFamily() {

	}

}
