package cc.lison.pojo;

public class EchoFile extends EchoPojo {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8917310651102310680L;
	
	private String file_name;
	private long file_size;

	public String getFile_name() {
		return file_name;
	}

	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}

	public long getFile_size() {
		return file_size;
	}

	public void setFile_size(long file_size) {
		this.file_size = file_size;
	}
}