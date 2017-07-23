/*
 * Creation : 4 mai 2017
 */
package visu;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.geom.Rectangle2D;


import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

public final class PanelA2l extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	private Rectangle2D r2d;

	private Timer timer;
	private long tps;
	private static final double nbSample = 1000;

	int cnt;


	// GUI
	private final JButton btOpen = new JButton("Start");
	private final JButton btStop = new JButton("Stop");
	private final JPanel panUp = new JPanel(new GridLayout(1, 1));
	private final JPanel panDown = new JPanel(new GridLayout(1, 1));
	private final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false, panUp, panDown);

	public PanelA2l() {
		
		setLayout(new BorderLayout());

		btOpen.addActionListener(new OpenA2l());
		panUp.add(btOpen);

		btStop.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (timer != null)
				timer.stop();

			}
		});
		panDown.add(btStop);

		
		splitPane.setUI(new MySplitPaneUI());
		
		this.add(splitPane,BorderLayout.CENTER);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.fill(r2d);
	}

	private final class OpenA2l implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			// final JFileChooser jFileChooser = new JFileChooser();
			// jFileChooser.setMultiSelectionEnabled(false);
			// jFileChooser.setFileFilter(new FileFilter() {
			//
			// @Override
			// public String getDescription() {
			// return "A2l *.a2l";
			// }
			//
			// @Override
			// public boolean accept(File f) {
			//
			// if (f.isDirectory())
			// return true;
			//
			// String extension = Utilitaire.getExtension(f);
			// if (extension.equals(Utilitaire.a2l)) {
			// return true;
			// }
			// return false;
			// }
			// });
			//
			// final int reponse = jFileChooser.showOpenDialog(PanelA2l.this);
			// if (reponse == JFileChooser.APPROVE_OPTION) {
			// A2LParser.parse(jFileChooser.getSelectedFile());
			// }

		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (splitPane.getRightComponent().getHeight()!=0)
		{
			splitPane.setDividerLocation((int)premOrdre(PanelA2l.this.getHeight(),tps=tps+timer.getDelay()));
		}else{
			timer.stop();
		}
	}

	private double premOrdre(int height, double t)
	{
		final double tau = timer.getDelay()/((nbSample/20)/nbSample);
		return height*(1 - (Math.exp(-t/tau)));
	}
	
	private final class MySplitPaneUI extends BasicSplitPaneUI
	{
		final MyBt bt = new MyBt();
		
		@Override
		public BasicSplitPaneDivider createDefaultDivider() {
			BasicSplitPaneDivider divider = new BasicSplitPaneDivider(this)
					{
						@Override
						public void setDividerSize(int paramInt) {
							super.setDividerSize(bt.getPreferredSize().height);
						}
					};
			divider.setLayout(new BorderLayout());
			divider.add(bt, BorderLayout.WEST);
			
			return divider;
		}
		
		
	}
	
	private final class MyBt extends JButton implements ActionListener
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public MyBt() {
			super("Down");
			addActionListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent paramActionEvent) {
			if(timer == null)
				timer = new Timer(1, PanelA2l.this);

				repaint();
				cnt = 0;
				tps = 0;

				timer.start();
		}
	}

}
