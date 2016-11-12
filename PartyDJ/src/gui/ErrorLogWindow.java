package gui;

import basics.Controller;
import basics.LoggedError;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
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

public class ErrorLogWindow extends JFrame
{
	private static final long serialVersionUID = 1L;

	private final JPanel contentPane;
	private final JTextField txtSenderType;
	private final JTextField txtException;
	private final JTextField txtSender;
	private final JTextField txtPriority;
	private final JTextField txtTimestamp;
	private final JList<LoggedError> lstErrors;
	private final DefaultListModel<LoggedError> lstErrorsListModel;
	private final JTextArea txtStackTrace;
	private final JTextField txtMessage;
	
	private final Controller controller;

	private final DateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	private final DateFormat localeDateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM);
	private JScrollPane scrollPaneStackTrace;

	/**
	 * Launch the application.
	 * @param args Ignored
	 */
	public static void main(final String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					final ErrorLogWindow frame = new ErrorLogWindow(null);
					frame.setVisible(true);
				}
				catch (final Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

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

		final JLabel lblPriority = new JLabel("Priority:");

		final JLabel lblSender = new JLabel("Sender:");

		final JLabel lblSenderType = new JLabel("Sender type:");

		final JLabel lblException = new JLabel("Exception:");

		txtSenderType = new JTextField();
		txtSenderType.setEditable(false);
		txtSenderType.setColumns(10);

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
		gl_contentPane.setHorizontalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING).addGroup(gl_contentPane.createSequentialGroup().addContainerGap().addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING).addComponent(scrollPaneErrors, GroupLayout.DEFAULT_SIZE, 559, Short.MAX_VALUE).addGroup(gl_contentPane.createSequentialGroup().addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING).addComponent(lblSenderType).addComponent(lblException).addComponent(lblSender).addComponent(lblPriority).addComponent(lblMessage).addComponent(lblTimestamp)).addPreferredGap(ComponentPlacement.UNRELATED).addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING, false).addComponent(txtMessage, Alignment.LEADING).addComponent(txtTimestamp, Alignment.LEADING).addComponent(txtException, Alignment.LEADING).addComponent(txtSender, Alignment.LEADING).addComponent(txtPriority, Alignment.LEADING).addComponent(txtSenderType, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 220, GroupLayout.PREFERRED_SIZE)).addPreferredGap(ComponentPlacement.RELATED).addComponent(scrollPaneStackTrace, GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE))).addContainerGap()));
		gl_contentPane.setVerticalGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING).addGroup(gl_contentPane.createSequentialGroup().addContainerGap().addComponent(scrollPaneErrors, GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE).addPreferredGap(ComponentPlacement.RELATED).addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING).addGroup(gl_contentPane.createSequentialGroup().addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE).addComponent(lblMessage).addComponent(txtMessage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)).addPreferredGap(ComponentPlacement.RELATED).addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE).addComponent(lblTimestamp).addComponent(txtTimestamp, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)).addPreferredGap(ComponentPlacement.RELATED).addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE).addComponent(lblPriority).addComponent(txtPriority, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)).addPreferredGap(ComponentPlacement.RELATED).addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE).addComponent(lblSender).addComponent(txtSender, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)).addPreferredGap(ComponentPlacement.RELATED).addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE).addComponent(lblSenderType).addComponent(txtSenderType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)).addPreferredGap(ComponentPlacement.RELATED).addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE).addComponent(lblException).addComponent(txtException, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))).addComponent(scrollPaneStackTrace, GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)).addContainerGap()));

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
				final LoggedError error = lstErrors.getSelectedValue();
				txtMessage.setText(error.getMessage());
				txtSender.setText(error.getSender());
				txtSenderType.setText(error.getSenderType());
				txtPriority.setText(Integer.toString(error.getPriority()));
				txtTimestamp.setText(localeDateFormat.format(error.getTimestamp()));
				if (error.getException() != null)
				{
					txtException.setText(error.getException().toString());
					final StringWriter sw = new StringWriter();
					final PrintWriter pw = new PrintWriter(sw);
					error.getException().printStackTrace(pw);
					txtStackTrace.setText(sw.toString());
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
		lstErrors.setCellRenderer(new ListCellRenderer<LoggedError>()
		{
			@Override
			public Component getListCellRendererComponent(final JList<? extends LoggedError> list, final LoggedError value, final int index, final boolean isSelected, final boolean cellHasFocus)
			{
				final JLabel lbl = new JLabel(isoDateFormat.format(value.getTimestamp()) + " [" + value.getPriority() + "] " + value.getMessage());
				lbl.setOpaque(true);
				switch(value.getPriority())
				{
				case 1:
				case 2:
					break;
				case 3:
				case 4:
					lbl.setBackground(Color.yellow);
					break;
				case 5:
					lbl.setBackground(Color.orange);
					break;
				case 6:
					lbl.setBackground(Color.red);
					lbl.setForeground(Color.white);
					break;
				case 7:
					lbl.setBackground(Color.black);
					lbl.setForeground(Color.white);
					break;
				default:
					lbl.setBackground(Color.magenta);
					lbl.setForeground(Color.white);
					break;
				}
				return lbl;
			}
		});
		scrollPaneErrors.setViewportView(lstErrors);
		contentPane.setLayout(gl_contentPane);
	}

	public void addError(final LoggedError error)
	{
		lstErrorsListModel.addElement(error);
		lstErrors.ensureIndexIsVisible(lstErrorsListModel.size() - 1);

		setTitle(lstErrorsListModel.size() + " Fehler");
	}
}
