package com.grabyo.wowza;

import java.util.*;

import com.wowza.wms.amf.AMFPacket;
import com.wowza.wms.application.IApplicationInstance;
import com.wowza.wms.logging.WMSLoggerFactory;
import com.wowza.wms.module.ModuleBase;
import com.wowza.wms.pushpublish.protocol.rtmp.*;
import com.wowza.wms.stream.*;

public class push extends ModuleBase
{
	Map<IMediaStream, PushPublishRTMP> publishers = new HashMap<IMediaStream, PushPublishRTMP>();
	
	class StreamNotify implements IMediaStreamActionNotify2
	{

		public void onPlay(IMediaStream stream, String streamName, double playStart, double playLen, int playReset)
		{
		}

		public void onPause(IMediaStream stream, boolean isPause, double location)
		{
		}

		public void onSeek(IMediaStream stream, double location)
		{
		}

		public void onStop(IMediaStream stream)
		{
		}

		public void onMetaData(IMediaStream stream, AMFPacket metaDataPacket)
		{
		}

		public void onPauseRaw(IMediaStream stream, boolean isPause, double location)
		{
		}
		
		public void onPublish(IMediaStream stream, String streamName, boolean isRecord, boolean isAppend)
		{
			if (!streamName.startsWith("push-")) // this is here to avoid looping pushes
			{
				try
				{
					IApplicationInstance appInstance = stream.getStreams().getAppInstance();
					
					synchronized(publishers)
					{
						PushPublishRTMP publisher = new PushPublishRTMP();
						
						// Source stream
						publisher.setAppInstance(appInstance);
						publisher.setSrcStreamName(streamName);
						
						// Destination stream
						
						//Details from FB Live API
						String dstHost = "live-api-s.facebook.com";
						int dstPort = 80;
						String dstApplication = "rtmp";
						String dstStreamName = "";
						String flashVersion = PushPublishRTMP.CURRENTFMLEVERSION;

						// Destination stream
						publisher.setHost(dstHost);
						publisher.setPort(dstPort);
						publisher.setDstApplicationName(dstApplication);
						publisher.setDstStreamName(dstStreamName);
						publisher.setConnectionFlashVersion(flashVersion);
			
						publisher.setSendFCPublish(true);
						publisher.setSendReleaseStream(true);
						publisher.setSendOnMetadata(true);
						publisher.setDebugLog(true);
						publisher.setDebugPackets(false);
						
						publisher.connect();
						publishers.put(stream, publisher);
					}
				}
				catch(Exception e)
				{
					WMSLoggerFactory.getLogger(null).error("ModulePushPublishSimpleExample#StreamNotify.onPublish: "+e.toString());
				}
			}
		}

		public void onUnPublish(IMediaStream stream, String streamName, boolean isRecord, boolean isAppend)
		{
			stopPublisher(stream);
		}
	}
	
	public void stopPublisher(IMediaStream stream)
	{
		try
		{
			synchronized(publishers)
			{
				PushPublishRTMP publisher = publishers.remove(stream);
				if (publisher != null)
					publisher.disconnect();
			}
		}
		catch(Exception e)
		{
			WMSLoggerFactory.getLogger(null).error("ModulePushPublishSimpleExample#StreamNotify.onPublish: "+e.toString());
		}
	}
	
	public void onStreamCreate(IMediaStream stream)
	{
		stream.addClientListener(new StreamNotify());
	}
	
	public void onStreamDestory(IMediaStream stream)
	{
		stopPublisher(stream);
	}
}