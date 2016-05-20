package cc.lison.pojo;

public class EchoMessage extends EchoPojo {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3648851810575375470L;

	private MessageType messageType;

	public MessageType getMessageType() {
		return messageType;
	}

	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}

	/**
	 * 构建消息对象(基本消息对象)
	 * 
	 * @param message
	 * @return
	 */
	public static EchoMessage buildMessage(String message) {

		EchoMessage echo = new EchoMessage();

		byte[] bytes = message.getBytes(Constants.getCharset());
		echo.setBytes(bytes);
		echo.setSumCountPackage(bytes.length);
		echo.setCountPackage(1);
		echo.setSend_time(System.currentTimeMillis());
		echo.setReceive_uid("0");

		return echo;
	}

	/**
	 * 根据messageType构建消息对象(可以发送的消息对象)
	 * 
	 * @param message
	 * @param messageType
	 * @return
	 */
	public static EchoMessage buildMessage(String message, MessageType messageType) {
		EchoMessage echo = null;

		switch (messageType) {
		case
		/**
		 * 服务端发送心跳包
		 */
		HEART_BEAT_SERVER:
			echo = buildMessage("");
			echo.setMessageType(messageType);
			break;

		/**
		 * 客户端发送心跳包
		 */
		case HEART_BEAT_CLIENT:
			echo = buildMessage("");
			echo.setMessageType(messageType);
			break;

		/**
		 * 客户端发送给服务器的业务消息
		 */
		case BUSINESS2SERVER:
			echo = buildMessage(message);
			echo.setMessageType(messageType);
			break;

		/**
		 * 服务端发送给客户端的业务消息
		 */
		case BUSINESS2CLIENT:
			echo = buildMessage(message);
			echo.setMessageType(messageType);
			break;

		/**
		 * 服务器上线
		 */
		case SERVER_ONLINE:
			echo = buildMessage("");
			echo.setMessageType(messageType);
			break;

		/**
		 * 服务器离线
		 */
		case SERVER_OFFLINE:
			echo = buildMessage("");
			echo.setMessageType(messageType);
			break;

		/**
		 * 客户端上线
		 */
		case CLIENT_ONLINE:
			echo = buildMessage("");
			echo.setMessageType(messageType);
			break;

		/**
		 * 客户端离线
		 */
		case CLIENT_OFFLINE:
			echo = buildMessage("");
			echo.setMessageType(messageType);
			break;
		/**
		 * 服务器接受客户端连接请求
		 */
		case SERVER_ACCEPT:
			echo = buildMessage("");
			echo.setMessageType(messageType);
			break;

		/**
		 * 服务器拒绝客户端连接请求
		 */
		case SERVER_REJECT:
			echo = buildMessage(message);
			echo.setMessageType(messageType);
			break;
		/**
		 * 服务端异常，客户端应持有最后的异常缓存区消息，重连后继续缓冲区消息业务处理
		 */
		case SERVER_EXCEPTION:
			echo = buildMessage("");
			echo.setMessageType(messageType);
			break;

		/**
		 * 客户端异常，服务器应持有最后的异常缓存区消息，重连后继续缓冲区信息业务处理
		 */
		case CLIENT_EXCEPTION:
			echo = buildMessage("");
			echo.setMessageType(messageType);
			break;
		}

		return echo;
	}

	/**
	 * 消息类型
	 * 
	 * @author Lison
	 *
	 */
	public enum MessageType {
		/**
		 * 服务端发送心跳包
		 */
		HEART_BEAT_SERVER,

		/**
		 * 客户端发送心跳包
		 */
		HEART_BEAT_CLIENT,

		/**
		 * 客户端发送给服务器的业务消息
		 */
		BUSINESS2SERVER,

		/**
		 * 服务端发送给客户端的业务消息
		 */
		BUSINESS2CLIENT,

		/**
		 * 服务器上线
		 */
		SERVER_ONLINE,

		/**
		 * 服务器离线
		 */
		SERVER_OFFLINE,

		/**
		 * 客户端上线
		 */
		CLIENT_ONLINE,

		/**
		 * 客户端离线
		 */
		CLIENT_OFFLINE,

		/**
		 * 服务器接受客户端连接请求
		 */
		SERVER_ACCEPT,

		/**
		 * 服务器拒绝客户端连接请求
		 */
		SERVER_REJECT,

		/**
		 * 服务端异常，客户端应持有最后的异常缓存区消息，重连后继续缓冲区消息业务处理
		 */
		SERVER_EXCEPTION,

		/**
		 * 客户端异常，服务器应持有最后的异常缓存区消息，重连后继续缓冲区信息业务处理
		 */
		CLIENT_EXCEPTION

	}
}
