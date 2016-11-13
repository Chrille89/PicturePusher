package de.bach.thwildau.pusher.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.OutboundEvent;
import org.glassfish.jersey.media.sse.SseBroadcaster;
import org.glassfish.jersey.media.sse.SseFeature;

@Singleton
@Path("events")
public class Pusher {

	private SseBroadcaster broadcaster = new SseBroadcaster();

	private static final String WEBCAM_IMAGE_DIRECTORY = "/srv/motion/";

	@GET
	@Path("/test")
	@Produces(MediaType.TEXT_PLAIN)
	public String test() {
		return "I'am ready!";
	}

	@POST
	@Path("/push")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.TEXT_PLAIN)
	public String broadcastMessage(String message) {
		
		String msg = message.split("/")[3];
		
		OutboundEvent.Builder eventBuilder = new OutboundEvent.Builder();
		OutboundEvent event = eventBuilder.name("message").mediaType(MediaType.TEXT_PLAIN_TYPE)
				.data(String.class, msg).build();

		broadcaster.broadcast(event);

		return "Message '" + msg + "' has been broadcast.\n";
	}

	@GET
	@Path("/stream")
	@Produces(SseFeature.SERVER_SENT_EVENTS)
	public EventOutput listenToBroadcast() {
		final EventOutput eventOutput = new EventOutput();
		this.broadcaster.add(eventOutput);

		return eventOutput;
	}

	@GET
	@Path("/get/{fileName}")
	@Produces("image/png")
	public Response getImage(@PathParam("fileName") String fileName) {
		
		BufferedImage image;
		byte[] imageData = null;
		File file = null;
		try {
			file = new File(WEBCAM_IMAGE_DIRECTORY + fileName);
			image = ImageIO.read(file);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, "png", baos);
			imageData = baos.toByteArray();
			
		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(Status.BAD_REQUEST).build();
		}
		return Response.ok(imageData).build();
	}

}
