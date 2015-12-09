package Interpreter;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

public class Output {
	
	private JFrame frmOutput;

	private static JTextArea outputArea;
	private JTabbedPane tabbedPane;
	private static JScrollPane scrollPane;
	
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
		frmOutput.setTitle("SKOS");
		frmOutput.setBounds(100, 100, 643, 510);
		frmOutput.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmOutput.getContentPane().setLayout(null);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 643, 488);
		frmOutput.getContentPane().add(tabbedPane);
		
		scrollPane = new JScrollPane();
		tabbedPane.addTab("Output", null, scrollPane, null);
		
		outputArea = new JTextArea();
		outputArea.setWrapStyleWord(true);
		outputArea.setLineWrap(true);
		outputArea.setEditable(false);
		scrollPane.setViewportView(outputArea);
	}
	
	public static void write(String text){
		outputArea.setText(outputArea.getText() + text +"\n");
		scrollToBottom();
	}
	
	public static void writeInLine(String text){
		outputArea.setText(outputArea.getText() + text);
		scrollToBottom();
	}
	
	public static void scrollToBottom(){
		JScrollBar vertical = scrollPane.getVerticalScrollBar();
		vertical.setValue( vertical.getMaximum() );
	}
	
	public static String loadCMD(String title){
		return JOptionPane.showInputDialog(title);
	}
}
