public class VitalController
{

   public final double trainWeight = 81800;     //weight of train in pounds
                                                //37,103,825 g = mass of train

   public VitalController()
      {
         System.out.println("Vital Controller Established");
            //P=Fv
            //F=ma
            //P=mav
            //we know m(constant) and v(commanded speed or set point velocity) so to find a we need to 
            //know the acceleration needed to reach that velocity. This is most likely the maximum acceleration the train can reach
            //maximum acceleration = 0.5m/s^2 ===> 1.64ft/s^2 ===> 1.1184681460272 mph/s
      }
   
   
   public double ComputePower(int numPassengers,Train temp)
      {
         double power;
         double mass = 37103.9 + numPassengers*88;
         double acceleration = temp.getAcceleration();
         double velocity = temp.getSPV();
         
         power = mass*acceleration*velocity*0.277;
      
         //compute power
         return power;
      }

  
}