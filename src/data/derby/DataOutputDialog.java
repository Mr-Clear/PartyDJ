package data.derby;
import java.awt.Color;
import java.awt.Font;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.swing.GroupLayout;
import javax.swing.JComponent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class DataOutputDialog extends javax.swing.JDialog
{
	private static final long serialVersionUID = 1528848749329252325L;
	private JTextArea textArea;
	private JScrollPane scrollPane;

	public DataOutputDialog(JFrame frame, Object output)
	{
		super(frame, "Query Output");
		GroupLayout thisLayout = new GroupLayout((JComponent)getContentPane());
		getContentPane().setLayout(thisLayout);
		{
			textArea = new JTextArea();
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(textArea);
		}
			thisLayout.setVerticalGroup(thisLayout.createSequentialGroup()
				.addComponent(scrollPane, 0, 480, Short.MAX_VALUE));
			thisLayout.setHorizontalGroup(thisLayout.createSequentialGroup()
				.addComponent(scrollPane, 0, 784, Short.MAX_VALUE));
			
		textArea.setFont(new Font(Font.MONOSPACED, 0, 12));
		if(output instanceof Throwable)
		{
			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stringWriter);
			((Throwable)output).printStackTrace(printWriter);
			textArea.setForeground(Color.RED);
			textArea.setText(stringWriter.toString());
		}
		else
			textArea.setText(output.toString());
		textArea.setSelectionStart(0);
		textArea.setSelectionEnd(0);
		this.setSize(800, 516);
		setVisible(true);
	}
}
