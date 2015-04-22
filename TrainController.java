//import java.util.ArrayList;
import java.io.*;
import java.util.*;
import java.net.*;
import java.lang.*;

//passenger = 88.9 kg

public class TrainController {
   public String nextCommand;
   public final double trainWeight = 37103.9;     //weight of train in kg

   public ArrayList<Train> trainList;
   private VitalController vc;
   public TrainControllerGUI gui;
   public double tm;
   
   public double maxPower = 0;
   private double uk = 0;
   private double uk_prev = 0;
   private double ek = 0;
   private double ek_prev = 0;
   private double maxEnginePower = 120; //in kiloWatts
   

   public static int webSocketNumber = 8008;
	public static int socketListenerNumber = 8009;
   public static int tmlSocketNumber = 8007;
   
   private static ArrayList<String> messageList = new ArrayList<String>();


   public TrainController()
      {
         System.out.println("Train Controller established");
         trainList = new ArrayList<Train>();
         vc = new VitalController();
         gui = new TrainControllerGUI();
         tm = 1;
      }
   public void printtrainList()
      {
         System.out.print("[");
         for (int i = 0; i < trainList.size(); i++)
            {
               System.out.print(trainList.get(i).getID() + " ");
                     
            }
         System.out.println("]");
      }


   public Train getTrain(int id)             //returns a train object with the given id
      {
         Train temp = null;
         for (int i = 0; i < trainList.size(); i++)
            {
               if (trainList.get(i).getID() == id)
                     temp = trainList.get(i);
            }
          return temp;
         
      }
         
   public void setStopDis(int id, double stopDis)
      {
         Train temp = this.getTrain(id);
         temp.calculateStopping();
         //temp.getBeacon().setToStation((int)stopDis);
      }
   public void setPosition(int id, double pos)
      {
         Train temp = this.getTrain(id);
         temp.setPos(pos);
         System.out.println("Distance until Station = " + temp.getAuthority() + " + " + temp.getPos() + " = " +(temp.getAuthority() - temp.getPos()));
         if(temp.getAuthority() - temp.getPos() <= temp.getStopping())
            regulateSpeed(id, temp.getSpeed());
         else
            gui.update(temp);
      }
   
   public void setAuth(int id, int auth)
      {
         Train temp = this.getTrain(id);
         temp.setAuthority(auth);
         this.regulateSpeed(id, temp.getSpeed());
         //gui.update(temp);
      }  
      
   public void createTrain(int id)     //instantiates a new train object with the given id and places it in the list
      {
         Train train = new Train(id);
         trainList.add(train);
         //gui = new TrainControllerGUI(id); 
         System.out.println("Train " + id + " created");
         printtrainList();
         if(trainList.size() == 1)
            gui.update(train);
         
      }
      
