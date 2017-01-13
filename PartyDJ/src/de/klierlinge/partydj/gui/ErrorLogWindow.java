package de.klierlinge.partydj.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import de.klierlinge.partydj.basics.Controller;
import de.klierlinge.partydj.basics.ErrorListener;
import de.klierlinge.partydj.logging.LoggedMessage;

public class ErrorLogWindow extends JFrame implements ErrorListener
{
	private static final long serialVersionUID = 1L;

	private final JPanel contentPane;
	private final JTextField txtThread;
	private final JTextField txtException;
	private final JTextField txtSender;
	private final JTextField txtPriority;
	private final JTextField txtTimestamp;
	private final JList<LoggedMessage> lstErrors;
	private final DefaultListModel<LoggedMessage> lstErrorsListModel;
	private final JTextArea txtStackTrace;
	private final JTextField txtMessage;
	
	private final Controller controller;

	private final DateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	private final DateFormat localeDateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM);
	private JScrollPane scrollPaneStackTrace;

	/**
	 * Create the frame.
	 * @param controller 
	 */
	public ErrorLogWindow(final Controller controller)
	{
		setTitle("Keine Fehler");
		this.controller = controller;
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e)
			{
				if(ErrorLogWindow.this.controller == null)
					dispose();
				else
					ErrorLogWindow.this.controller.unregisterWindow(ErrorLogWindow.this);
			}
			@Override
			public void windowOpened(final WindowEvent e)
			{
				if(ErrorLogWindow.this.controller != null)
					ErrorLogWindow.this.controller.registerWindow(ErrorLogWindow.this);
			}
		});
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setBounds(100, 100, 605, 450);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		final JScrollPane scrollPaneErrors = new JScrollPane();

		final JLabel lblTimestamp = new JLabel("Timestamp:");

		final JLabel lblMessage = new JLabel("Message:");

		final JLabel lblPriority = new JLabel("Level:");

		final JLabel lblSender = new JLabel("Sender:");

		final JLabel lblThread = new JLabel("Thread:");

		final JLabel lblException = new JLabel("Exception:");

		txtThread = new JTextField();
		txtThread.setEditable(false);
		txtThread.setColumns(10);

		txtException = new JTextField();
		txtException.setEditable(false);
		txtException.setColumns(10);

		txtSender = new JTextField();
		txtSender.setEditable(false);
		txtSender.setColumns(10);

		txtPriority = new JTextField();
		txtPriority.setEditable(false);
		txtPriority.setColumns(10);

		txtMessage = new JTextField();
		txtMessage.setEditable(false);
		txtMessage.setColumns(10);

		txtTimestamp = new JTextField();
		txtTimestamp.setEditable(false);
		txtTimestamp.setColumns(10);

		scrollPaneStackTrace = new JScrollPane();
		final GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
		    gl_contentPane.createParallelGroup(Alignment.LEADING)
		        .addGroup(gl_contentPane.createSequentialGroup()
		            .addContainerGap()
		            .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
		                .addComponent(scrollPaneErrors, GroupLayout.DEFAULT_SIZE, 559, Short.MAX_VALUE)
		                .addGroup(gl_contentPane.createSequentialGroup()
		                    .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
		                        .addComponent(lblThread)
		                        .addComponent(lblSender)
		                        .addComponent(lblPriority)
		                        .addComponent(lblMessage)
		                        .addComponent(lblTimestamp, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		                        .addComponent(lblException, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		                    .addPreferredGap(ComponentPlacement.UNRELATED)
		                    .addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING, false)
		                        .addComponent(txtMessage, Alignment.LEADING)
		                        .addComponent(txtTimestamp, Alignment.LEADING)
		                        .addComponent(txtException, Alignment.LEADING)
		                        .addComponent(txtSender, Alignment.LEADING)
		                        .addComponent(txtPriority, Alignment.LEADING)
		                        .addComponent(txtThread, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 220, GroupLayout.PREFERRED_SIZE))
		                    .addPreferredGap(ComponentPlacement.RELATED)
		                    .addComponent(scrollPaneStackTrace, GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)))
		            .addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
		    gl_contentPane.createParallelGroup(Alignment.TRAILING)
		        .addGroup(gl_contentPane.createSequentialGroup()
		            .addContainerGap()
		            .addComponent(scrollPaneErrors, GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
		            .addPreferredGap(ComponentPlacement.RELATED)
		            .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
		                .addGroup(gl_contentPane.createSequentialGroup()
		                    .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
		                        .addComponent(lblMessage)
		                        .addComponent(txtMessage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		                    .addPreferredGap(ComponentPlacement.RELATED)
		                    .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
		                        .addComponent(lblTimestamp)
		                        .addComponent(txtTimestamp, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		                    .addPreferredGap(ComponentPlacement.RELATED)
		                    .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
		                        .addComponent(lblPriority)
		                        .addComponent(txtPriority, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		                    .addPreferredGap(ComponentPlacement.RELATED)
		                    .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
		                        .addComponent(lblSender)
		                        .addComponent(txtSender, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		                    .addPreferredGap(ComponentPlacement.RELATED)
		                    .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
		                        .addComponent(lblThread)
		                        .addComponent(txtThread, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		                    .addPreferredGap(ComponentPlacement.RELATED)
		                    .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
		                        .addComponent(lblException)
		                        .addComponent(txtException, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
		                .addComponent(scrollPaneStackTrace, GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))
		            .addContainerGap())
		);

		txtStackTrace = new JTextArea();
		scrollPaneStackTrace.setViewportView(txtStackTrace);
		txtStackTrace.setFont(new Font("Courier New", Font.PLAIN, 11));
		txtStackTrace.setEditable(false);

		lstErrors = new JList<>();
		lstErrors.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lstErrors.addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(final ListSelectionEvent e)
			{
				final LoggedMessage error = lstErrors.getSelectedValue();
				txtMessage.setText(error.getLogMessage());
				txtSender.setText(error.getLogger());
				txtThread.setText(error.getThread());
				txtPriority.setText(error.getLevel().name());
				txtTimestamp.setText(localeDateFormat.format(error.getTimestamp()));
				if (error.getExceptionMessage() != null)
				{
					txtException.setText(error.getExceptionMessage());
					txtStackTrace.setText(error.getStackTrace());
					txtStackTrace.setCaretPosition(0);
				}
				else
				{
					txtException.setText("");
					txtStackTrace.setText("");
				}
			}
		});
		lstErrorsListModel = new DefaultListModel<>();
		lstErrors.setModel(lstErrorsListModel);
		lstErrors.setCellRenderer(new ListCellRenderer<LoggedMessage>()
		{
			@Override
			public Component getListCellRendererComponent(final JList<? extends LoggedMessage> list, final LoggedMessage value, final int index, final boolean isSelected, final boolean cellHasFocus)
			{
                final Level level = value.getLevel();
				final JLabel lbl = new JLabel(isoDateFormat.format(value.getTimestamp()) + " [" + level + "] " + value.getLogMessage());
				lbl.setOpaque(true);
				if(level.isMoreSpecificThan(Level.FATAL))
				{
                    lbl.setBackground(Color.black);
                    lbl.setForeground(Color.red);
				}
                else if(level.isMoreSpecificThan(Level.ERROR))
                {
                    lbl.setBackground(Color.red);
                    lbl.setForeground(Color.black);
                }
				else if(level.isMoreSpecificThan(Level.WARN))
                {
                    lbl.setBackground(Color.orange);
                    lbl.setForeground(Color.black);
                }
                else if(level.isMoreSpecificThan(Level.INFO))
                {
                    lbl.setBackground(Color.white);
                    lbl.setForeground(Color.black);
                }
                else if(level.isMoreSpecificThan(Level.DEBUG))
                {
                    lbl.setBackground(Color.gray);
                    lbl.setForeground(Color.black);
                }
				else
				{
                    lbl.setBackground(Color.lightGray);
                    lbl.setForeground(Color.black);
				}
				return lbl;
			}
		});
		scrollPaneErrors.setViewportView(lstErrors);
		contentPane.setLayout(gl_contentPane);
		
		controller.addErrorListener(this);
	}

    @Override
    public void errorOccurred(LogEvent event)
    {
        lstErrorsListModel.addElement(new LoggedMessage(event));
        lstErrors.ensureIndexIsVisible(lstErrorsListModel.size() - 1);

        setTitle(lstErrorsListModel.size() + " Fehler");
        if(event.getLevel().isMoreSpecificThan(Level.ERROR))
            setVisible(true);
    }
}
