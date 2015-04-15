/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author zach
 */
public class Block {
    private double gradient;
    private String beaconData;
    private int authority;
    private int speedlimit;
    private int length;
    private boolean tunnel;
    private boolean yard;
    
    private Block(double gradient, String beaconData, int authority, int speedlimit, int length, boolean tunnel, boolean yard)
    {
        
        this.gradient = gradient;
        this.beaconData = beaconData;
        this.authority = authority;
        this.speedlimit = speedlimit;
        this.length = length;
        this.tunnel = tunnel;
        this.yard = yard;
    }
    public static Block createblock(double gradient, String beaconData, int authority, int speedlimit, int length, boolean tunnel)
    {
        return createblock( gradient, beaconData, authority, speedlimit, length, tunnel, false);
    }
    public static Block createblock(double gradient, String beaconData, int authority, int speedlimit, int length, boolean tunnel, boolean yard)
    {
        //TODO add gradient and beaconData checking
        if(authority<0) //Invalid authority
            return null;
        if(speedlimit<=0)//Invalid speedlimit
            return null;
        if(length<=0) //Invalid length
            return null;
        return new Block(gradient, beaconData, authority, speedlimit, length, tunnel, yard);
    }
    public double getGradient() {
        return gradient;
    }

    public String getBeacon() {
        return beaconData;
    }

    public int getAuth() {
        return authority;
    }

    public int getSpeed() {
        return speedlimit;
    }

    public int getLength() {
        return length;
    }

    public boolean isTunnel() {
        return tunnel;
    }
    public boolean isYard() {
        return yard;
    }
}
