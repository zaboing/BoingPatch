package at.zaboing.patcher.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.function.Consumer;

public class WindowClosingListener extends WindowAdapter
{
	private Consumer<WindowEvent> callback;

	public WindowClosingListener(Consumer<WindowEvent> callback)
	{
		this.callback = callback;
	}

	public WindowClosingListener(Runnable callback)
	{
		this.callback = (event) -> callback.run();
	}

	@Override
	public void windowClosing(WindowEvent event)
	{
		if (callback != null)
		{
			callback.accept(event);
		}
	}

	public static WindowClosingListener create(Consumer<WindowEvent> callback)
	{
		return new WindowClosingListener(callback);
	}

	public static WindowClosingListener create(Runnable callback)
	{
		return new WindowClosingListener(callback);
	}
}