   public void deleteTrain(int id)
      {
         Train toDelete = this.getTrain(id);
         trainList.remove(toDelete);
      }   
   private void regulateSpeed(int id, double actualSpeed)
      {
         //System.out.println("INSIDE REGULATESPEED");
         double power = 0;
         Train temp = getTrain(id);
         temp.setSpeed(actualSpeed*3.6);
         //recalculate stopping distance for new speed
         
         if(temp.getAcceleration() == 0 && temp.getSpeed() == 0 && temp.getAuthority() >0 && temp.getSpeedLimit() > 0)
            {
               System.out.println("Train is idle");
               //train is idle. We need to accelerate
               if(temp.getServiceBrake() == true)
                  {
                     temp.setBrakes(false);
                     MessageLibrary.sendMessage("localhost",8007,"Train Controller: " + id + " : set,  Brakes = none");
                  }
               temp.setAcceleration(0.5);
               
               temp.calculateStopping();
               System.out.println("Train stopping distance = " +  temp.getStopping());
               
               //compute power for max speed and send to Train Model
               power = ComputePower(temp);
               if(power != temp.getTargetPower())
               {
                  temp.setTargetPower(power);
                  MessageLibrary.sendMessage("localhost",8007,"Train Controller: " + id + " : set, Power = " + power);
               }
            }
         else if (temp.getSpeed() == 0 && temp.getAuthority() >0 && temp.getPos() - temp.getAuthority() == 0) //stopped at a station
            {
               this.dwell(temp);
            }  
         else if (temp.getAcceleration() >= 0 && temp.getSpeed() != 0)   //train is accelerating
            {
               ////System.out.println("Train is accelerating");
               if (temp.getAuthority() - temp.getPos() <= temp.getStopping()) //train is accelerating and at the stopping distance
                  {
                     
                       
                       temp.setAcceleration(-1.2);
                       
                       //set service brake and send message to Train Model
                       if(temp.getServiceBrake() == false)
                       {
                           temp.setServiceBrake(true);
                           System.out.println("Train is decelerating");
                           MessageLibrary.sendMessage("localhost",8007,"Train Controller: " + id + ": set, Power = " + 0);
                           MessageLibrary.sendMessage("localhost",8007,"Train Controller: " + id + ": set, Brakes = service");
                        }
                  }
               else if (temp.getSpeed() == temp.getSpeedLimit())   //train is at speed limit
                     {
                        System.out.println("1");
                        temp.setAcceleration(0);
                        temp.calculateStopping();
                        //send message to Train Model to decrease
                        if(temp.getTargetPower() != 0)
                           MessageLibrary.sendMessage("localhost",8007,"Train Controller: " + id + ": set, Power = " + 0);
                        
                     }
                else    //train is accelerating and not at the stopping distance or going as fast as the speed limit
                  {
                     System.out.println("Train should continue accelerating");
                     
                     //reupdate the Train MOdel with power ---> might not be necessary
                     temp.calculateStopping();
                     System.out.println("Train Controller:" + id + ": set, Power = " + temp.getPower());
                     if (temp.getTargetPower() != ComputePower(temp))
                        {
                           MessageLibrary.sendMessage("localhost",8007,"Train Controller: " + id + " : set, Power = " + ComputePower(temp));
                           temp.setTargetPower(ComputePower(temp));
                        }
                  
                  }     
                     
            }
         gui.update(temp);
       }
       
     
    public void setPassengers(int id, int numPassengers)
      { 
         this.getTrain(id).setPassengers(numPassengers);
         System.out.println("Train " + id + "passengers changed to " + numPassengers);
      }
       

   public double ComputePower(Train temp){
		if(temp.getServiceBrake() == true || temp.getEmergencyBrake() == true) 
         return 0;
		ek = temp.getSpeedLimit() - temp.getSpeed();
		uk = uk_prev + 0.005 * (ek + ek_prev);
		maxPower = (0.5*(ek) + 0.8*(uk))*10000;
		System.out.println("Target Power: " + maxPower);
		if(maxPower > maxEnginePower){
			uk = uk_prev;
			maxPower = (0.5*(ek) + 0.8*(uk))*10000;
		}
		//System.out.println("ek: " + ek + " uk: " + uk);
		ek_prev = ek;
		uk_prev = uk;
      
      double power2 = vc.ComputePower(temp.getPassengers(), temp);
      //System.out.println("OtherPower = " + power2);
      
      gui.updatePower(maxPower);
		return maxPower;
	}   
      
     
            
     
   public void authority(int i, String string)
      {  
         int auth = findInt("Length", string);
         this.setAuth(i, auth);



      }   
      public void decodeString(String string)
      {
       int idBegins = 10, idEnds = 11, id;
       string = string.replaceAll(" ", "");
       id = findID(string);
       if(getTrain(id) == null && !(string.contains("Create")))
         createTrain(id);
       
         if (string.contains("CTC"))      //Any messages received directly from the CTC concern the time multiplier
            {
               System.out.println("Changing Time Multiplier...");
               this.timeMult(string);
               
            }
         if (string.contains("Create"))
            {
               this.createTrain(id);    //jump to create method
            }
         if (string.contains("Delete"))
            {
               this.deleteTrain(id);    //jump to delete method
            }
         if (string.contains("Length"))
            {
               //this.authority(id, string);
               //this.length(id, string); 
            }//jump to length method

         if (string.contains("Authority"))
            {
               //this.authority(id, string);    //jump to authority method
            }
            //System.out.println("hello");
         if (string.contains("SpeedLimit"))
            {
               this.speedLimit(id, string);    //jump to speed limit method
            }
         if (string.contains("Lights"))
            {
               //this.lights(id, string);
            }//jump to lights method
         if (string.contains("BeaconString"))
            {
               this.getBeaconString(id, string);
            }
         if (string.contains("Passengers"))
            {
               this.passengers(id, string);  //jump to passenger method
            }
            
         if (string.contains("Speed="))
            {
               this.getSpeed(id, string);
               //this.//jump to speed method
      
            }
         if (string.contains("Position"))
            {
               this.getPosition(id, string);
            }
      
      }
      
