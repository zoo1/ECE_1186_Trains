package src;

import java.awt.EventQueue;


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
					
					Object obj = parser.parse(new FileReader("/Users/shalinpmodi/Desktop/Spring2015/TrackControl/src/src/Switches.json"));
					/*JSONArray jsonArr = (JSONArray) obj;
					for (int index = 0; index < jsonArr.size(); index++) {
						org.json.simple.JSONObject switchObject = (org.json.simple.JSONObject) jsonArr.get(index);
						System.out.println(switchObject.get("Name"));
						System.out.println(switchObject.get("Line"));
						System.out.println(switchObject.get("Block_Numbers"));
					}*/
					
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
		frame.setBounds(100, 100, 544, 271);
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
		
		
		/*JPanel panel_87 = new JPanel();
		panel.add(panel_87);
		
		lblBrokenRail = new JLabel("Broken Rail?");
		panel_87.add(lblBrokenRail);
		
		txtYesOrNo = new JTextField();
		txtYesOrNo.setHorizontalAlignment(SwingConstants.CENTER);
		txtYesOrNo.setText("Y/N");
		txtYesOrNo.setColumns(3);
		panel_87.add(txtYesOrNo);*/
		
			
		panel_3 = new JPanel();
		panel_2.add(panel_3);
		
		lblCommandedAuthority = new JLabel("Suggested Authority");
		panel_3.add(lblCommandedAuthority);
		
		//String sugAuthority = (TrackController.getSpeed()).toString();
		
		textField_1 = new JTextField();
		uiElements.put("authority", textField_1);
		textField_1.setEditable(false);
		textField_1.setColumns(2);
		panel_3.add(textField_1);
		
		panel_1 = new JPanel();
		panel_2.add(panel_1);
		
		lblSuggestedSpeed = new JLabel("Suggested Speed");
		panel_1.add(lblSuggestedSpeed);
		txtCtcspeed = new JTextField();
		uiElements.put("speed", txtCtcspeed);
		panel_1.add(txtCtcspeed);
		txtCtcspeed.setEditable(false);
		txtCtcspeed.setColumns(2);
		
		lblKmhr = new JLabel("mph");
		panel_1.add(lblKmhr);
		
		/*panel_10 = new JPanel();
		panel_2.add(panel_10);
		
		lblSwitchPositiion = new JLabel("Switch Position");
		panel_10.add(lblSwitchPositiion);
		
		txtSwitchNumber = new JTextField();
		txtSwitchNumber.setText("Switch Number");
		uiElements.put("switchnum", txtSwitchNumber);
		txtSwitchNumber.setHorizontalAlignment(SwingConstants.CENTER);
		txtSwitchNumber.setEditable(false);
		txtSwitchNumber.setColumns(8);
		panel_10.add(txtSwitchNumber);
		
		txtOpenclose = new JTextField();
		txtOpenclose.setText("Open/Close");
		uiElements.put("swtichOC", txtOpenclose);
		txtOpenclose.setHorizontalAlignment(SwingConstants.CENTER);
		txtOpenclose.setEditable(false);
		txtOpenclose.setColumns(7);
		panel_10.add(txtOpenclose);*/
		
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
				
		/*panel_9 = new JPanel();
		panel_2.add(panel_9);
			
		lblTrackOccupancy = new JLabel("Track Occupancy");
		panel_9.add(lblTrackOccupancy);
				
		txtYn = new JTextField();
		txtYn.setText("Y/N");
		txtYn.setHorizontalAlignment(SwingConstants.CENTER);
		txtYn.setEditable(false);
		txtYn.setColumns(3);
		panel_9.add(txtYn);*/
				
		panel_11 = new JPanel();
		panel_2.add(panel_11);
				
		lblLights = new JLabel("Lights");
		panel_11.add(lblLights);
				
		txtROrG_1 = new JTextField();
		txtROrG_1.setText("R/G");
		uiElements.put("lights", txtROrG_1);
		uiElements.get("lights").setText("G");
		txtROrG_1.setHorizontalAlignment(SwingConstants.CENTER);
		txtROrG_1.setEditable(false);
		txtROrG_1.setColumns(2);
		panel_11.add(txtROrG_1);
				
		//if (txtROrG_1.getText() == "R" || txtROrG_1.getText() == "Red" || txtROrG_1.getText() == "red" || txtROrG_1.getText() == "r")
		//{
			//Set Up swithcing conditions
		//}
				
		/*panel_12 = new JPanel();
		panel_2.add(panel_12);
				
		lblRrCrossing = new JLabel("RR Crossing?");
		panel_12.add(lblRrCrossing);
				
		txtYn_1 = new JTextField();
		uiElements.put("crossing", txtYn_1);
		txtYn_1.setText("Y/N");
		txtYn_1.setEditable(false);
		txtYn_1.setColumns(3);
		panel_12.add(txtYn_1);
				
		panel_13 = new JPanel();
		panel_12.add(panel_13);
				
		lblCrossbar = new JLabel("Crossbar");
		panel_13.add(lblCrossbar);
				
		txtUpdown = new JTextField();
		//if (txtYn_1.getText() == "Y" || txtYn_1.getText() == "y" || txtYn_1.getText() == "Yes" || txtYn_1.getText() == "yes") {
			//txtUpdown.setText("Down");
		//}
		txtUpdown.setText("Up/Down");
		uiElements.put("barUD", txtUpdown);
		txtUpdown.setHorizontalAlignment(SwingConstants.CENTER);
		txtUpdown.setEditable(false);
		txtUpdown.setColumns(5);
		panel_13.add(txtUpdown);*/
		
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
