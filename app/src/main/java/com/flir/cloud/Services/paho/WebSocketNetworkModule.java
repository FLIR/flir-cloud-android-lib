/*
 * Copyright (c) 2014 Inventit Inc.
 */
package com.flir.cloud.Services.paho;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URI;
import java.nio.ByteBuffer;

/*import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;*/
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.internal.NetworkModule;
import org.eclipse.paho.client.mqttv3.logging.Logger;
import org.eclipse.paho.client.mqttv3.logging.LoggerFactory;

/**
 * A network module for connecting over WebScoket with Jetty 9.
 */
public class WebSocketNetworkModule /*extends WebSocketAdapter*/ implements
		NetworkModule {
	private static final String CLASS_NAME = WebSocketNetworkModule.class
			.getName();
	private static final Logger log = LoggerFactory.getLogger(
			LoggerFactory.MQTT_CLIENT_MSG_CAT, CLASS_NAME);

	/**
	 * WebSocket URI
	 */
	private final URI uri;

	/**
	 * Sub-Protocol
	 */
	private final String subProtocol;

	/**
	 * A stream for outgoing data
	 */
	private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream() {
		@Override
		public void flush() throws IOException {
			final ByteBuffer byteBuffer;
			synchronized (this) {
				byteBuffer = ByteBuffer.wrap(toByteArray());
				reset();
			}
			// Asynchronous call
		/*	if(getRemote() != null) {
				getRemote().sendBytes(byteBuffer);
				getRemote().flush();
			}*/
		}
	};

	/**
	 * A pair of streams for incoming data
	 */
	private final PipedOutputStream receiverStream = new PipedOutputStream();
	private final PipedInputStream inputStream;

	//private WebSocketClient client;
	private int conTimeout;

	/**
	 * Constructs a new WebSocketNetworkModule using the specified URI.
	 * 
	 * @param uri
	 * @param subProtocol
	 * @param resourceContext
	 */
	public WebSocketNetworkModule(URI uri, String subProtocol,
			String resourceContext) {
		log.setResourceName(resourceContext);
		this.uri = uri;
		this.subProtocol = subProtocol;
		try {
			this.inputStream = new PipedInputStream(receiverStream);
		} catch (IOException unexpected) {
			throw new IllegalStateException(unexpected);
		}
	}

	/**
	// * A factory method for {@link ClientUpgradeRequest} class
	 * 
	 * @return
	 */
/*	protected ClientUpgradeRequest createClientUpgradeRequest() {
		final ClientUpgradeRequest request = new ClientUpgradeRequest();
		// you can manipulate the request by overriding this method.
		return request;
	}*/

	/**
	 * A factory method for {@link WebSocketClient} class
	 * 
	 * @return
	 */
/*	protected WebSocketClient createWebSocketClient() {
		final WebSocketClient client = new WebSocketClient(
				createSslContextFactory());
		// you can manipulate the client by overriding this method.
		return client;
	}*/

	/**
	 * A factory method for {@link SslContextFactory} class, used for
	 * instantiating a WebSocketClient()
	 * 
	 * @return
	 */
	/*protected SslContextFactory createSslContextFactory() {
		return new SslContextFactory(true){
	        @Override
	        public void customize(SSLEngine sslEngine) {
	        	 SSLParameters sslParams = sslEngine.getSSLParameters();
	            //sslParams.setEndpointIdentificationAlgorithm(_endpointIdentificationAlgorithm);
	            sslEngine.setSSLParameters(sslParams);

	            if (getWantClientAuth())
	                sslEngine.setWantClientAuth(getWantClientAuth());
	            if (getNeedClientAuth())
	                sslEngine.setNeedClientAuth(getNeedClientAuth());

	            sslEngine.setEnabledCipherSuites(selectCipherSuites(
	                    sslEngine.getEnabledCipherSuites(),
	                    sslEngine.getSupportedCipherSuites()));

	            sslEngine.setEnabledProtocols(selectProtocols(sslEngine.getEnabledProtocols(),sslEngine.getSupportedProtocols()));
	        }
	    };
	}*/

	/**
	 * Starts the module, by creating a TCP socket to the server.
	 */
	@Override
	public void start() throws IOException, MqttException {
		/*final String methodName = "start";
		try {
			// @TRACE 252=connect to host {0} port {1} timeout {2}
			if (log.isLoggable(Logger.FINE)) {
				log.fine(
						CLASS_NAME,
						methodName,
						"252",
						new Object[] { uri.toString(),
								Integer.valueOf(uri.getPort()),
								Long.valueOf(conTimeout * 1000) });
			}
			client = createWebSocketClient();
			client.setConnectTimeout(conTimeout * 1000);
			if (client.isStarted() == false) {
				client.start();
			}

			final ClientUpgradeRequest request = createClientUpgradeRequest();
			request.setSubProtocols(subProtocol);
			final Future<Session> future = client.connect(this, uri, request);
			// Replays the same behavior as Socket.connect().
			// blocks until the connection is established or some error occurs.
			future.get();

		} catch (ConnectException ex) {
			// @TRACE 250=Failed to create TCP socket
			log.fine(CLASS_NAME, methodName, "250", null, ex);
			throw new MqttException(
					MqttException.REASON_CODE_SERVER_CONNECT_ERROR, ex);

		} catch (Exception ex) {
			// @TRACE 250=Failed to create TCP socket
			log.fine(CLASS_NAME, methodName, "250", null, ex);
			throw new MqttException(MqttException.REASON_CODE_UNEXPECTED_ERROR,
					ex);
		}*/
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return inputStream;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return outputStream;
	}

	/**
	 * Stops the module, by closing the web socket.
	 */
	@Override
	public void stop() throws IOException {
		/*try {
			client.stop();
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}*/
	}

	@Override
	public String getServerURI() {
		return this.uri.toString();
	}

	/**
	 * Set the maximum time in seconds to wait for a socket to be established
	 * 
	 * @param timeout
	 *            in seconds
	 */
	public void setConnectTimeout(int timeout) {
		this.conTimeout = timeout;
	}

	/*@Override
	public void onWebSocketBinary(byte[] payload, int offset, int len) {
		try {
			this.receiverStream.write(payload, offset, len);
			this.receiverStream.flush();
		} catch (IOException e) {
			log.fine(CLASS_NAME, "onWebSocketError", "401", null, e);
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void onWebSocketError(Throwable cause) {
		if (log.isLoggable(Logger.FINE)) {
			log.fine(CLASS_NAME, "onWebSocketError", "401", null, cause);
		}
	}

	@Override
	public void onWebSocketConnect(Session sess) {
		super.onWebSocketConnect(sess);
		if (log.isLoggable(Logger.FINE)) {
			log.fine(CLASS_NAME, "onWebSocketConnect", "116",
					new Object[] { uri.toString() + ", WebSocket CONNECTED." });
		}
	}

	@Override
	public void onWebSocketClose(int statusCode, String reason) {
		super.onWebSocketClose(statusCode, reason);
		if (log.isLoggable(Logger.FINE)) {
			log.fine(CLASS_NAME, "onWebSocketConnect", "116",
					new Object[] { uri.toString() + ", WebSocket CLOSED." });
		}
	}
*/
}
