
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private final double TRAINFRICTION = .001;
    private final double GRAVITY = 9.81; //m/s²
    private volatile double totalmass;
    private volatile double timemodifier = 1;
    private int brakestatus = 0;
    private boolean leftdoors = false;
    private boolean rightdoors = false;
    private volatile double power = 0; //in Watts
    private volatile static double position = 0; //in m
    private volatile static double velocity = 0; //in m/s
    private static double acceleration = 0;//in m/s^2
    private long prevtime;//in s
    final private double maxspeed = 19.444444444444444; //m/s
    final private double maxacceleration = 0.5; //m/s^2
    private volatile boolean lights = false;
    private volatile boolean engFail = false;
    private volatile boolean sigFail = false;
    private volatile boolean brakeFail = false;

    public boolean isEngFail() {
        return engFail;
    }

    public void setEngFail(boolean engFail) {
        this.engFail = engFail;
        MessageLibrary.sendMessage("localhost", TRAINCONTROLLER, "Train Model : " + UID + " :set, EngineFail=" + brakeFail);
    }

    public boolean isSigFail() {
        return sigFail;
    }

    public void setSigFail(boolean sigFail) {
        this.sigFail = sigFail;
        MessageLibrary.sendMessage("localhost", TRAINCONTROLLER, "Train Model : " + UID + " :set, SignalFail=" + brakeFail);
    }

    public boolean isBrakeFail() {
        return brakeFail;
    }

    public void setBrakeFail(boolean brakeFail) {
        this.brakeFail = brakeFail;
        MessageLibrary.sendMessage("localhost", TRAINCONTROLLER, "Train Model : " + UID + " :set, BrakeFail=" + brakeFail);
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
        if(power < 0)
            this.power = 0;
        else if(power > 120000) //Max Power 120 kilowatts
            this.power = 120000;
        else
            this.power = power;
    }
    
    public void run() {
        boolean running=true;
        while (running) {
            double finalvelocity = 0; // v(t)
            double finalaccel = 0;// a(t)
            double lastposition=position;
            double truetimestep = (System.currentTimeMillis() - prevtime) / 1000.0; //calc last function call * time modifier
            //store last time
            prevtime = System.currentTimeMillis();
            if (brakestatus == 1 && !brakeFail) //Service Brake
            {
                acceleration = -SERVICEBRAKE;
                finalaccel = -SERVICEBRAKE;
            }
            if (brakestatus == 2 && !brakeFail) //E Brake
            {
                acceleration = -EMERGENCYBRAKE;
                finalaccel = -EMERGENCYBRAKE;
            }
            finalvelocity = velocity/timemodifier + (acceleration/timemodifier) * truetimestep;
            if (brakestatus > 0 && finalvelocity <= 0 && !brakeFail) { //If brakes are engaged and the train has negative velocity it should be stopped
                finalvelocity = 0;
                finalaccel = 0;
            } else if (brakestatus == 0 || brakeFail) {
                double blockradient = Math.toRadians(Math.toDegrees(Math.atan(currentBlock.getGradient()/100)));
                double rollingforce = TRAINFRICTION * (Math.cos(blockradient) * GRAVITY * totalmass);
                double gradeforce = Math.sin(blockradient) * totalmass * GRAVITY;
                //EngForce = power / v;
                double engforce = 0;
                if (power == 0) {
                    engforce = 0;
                } else if (finalvelocity == 0) {
                    engforce = maxacceleration * totalmass;
                } else if (engFail)
                    engforce = 0;
                else {
                    engforce = power / finalvelocity;
                }
                double finalforce=0;
                //3 different force summations
                if(Math.abs(engforce-gradeforce) < rollingforce) //Rolling Force should not be the driving force
                {
                    if(velocity > 0)
                    {
                        finalforce = engforce-gradeforce-rollingforce;
                    }
                    else
                    {
                        finalforce=0;
                        finalvelocity = 0;
                        velocity = 0;
                    }
                }
                else
                    finalforce=engforce-gradeforce-rollingforce;
                
                finalaccel = finalforce / totalmass;
                if(finalaccel > maxacceleration)
                {
                    finalaccel=maxacceleration;
                }
                if(finalvelocity > maxspeed) { //cap speed at speed limit
                    finalvelocity = maxspeed;
                    finalaccel = 0; //and stop acceleration (for position calc in next step)
                }
            }
            finalvelocity *= timemodifier;
            finalaccel *= timemodifier;
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
            if (velocity != finalvelocity&&running) 
            {
                MessageLibrary.sendMessage("localhost", TRAINCONTROLLER, "Train Model : " + UID + " : set, Actual Speed=" + finalvelocity);
            }
            velocity = finalvelocity;
            acceleration = finalaccel;
            try {
                Thread.sleep(1); //millisecond time tic
            } catch (InterruptedException ex) {
                Logger.getLogger(Train.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
