package LapTimer;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import javax.swing.SwingConstants;


@SuppressWarnings("serial")
public class LapTimer extends JFrame {

	private Font counterFont = new Font("Arial", Font.BOLD, 20);
	private Font totalFont = new Font("Arial", Font.PLAIN, 14);

	private JLabel lapLabel = new JLabel("Seconds running:");
	private JTextField lapField = new JTextField(15);
	private JLabel totalLabel = new JLabel("Total seconds:");
	private JTextField totalField = new JTextField(15);


	private JButton startButton = new JButton("START");
	private JButton lapButton = new JButton("LAP");
	private JButton stopButton = new JButton("STOP");

	// The text area and the scroll pane in which it resides
	private JTextArea display;

	private JScrollPane myPane;

	// These represent the menus
	private JMenuItem saveData = new JMenuItem("Save data", KeyEvent.VK_S);
	private JMenuItem displayData = new JMenuItem("Display data", KeyEvent.VK_D);
	
	private JMenu options = new JMenu("Options");

	private JMenuBar menuBar = new JMenuBar();

	private boolean started;

	private float totalSeconds = (float)0.0;
	private float lapSeconds = (float)0.0;

	private int lapCounter = 1;

	private LapTimerThread lapThread;
	Thread thrd;

	private Session currentSession;
	
	private final JLabel lblLapTimeGoal = new JLabel("Lap time goal:");
	private final JTextField goalTextField = new JTextField();
	private final JPanel SetTimePanel = new JPanel();
	private final JLabel lblSeconds = new JLabel("seconds");
	
	private String[] goal_message = {"GOAL REACHED", "GOAL NOT REACHED"};


