import java.lang.*;

public class Train
{
   private int id;
   public BeaconInfo beacon;
   
   //public double authority;
   public double acceleration;
   public boolean leftdoor;
   public boolean rightdoor;
   public boolean lights;
   public boolean ac;
   public boolean automatic;
   public int numPassengers;
   public double position;
   public int distance;
   public boolean EmergencyBrake;
   public boolean ServiceBrake;
   public double stoppingDis;
   
   public int authority;
   public double currentPower;
   public double targetPower;
   public double setPointVelocity;
   public double actualSpeed;
   public String nextStop;
   
   public Train(int identificationNumber)
      {
         id = identificationNumber;
         beacon = new BeaconInfo();
         beacon.setSpeedLimit(0);
         actualSpeed = 0;
         setPointVelocity = 0;
         automatic = true;
         currentPower = 0;
         targetPower = 0;
         numPassengers = 0;
         distance = 0;
         position = 0.0;
         acceleration = 0;
         authority = 0;
         lights = false;
      }
   public void setPos(double po)
      {
         position = po;
      }
   public double getPos()
      {
         return position;
      }
   
   
   public void setDistance(int di)
      {
         distance = di;
      }
   public int getDistance()
      {
         return distance;
      }


   public int getID()                                 //Returns this train's ID
      {
         return id;
      }
      
   public double getPower()
      {
         return currentPower;
      } 
   
   
   public boolean getLights()
      {
         return lights;
      }
   public void setLights(boolean newLights)
      {
         lights = newLights;
      }
   
   
   public BeaconInfo getBeacon()                      //Returns this train's current BeaconInfo object
      {
         return beacon;
      }
      
   public void updateBeacon(BeaconInfo newBeacon)     //updates this train's current BeaconInfo object
      {
         beacon = newBeacon;
      }
      
   public double getSPV()              //returns the value of the set point velocity
      {
         return setPointVelocity;
      }
   
   public void setSPV(double setPoint)       //sets the set Point Velocity and moves the train into Manual Mode
      {
         automatic = false;
         setPointVelocity = setPoint;
         System.out.println("Set point velocity changed to " + setPointVelocity);
      }
   
   public void setAcceleration(double acc)
      {
         acceleration = acc;
      }
   
   public double getAcceleration()
      {
         return acceleration;
      }
   
   
   public void setSpeed(double speed)     //sets the actual speed of this train
      {
         actualSpeed = speed;
      }
      
   public double getSpeed()
      {
         return actualSpeed;
      }  
      
   public double getSpeedLimit()
      {
         return beacon.getSpeedLimit();
      }
   
   public void setSpeedLimit(double newsl)
      {
         beacon.setSpeedLimit(newsl);
      }   

   public boolean isAutomatic()
      {
         return automatic;
      }
      
   public void setPower(double newPower)
      {
         currentPower = newPower;
      }
   
   public void setTargetPower(double newPow)
      {
         targetPower = newPow;
      }
   public double getTargetPower()
      {
         return targetPower;
      }   
  

   public void setAuthority(int newAuth)
      {
         authority = newAuth;
      }
      
   public int getAuthority()
      {
         return authority;
      }   
      
   public void setPassengers(int num)
      {
         numPassengers = num;
      }
   public int getPassengers()
      {
         return numPassengers;
      }
   public void setDoors(int i)
      {
         if (i == 0)
            {
               leftdoor = false;
               rightdoor = false;
            }
         else if (i == 1)
            {
               leftdoor = true;
               rightdoor = false;
            }
         else if (i == 2)
            {
               leftdoor = false;
               rightdoor = true;
            }
         else if (i == 3)
            {
               leftdoor = true;
               rightdoor = true;
            }
         //System.out.println("Train Controller: " + id + " : LeftDoor = " + leftdoor + ", RightDoor = " + rightdoor);
         MessageLibrary.sendMessage("localhost",8007,"Train Controller: " + id + " : Left Door = " + leftdoor + ", Right Door = " + rightdoor);
     }
     public void setServiceBrake(boolean brake)
      {
         ServiceBrake = brake;
         //if(brake = true)
            //MessageLibrary.sendMessage("localhost",8007,"Train Controller: " + id + " : Brakes = Service");
         
      }
     
     public void setBrakes(boolean brake)
      {
         setServiceBrake(brake);
         setEmergencyBrake(brake);
      
         /*if(brake == true)
            {
               
               MessageLibrary.sendMessage("localhost",8007,"Train Controller: " + id + " : Brakes = Service");
               MessageLibrary.sendMessage("localhost",8007,"Train Controller: " + id + " : Brakes = Emergency");
               System.out.println("Train Controller: " + id + " : Brakes = Service");
               System.out.println("Train Controller: " + id + " : Brakes = Emergency");
            }
         else
            {
               /*MessageLibrary.sendMessage("localhost",8007,"Train Controller: " + id + " : Brakes = None");
               System.out.println("Train Controller: " + id + " : Brakes = None");
            }*/
      }   
      
     public boolean getEmergencyBrake()
      {
         return EmergencyBrake;
      }
      
     public void setEmergencyBrake(boolean brake)
      {
         EmergencyBrake = brake;
         //if(brake = true)
            //MessageLibrary.sendMessage("localhost",8007,"Train Controller: " + id + " : Brakes = Emerency");
            
      }   
      
     public boolean getServiceBrake()
      {
         return ServiceBrake;
      }


    public void calculateStopping()
      {
         stoppingDis = Math.pow((((double)this.getSpeed())*.27777), 2)/(2.4);
      }
   public double getStopping()   
      {
         return stoppingDis;
      }
}

