package gui.test;

import mocks.MockAnimation;
import mocks.MockSensor;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;
import transducer.TransducerDebugMode;
import engine.agent.ConveyorAgent;
import engine.agent.Glass;
import engine.agent.Recipe;
import engine.util.ConveyorFamily;
import junit.framework.TestCase;

public class ConveyorTest extends TestCase {
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
				"Mock animation should have received the msg after the pickAndExecuteAnAction. Event log: "
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
		try {
			transducer.transducerThread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		// and conveyor will deliver glass to the back end sensor if has any
		// because the state is already returned to null (just back to work) (to
		// test wether the front end sensor will send glass after the respawn of
		// the conveyor is not required for conveyor agent test, I will do it on
		// sensor test )
		// finally I proved that msgCanISendGlass, msgHereIsGlass, msgIAmEmpty,
		// msgIAmOccupied, msgStart, msgStop works properly
		// complete test for conveyorAgent

	}

}
