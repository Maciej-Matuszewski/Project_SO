package Interpreter;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import java.awt.Color;

public class Output {

	public static JTextArea outputArea;
	
	private JFrame frmOutput;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Output window = new Output();
					window.frmOutput.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Output() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmOutput = new JFrame();
		frmOutput.setTitle("Output");
		frmOutput.getContentPane().setBackground(Color.WHITE);
		frmOutput.setBounds(100, 100, 450, 300);
		frmOutput.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmOutput.getContentPane().setLayout(null);
		
		outputArea = new JTextArea();
		outputArea.setEditable(false);
		outputArea.setWrapStyleWord(true);
		outputArea.setLineWrap(true);
		outputArea.setBounds(6, 6, 438, 266);
		frmOutput.getContentPane().add(outputArea);
	}
	
	public static void write(String text){
		outputArea.setText(outputArea.getText() + text +"\n");
	}
	
	public static String loadCMD(String title){
		return JOptionPane.showInputDialog(title);
	}
}
