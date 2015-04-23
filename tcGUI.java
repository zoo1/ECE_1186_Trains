import java.awt.EventQueue;
import java.awt.BorderLayout;

import javax.swing.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.io.FileReader;
import java.util.*;

//import org.json.simple.*;
//import org.json.JSONObject;
import org.json.simple.parser.*;
//import org.json.simple.JSONArray;
//import org.json.simple.parser.JSONParser;


public class tcGUI {

	private static final Object[] String = null;
	public static Hashtable<String, JTextField> uiElements = new Hashtable<String, JTextField>();
	
	private JFrame frame;
	private JTextField textField;
	private JLabel lblSuggestedSpeed;
	/**
	 * @wbp.nonvisual location=279,11
	 */
	private JButton btnSubmit;
	private JTextField txtCtcspeed;
	private JPanel panel_1;
	private JPanel panel_2;
	private JPanel panel_3;
	private JTextField textField_1;
	private JLabel lblCommandedAuthority;
	private JPanel panel_4;
	private JPanel panel_6;
	private JLabel lblLine;
	private JTextField txtROrG;
	private JPanel panel_7;
	private JLabel lblWeatherCondition;
	private JTextField txtGood;
	private JPanel panel_8;
	private JLabel lblBrokenRail;
	private JTextField txtYesOrNo;
	private JPanel panel_9;
	private JLabel lblTrackOccupancy;
	private JTextField txtYn;
	private JPanel panel_10;
	private JLabel lblSwitchPositiion;
	private JTextField txtSwitchNumber;
	private JPanel panel_11;
	private JLabel lblLights;
	private JTextField txtROrG_1;
	private JTextField txtOpenclose;
	private JPanel panel_12;
	private JLabel lblRrCrossing;
	private JTextField txtYn_1;
	private JPanel panel_13;
	private JLabel lblCrossbar;
	private JTextField txtUpdown;
	private JPanel panel_14;
	private JLabel lblPassInformationTo;
	private JPanel panel_15;
	private JLabel lblUserInput;
	private final JPanel panel_16 = new JPanel();
	private JLabel lblKmhr;
	private JLabel lblTrackHeater;
	private JTextField textField_2;
	private JLabel label;
	private JPanel panel_5;
	private JButton btnLoadPlc;
	private JPanel panel_17;
	private JButton btnKeepSuggestedData;
	private JLabel lblBlockNumber;
	private JTextField txtInt;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				JSONParser parser = new JSONParser();
				try {
					tcGUI window = new tcGUI();
					window.frame.setVisible(true);
					SocketListener listener = new SocketListener();
					listener.start();
					
					//TrackController.sendSwitchPosition();
					//MessageLibrary.sendMessage(8003, "CTC : 0 : set, Path = [{0/80}, 1, 2, 3, {4/20}, 5, 6]");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public tcGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 613, 399);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Track Controller UI");
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.NORTH);
		
		panel_15 = new JPanel();
		panel.add(panel_15);
		
		lblUserInput = new JLabel("User Input:");
		panel_15.add(lblUserInput);
		Font font = lblUserInput.getFont();
		Font boldFont = new Font(font.getFontName(), font.BOLD, font.getSize());
		lblUserInput.setFont(boldFont);
		
		panel_7 = new JPanel();
		panel.add(panel_7);
		
		JLabel lblComAuth = new JLabel("Commanded Authority");
		lblComAuth.setHorizontalAlignment(SwingConstants.LEFT);
		panel_7.add(lblComAuth);
		
		txtGood = new JTextField();
		txtGood.setText("Int");
		txtGood.setHorizontalAlignment(SwingConstants.CENTER);
		txtGood.setColumns(3);
		panel_7.add(txtGood);
		
		panel_4 = new JPanel();
		panel.add(panel_4);
		
		JLabel lblCommandedSpeed = new JLabel("Commanded Speed");
		panel_4.add(lblCommandedSpeed);
		lblCommandedSpeed.setHorizontalAlignment(SwingConstants.LEFT);
		
		textField = new JTextField();
		textField.setText("Int");
		textField.setHorizontalAlignment(SwingConstants.CENTER);
		textField.setColumns(3);
		panel_4.add(textField);
		
		label = new JLabel("mph");
		panel_4.add(label);
		
		
		panel_2 = new JPanel();
		frame.getContentPane().add(panel_2, BorderLayout.CENTER);
		
		//lblTrackHeater = new JLabel("Heater Functioning");
		//panel_17.add(lblTrackHeater);
		
		//textField_2 =  new JTextField();
		//textField_2.setText("Y/N");
		//textField_2.setHorizontalAlignment(SwingConstants.CENTER);
		//textField_2.setColumns(3);
		//panel_17.add(textField_2);
		panel_2.add(panel_16);
		
		panel_14 = new JPanel();
		panel_16.add(panel_14);
		
		lblPassInformationTo = new JLabel("Pass Information to the CTC and the Train");
		panel_14.add(lblPassInformationTo);
		
