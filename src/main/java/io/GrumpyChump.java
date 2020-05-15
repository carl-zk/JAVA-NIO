package io;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * test @BlockingServerThreaded to get OOM
 * 
 * @author carl
 *
 */
public class GrumpyChump {

	public static void main(String[] args) throws InterruptedException {
		Socket[] sockets = new Socket[3000];
		for (int i = 0; i < sockets.length; i++) {
			try {
				sockets[i] = new Socket("localhost", 8080);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		TimeUnit.HOURS.sleep(1);
	}
}
