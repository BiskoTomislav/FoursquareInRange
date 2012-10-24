package hr.tomislav.android.foursquare;

import hr.tomislav.algorithms.Coordinate;

import java.io.Serializable;

public class VenueModel implements Serializable {

	private static final long serialVersionUID = 6273152723525676699L;
	
	private String ID;
	private String name;
	private String phone;
	private String address;
	private String category;
	private Integer checkinsCount;
	private Integer distance;
	private boolean checked;
	private Coordinate geoPosition;

	public VenueModel() {
	}

	public VenueModel(String iD, String name, String phone,
			String address, String category, Integer checkinsCount, Integer distance) {
		super();
		ID = iD;
		this.name = name;
		this.phone = phone;
		this.address = address;
		this.category = category;
		this.checkinsCount = checkinsCount;
		this.distance = distance;
		this.checked = false;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Integer getCheckinsCount() {
		return checkinsCount;
	}

	public void setCheckinsCount(Integer checkinsCount) {
		this.checkinsCount = checkinsCount;
	}

	public Integer getDistance() {
		return distance;
	}

	public void setDistance(Integer distance) {
		this.distance = distance;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public Coordinate getGeoPosition() {
		return geoPosition;
	}

	public void setGeoPosition(Coordinate geoPosition) {
		this.geoPosition = geoPosition;
	}
	
}