		btnSubmit = new JButton("Submit");
		panel_14.add(btnSubmit);
		//What is the button supposed to do after pressed
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				String cmdSpeed = textField.getText();
				String cmdAuthority = txtGood.getText();
				
				//Send the user input to do some calculations
				TrackController sendGUIInfo = new TrackController();
				sendGUIInfo.checkSpeed(Double.parseDouble(cmdSpeed));
				sendGUIInfo.checkAuthority(Double.parseDouble(cmdAuthority));

				//Pass the relevant information to the CTC and the Train
			}
		});
		btnSubmit.setEnabled(true);
		
		panel_17 = new JPanel();
		panel_2.add(panel_17);
		
			
		panel_3 = new JPanel();
		panel_17.add(panel_3);
		
		lblCommandedAuthority = new JLabel("Suggested Authority");
		panel_3.add(lblCommandedAuthority);
		
		textField_1 = new JTextField();
		uiElements.put("authority", textField_1);
		textField_1.setEditable(false);
		textField_1.setColumns(2);
		panel_3.add(textField_1);
		
		panel_1 = new JPanel();
		panel_17.add(panel_1);
		
		lblSuggestedSpeed = new JLabel("Suggested Speed");
		panel_1.add(lblSuggestedSpeed);
		txtCtcspeed = new JTextField();
		uiElements.put("speed", txtCtcspeed);
		panel_1.add(txtCtcspeed);
		txtCtcspeed.setEditable(false);
		txtCtcspeed.setColumns(2);
		
		lblKmhr = new JLabel("mph");
		panel_1.add(lblKmhr);
		
		panel_10 = new JPanel();
		panel_2.add(panel_10);
		
		lblSwitchPositiion = new JLabel("Switch Position");
		panel_10.add(lblSwitchPositiion);
		
		txtSwitchNumber = new JTextField();
		txtSwitchNumber.setText("Blocks Connected");
		uiElements.put("switchnum", txtSwitchNumber);
		txtSwitchNumber.setHorizontalAlignment(SwingConstants.CENTER);
		txtSwitchNumber.setEditable(false);
		txtSwitchNumber.setColumns(10);
		panel_10.add(txtSwitchNumber);
		
		panel_6 = new JPanel();
		panel_2.add(panel_6);
		
		lblLine = new JLabel("Line");
		panel_6.add(lblLine);
		
		txtROrG = new JTextField();
		txtROrG.setText("R/G");
		uiElements.put("line", txtROrG);
		txtROrG.setEditable(false);
		txtROrG.setColumns(2);
		panel_6.add(txtROrG);
				
		panel_11 = new JPanel();
		panel_2.add(panel_11);
				
		lblLights = new JLabel("Lights");
		panel_11.add(lblLights);
				
		txtROrG_1 = new JTextField();
		uiElements.put("lights", txtROrG_1);
		uiElements.get("lights").setText("G");
		txtROrG_1.setHorizontalAlignment(SwingConstants.CENTER);
		txtROrG_1.setEditable(false);
		txtROrG_1.setColumns(2);
		panel_11.add(txtROrG_1);
		
		panel_9 = new JPanel();
		panel_2.add(panel_9);
		
		lblTrackOccupancy = new JLabel("Track Occupancy");
		panel_9.add(lblTrackOccupancy);
		
		txtYn = new JTextField();
		uiElements.put("occupancy", txtYn);
		txtYn.setText("Y/N");
		txtYn.setHorizontalAlignment(SwingConstants.CENTER);
		txtYn.setEditable(false);
		txtYn.setColumns(3);
		panel_9.add(txtYn);
		
		lblBlockNumber = new JLabel("Block Number");
		panel_9.add(lblBlockNumber);
		txtInt = new JTextField();
		uiElements.put("blocknum", txtInt);
		txtInt.setText("Int");
		panel_9.add(txtInt);
		txtInt.setColumns(3);
				
		panel_12 = new JPanel();
		panel_2.add(panel_12);
				
		lblRrCrossing = new JLabel("RR Crossing?");
		panel_12.add(lblRrCrossing);
				
		txtYn_1 = new JTextField();
		uiElements.put("crossing", txtYn_1);
		txtYn_1.setEditable(false);
		txtYn_1.setColumns(3);
		panel_12.add(txtYn_1);
				
		panel_13 = new JPanel();
		panel_12.add(panel_13);
				
		lblCrossbar = new JLabel("Crossbar");
		panel_13.add(lblCrossbar);
				
		txtUpdown = new JTextField();
		txtUpdown.setText("U/D");
		uiElements.put("barUD", txtUpdown);
		txtUpdown.setHorizontalAlignment(SwingConstants.CENTER);
		txtUpdown.setEditable(false);
		txtUpdown.setColumns(5);
		panel_13.add(txtUpdown);
		
		panel_5 = new JPanel();
		panel_2.add(panel_5);
		
		btnLoadPlc = new JButton("Load PLC");
		btnLoadPlc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			TrackController PLC = new TrackController();
			PLC.loadPLC();
			}
		});
		panel_5.add(btnLoadPlc);
	}

}
