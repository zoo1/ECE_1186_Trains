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
     * Test of if gradient is too low block will not be created
     */
    @Test
    public void testtolowgradient() {
        Block result = Block.createblock(-3, "easy", 1, 5, 10, true, true);
        assertNull(result);
    }
    
    /**
     * Test of if gradient is just right a block will be created and hold correct values
     */
    @Test
    public void testjustrightgradient() {
        for(double i = -2; i < 3; i++)
        {
            System.out.println("Tested Grade: "+ i);
            Block result = Block.createblock(i, "easy", 1, 5, 10, true, true);
            assertEquals(result.getGradient(),i,0);
        }
    }
    
    /**
     * Test of if gradient is too high block will not be created
     */
    @Test
    public void testtohighgradient() {
        Block result = Block.createblock(3, "easy", 1, 5, 10, true, true);
        assertNull(result);
    }

    /**
     * Test of if beaconstring is too long block will not be created
     */
    @Test
    public void testtoolongbeaconstring() {
        Block result = Block.createblock(3, "extremelylongbeaconstringthatshouldmakethistestcasefail", 1, 5, 10, true, true);
        assertNull(result);
    }
    
    /**
     * Test of if beaconstring is null block will not be created
     */
    @Test
    public void testnullbeaconstring() {
        Block result = Block.createblock(3, null, 1, 5, 10, true, true);
        assertNull(result);
    }
    
     /**
     * Test of if beaconstring is correct block will store the strin
     */
    @Test
    public void testcorrectbeaconstring() {
        String[] teststrings = {"test1" , "testtwo", "TestThreee"};
        for(int i = 0; i < teststrings.length;i++)
        {
            System.out.println("Testing String: " + teststrings[i]);
            Block result = Block.createblock(0, teststrings[i], 1, 5, 10, true, true);
            assertTrue(result.getBeacon().equals(teststrings[i]));
        }
    }
    
    /**
     * Test of if authority is negative block will not be created
     */
    @Test
    public void testnegativeauthority() {
        Block result = Block.createblock(0, "test", -1, 5, 10, true, true);
        assertNull(result);
    }
    
    /**
     * Test of if authority is positive block will be created
     */
    @Test
    public void testpositiveauthority() {
        Block result = Block.createblock(0, "test", 1, 5, 10, true, true);
        assertEquals(1,result.getAuth());
    }
    
    /**
     * Test of if authority is zero block will be created
     */
    @Test
    public void testzeroauthority() {
        Block result = Block.createblock(0, "test", 0, 5, 10, true, true);
        assertEquals(0,result.getAuth());
    }
    
    /**
     * Test of if speedlimit is negative block will not be created
     */
    @Test
    public void testnegativespeedlimit() {
        Block result = Block.createblock(0, "test", 1, -1, 10, true, true);
        assertNull(result);
    }
    
    /**
     * Test of if speedlimit is positive block will be created
     */
    @Test
    public void testpositivespeedlimit() {
        Block result = Block.createblock(0, "test", 1, 1, 10, true, true);
        assertEquals(1,result.getSpeed(),0);
    }
    
    /**
     * Test of if speedlimit is zero block will be created
     */
    @Test
    public void testzerospeedlimit() {
        Block result = Block.createblock(0, "test", 1, 0, 10, true, true);
        assertEquals(0,result.getSpeed(),0);
    }
    
    /**
     * Test of if length is negative block will not be created
     */
    @Test
    public void testnegativelength() {
        Block result = Block.createblock(0, "test", 1, 1, -10, true, true);
        assertNull(result);
    }
    
    /**
     * Test of if length is positive block will be created
     */
    @Test
    public void testpositivelength() {
        Block result = Block.createblock(0, "test", 1, 1, 10, true, true);
        assertEquals(1,result.getLength(),10);
    }
    
    /**
     * Test of if length is zero block will not be created
     */
    @Test
    public void testzerolength() {
        Block result = Block.createblock(0, "test", 1, 1, 0, true, true);
         assertNull(result);
    }
    
    /**
     * Test block creation stores tunnel correctly
     */
    @Test
    public void testtunnel() {
        Block result = Block.createblock(0, "test", 1, 1, 10, true, false);
        assertTrue(result.isTunnel());
        result = Block.createblock(0, "test", 1, 1, 10, false, true);
        assertFalse(result.isTunnel());
    }
    
    /**
     * Test block creation stores yard correctly
     */
    @Test
    public void testyard() {
        Block result = Block.createblock(0, "test", 1, 1, 10, false, true);
        assertTrue(result.isYard());
        result = Block.createblock(0, "test", 1, 1, 10, true, false);
        assertFalse(result.isYard());
    }
    
    /**
     * Test 5 arg block creation defaults yard to false
     */
    @Test
    public void test5arg() {
        Block result = Block.createblock(0, "test", 1, 1, 10, true);
        assertFalse(result.isYard());
    }
    
}
