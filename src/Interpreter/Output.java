package Interpreter;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;


import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import java.awt.Color;
import java.awt.Font;

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
		frmOutput.setIconImage(Toolkit.getDefaultToolkit().getImage(Interpreter.class.getResource("/Interpreter/iconSmall.png")));
		frmOutput.setTitle("UPS - UnixoPodobny System");
		frmOutput.setBounds(100, 100, 643, 510);
		frmOutput.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmOutput.getContentPane().setLayout(null);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 643, 488);
		frmOutput.getContentPane().add(tabbedPane);
		
		scrollPane = new JScrollPane();
		tabbedPane.addTab("Output", null, scrollPane, null);
		
		outputArea = new JTextArea();
		outputArea.setForeground(new Color(0, 250, 154));
		outputArea.setBackground(new Color(0, 0, 0));
		outputArea.setWrapStyleWord(true);
		outputArea.setLineWrap(true);
		outputArea.setEditable(false);
		scrollPane.setViewportView(outputArea);
		
		outputArea.setText("++++++++++++++++++++=+=++++++++++++++++++++++++++++++++++++++++++++++++++++MMZZZZZZOMN\n=======+++++++==MMMNM=+++++++++++++++++++++++++++++++++++++++++++++++++++=MMZZZZZZNM\n+++MMMMMM++=MMMMNOOZM=+++++++++=+=MMMMM~+++++++++++++++++++=+++++++++=MNZZZZZZNM\nMOZZZZZZZ8M=~MZZZZZZZZN=+++=~MMMMMN8ZZZZZ8MM~=++++++=MMMMMMMMMMM=++=MOZZZZZZMM\n=MZZZZZZZZZM==MZZZZZZZZNMMMNDZZZZZZZZZZZZZZZZNM+++=MMNZOZOZZZZZZMM++++MZZZZZZZMD++\nMZZZZZZZZZM+=MZZZZZZZOMNZZZZZZZZZZZZZZZZZZZZOMM=~M8$ZZZZZZZZZZZM~++++++NMZZZZZZOM~+\nMNZZZZZZZZM=+MDZZZZZZZNNZZZZZZZZZZZZZZZZZZZZODMMNZZZZZZZZZZZZZNM=+++++MMZZZZZZOM=+\nMNZZZZZZZZM=+=MZZZZZZZDMOZZZZZZZZZONDZZZZZZZZZZZZZZZZZZDNNN8ZZMM+++++MNZZZZZZZM=++\nMMZZZZZZZZM=++MOZZZZZZOMNOZZZZZZNMM=MMZZZZZZZZZZZZZZOMMM===MMMM~+++MZZZZZZ$DZ+++\n~MZZZZZZZZM=++M8ZZZZZZZMNZZZZZZZM=+++MOZZZZZZZZZZZZ$N=++++++++++++++++MZZZZZZZN=++++\n=MZZZZZZZZN=++MMZZZZZZZMMZZZZZZZM+++=MOZZZZZZZZZZZZZZMMMMMM~=++++++~MZZZZZZZM=++++\n=MZZZZZZZZN=++MMZZZZZZZMMOZZZZZOM==MMDZZZZZZONDZZZZZZZZZZONMMM=++~NZZZZZZZM=++++\n~MZZZZZZZZN=++=MMZZZZZZMMOZZZZZZM~MDZZZZZZZZM=MM8ZZZZZZZZZZZZZNMM++8NZZZZZZDM++++\n=MZZZZZZZZN=++=MMZZZZZZ8MNZZZZZZZOZZZZZZZZOM=++~MNZZZZZZZZZZZZZ$8M=~NZZZZZONM+++++\n+MZZZZZZZZN=++=MMZZZZZZOMMZZZZZZZZZZZZZZZMM~+++==~MMM8ZZZZZZZZZZZM==MOOZZZZMO++++\n=MZZZZZZZZOM+++MNZZZZZZZNMZZZZZZZZZZZZO8M~++++++++++===MMNZZZZZZZMM==~===MMM=+++++\n+~NZZZZZZZZMM=MMZZZZZZZZNMZZZZZZZZZZONMM++++++++++++++++=MNZZZZZZMM====+++++=+++++\n+~MZZZZZZZZZNMNZOZZZZZZ8MMZZZZZZZZ8MMM==++++++===~=++++=~MNZZZZZZ8M++MMMMM8=++++++\n==MOZZZZZZZZZZZZZZZZZZZM=~MZZZZZZMM+++++++++++=MNNMMMMMMNZZZZZZZZZ+ZZZZZZZM~++++++\n++MMZZZZZZZZZZZZZZZZZOM~+~NZZZZZZMM+++++++++++=MZZZZZZZZZZZZZZZ8MM+++ZZZZZZM=+++++++\n++=MMZZZZZZZZZZZZZZZNM=++=MZZZZZZNM+++++++++++~NZZZZZZZZZZZZZZDM:+++++ZZZZZZM=+++++++\n++++~MNZZZOZ$ZZZZNMM~+++++MOZZZZZNM+++++++++=MMZZZZZZZZZZZZ8NM==+++=MMMMMM=+++++++\n+++++=~MMMMMMMMM===+++++++MMZZZZZDM=++++++++++~MMMMMMNNMMMM===++++++==+=+++++++++\n++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n++++Burdelak+++++++Florczak++++Belka+++++Kot+++++++++++++++++++++++++++++++++++++++++++++++\n++++++++++++++++Maleszczuk++++++++++Matuszewski+++++++++++++++++++++++++++++++++++++++++++\n++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");
	}
	
	public static void write(String text){

		for (int i = 0; i< text.length();i++){
			outputArea.setText(outputArea.getText() + text.charAt(i));
			try {
			    Thread.sleep(5);
			} catch(InterruptedException ex) {
			}
		}

		outputArea.setText(outputArea.getText() + "\n");
		scrollToBottom();
	}
	
	public static void writeInLine(String text){
		for (int i = 0; i< text.length();i++){
			outputArea.setText(outputArea.getText() + text.charAt(i));
			try {
			    Thread.sleep(5);
			} catch(InterruptedException ex) {
			}
		}
		//outputArea.setText(outputArea.getText() + text);
		scrollToBottom();
	}
	
	public static void scrollToBottom(){
		try {
		    Thread.sleep(50);
		} catch(InterruptedException ex) {
		}
		JScrollBar vertical = scrollPane.getVerticalScrollBar();
		vertical.setValue( vertical.getMaximum() );
	}
	
	public static String loadCMD(String title){
		String[] options = {"OK"};
		JPanel panel = new JPanel(new GridLayout(2, 1));
		JLabel lbl = new JLabel(title+"\n");
		JTextField txt = new JTextField(30);
		panel.add(lbl);
		panel.add(txt);
		ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Interpreter.class.getResource("/Interpreter/iconSmall.png")));
		int selectedOption = JOptionPane.showOptionDialog(null, panel, "UPS", JOptionPane.NO_OPTION, JOptionPane.QUESTION_MESSAGE, icon, options , options[0]);

		if(selectedOption == 0)
		{
		    String text = txt.getText();
		    return text;
		}
		
		return "";
	}
}
