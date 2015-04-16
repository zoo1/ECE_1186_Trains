/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author zach
 */
import java.io.*;
import java.util.*;
import java.net.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class WebServer {
    /* DEFINE THESE CONSTANTS */

    final private static int WEBSOCKET = 8006; // localhost:YOUR_SOCKET/path
    final private static int MYLISTENSOCKET = 8007; // The socket that your component will be listening on
    final private static int TRACKMODEL = 8005; //Track model socket
    final private static int TRAINCONTROLLER = 8009; //Train Controller Socket
    private static double timemodifier = 1;
    public static String staticDir = "/static"; // The directory out of which you can serve static files.
    public static String handlerDir = "/handlers"; // The parent directory that has subdirectories with custom handlers. Going to these URLs from a browser will fire up the ClientInteractionHandler in Java.
    private static volatile HashMap<Integer, Train> trains;

    public static void main(String args[]) {
        trains = new HashMap<Integer, Train>();
        startWebServer();
        startSocketListener();
    }
// Starts the webserver, if you want to use it.

    public static void startWebServer() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(WEBSOCKET), 0);
            server.createContext(staticDir, new StaticResourceHandler());
            server.createContext(handlerDir, new ClientInteractionHandler());
            server.setExecutor(null);
            server.start();
        } catch (Exception e) {
        }
    }
// Starts the socket listener. This listens for incoming connections from other components and can handle their requests.

    public static void startSocketListener() {
        SocketListener listener = new SocketListener();
        listener.start();
    }
// Serves static resources out of the /static/ directory.

    static class StaticResourceHandler implements HttpHandler {

        public void handle(HttpExchange exchange) throws IOException {
            MessageLibrary.sendHttpResponse(exchange, MessageLibrary.readFile(exchange.getRequestURI().getPath().substring(1)));
        }
    }
