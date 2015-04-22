public class BeaconInfo
{
   //public double distanceToStation;//
   //public boolean stopAtStation;
   public boolean tunnel;
   public boolean sideOfNextStop;      //true = right, false = left
   public String stationName;//
   public double speedLimit;
   public int dwellTime;//


   public BeaconInfo()     //constructor
   {
      
   }
   
   
   ////TUNNEL////
   public boolean getTunnel()
   {
      return tunnel;
   }
   
   public void setTunnel(boolean newTun)
   {
      tunnel = newTun;
   }
   
   ////SPEED LIMIT//////
   public double getSpeedLimit()
   {
      return this.speedLimit;
   }
        
   public void setSpeedLimit(double newspeedl)
   {
      this.speedLimit = newspeedl;
   }
   
   
   ////DWELL TIME//////   
   public int getDwellTime()
   {
      return dwellTime;
   }
   
   public void setDwellTime(int newDwell)
   {
      dwellTime = newDwell;
   }
   
   
  /* public double getDistanceToStation()
      {
         
         return this.distanceToStation;
      }
   public void setDistanceToStation(int i)
      {
         distanceToStation = i; 
      }*/
  
   ////SIDE OF NEXT STOP///////   
   public boolean getSideOfNextStop()
   {
      return sideOfNextStop;
   }
   
   public void setSideOfNextStop(boolean newside) 
   {
      sideOfNextStop = newside;
   }
   
   
   ////STATION NAME///////
   public String getStationName()
   {
      return stationName;
   }  
   
   public void setStationName(String newStation)
   {
      stationName = newStation;
   } 



   ////UPDATE///////
   public void update(BeaconInfo newBeacon)
   {

      this.tunnel = newBeacon.tunnel;
      System.out.println("Tunnel has been set to " + tunnel);
      this.sideOfNextStop = newBeacon.sideOfNextStop;
      System.out.println("Side of Next Stop has been changed to " + sideOfNextStop);
      this.stationName = newBeacon.stationName;
      System.out.println("Next Station Name has been changed to " + stationName);
      this.dwellTime = newBeacon.dwellTime;
      System.out.println("Dwell Time has been changed to " + dwellTime);
   }
         
}