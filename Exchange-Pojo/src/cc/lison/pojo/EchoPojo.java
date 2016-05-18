package cc.lison.pojo;

import java.io.Serializable;

/**
 * 信息交换对象
 * @author Lison-Liou
 *
 */
public class EchoPojo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4010249994097151671L;

	/**
	 * 总包数
	 */
	private int sumCountPackage;
	
	/**
	 * 当前包数
	 */
	private int countPackage;
	
	/**
	 * 交换信息数据字节
	 */
	private byte[] bytes;
	
	/**
	 * 发送人业务id
	 */
	private String send_uid;
	
	/**
	 * 接收人业务id (0 接收目标为系统 其他为业务id)
	 */
	private String receive_uid;

	/**
	 * 发送包时间
	 */
	private long send_time;
	
	/**
	 * 接收包时间
	 */
	private long receive_time;

	public int getSumCountPackage() {
		return this.sumCountPackage;
	}

	public void setSumCountPackage(int sumCountPackage) {
		this.sumCountPackage = sumCountPackage;
	}

	public int getCountPackage() {
		return this.countPackage;
	}

	public void setCountPackage(int countPackage) {
		this.countPackage = countPackage;
	}

	public byte[] getBytes() {
		return this.bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	public String getSend_uid() {
		return send_uid;
	}

	public void setSend_uid(String send_uid) {
		this.send_uid = send_uid;
	}

	public String getReceive_uid() {
		return receive_uid;
	}

	public void setReceive_uid(String receive_uid) {
		this.receive_uid = receive_uid;
	}

	public long getSend_time() {
		return send_time;
	}

	public void setSend_time(long send_time) {
		this.send_time = send_time;
	}

	public long getReceive_time() {
		return receive_time;
	}

	public void setReceive_time(long receive_time) {
		this.receive_time = receive_time;
	}
}