// Handlers for any requests sent to the /handle/ URL.

    static class ClientInteractionHandler implements HttpHandler {

        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equals("POST")) {
                int TrainUID = Integer.valueOf((exchange.getRequestURI().getPath().substring(handlerDir.length() + 1)).replaceAll("\\s+", ""));
                String body = getBody(exchange);
                String[] splitbody = body.split("&");
                if (splitbody[0].equals("Component=Fails")) //Update failure modes
                {
                    boolean engine = false, signal = false, brake = false;
                    for (int i = 1; i < splitbody.length; i++) {
                        String[] line = splitbody[i].split("=");
                        if (line.length != 2) {
                            MessageLibrary.sendHttpResponse(exchange, "Request Invalid");
                        }
                        if (line[0].equals("TEF")) {
                            engine = true;
                        } else if (line[0].equals("SPF")) {
                            signal = true;
                        } else if (line[0].equals("BF")) {
                            brake = true;
                        }
                    }
                    trains.get(TrainUID).setBreakFail(brake);
                    trains.get(TrainUID).setEngFail(engine);
                    trains.get(TrainUID).setSigFail(signal);
                } else if (splitbody[0].equals("Component=Block")) //Update Block
                {
                    boolean yard=false, tunnel=false;
                    double speedLimit=-9999,gradient=-9999;
                    String beaconString=null;
                    int authority=-1, length=-99999;
                    for(int i = 1; i < splitbody.length; i++) {
                        String[] line = splitbody[i].split("=");
                        System.out.println(line[0]);
                        if(line[0].contains("TMSS"))
                        {
                            speedLimit=Double.valueOf(line[1]);
                        }
                        else if(line[0].contains("TML"))
                        {
                            length=Integer.valueOf(line[1]);
                        }
                        else if(line[0].contains("TMA"))
                        {
                            authority=Integer.valueOf(line[1]);
                        }
                        else if(line[0].contains("TMG"))
                        {
                            gradient=Double.valueOf(line[1]);
                        }
                        else if(line[0].contains("TMBS"))
                        {
                            beaconString=line[1];
                        }
                        else if(line[0].equals("T"))
                        {
                            tunnel=true;
                        }
                        else if(line[0].equals("Y"))
                        {
                            yard=true;
                        }
                    }
                    if(beaconString!=null)
                    {
                    Block b = Block.createblock(gradient, beaconString, authority, speedLimit, length, tunnel, yard); //Create Block and append to train
                    if(b!=null)
                    {
                        trains.get(TrainUID).updateBlock(b);
                    }
                    else
                        MessageLibrary.sendHttpResponse(exchange, "Request Invalid");
                    }
                    else
                        MessageLibrary.sendHttpResponse(exchange, "Request Invalid");
                } else if (splitbody[0].equals("Component=Passengers")) //Update Passengers
                {
                    String[] line = splitbody[1].split("=");
                    if (line[0].equals("TMAP")) {
                        trains.get(TrainUID).addpassengers(Integer.valueOf(line[1]));
                    }
                } else if (splitbody[0].equals("Component=Control")) {
                    boolean lights = false, right = false, left = false;
                    for (int i = 1; i < splitbody.length; i++) {
                        String[] line = splitbody[i].split("=");
                        if (line.length != 2) {
                            MessageLibrary.sendHttpResponse(exchange, "Request Invalid");
                        }
                        if (line[0].equals("TCP")) {
                            trains.get(TrainUID).UpdatePower(Double.valueOf(line[1]));
                        } else if (line[0].equals("BRAKES")) {
                            trains.get(TrainUID).updateBreak(Integer.valueOf(line[1]));
                        } else if (line[0].equals("L")) {
                            lights = true;
                        } else if (line[0].equals("LD")) {
                            left = true;
                        } else if (line[0].equals("RD")) {
                            right = true;
                        }
                    }
                    trains.get(TrainUID).leftdoor(left);
                    trains.get(TrainUID).rightdoor(right);
                    trains.get(TrainUID).Lights(lights);
                }
                MessageLibrary.sendHttpResponse(exchange, "Update Complete");
                return;
            } else if (exchange.getRequestMethod().equals("GET")) {
                if ((exchange.getRequestURI().getPath().substring(handlerDir.length() + 1)).equals("Trains")) //Request is for a list of Trains
                {
                    String trainlist = "";
                    boolean first = true;
                    for (Integer key : trains.keySet()) {
                        if (first) {
                            trainlist = +key + "";
                            first = false;
                        } else {
                            trainlist = trainlist + " " + key;
                        }
                    }
                    MessageLibrary.sendHttpResponse(exchange, trainlist);
                    return;
                } else //Request is for train data
                {
                    int TrainUID = Integer.valueOf((exchange.getRequestURI().getPath().substring(handlerDir.length() + 1)).replaceAll("\\s+", ""));
                    if (trains.containsKey(TrainUID)) {
                        MessageLibrary.sendHttpResponse(exchange, "" + String.format("%.5f", trains.get(TrainUID).getVelocity()) + " " + trains.get(TrainUID).getPassengers() + " " + trains.get(TrainUID).lightsOn() + " " + trains.get(TrainUID).isLeftdoors() + " " + trains.get(TrainUID).isRightdoors() + " " + trains.get(TrainUID).isEngFail() + " " + trains.get(TrainUID).isSigFail() + " " + trains.get(TrainUID).isBreakFail() + " " + trains.get(TrainUID).getBrakestatus());
                    } else {
                        MessageLibrary.sendHttpResponse(exchange, "" + "0000");
                    }
                }
                return;
            } else {
                MessageLibrary.sendHttpResponse(exchange, "The request form is not supported");
                return;
            }
        }
    }

    public static String getBody(HttpExchange request) throws IOException {

        String body = null;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            InputStream inputStream = request.getRequestBody();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }

        body = stringBuilder.toString();
        return body;
    }

    static class SocketListener extends Thread {

        public void run() {
            while (true) {
                try (
                        ServerSocket serverSocket = new ServerSocket(MYLISTENSOCKET, 0, InetAddress.getByName(null));
                        Socket clientSocket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);) {
                    out.println("Connection accepted.");
                    StringBuilder completeInput = new StringBuilder("");
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        completeInput.append(inputLine);
                    }
                    evaluateString(completeInput.toString());
// Message input complete. Do something with the message here.
                } catch (IOException e) {
                    System.out.println("Error: Port listening exception on port " + MYLISTENSOCKET + ".");
                    System.out.println(e.getMessage());
                }
            }
        }

        private void evaluateString(String input) {
            try {
                String[] command = input.split(":");
                if (command.length < 2) //string is not formatted acording to standard
                {
                    return;
                }
                if (command[0].contains("Train Controller")) //Command sent from Train Controller
                {
                    int UID = Integer.valueOf(command[1].replaceAll("\\s+", ""));
                    if (command[2].split(",")[0].contains("get")) {

                    } else if (command[2].split(",")[0].contains("set")) {
                        String[] parameters = command[2].split(",");
                        String[] sides = parameters[1].split("=");
                        if (sides[0].contains("Brakes")) // Update Brakes on specific Train
                        {
                            int brakes = 0;
                            if (sides[1].contains("None")) {
                                brakes = 0;
                            } else if (sides[1].contains("Service")) {
                                brakes = 1;
                            } else if (sides[1].contains("Emergency")) {
                                brakes = 2;
                            } else {
                                throw new IllegalArgumentException("all required parameters were not set");
                            }
                            trains.get(UID).updateBreak(brakes);
                        } else if (sides[0].contains("Left Doors")) {
                            boolean leftdoors = sides[1].contains("true");
                            trains.get(UID).leftdoor(leftdoors);
                        } else if (sides[0].contains("Right Doors")) {
                            boolean rightdoors = sides[1].contains("true");
                            trains.get(UID).rightdoor(rightdoors);
                        } else if (sides[0].contains("Power")) {
                            double power = Double.valueOf(sides[1].replaceAll("\\s+", ""));
                            trains.get(UID).UpdatePower(power);
                        } else if (sides[0].contains("Train Lights")) {
                            boolean lights=false;
                            if(sides[1].contains("On"))
                                lights=true;
                            trains.get(UID).Lights(lights);
                        } else {
                            throw new IllegalArgumentException("all required parameters were not set");
                        }
                    } else {
                        throw new IllegalArgumentException("all required parameters were not set");
                    }
                } else if (command[0].contains("Track Model")) //Command sent from Track Model
                {
                    int UID = Integer.valueOf(command[1].replaceAll("\\s+", ""));
                    if (command[2].split(",")[0].contains("create")) //Create New Train
                    {
                        double gradient = 9999999;
                        String beaconData = null;
                        int authority = -1;
                        int speedlimit = -1;
                        int length = -1;
                        boolean tunnel = false;
                        boolean tunnelisset = false;
                        String[] parameters = command[2].split(",");
                        for (int i = 1; i < parameters.length; i++) {
                            String[] sides = parameters[i].split("=");
                            if (sides[0].contains("Gradient")) {
                                gradient = Double.valueOf(sides[1].replaceAll("\\s+", ""));
                            } else if (sides[0].contains("Beacon String")) {
                                beaconData = sides[1];
                            } else if (sides[0].contains("Authority")) {
                                authority = Integer.valueOf(sides[1].replaceAll("\\s+", ""));
                            } else if (sides[0].contains("Speed Limit")) {
                                speedlimit = Integer.valueOf(sides[1].replaceAll("\\s+", ""));
                            } else if (sides[0].contains("Length")) {
                                length = Integer.valueOf(sides[1].replaceAll("\\s+", ""));
                            } else if (sides[0].contains("Tunnel")) {
                                tunnel = sides[1].contains("true");
                                tunnelisset = true;
                            }
                        }
                        if (gradient != 9999999 && beaconData != null && authority != -1 && speedlimit != -1 && length != -1 && tunnelisset) {
                            trains.put(UID, new Train(String.valueOf(UID), Block.createblock(gradient, beaconData, authority, speedlimit, length, tunnel), timemodifier)); //Create Train
                            MessageLibrary.sendMessage("localhost", TRAINCONTROLLER, "Train Model :" + command[1] + ":" + command[2]); //Create Train Controller
                            trains.get(UID).start(); //Start Train
                        } else {
                            throw new IllegalArgumentException("all required parameters were not set");
                        }
                    } else if (command[2].split(",")[0].contains("set")) {
                        String[] parameters = command[2].split(",");
                        String[] sides = parameters[1].split("=");
                        if (sides[0].contains("Passengers")) // Add passengers to a specific Train
                        {
                            int passengers = Integer.valueOf(sides[1].replaceAll("\\s+", ""));
                            trains.get(UID).addpassengers(passengers);
                        } else if (sides[0].contains("Gradient") || sides[0].contains("Beacon String") || sides[0].contains("Authority") || sides[0].contains("Speed Limit") || sides[0].contains("Length") || sides[0].contains("Tunnel") || sides[0].contains("Yard")) //Add new block to train
                        {
                            double gradient = 9999999;
                            String beaconData = null;
                            int authority = -1;
                            int speedlimit = -1;
                            int length = -1;
                            boolean tunnel = false;
                            boolean yard = false;
                            boolean tunnelisset = false;
                            boolean yardset = false;
                            for (int i = 1; i < parameters.length; i++) {
                                sides = parameters[i].split("=");
                                if (sides[0].contains("Gradient")) {
                                    gradient = Double.valueOf(sides[1].replaceAll("\\s+", ""));
                                } else if (sides[0].contains("Beacon String")) {
                                    beaconData = sides[1];
                                } else if (sides[0].contains("Authority")) {
                                    authority = Integer.valueOf(sides[1].replaceAll("\\s+", ""));
                                } else if (sides[0].contains("Speed Limit")) {
                                    speedlimit = Integer.valueOf(sides[1].replaceAll("\\s+", ""));
                                } else if (sides[0].contains("Length")) {
                                    length = Integer.valueOf(sides[1].replaceAll("\\s+", ""));
                                } else if (sides[0].contains("Tunnel")) {
                                    tunnel = sides[1].contains("true");
                                    tunnelisset = true;
                                } else if (sides[0].contains("Yard")) {
                                    yard = sides[1].contains("true");
                                    yardset = true;
                                }
                            }
                            if (gradient != 9999999 && beaconData != null && authority != -1 && speedlimit != -1 && length != -1 && tunnelisset && yardset) {
                                trains.get(UID).updateBlock(Block.createblock(gradient, beaconData, authority, speedlimit, length, tunnel, yard));
                                MessageLibrary.sendMessage("localhost", TRAINCONTROLLER, "Train Model :" + command[1] + ":" + command[2]);
                            } else {
                                throw new IllegalArgumentException("all required parameters were not set");
                            }
                        } else {
                            throw new IllegalArgumentException("all required parameters were not set");
                        }
                    } else if (command[2].split(",")[0].contains("get")) {

                    } else {
                        throw new IllegalArgumentException("all required parameters were not set");
                    }
                } else if (command[0].contains("CTC")) //Used to update timemodifier
                {
                    if (command[2].split(",")[0].contains("set")) {
                        String[] param = command[2].split(",")[1].split("=");
                        if (param[0].contains("TimeModifier")) {
                            timemodifier = Double.valueOf(param[1].replaceAll("\\s+", ""));
                            for (Integer key : trains.keySet()) {
                                trains.get(key).updateTime(timemodifier);
                            }
                        } else {
                            throw new IllegalArgumentException("all required parameters were not set");
                        }
                    } else {
                        throw new IllegalArgumentException("all required parameters were not set");
                    }
                } else if (command[0].contains("Train Model") && command[2].contains("delete")) //Used to delete a train
                {
                    int UID = Integer.valueOf(command[1].replaceAll("\\s+", ""));
                    trains.remove(UID);
                } else {
                    throw new IllegalArgumentException("all required parameters were not set");
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(input);
            }
        }
    }
}
