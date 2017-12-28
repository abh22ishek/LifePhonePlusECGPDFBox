package com.lppbpl.android.userapp.listener;

public interface SecondaryUserNotification {

	/**
	 * Notify bg record type.
	 */
	public abstract void notifyBgRecordType();

	/**
	 * Method parseMyMembersResponse.
	 *
	 * @param response
	 *            String
	 */
	public abstract void parseMyMembersResponse(String response);

	/**
	 * Method updateSelectedMemberUI.
	 *
	 * @param selectedMember
	 *            String
	 */
	public abstract void updateSelectedMemberUI(String selectedMember);

}
