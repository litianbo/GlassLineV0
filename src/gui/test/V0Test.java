package gui.test;

import org.junit.Test;

import mocks.MockPopup;
import mocks.MockSensor;
import mocks.MockWorkStation;
import engine.agent.ConveyorAgent;
import engine.agent.Glass;
import engine.agent.PopupAgent;
import engine.agent.Recipe;
import engine.agent.WorkStationAgent;
import engine.util.ConveyorFamily;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;
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
		// create a glass
		Glass glass = new Glass(new Recipe(), "glass1");
		// create a conveyor family
		ConveyorFamily conveyorFamily1 = new ConveyorFamily();
		// create a conveyor agent for testing purpose
		ConveyorAgent conveyor = new ConveyorAgent("Conveyor1", transducer,
				conveyorFamily1);
		conveyorFamily1.setConveyor1(conveyor);
		// create a popupagent for testing purpose

		// create a mock sensor
		MockSensor sensor1 = new MockSensor("Sensor1", transducer,
				conveyorFamily1);
		MockSensor sensor2 = new MockSensor("Sensor2", transducer,
				conveyorFamily1);

		// replace the agents with the mocks for the testing

		conveyorFamily1.setSensor1(sensor1);
		conveyorFamily1.setSensor2(sensor2);
		// neither sensor1 or sensor2 should have message right now, so use
		// 'equal' to check it
		assertEquals(
				"Mock sensor should have an empty event log now. Instead, the mock sensor event log reads: "
						+ sensor1.log.toString(), 0, sensor1.log.size());
		assertEquals(
				"Mock sensor should have an empty event log now. Instead, the mock sensor event log reads: "
						+ sensor2.log.toString(), 0, sensor2.log.size());
		// now, give the conveyor the right state in order to test,
		conveyor.msgHereIsGlass(glass);
		// now, neither sensor1 or sensor2 should still have message right now,
		// so, use 'equal' to check it
		assertEquals(
				"Mock sensor should have an empty event log now. Instead, the mock sensor event log reads: "
						+ sensor1.log.toString(), 0, sensor1.log.size());
		assertEquals(
				"Mock sensor should have an empty event log now. Instead, the mock sensor event log reads: "
						+ sensor2.log.toString(), 0, sensor2.log.size());

		// after I run the scheduler, sensor2 will receive the msg:
		// msgHereIsGlass;
		conveyor.pickAndExecuteAnAction();
		assertTrue(
				"Mock sensor2 should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ sensor2.log.toString(),
				sensor2.log.containsString("Sensor2 received glass"));
		// now we want to prove that when the sensor is currently working, the
		// conveyor stop giving the glass to the sensor

		// suppose the sensor sent a msgIAmOccupied to conveyor (Test the
		// working
		// of sensor later, now, just assume the sensor work and test conveyor)
		conveyor.msgIAmOccupied();
		// give a new glass named glass2
		conveyor.msgHereIsGlass(new Glass(new Recipe(), "glass2"));
		// I need to make the state of conveyor to WAITING_FOR_POP by calling
		// the scheudler
		conveyor.pickAndExecuteAnAction();
		// call it again to send the msgGlassIsWaiting to the sensor
		conveyor.pickAndExecuteAnAction();
		// now the mocksensor received the msgGlassIsWaiting, let's test it!!!
		assertTrue(
				"Mock sensor should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ sensor2.log.toString(),
				sensor2.log.containsString("I know that there is "));
		// Test for interaction with front end sensor
		// if conveyor has more than three glass?, does front end sensor stop
		// sending glass?
		conveyor.msgHereIsGlass(new Glass(new Recipe(), "glass3"));
		conveyor.msgHereIsGlass(new Glass(new Recipe(), "glass4"));
		// now, run scheduler to see what happoen?
		conveyor.pickAndExecuteAnAction();
		assertTrue(
				"Mock sensor should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ sensor1.log.toString(),
				sensor1.log
						.containsString("I know that I should stop sending glass"));
		// TODO: More on conveyor implementation~!!!

		// sensor.eventFired(TChannel.SENSOR, TEvent.SENSOR_GUI_PRESSED, args);
		// popup.eventFired(TChannel.POPUP, TEvent.POPUP_GUI_RELEASE_FINISHED,
		// args);
		// check if log gets the event fired from above;
		/*
		 * assertTrue(
		 * "Mock sensor should have received the event after the fire. Event log: "
		 * + sensor.log.toString(), sensor.log .containsString(
		 * "Sensor received event SENSOR_GUI_PRESSED from channel SENSOR with arguement: 0"
		 * ));
		 */

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
		popup.msgCanISendGlass(glass1);

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
		popup.msgIAmEmpty();
		// suppse the workstation sent the finished glass to popup
		popup.msgGlassDone(glass1);
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
		popup.msgCanISendGlass(glass1);
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

		popup.msgCanISendGlass(glass2);
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
		popup.msgCanISendGlass(glass3);
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
		popup.msgGlassDone(glass1);
		// now, next conveyor family received incoming glass message
		// Again, here, suppose sensor works properly(test sensor later), and it
		// sends the msgIAmEmpty back to the previous popup agent
		popup.msgIAmEmpty();
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
		popup.msgIAmEmpty();
		// run the scheduler, it will sent the glass to sensor
		popup.pickAndExecuteAnAction();
		assertTrue(
				"Mock sensor1 should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ sensor1.log.toString(),
				sensor1.log
						.containsString("I know that there is glass incoming"));
		sensor1.log.clear();
		// next, test the second glass finished
		popup.msgGlassDone(glass2);
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
		popup.msgIAmOccupied();
		popup.pickAndExecuteAnAction();
		// here is important!!! the sensor1 should not receive anything because
		// we are not passing glass due to the capacity of the sensor
		assertEquals(
				"0 message should have been sent to the sensor1. Event log: "
						+ sensor1.log.toString(), 0, sensor1.log.size());
		sensor1.log.clear();
		// assume 1 year later, the sensor sent the msgIAmEmpty() to the popup
		popup.msgIAmEmpty();
		popup.pickAndExecuteAnAction();
		assertTrue(
				"Mock sensor1 should have received the msg after the pickAndExecuteAnAction. Event log: "
						+ sensor1.log.toString(),
				sensor1.log
						.containsString("I know that there is glass incoming"));
		sensor1.log.clear();
		// now, can a third one raise to the workstation?
		popup.msgCanISendGlass(glass3);
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
		//no more about the popup to test at this point
	}

	/**
	 * TODO: test the sensors
	 */
	public void testSensor() {

	}

	/**
	 * TODO: test three glass pass to conveyor family
	 */
	public void testThreeGlassPassToConveyorFamily() {

	}

}
