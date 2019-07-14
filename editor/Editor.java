package editor;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

public class Editor extends JFrame {

	public Editor() {
		JPanel cp = new JPanel(new BorderLayout());

		RSyntaxTextArea textArea = new RSyntaxTextArea(20, 60);
		textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
		textArea.setCodeFoldingEnabled(true);
		RTextScrollPane sp = new RTextScrollPane(textArea);
		cp.add(sp);

		setContentPane(cp);
		setTitle("FHDL Editor");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(800, 600);
		setLocationRelativeTo(null);
		setVisible(true);

	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Editor();
			}
		});
	}

}
