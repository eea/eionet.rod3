package eionet.rod.model;

public class ObligationCountry {
	 
	private Integer countryId;
	private String countryName;
	private String twoLetter;
	private String isMemberCountry;

	private String voluntary;

	public Integer getCountryId() {
		return countryId;
	}

	public void setCountryId(Integer countryId) {
		this.countryId = countryId;
	}
	
	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	
	public String getTwoLetter() {
		return twoLetter;
	}

	public void setTwoLetter(String twoLetter) {
		this.twoLetter = twoLetter;
	}

	public String getIsMemberCountry() {
		return isMemberCountry;
	}

	public void setIsMemberCountry(String isMemberCountry) {
		this.isMemberCountry = isMemberCountry;
	}

	public String getVoluntary() {
		return voluntary;
	}

	public void setVoluntary(String voluntary) {
		this.voluntary = voluntary;
	}
}
