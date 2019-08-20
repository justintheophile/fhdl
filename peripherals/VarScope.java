package peripherals;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import fhdl.lang.Bus;
import fhdl.lang.Script;

public class VarScope extends Peripheral {
	// imperfect example of peripheral
	public int polingRate = 10;
	public int ticks;
	public String target;
	public Bus targetValue;
	
	Thread process;
	ScopeScreen screen;

	public ArrayList<Bus> history = new ArrayList<Bus>();

	public VarScope(Script script, String target) {
		super(script);
		this.target = target;
		screen = new ScopeScreen(this);
		process = new Thread(new Runnable() {
			public void run() {
				boolean run = true;
				while (run) {
					if (Thread.interrupted()) {
						break;
					}
					update();
					Script.milli(polingRate);
					ticks++;
				}
			}

		});

	}

	public void update() {
		targetValue = (Bus) script.getScopeController().getVariable(target);
		if (targetValue != null) {
			history.add(new Bus(targetValue));

		}
	}

	public void on() {
		screen.on();
		process.start();
	}

	public void off() {
		process.interrupt();
		screen.off();

	}

	class ScopeScreen extends JPanel implements ActionListener {
		private static final long serialVersionUID = 1L;

		public JFrame frame;
		Timer timer = new Timer(10, this);
		VarScope scope;

		public int height = 20;
		public int spacing = 30;
		public int stretch = 2;
		public int topOffset = height+spacing+30;
		
		public ScopeScreen(VarScope scope) {
			this.scope = scope;

			frame = new JFrame("Timing");
			frame.setSize(600, 400);
			frame.add(this);
			frame.setResizable(false);
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}

		public void on() {
			frame.setVisible(true);
			timer.start();
		}

		public void off() {
			frame.setVisible(false);
			frame.dispose();
			timer.stop();

		}

		public void actionPerformed(ActionEvent e) {
			update();
			repaint();

		}

		public void update() {

		}

		public void paint(Graphics g) {
			super.paint(g);
			Graphics2D g2d = (Graphics2D) g;
			if (targetValue != null) {
				for (int j = 0; j < targetValue.getWidth(); j++) {
					boolean last = false;
					for (int i = 0; i < scope.history.size(); i++) {
						Bus bus = scope.history.get(i);
						boolean on = bus.getBitValue(j);
						int onHeight = (on ? 1 : 0)*height;
						g2d.drawLine(i*stretch, j*spacing - onHeight + topOffset ,(i+1)*stretch, j*spacing - onHeight + topOffset);
						if(last != on) {
							g2d.drawLine(i*stretch, j*spacing + topOffset, i*stretch, j*spacing - height + topOffset);
						}
						last = on;
					}
				}
			}
		}

	}

}
