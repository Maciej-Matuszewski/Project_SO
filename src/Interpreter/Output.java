package Interpreter;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import java.awt.Color;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

public class Output {
	
	private JFrame frmOutput;

	private static JTextArea outputArea;
	
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
		frmOutput.setResizable(false);
		frmOutput.setTitle("Output");
		frmOutput.setBounds(100, 100, 643, 510);
		frmOutput.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmOutput.getContentPane().setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(6, 6, 631, 476);
		frmOutput.getContentPane().add(scrollPane);
		
		outputArea = new JTextArea();
		outputArea.setWrapStyleWord(true);
		outputArea.setLineWrap(true);
		outputArea.setEditable(false);
		scrollPane.setViewportView(outputArea);
	}
	
	public static void write(String text){
		outputArea.setText(outputArea.getText() + text +"\n");
	}
	
	public static void writeInLine(String text){
		outputArea.setText(outputArea.getText() + text);
	}
	
	public static String loadCMD(String title){
		return JOptionPane.showInputDialog(title);
	}
}