	public LapTimer() 
	{

		setTitle("Lap Timer Application");

		MigLayout layout = new MigLayout("fillx");
		JPanel panel = new JPanel(layout);
		getContentPane().add(panel);

		options.add(saveData);
		options.add(displayData);
		menuBar.add(options);

		this.setJMenuBar(menuBar);

		MigLayout centralLayout = new MigLayout("fillx");

		JPanel centralPanel = new JPanel(centralLayout);

		GridLayout timeLayout = new GridLayout(0,2);

		JPanel timePanel = new JPanel(timeLayout);

		lapField.setEditable(false);
		lapField.setFont(counterFont);
		lapField.setText("00:00:00.0");

		totalField.setEditable(false);
		totalField.setFont(totalFont);
		totalField.setText("00:00:00.0");
		timePanel.add(lblLapTimeGoal);
		
		timePanel.add(SetTimePanel);
		goalTextField.setText("60");
		goalTextField.setColumns(10);
		SetTimePanel.add(goalTextField);
		SetTimePanel.add(lblSeconds);

		// Setting the alignments of the components
		lblLapTimeGoal.setHorizontalAlignment(SwingConstants.RIGHT);
		goalTextField.setHorizontalAlignment(SwingConstants.CENTER);
		totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		lapLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		lapField.setHorizontalAlignment(JTextField.CENTER);
		totalField.setHorizontalAlignment(JTextField.CENTER);

		timePanel.add(lapLabel);
		timePanel.add(lapField);
		timePanel.add(totalLabel);
		timePanel.add(totalField);

		centralPanel.add(timePanel, "wrap");

		GridLayout buttonLayout = new GridLayout(1, 3);

		JPanel buttonPanel = new JPanel(buttonLayout);

		buttonPanel.add(startButton);
		buttonPanel.add(lapButton);
		buttonPanel.add(stopButton);

		centralPanel.add(buttonPanel, "spanx, growx, wrap");

		panel.add(centralPanel, "wrap");

		display = new JTextArea(100,150);
		display.setMargin(new Insets(5,5,5,5));
		display.setEditable(false);
		myPane = new JScrollPane(display, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panel.add(myPane, "alignybottom, h 100:320, wrap");


		// Initial state of system
		started = false;
		currentSession = new Session();

		// Allowing interface to be displayed
		setSize(400, 500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		/* This method should allow the user to save data to a file called textData.txt */
		saveData.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				
				File dataFile = new File("textData.txt");
				 try 
				{
					writeDataFile(dataFile);
					
				} catch (IOException e1) 
				 {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
		
			}
		});

		/* This method should retrieve the contents of a file representing a 
		   previous report using a JFileChooser. The result should be displayed 
		   as the contents of a dialog object. */
		displayData.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				try
				{
					readDataFile();
				} catch (ClassNotFoundException e1) 
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) 
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		/* This method should check to see if the application is already running, 
		   and if not, launch a LapTimerThread object, but if there is another 
		   session already under way, it should ask the user whether they want to 
		   restart - if they do then the existing thread and session should be 
		   reset. The lap counter should be set to 1 and a new Session object 
		   should be created. A new LapTimerThread object should be created with 
		   totalSeconds set to 0.0 and the display area should be cleared. When the 
		   new thread is started, make sure the goal textField is disabled */
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				
				/* Insert code here */
				if(e.getActionCommand().equalsIgnoreCase("Start")) 
				{
					
					if(lapThread != null && lapThread.isRunning())
					{
						
						int input = JOptionPane.showConfirmDialog(LapTimer.this, "Another session is already running. Would you like to restart?");
						if(input == 0)
						{
							JOptionPane.showMessageDialog(LapTimer.this, "The new session will start now.");
							totalSeconds = (float) 0.0;
							lapCounter = 1;
							
							
							lapThread.stop();
							lapThread = new LapTimerThread(LapTimer.this, LapTimer.this.totalSeconds);
							thrd = new Thread(lapThread);
							
							thrd.start();
							display.setText(null);
						}
					}
						else
						{
							
							display.setText(null);
							goalTextField.setEnabled(false);
							lapThread = new LapTimerThread(LapTimer.this, LapTimer.this.totalSeconds);
							thrd = new Thread(lapThread);
							thrd.start();
							
						}
						
						currentSession = new Session();
					}
					
				 }
				
				
			
		});

		/* This method should only work if a session has been started. Once started,
		   clicking the Lap button should cause the length of the current lap to be
		   retrieved and used to create a new Lap object which is added to the 
		   session collection. The old LapTimerThread object should be stopped and 
		   a new thread should be started with the updated value of total seconds.
		   The lap number and time should be added to the display area. The message 
		   saying if the goal was reached also need to be added */
		lapButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				/* Insert code here */

				if(e.getActionCommand().equalsIgnoreCase("Lap")) 
				{
						//retrieve length of current lap
						int length = currentSession.laps.size()+1;
						
						String goalNotReached = goal_message[1];
						String goalReached = goal_message[0];
						
						lapSeconds = lapThread.getLapSeconds();
						Lap l = new Lap(length, lapSeconds);//create new lap object
						
						currentSession.addLap(l);// add it to the collection
						lapCounter++;
						lapThread.stop();
						
						lapThread = new LapTimerThread(LapTimer.this,lapThread.getTotalSeconds() );
						
						thrd = new Thread(lapThread);
						thrd.start();
						
						if(isGoalReached())
							display.append("Lap " + length + "\t" + lapField.getText() + "\t"+ goalReached + "\n");
						else
							display.append("Lap " + length + "\t" + lapField.getText() + "\t"+ goalNotReached + "\n");
							
					
				}
				
				
			}
		});
		
		

		/* This method should have most of the same functionality as the Lap
		   button's action listener, except that a new LapTimerThread object is
		   NOT started. In addition, the total time for all the laps should be 
		   calculated and displayed in the text area, along with the average lap
		   time and the numbers and times of the fastest and slowest laps. */
		stopButton.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{

				/* Insert code here */
				if(e.getActionCommand().equalsIgnoreCase("Stop"))
				{
					
							int length = currentSession.laps.size()+1;
							
							String goalNotReached = goal_message[1];
							String goalReached = goal_message[0];
							
							
							Lap l = new Lap(length, lapThread.getLapSeconds());
							
							
							currentSession.addLap(l);
							lapCounter++;
							lapThread.stop();
							
							lapThread = new LapTimerThread(LapTimer.this,lapThread.getTotalSeconds() );
							
							thrd = new Thread(lapThread);
							thrd.stop();
							
							if(isGoalReached())
								display.append("Lap " + length + "\t" + lapField.getText() + "\t"+ goalReached + "\n");
							else
								display.append("Lap " + length + "\t" + lapField.getText() + "\t"+ goalNotReached + "\n");
								
							display.append("Stopped at: "+ "\t" + totalField.getText() + "\n"+"Average: "+currentSession.calculateAverageTime()+""
										+ "\nSlowest lap: "+currentSession.getSlowestLap().getId() + "\t \t" +convertToHMSString(currentSession.getSlowestLap().getLapTime())+""
												+  "\nFastest Lap: "+currentSession.getFastestLap().getId() + "\t \t" +convertToHMSString(currentSession.getFastestLap().getLapTime()));
						
					
				 }
			}
								
			
		});

	}
	
	public boolean isGoalReached()
	{
		
		 if(lapThread.getLapSeconds() < Float.parseFloat(getGoalValue()))
			 return true;
		 else
			 return false;
			 
	}
	
	/* These two methods are used by the LapTimerThread to update the values
	   displayed in the two text fields. Each value is formatted as a 
	   hh:mm:ss.S string by calling the convertToHMSString method below/. */

	public void updateLapDisplay(float value) {

		lapField.setText(convertToHMSString(value));

	}

	public void updateTotalDisplay(float value) {

		totalField.setText(convertToHMSString(value));

	}
	
	/* These methods are here to help access the
	 *  goaltextField in the GUI */
	
	public String getGoalValue(){
		return  goalTextField.getText();
	}
	
	public void EnableGoalEditing(boolean makeEditable){
		goalTextField.setEditable(makeEditable);
	}
	
	public void setTextArea(String str){
		display.setText(str);
	}

	private String convertToHMSString(float seconds) {
		long msecs, secs, mins, hrs;
		// String to be displayed
		String returnString = "";

		// Split time into its components

		long secondsAsLong = (long)(seconds * 10);

		msecs = secondsAsLong % 10;
		secs = (secondsAsLong / 10) % 60;
		mins = ((secondsAsLong / 10) / 60) % 60;
		hrs = ((secondsAsLong / 10) / 60) / 60;

		// Insert 0 to ensure each component has two digits
		if (hrs < 10) {
			returnString = returnString + "0" + hrs;
		}
		else returnString = returnString + hrs;
		returnString = returnString + ":";

		if (mins < 10) {
			returnString = returnString + "0" + mins;		
		}
		else returnString = returnString + mins;
		returnString = returnString + ":";

		if (secs < 10) {
			returnString = returnString + "0" + secs;		
		}
		else returnString = returnString + secs;

		returnString = returnString + "." + msecs;

		return returnString;

	}

	/* These methods will be used by the action listeners attached
	   to the two menu items. */

	public synchronized void writeDataFile(File f) throws IOException, FileNotFoundException 
	{
		/* Insert code here */
		 
			ObjectOutputStream out = null;
				
				try 
				{
					out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(f)));
					out.writeObject(display.getText());
					
				} 
				catch (IOException ex) 
				{
					JOptionPane.showMessageDialog(LapTimer.this, "I/O Exception\n " + ex.toString(), "Error Message", JOptionPane.ERROR_MESSAGE);
					ex.printStackTrace();
				}
				
				finally 
				{
					out.close();
				}
				

	}

	public synchronized String readDataFile() throws IOException, ClassNotFoundException 
	{
		String res = new String();
		// Used to indicate whether data is already in a file
		File currentFile;
	

        JFileChooser fChooser = new JFileChooser();
        
        int returnVal = fChooser.showOpenDialog(LapTimer.this);
       fChooser.setCurrentDirectory( new File(System.getProperty("user.dir")));
        if (returnVal == JFileChooser.APPROVE_OPTION) 
        {
            // Confirm if the user wants to replace the current data with a new data
            int result = JOptionPane.showConfirmDialog(LapTimer.this, "This will replace the existing data.\nAre you sure you want to do this?");

            if (result == JOptionPane.YES_OPTION) 
            {
                // Load the objects from file
                try 
                {
                    currentFile = fChooser.getSelectedFile();
                    writeDataFile(currentFile);
                    ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(currentFile)));
					System.out.println((String)in.readObject());
                   
                } 
                catch (Exception ex) 
                {
                    JOptionPane.showMessageDialog(LapTimer.this, "Exception\n " + ex.toString(), "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
		return res;
	}

	public static void main(String[] args) {

		LapTimer timer = new LapTimer();
		timer.setVisible(true);

	}


}
