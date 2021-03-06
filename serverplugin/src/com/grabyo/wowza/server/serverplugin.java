package com.grabyo.wowza.server;

import java.io.IOException;

import com.wowza.wms.logging.WMSLoggerFactory;
import com.wowza.wms.server.IServer;
import com.wowza.wms.server.IServerNotify2;


public class serverplugin implements IServerNotify2
{
	/*
	 * Called after the Server.xml file has been loaded. This is the earliest point where you can access Server events.
	 */
	public void onServerConfigLoaded(IServer server)
	{
		WMSLoggerFactory.getLogger(null).info("onServerConfigLoaded");
	}

	/*
	 * Called after the Server is created but not yet initialized.
	 */
	public void onServerCreate(IServer server)
	{
		WMSLoggerFactory.getLogger(null).info("onServerCreate");
	}

	/*
	 * Called after the Server is initialized.  At this point, the server is running, all VHosts have been initialized and the Server is ready to accept connections.
	 */
	public void onServerInit(IServer server)
	{
		
		System.out.println("SERVER PLUGIN INITIATED.");
		WMSLoggerFactory.getLogger(null).info("onServerInit");
		
		//Creating FFMPEG bash command which overlays the Grabyo logo with an MP4 file and sends it to the local server. This will start the 'live' application within the server.
		ProcessBuilder pb = new ProcessBuilder("ffmpeg","-re","-i","/home/finn/Downloads/big_buck_bunny_480p_h264.mov","-i","/home/finn/Downloads/logo-grabyo-logoonly-512x512.png", "-filter_complex", "[1:v]scale=100:100[ovr1],[0:v][ovr1]overlay=(main_w-overlay_w):0","-pix_fmt","yuv420p","-c:a","aac","-strict","-2","-f","flv","rtmp://localhost:1935/live/myStream");
		
		
		//Redirecting output and errors to server terminal
		pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
		pb.redirectError(ProcessBuilder.Redirect.INHERIT);
		
		try {
			Process p = pb.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Called when the Server starts to shut down.
	 */
	public void onServerShutdownStart(IServer server)
	{
		WMSLoggerFactory.getLogger(null).info("onServerShutdownStart");
	}

	/*
	 * Called when the Server shutdown process is complete.  This is the last event that is triggered before the Wowza process exits.
	 */
	public void onServerShutdownComplete(IServer server)
	{
		WMSLoggerFactory.getLogger(null).info("onServerShutdownComplete");
	}
}
