/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
public class BlockTest {
    
    public BlockTest() {
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
     * Test of createblock method, of class Block.
     */
    @Test
    public void testCreateblock_6args() {
        System.out.println("createblock");
        double gradient = 0.0;
        String beaconData = "";
        int authority = 0;
        int speedlimit = 0;
        int length = 0;
        boolean tunnel = false;
        Block expResult = null;
        Block result = Block.createblock(gradient, beaconData, authority, speedlimit, length, tunnel);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createblock method, of class Block.
     */
    @Test
    public void testCreateblock_7args() {
        System.out.println("createblock");
        double gradient = 0.0;
        String beaconData = "";
        int authority = 0;
        int speedlimit = 0;
        int length = 0;
        boolean tunnel = false;
        boolean yard = false;
        Block expResult = null;
        Block result = Block.createblock(gradient, beaconData, authority, speedlimit, length, tunnel, yard);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getGradient method, of class Block.
     */
    @Test
    public void testGetGradient() {
        System.out.println("getGradient");
        Block instance = null;
        double expResult = 0.0;
        double result = instance.getGradient();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getBeacon method, of class Block.
     */
    @Test
    public void testGetBeacon() {
        System.out.println("getBeacon");
        Block instance = null;
        String expResult = "";
        String result = instance.getBeacon();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAuth method, of class Block.
     */
    @Test
    public void testGetAuth() {
        System.out.println("getAuth");
        Block instance = null;
        int expResult = 0;
        int result = instance.getAuth();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSpeed method, of class Block.
     */
    @Test
    public void testGetSpeed() {
        System.out.println("getSpeed");
        Block instance = null;
        int expResult = 0;
        int result = instance.getSpeed();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLength method, of class Block.
     */
    @Test
    public void testGetLength() {
        System.out.println("getLength");
        Block instance = null;
        int expResult = 0;
        int result = instance.getLength();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isTunnel method, of class Block.
     */
    @Test
    public void testIsTunnel() {
        System.out.println("isTunnel");
        Block instance = null;
        boolean expResult = false;
        boolean result = instance.isTunnel();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isYard method, of class Block.
     */
    @Test
    public void testIsYard() {
        System.out.println("isYard");
        Block instance = null;
        boolean expResult = false;
        boolean result = instance.isYard();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
