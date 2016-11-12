package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI
 * Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose
 * whatever) then you should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details. Use of Jigloo implies
 * acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN
 * PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class SynthTestWindow extends javax.swing.JFrame
{
	private static final long serialVersionUID = -3155020125918260189L;

	{
		//Set Look & Feel
		try
		{
			javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		}
		catch(final Exception e)
		{
			e.printStackTrace();
		}
	}

	private JButton stopBtn;
	private JButton prefBtn;
	private JButton skipBackBtn;
	private JButton skipFwdBtn;
	private JLabel totalTimeLbl;
	private JLabel remainingTimeLbl;
	private JLabel playedTimeLbl;
	private JButton fwdBtn;
	private JButton backBtn;
	private JButton playPauseBtn;
	private JButton playBtn;

	private JPanel controlPanel;
	private JPanel buttonPanel;
	private JPanel btnRow1;
	private JPanel btnRow2;
	private JPanel trackPanel;
	private JPanel listPanel4;
	private JPanel listPanel3;
	private JPanel listPanel2;
	private JPanel listPanel1;

	private JLabel trackLabel;

	private JProgressBar trackProgressBar;

	private JTabbedPane jTabbedPane4;
	private JTabbedPane jTabbedPane3;
	private JTabbedPane jTabbedPane2;
	private JTabbedPane jTabbedPane1;

	private JSlider volumeSlider;

	/**
	 * Auto-generated main method to display this JFrame
	 * 
	 * @param args Keine
	 */
	public static void main(final String[] args)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				final SynthTestWindow inst = new SynthTestWindow();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public SynthTestWindow()
	{
		super();
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		initGUI();
	}

	private void initGUI()
	{
		{
			final GridBagLayout thisLayout = new GridBagLayout();
			thisLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.1, 0.0, 0.1, 0.0};
			thisLayout.rowHeights = new int[]{6, 67, 18, 10, 241, 11, 241, 6};
			thisLayout.columnWeights = new double[]{0.0, 0.1, 0.0, 0.0, 0.0, 0.1, 0.0, 0.1, 0.1, 0.0};
			thisLayout.columnWidths = new int[]{6, 154, 3, 48, 3, 174, 6, 178, 204, 6};
			getContentPane().setLayout(thisLayout);
			this.setMaximumSize(new java.awt.Dimension(800, 600));

			this.setFocusableWindowState(false);
			{
				buttonPanel = new JPanel();
				final BoxLayout buttonPanelLayout = new BoxLayout(buttonPanel, javax.swing.BoxLayout.Y_AXIS);
				buttonPanel.setLayout(buttonPanelLayout);
				{
					btnRow1 = new JPanel();
					buttonPanel.add(btnRow1);
					btnRow1.setOpaque(false);
					btnRow1.setPreferredSize(new java.awt.Dimension(309, 42));
					{
						playBtn = new JButton();
						btnRow1.add(playBtn);
						playBtn.setPreferredSize(new java.awt.Dimension(32, 32));
					}
					{
						playPauseBtn = new JButton();
						btnRow1.add(playPauseBtn);
						playPauseBtn.setPreferredSize(new java.awt.Dimension(32, 32));
					}

					{
						stopBtn = new JButton();
						btnRow1.add(stopBtn);
						stopBtn.setPreferredSize(new java.awt.Dimension(32, 32));
					}
					{
						prefBtn = new JButton();
						btnRow1.add(prefBtn);
						prefBtn.setPreferredSize(new java.awt.Dimension(32, 32));
					}

					btnRow2 = new JPanel();
					buttonPanel.add(btnRow2);
					{
						skipBackBtn = new JButton();
						btnRow2.add(skipBackBtn);
						skipBackBtn.setPreferredSize(new java.awt.Dimension(32, 32));
					}
					{
						backBtn = new JButton();
						btnRow2.add(backBtn);
						backBtn.setPreferredSize(new java.awt.Dimension(32, 32));
					}
					{
						fwdBtn = new JButton();
						btnRow2.add(fwdBtn);
						fwdBtn.setPreferredSize(new java.awt.Dimension(32, 32));
					}
					{
						skipFwdBtn = new JButton();
						btnRow2.add(skipFwdBtn);
						skipFwdBtn.setPreferredSize(new java.awt.Dimension(32, 32));
					}
				}
			}
			{
				volumeSlider = new JSlider(SwingConstants.VERTICAL);
				volumeSlider.setPaintTicks(true);
				volumeSlider.setMajorTickSpacing(10);
				volumeSlider.setMinorTickSpacing(5);
				volumeSlider.setPreferredSize(new java.awt.Dimension(68, 20));
				volumeSlider.setSize(68, 20);
			}
			{
				trackPanel = new JPanel();
				final GroupLayout trackPanelLayout = new GroupLayout(trackPanel);
				trackPanel.setLayout(trackPanelLayout);
				trackPanel.setPreferredSize(new java.awt.Dimension(0, 0));
				{
					trackLabel = new JLabel();
					trackLabel.setText("Playing...");
					trackLabel.setFont(new java.awt.Font("Tahoma", 0, 26));
				}
				{
					trackProgressBar = new JProgressBar();
				}
				{
					totalTimeLbl = new JLabel();
					totalTimeLbl.setText("08:15");
				}
				{
					remainingTimeLbl = new JLabel();
					remainingTimeLbl.setText("-00:00");
				}
				{
					playedTimeLbl = new JLabel();
					playedTimeLbl.setText("08:15");
				}
				trackPanelLayout.setHorizontalGroup(trackPanelLayout.createParallelGroup()
					.addGroup(GroupLayout.Alignment.LEADING, trackPanelLayout.createSequentialGroup()
					    .addComponent(playedTimeLbl, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE)
					    .addGap(0, 222, GroupLayout.PREFERRED_SIZE)
					    .addComponent(totalTimeLbl, 0, 34, Short.MAX_VALUE)
					    .addGap(0, 223, GroupLayout.PREFERRED_SIZE)
					    .addComponent(remainingTimeLbl, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE))
					.addComponent(trackLabel, GroupLayout.Alignment.LEADING, 0, 547, Short.MAX_VALUE)
					.addComponent(trackProgressBar, GroupLayout.Alignment.LEADING, 0, 547, Short.MAX_VALUE));
				trackPanelLayout.setVerticalGroup(trackPanelLayout.createSequentialGroup()
					.addComponent(trackLabel, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addComponent(trackProgressBar, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(trackPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					    .addComponent(playedTimeLbl, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					    .addComponent(remainingTimeLbl, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
					    .addComponent(totalTimeLbl, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)));
			}
			{
				controlPanel = new JPanel();
				final BoxLayout controlPanelLayout = new BoxLayout(controlPanel, javax.swing.BoxLayout.X_AXIS);
				controlPanel.setLayout(controlPanelLayout);
				getContentPane().add(controlPanel, new GridBagConstraints(1, 1, 8, 2, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
				{
					controlPanel.add(buttonPanel);
					buttonPanel.setPreferredSize(new java.awt.Dimension(154, 85));
					buttonPanel.setSize(158, 85);
					buttonPanel.setMaximumSize(new java.awt.Dimension(154, 85));
					buttonPanel.setMinimumSize(new java.awt.Dimension(154, 85));
					controlPanel.add(volumeSlider);
					controlPanel.add(trackPanel);
				}
			}
			{
				listPanel1 = new JPanel();
				final GroupLayout listPanel1Layout = new GroupLayout(listPanel1);
				listPanel1.setLayout(listPanel1Layout);
				getContentPane().add(listPanel1, new GridBagConstraints(1, 4, 5, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
				{
					jTabbedPane2 = new JTabbedPane();
				}
				listPanel1Layout.setHorizontalGroup(listPanel1Layout.createSequentialGroup().addComponent(jTabbedPane2, 0, 501, Short.MAX_VALUE));
				listPanel1Layout.setVerticalGroup(listPanel1Layout.createSequentialGroup().addComponent(jTabbedPane2, 0, 297, Short.MAX_VALUE));
			}
			{
				listPanel2 = new JPanel();
				final GroupLayout listPanel2Layout = new GroupLayout(listPanel2);
				listPanel2.setLayout(listPanel2Layout);
				getContentPane().add(listPanel2, new GridBagConstraints(7, 4, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
				{
					jTabbedPane1 = new JTabbedPane();
				}
				listPanel2Layout.setHorizontalGroup(listPanel2Layout.createSequentialGroup().addComponent(jTabbedPane1, 0, 370, Short.MAX_VALUE));
				listPanel2Layout.setVerticalGroup(listPanel2Layout.createSequentialGroup().addComponent(jTabbedPane1, 0, 241, Short.MAX_VALUE));
			}
			{
				listPanel3 = new JPanel();
				final GroupLayout listPanel3Layout = new GroupLayout(listPanel3);
				listPanel3.setLayout(listPanel3Layout);
				getContentPane().add(listPanel3, new GridBagConstraints(1, 6, 5, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
				{
					jTabbedPane3 = new JTabbedPane();
				}
				listPanel3Layout.setHorizontalGroup(listPanel3Layout.createSequentialGroup().addComponent(jTabbedPane3, 0, 438, Short.MAX_VALUE));
				listPanel3Layout.setVerticalGroup(listPanel3Layout.createSequentialGroup().addComponent(jTabbedPane3, 0, 232, Short.MAX_VALUE));
			}
			{
				listPanel4 = new JPanel();
				final GroupLayout listPanel4Layout = new GroupLayout(listPanel4);
				listPanel4.setLayout(listPanel4Layout);
				getContentPane().add(listPanel4, new GridBagConstraints(7, 6, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
				{
					jTabbedPane4 = new JTabbedPane();
				}
				listPanel4Layout.setHorizontalGroup(listPanel4Layout.createSequentialGroup().addComponent(jTabbedPane4, 0, 622, Short.MAX_VALUE));
				listPanel4Layout.setVerticalGroup(listPanel4Layout.createSequentialGroup().addComponent(jTabbedPane4, 0, 309, Short.MAX_VALUE));
			}
		}
		{
			this.setSize(800, 600);
		}

	}

}
