package players.jl;

import basics.Controller;
import data.IData;
import players.IPlayer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer.Info;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Settings extends JPanel
{
	private static final long serialVersionUID = 1L;
	final IData data = Controller.getInstance().getData();
	final IPlayer player = Controller.getInstance().getPlayer();

	public Settings()
	{
		final JLabel lblSeundDevice = new JLabel("Seund device:");

		final JButton btnOk = new JButton("Übernehmen");
		btnOk.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				apply();
			}
		});
		
		final JScrollPane scrollPane = new JScrollPane();
		final GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblSeundDevice)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 358, Short.MAX_VALUE))
						.addComponent(btnOk, Alignment.TRAILING))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblSeundDevice)
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnOk)
					.addContainerGap())
		);
		
		final Info[] infos = AudioSystem.getMixerInfo();
		final List<String> mixers = new ArrayList<>();
		for(final Info info : infos)
			if(SoundAudioDevice.test(info))
				mixers.add(info.getName());
		final String[] mixerArray = new String[mixers.size()];
		mixers.toArray(mixerArray);
		final JList<String> list = new JList<>(mixerArray);
		String currentMixer =  data.readSetting("JLPlayer.Mixer");
		if(currentMixer == null)
			currentMixer = AudioSystem.getMixer(null).getMixerInfo().getName();
		if(currentMixer != null)
			for(int i = 0; i < mixerArray.length; i++)
				if(currentMixer.equals(mixerArray[i]))
				{
					list.setSelectedIndex(i);
					break;
				}
		scrollPane.setViewportView(list);
		setLayout(groupLayout);
		
		list.addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(final ListSelectionEvent e)
			{
				data.writeSetting("JLPlayer.Mixer", list.getSelectedValue());
			}
		});
		
		list.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(final MouseEvent e)
			{
				if(e.getClickCount() == 1)
					apply();
			}
		});
	}
	
	private void apply()
	{
		if(player.getPlayState())
			player.setPosition(player.getPosition(), true);
	}
}
