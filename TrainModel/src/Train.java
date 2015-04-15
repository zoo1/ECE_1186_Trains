
import java.util.Random;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author zach
 */
public class Train extends Thread {

    final private static int TRACKMODEL = 8005; //Track model socket
    final private static int TRAINCONTROLLER = 8009; //Train Controller Socket
    public final String UID;
    private volatile Block currentBlock;
    private volatile int passengers;
    private final int crew;
    private final double MASS = 37103.86; //kg
    private final double PERSONMASS = 88.9; //kg
    private final int MAXIMUMPASSENGERS = 222;
    private final double SERVICEBRAKE = 1.2; //m/s²
    private final double EMERGENCYBRAKE = 2.73; //m/s²
    private volatile double totalmass;
    private volatile double timemodifier = 1;
    private int brakestatus = 0;
    private boolean leftdoors = false;
    private boolean rightdoors = false;
    private volatile double power = 0; //in Watts
    private static double position = 0; //in m
    private static double velocity = 0; //in m/s
    private static double acceleration = 0;//in m/s^2
    private long prevtime;//in s
    final private double maxspeed = 19.444444444444444; //m/s
    final private double maxacceleration = 0.5; //m/s^2
    private volatile boolean lights = false;
    private volatile boolean engFail = false;
    private volatile boolean sigFail = false;
    private volatile boolean breakFail = false;

    public boolean isEngFail() {
        return engFail;
    }

    public void setEngFail(boolean engFail) {
        this.engFail = engFail;
    }

    public boolean isSigFail() {
        return sigFail;
    }

    public void setSigFail(boolean sigFail) {
        this.sigFail = sigFail;
    }

    public boolean isBreakFail() {
        return breakFail;
    }

    public void setBreakFail(boolean breakFail) {
        this.breakFail = breakFail;
    }

    public boolean lightsOn() {
        return lights;
    }

    public void Lights(boolean lights) {
        this.lights = lights;
    }

    Train(String UID, Block firstblock, double timemodifier) {
        this.UID = UID;
        this.currentBlock = firstblock;
        this.crew = 2;
        this.passengers = 0;
        totalmass = MASS + (PERSONMASS * crew);
        this.timemodifier = timemodifier;
        this.prevtime = System.currentTimeMillis();
    }

    public void addpassengers(int count) {
        passengers = passengers + count;
        if (passengers > MAXIMUMPASSENGERS) //too many passengers on train
        {
            MessageLibrary.sendMessage("localhost", TRACKMODEL, "Train Model : " + UID + " :set, Passengers=" + (passengers - MAXIMUMPASSENGERS));
            passengers = MAXIMUMPASSENGERS;
        }
        MessageLibrary.sendMessage("localhost", TRAINCONTROLLER, "Train Model : " + UID + " :set, Passengers=" + passengers);
        totalmass = MASS + (PERSONMASS * (crew + passengers));
    }

    public int getPassengers() {
        return passengers;
    }

    public int getBrakestatus() {
        return brakestatus;
    }

    public boolean isLeftdoors() {
        return leftdoors;
    }

    public boolean isRightdoors() {
        return rightdoors;
    }

    public double getVelocity() {
        return velocity;
    }

    public void updateBlock(Block block) {
        if (block != null) {
            this.currentBlock = block;
        }
    }

    public void updateTime(double time) {
        if (time > 0) {
            this.timemodifier = time;
        }
    }

    public void updateBreak(int brake) {
        if (brake == 0 || brake == 1 || brake == 2) {
            this.brakestatus = brake;
        }
    }

    public void leftdoor(boolean status) {
        this.leftdoors = status;
        if (status) {
            opendoors();
        }
    }

    public void rightdoor(boolean status) {
        this.rightdoors = status;
        if (status) {
            opendoors();
        }
    }

    private void opendoors() {
        Random rand = new Random();
        int passengersleaving = rand.nextInt(passengers + 1);
        passengers = passengers - passengersleaving;
        MessageLibrary.sendMessage("localhost", TRACKMODEL, "Train Model : " + UID + " :set, Passengers=" + passengersleaving);
        MessageLibrary.sendMessage("localhost", TRAINCONTROLLER, "Train Model : " + UID + " :set, Passengers=" + passengers);
        totalmass = MASS + (PERSONMASS * (crew + passengers));
    }

    public void UpdatePower(double power) {
        if (power > 0 && power<=120000) { //Max Power 120 kilowatss
            this.power = power;
        }
    }

    public void run() {
        boolean running=true;
        while (running) {
            double finalvelocity = 0; // v(t)
            double finalaccel = 0;// a(t)
            double lastposition=position;
            double truetimestep = timemodifier * (System.currentTimeMillis() - prevtime) / 1000.0; //calc last function call * time modifier
            //store last time
            prevtime = System.currentTimeMillis();
            if (brakestatus == 1) //Service Brake
            {
                acceleration = -SERVICEBRAKE;
            }
            if (brakestatus == 2) //E Brake
            {
                acceleration = -EMERGENCYBRAKE;
            }
            finalvelocity = velocity + acceleration * truetimestep;
            if (brakestatus > 0 && finalvelocity <= 0) { //If brakes are engaged and the train has negative velocity it should be stopped
                finalvelocity = 0;
                finalaccel = 0;
            } else if (brakestatus == 0) {
                //EngForce = power / v;
                double engforce;
                if (power == 0) {
                    engforce = 0;
                } else if (finalvelocity == 0) {
                    engforce = maxacceleration * totalmass;
                } else {
                    engforce = power / finalvelocity;
                }

                finalaccel = engforce / totalmass;
                if (finalvelocity > maxspeed) { //cap speed at speed limit
                    finalvelocity = maxspeed;
                    finalaccel = 0; //and stop acceleration (for position calc in next step)
                }
            }
            position = position + (.5 * finalvelocity * truetimestep) + (.25 * finalaccel * truetimestep * truetimestep);
            if (position > (currentBlock.getLength() * .97)) // request next block
            {
                if(currentBlock.isYard())
                {
                    MessageLibrary.sendMessage("localhost", TRAINCONTROLLER, "Train Model : " + UID + " : delete");
                    running=false;
                    MessageLibrary.sendMessage("localhost", 8007, "Train Model: "+ UID + " : delete");
                }
                else
                {
                    MessageLibrary.sendMessage("localhost", TRACKMODEL, "Train Model : " + UID + " : get,block");
                    position = 0;
                }
            }
            if (position != lastposition&&running)
            {
                MessageLibrary.sendMessage("localhost", TRAINCONTROLLER, "Train Model : " + UID + " : set, Position=" + position);
            }
            if (velocity != finalvelocity&&running) {
                MessageLibrary.sendMessage("localhost", TRAINCONTROLLER, "Train Model : " + UID + " : set, Actual Speed=" + finalvelocity);
            }
            velocity = finalvelocity;
            acceleration = finalaccel;
        }
    }

}
