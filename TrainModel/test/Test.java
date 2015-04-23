
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
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

public class Test {
    final private static int MYLISTENSOCKET = 8005;
    private static String[] messages;
    private static int i=0;
    public static void main(String args[]) {
        MessageLibrary.sendMessage("localhost",8007,"Track Model : 1 : create, Gradient=0, Beacon String=L_Harbor_60_200, Authority=5, Speed Limit=5, Length=500, Tunnel=true");
        //MessageLibrary.sendMessage("localhost",8007,"CTC : 0 : set, TimeModifier = 11.0");
        }
}