      public int findID(String string)
      {
         //System.out.println(string);
         //int index = string.indexOf(':');
         int index = string.indexOf(':');
         String toAnalyze = string.substring(index+1);
         //toAnalyze = string.substring(index+1);
         //System.out.println("Concatenated at first colon = " + toAnalyze);
         int Endindex = toAnalyze.indexOf(":");
         toAnalyze = toAnalyze.substring(0, Endindex);
         //System.out.println("Concatenated between both colons = " + toAnalyze);
         int id = Integer.parseInt(toAnalyze);
         return id; 
      }
      
      
      
      public void timeMult(String string)
      {
         tm = findDouble("TimeModifier", string);
         System.out.println("The time multiplier has been set to " + tm);
         
       
      }
      public int findInt(String keyword, String string)
      {
         int index = string.indexOf(keyword);
         int num = 1;
        // System.out.println(string);
         String toAnalyze = string.substring(index);
         //System.out.println(toAnalyze);
         index = toAnalyze.indexOf("=");
         toAnalyze = toAnalyze.substring(index+1);
         if (toAnalyze.indexOf(',') != -1)
            toAnalyze = toAnalyze.substring(0, toAnalyze.indexOf(','));
         //System.out.println(toAnalyze);
         num = Integer.parseInt(toAnalyze);  
         return num;
      } 
      
      
      public double findDouble(String keyword, String string)
      {
         int index = string.indexOf(keyword);
         double num = 1;
         System.out.println(string);
         String toAnalyze = string.substring(index);
         System.out.println(toAnalyze);
         index = toAnalyze.indexOf("=");
         toAnalyze = toAnalyze.substring(index+1);
         if (toAnalyze.indexOf(',') != -1)
            toAnalyze = toAnalyze.substring(0, toAnalyze.indexOf(','));
         System.out.println(toAnalyze);
         num = Double.parseDouble(toAnalyze);  
         return num;
      }
      
      public void getPosition(int i, String string)
      {
         double pos = findDouble("Position", string);
         this.setPosition(i, pos);
         
      }
         
         
      public void getBeaconString(int i, String string)
      {
         BeaconInfo tempBeac = new BeaconInfo();
         Train temp = this.getTrain(i);
         
         
         String toAnalyze = findString("BeaconString", string);   //Contains L_Harbor Ferry_60
         if (toAnalyze.length() == 0)
            this.setAuth(i, 10000000);
         else
            {
               System.out.println(toAnalyze.charAt(0));
               if(toAnalyze.charAt(0) == 'L')
                  tempBeac.setSideOfNextStop(false);
               else
                  tempBeac.setSideOfNextStop(true);
         
               String StationName = toAnalyze.substring(2);
               String dwellTime = StationName.substring(StationName.indexOf('_')+1);
               String authority = dwellTime.substring(dwellTime.indexOf('_')+1);
               int auth = Integer.parseInt(authority);
               this.setAuth(i, auth);
               System.out.println(auth);
               dwellTime=dwellTime.substring(0, dwellTime.indexOf('_'));
               
               tempBeac.setDwellTime(Integer.parseInt(dwellTime));
               System.out.println(tempBeac.getDwellTime());
         
               StationName = StationName.substring(0, StationName.indexOf('_'));
               System.out.println(StationName);
               tempBeac.setStationName(StationName);
               
               temp.getBeacon().update(tempBeac);
            }  
             
  
      }         
            
         
       /*  if (string.contains("SideOfStation"))  //the messsage contains information about the side that the next Station is on
            {
               if (findInt("SideOfStation", string) == 0)
                  {
                     tempBeac.setSideOfNextStop(false);
                  }
               else
                     tempBeac.setSideOfNextStop(true);
            }
         if (string.contains("StationName"))    //the message contains information about the next Station's Name
            {
               tempBeac.setStationName(findString("StationName", string));      
            }
         if (string.contains("DwellTime"))      //the message contains information on Dwell Times
            {
               tempBeac.setDwellTime(findInt("DwellTime", string));
            }
           
         if (string.contains("Tunnel"))         //the message contains information on Tunnels
            {
               if (findInt("Tunnel", string) == 0)
                  {
                     tempBeac.setTunnel(false);
                  }
               else
                     tempBeac.setTunnel(true);
            }
         Train temp = this.getTrain(i);
         temp.getBeacon().update(tempBeac);   
            //jump to method to find tunnel
      
      }*/   
         
