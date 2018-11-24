package cs601.project4.bean;

public class Event {
	private int id;
	private String name;
	private int userid;
	private int avail;
	private int purchased;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public int getAvail() {
		return avail;
	}
	public void setAvail(int avail) {
		this.avail = avail;
	}
	public int getPurchased() {
		return purchased;
	}
	public void setPurchased(int purchased) {
		this.purchased = purchased;
	}
	
	
}
