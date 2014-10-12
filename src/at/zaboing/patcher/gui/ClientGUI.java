package at.zaboing.patcher.gui;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import at.zaboing.patcher.main.Client;

public class ClientGUI
{
	private JFrame frame;

	private JTextField targetDirectory;
	private JTextField remoteOrigin;
	private JCheckBox useMultiThreading;
	private JTextField numberOfThreads;

	public ClientGUI()
	{
		frame = new JFrame("BoingPatcher");
		frame.setSize(640, 480);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(WindowClosingListener.create(this::windowClosing));
		frame.setResizable(false);

		frame.setLayout(new GridLayout(5, 2));

		JButton ok, cancel;

		frame.add(new JLabel("Target Directory"));
		frame.add(targetDirectory = new JTextField("C:\\Users\\Za\\Desktop\\BoingPatch"));

		frame.add(new JLabel("Remote origin"));
		frame.add(remoteOrigin = new JTextField("http://localhost/boingpatch"));

		frame.add(new JLabel("Use multithreading"));
		frame.add(useMultiThreading = new JCheckBox((String) null, true));

		frame.add(new JLabel("Number of threads"));
		frame.add(numberOfThreads = new JTextField());

		frame.add(ok = new JButton("Start"));
		frame.add(cancel = new JButton("Cancel"));

		useMultiThreading.addActionListener(event -> numberOfThreads.setEnabled(useMultiThreading.isSelected()));

		numberOfThreads.setText(String.valueOf(Runtime.getRuntime().availableProcessors()));

		ok.addActionListener(event -> startClient());
		cancel.addActionListener(event -> frame.dispose());

		frame.setVisible(true);
	}

	public void windowClosing()
	{

	}

	public void startClient()
	{
		Object lock = new Object();
		JDialog dialog = new JDialog(frame, true);
		dialog.setUndecorated(true);
		dialog.setLayout(new GridLayout(2, 1));
		JProgressBar progressBar = new JProgressBar(0, 1000);
		JLabel currentAction = new JLabel();
		dialog.add(progressBar);
		dialog.add(currentAction);
		dialog.pack();
		dialog.setLocationRelativeTo(null);

		Client client = new Client(targetDirectory.getText(), remoteOrigin.getText(), useMultiThreading.isSelected(), Integer.parseInt(numberOfThreads.getText()));
		client.progressUpdateCallback = () -> {
			progressBar.setValue((int) (client.progress / client.maxProgress * 1000));
			currentAction.setText(client.currentAction);
		};
		client.successCallback = () -> {
			synchronized (lock)
			{
				dialog.dispose();
			}
		};
		client.run();
		synchronized (lock)
		{
			if (client.progress != client.maxProgress)
			{
				dialog.setVisible(true);
			}
		}
	}

	public static void main(String[] args)
	{
		new ClientGUI();
	}
}
