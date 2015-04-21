/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author zach
 */
public class TrainTest {

    public TrainTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test setters and getters of EngFail, of class Train.
     */
    @Test
    public void testEngFail() {
        System.out.println("EngFail");
        Train instance = new Train("10", Block.createblock(0, "mock", 1, 5, 10, true, true), 1);
        instance.setEngFail(true);
        assertTrue(instance.isEngFail());
        instance.setEngFail(false);
        assertFalse(instance.isEngFail());
    }

    /**
     * Test setters and getters of SigFail, of class Train.
     */
    @Test
    public void testSigFail() {
        System.out.println("SigFail");
        Train instance = new Train("10", Block.createblock(0, "mock", 1, 5, 10, true, true), 1);
        instance.setSigFail(true);
        assertTrue(instance.isSigFail());
        instance.setSigFail(false);
        assertFalse(instance.isSigFail());
    }

    /**
     * Test of setters and getters of BrakeFail, of class Train.
     */
    @Test
    public void testBrakeFail() {
        System.out.println("BrakeFail");
        Train instance = new Train("10", Block.createblock(0, "mock", 1, 5, 10, true, true), 1);
        instance.setBrakeFail(true);
        assertTrue(instance.isBrakeFail());
        instance.setBrakeFail(false);
        assertFalse(instance.isBrakeFail());
    }

    /**
     * Test of setters and getters of Lights, of class Train.
     */
    @Test
    public void testLights() {
        System.out.println("Lights");
        Train instance = new Train("10", Block.createblock(0, "mock", 1, 5, 10, true, true), 1);
        instance.Lights(true);
        assertTrue(instance.lightsOn());
        instance.Lights(false);
        assertFalse(instance.lightsOn());
    }

    /**
     * Test of addpassengers method for adding postive passengers, of class
     * Train.
     */
    @Test
    public void testAddpositivepassengers() {
        System.out.println("addpositivepassengers");
        Train instance = new Train("10", Block.createblock(0, "mock", 1, 5, 10, true, true), 1);
        instance.addpassengers(5);
        assertEquals(5, instance.getPassengers());
        instance.addpassengers(5);
        assertEquals(10, instance.getPassengers());
    }

    /**
     * Test addpassengers method for max passengers, of class Train.
     */
    @Test
    public void testaddMaxPassengers() {
        System.out.println("addMaxPassengers");
        Train instance = new Train("10", Block.createblock(0, "mock", 1, 5, 10, true, true), 1);
        instance.addpassengers(999999999);
        assertTrue(instance.getPassengers() <= 222);
    }

    /**
     * Test of addpassengers method for negative passengers, of class Train.
     */
    @Test
    public void testaddNegativePassengers() {
        System.out.println("addNegativePassengers");
        Train instance = new Train("10", Block.createblock(0, "mock", 1, 5, 10, true, true), 1);
        instance.addpassengers(-7);
        assertEquals(0, instance.getPassengers());
    }

    /**
     * Test invalid input to getBrakestatus method, of class Train.
     */
    @Test
    public void testInvalidBrakestatus() {
        System.out.println("invalidBrakestatus");
        Train instance = new Train("10", Block.createblock(0, "mock", 1, 5, 10, true, true), 1);
        instance.updateBrake(-1);
        assertTrue(-1 != instance.getBrakestatus());
        instance.updateBrake(8);
        assertTrue(8 != instance.getBrakestatus());
    }

    /**
     * Test valid input to getBrakestatus method, of class Train.
     */
    @Test
    public void testValidBrakestatus() {
        System.out.println("validBrakestatus");
        Train instance = new Train("10", Block.createblock(0, "mock", 1, 5, 10, true, true), 1);
        instance.updateBrake(0);
        assertTrue(0 == instance.getBrakestatus());
        instance.updateBrake(1);
        assertTrue(1 == instance.getBrakestatus());
        instance.updateBrake(2);
        assertTrue(2 == instance.getBrakestatus());
    }

    /**
     * Test of setters and getters of LeftDoors, of class Train.
     */
    @Test
    public void testLeftDoors() {
        System.out.println("LeftDoors");
        Train instance = new Train("10", Block.createblock(0, "mock", 1, 5, 10, true, true), 1);
        instance.leftdoor(true);
        assertTrue(instance.isLeftdoors());
        instance.leftdoor(false);
        assertFalse(instance.isLeftdoors());
    }

    /**
     * Test of setters and getters of RightDoors, of class Train.
     */
    @Test
    public void testRightDoors() {
        System.out.println("RightDoors");
        Train instance = new Train("10", Block.createblock(0, "mock", 1, 5, 10, true, true), 1);
        instance.rightdoor(true);
        assertTrue(instance.isRightdoors());
        instance.rightdoor(false);
        assertFalse(instance.isRightdoors());
    }

    /**
     * Test of getVelocity method, after power is set, of class Train.
     */
    @Test
    public void testVelocity() {
        System.out.println("Velocity");
        Train instance = new Train("10", Block.createblock(0, "mock", 1, 5, 10, true, true), 1);
        instance.start();
        instance.UpdatePower(100);
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            Logger.getLogger(TrainTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        assertTrue(0 != instance.getVelocity());
    }

    /**
     * Test of updateBlock method with null block, of class Train.
     */
    @Test
    public void testNullUpdateBlock() {
        System.out.println("NullUpdateblock");
        Train instance = new Train("10", Block.createblock(0, "mock", 1, 5, 10, true, true), 1);
        try {
            instance.start();
            instance.updateBlock(null);
        } catch (Exception e) {
            fail("Adding a null block should not crash a train");
        }
    }

    /**
     * Test of updateBlock method with block, of class Train.
     */
    @Test
    public void testUpdateBlock() {
        System.out.println("NullUpdateblock");
        Train instance = new Train("10", Block.createblock(0, "mock", 1, 5, 10, true, true), 1);
        try {
            instance.start();
            instance.updateBlock(Block.createblock(0, "mock", 1, 5, 10, true, true));
        } catch (Exception e) {
            fail("Adding a block should not crash a train");
        }
    }

    /**
     * Test Constructer throws exception when null UID arises
     */
    @Test
    public void testNullUID() {
        System.out.println("NullUID");
        try {
            Train instance = new Train(null, Block.createblock(0, "mock", 1, 5, 10, true, true), 1);
            fail("Constructor didn't throw when I expected it to");
        } catch (Exception e) {
        }

    }

    /**
     * Test Constructer throws exception when null Block arises
     */
    @Test
    public void testNullBlock() {
        System.out.println("NullBlock");
        try {
            Train instance = new Train("1", null, 1);
            fail("Constructor didn't throw when I expected it to");
        } catch (Exception e) {
        }
    }

    /**
     * Test Constructer throws exception when invalid time multiplier arises
     */
    @Test
    public void testInvalidTimeModifier() {
        System.out.println("NullBlock");
        try {
            Train instance = new Train("1", Block.createblock(0, "mock", 1, 5, 10, true, true), 0);
            fail("Constructor didn't throw when I expected it to");
        } catch (Exception e) {
        }
    }
}
