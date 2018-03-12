package eionet.rod.model;

import java.util.List;

public class Roles {

	private String roleName;
	private String roleEmail;
	private String roleUrl;
	private String roleId;
	private String roleMembers;
	private String roleLastHarvested;
	private String roleStatus;
	
	private List<String> RolesAdd;
	
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String getRoleEmail() {
		return roleEmail;
	}
	public void setRoleEmail(String roleEmail) {
		this.roleEmail = roleEmail;
	}
	public String getRoleUrl() {
		return roleUrl;
	}
	public void setRoleUrl(String roleUrl) {
		this.roleUrl = roleUrl;
	}
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public String getRoleMembers() {
		return roleMembers;
	}
	public void setRoleMembers(String roleMembers) {
		this.roleMembers = roleMembers;
	}
	public String getRoleLastHarvested() {
		return roleLastHarvested;
	}
	public void setRoleLastHarvested(String roleLastHarvested) {
		this.roleLastHarvested = roleLastHarvested;
	}
	public String getRoleStatus() {
		return roleStatus;
	}
	public void setRoleStatus(String roleStatus) {
		this.roleStatus = roleStatus;
	}
	public List<String> getRolesAdd() {
		return RolesAdd;
	}
	public void setRolesAdd(List<String> rolesAdd) {
		RolesAdd = rolesAdd;
	}
	
}