      public String findString(String keyword, String string)
      {
         //find a string like "StationName=HighlandPark"
         int index = string.indexOf(keyword);
         //String temp = "";
         System.out.println(string);
         String toAnalyze = string.substring(index);
         System.out.println(toAnalyze);
         index = toAnalyze.indexOf("=");
         toAnalyze = toAnalyze.substring(index+1);
         if (toAnalyze.indexOf(',') != -1)
            toAnalyze = toAnalyze.substring(0, toAnalyze.indexOf(','));
         System.out.println(toAnalyze);
           
         return toAnalyze;
      }   
         
         
       
      
      public void getStopData(int i, String string)
         {
    
            
                         
         
      }
                  
      
      public void getSpeed(int i, String string)
      {
         double speed = findDouble("Speed", string);
         this.regulateSpeed(i, speed);
      }
      
      
      public void passengers(int i, String string)
      {
         int numPassengers = findInt("Passengers", string);
            this.setPassengers(i, numPassengers);
      }
      
  
   public void updateBeacon(int id, BeaconInfo newBeacon)
      {
         Train temp = getTrain(id);
         if(temp != null)
            temp.getBeacon().update(newBeacon);
            
      }  
   
                       
   public static void main(String args[]) 
      {
         TrainController tc = new TrainController();
         
         //SocketListener listener = new SocketListener();
		   //listener.start();
         
         MessageLibrary.sendMessage("localhost", tmlSocketNumber, "Train Controller : 0 : set, Power = 500000");     //test
     
      }       
      
     
      public void speedLimit(int i, String string)  
      {
         int limit = findInt("SpeedLimit", string);
         this.updateSpeedLimit(i, limit);
         System.out.println("Speed Limit changed to " + limit); 
         
      }
      
      public void updateSpeedLimit(int id, int limit)
      {
         Train temp = this.getTrain(id);
         //System.out.println("Hello: " + temp.getPos());
         temp.setSpeedLimit(limit*3.6);
         this.regulateSpeed(id,temp.getSpeed());
            //gui.update(temp);
      }
            
      
      public void dwell(Train temp)
      {
         int dwellTime = temp.getBeacon().getDwellTime();
         int id = temp.getID();
         System.out.println("Train has stopped. Dwelling for " + dwellTime + " Seconds");
         temp.setAcceleration(0);
         if (temp.getBeacon().getSideOfNextStop() == true)  //station is on the right
            {
               temp.setDoors(2);
               MessageLibrary.sendMessage("localhost",8007,"Train Controller: " + id + " : set, Right Doors = true");
               System.out.println("Train Controller: " + id + " : set, RightDoors = true");
            }
         if (temp.getBeacon().getSideOfNextStop() == false)  //station is on the left
            {
               System.out.println("Train Controller: " + id + " : set, LeftDoors = true");
               temp.setDoors(1);   
               MessageLibrary.sendMessage("localhost",8007,"Train Controller: " + id + " : set, Left Doors = true");
               System.out.println("Train Controller: " + id + " : set, LeftDoors = true");
            }
            
         //wait for specified dwell time
         System.out.println("Please wait while we dwell...");
         try{
               Thread.sleep((int)((dwellTime*1000)/tm));  
            } catch (InterruptedException ie) {
                  System.out.println(ie);
            } 
          
          
         //close Doors and send message to Train Model 
         temp.setDoors(0);    
         MessageLibrary.sendMessage("localhost",8007,"Train Controller: " + id + " :set, Left Doors = false"); 
         MessageLibrary.sendMessage("localhost",8007,"Train Controller: " + id + " :set, Right Doors = false");
         
         System.out.println("Train Controller: " + id + " : Left Doors = false");
         System.out.println("Train Controller: " + id + " :set, Right Doors = false");
    
         //set Authority to 0 
         temp.setAuthority(0);
         System.out.println("The authority of the train has been reset to 0");
         
         //set Position to 0
         temp.setPos(0); 
         System.out.println("The position of the train has been reset to 0");
         
         //set Power to 0 and send message to Train Model
         temp.setPower(0);
         MessageLibrary.sendMessage("localhost",8007,"Train Controller: " + id + " : set, Power = 0");
         System.out.println("Train Controller: " + id + " : Power = 0");
         
         
         //release brakes and send message to Train Model 
         temp.setBrakes(false);
         MessageLibrary.sendMessage("localhost",8007,"Train Controller: " + id + " : set,  Brakes = none");
         System.out.println("Train Controller: " + id + " : Brakes = none");
         
         
         System.out.println("Awaiting for next message...");
      }
            

        
}
