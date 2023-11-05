package com.greenjon902.hisdoc.pageBuilder.scripts;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;
import com.greenjon902.hisdoc.pageBuilder.PageVariable;
import com.greenjon902.hisdoc.sql.results.UserData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This script provides the functionality to lazy load account names on the client side.
 * This works by replacing any instances of the page variable in the whole document with the correct value that was
 * loaded using the user info.
 */
public class LazyLoadAccountNameScript extends Script {
	private final List<PageVariable> accountNameVars = new ArrayList<>();  // Index of accountNameVars corresponds to index of userDatas
	private final List<UserData> userDatas = new ArrayList<>();

	public LazyLoadAccountNameScript(UserData userData, PageVariable accountNameVar) {  // Quick method for when only one
		add(userData, accountNameVar);
	}

	public LazyLoadAccountNameScript() {}

	/**
	 * Adds a userInfo to replace a {@link PageVariable} once it has been loaded on the client side.
	 * @param userData The user data to use
	 * @param accountNameVar The page variable
	 */
	public void add(UserData userData, PageVariable accountNameVar) {
		userDatas.add(userData);
		accountNameVars.add(accountNameVar);
	}

	@Override
	protected void writeScriptContents(HtmlOutputStream stream) throws IOException {
		assert userDatas.size() == accountNameVars.size();

		// Declare all the loading functions
		// Name format is "load" + prefix + "AccountName"
		StringBuilder js = new StringBuilder("""
			async function loadMISCELLANEOUSAccountName(info, accountNameVarName) {
				const name = info;
				console.log("Name: " +  name);
				//document.body.innerHTML = document.body.innerHTML.replaceAll(accountNameVarName, name);
			};
			
			async function loadMINECRAFTAccountName(info, accountNameVarName) {
				url = "https://playerdb.co/api/player/minecraft/" + info;
				console.log("Url: " + url);
				
				url = "https://corsproxy.io/?" + encodeURIComponent(url);
				console.log("Cors URL: " + url);
				const response = await fetch(url);
				
				console.log("Response: ");
				console.log(response);
				const responseJson = await response.json();
				console.log("Response Json: ");
				console.log(responseJson);
				
				const name = responseJson.data.player.username;
				console.log("Name: " +  name);
				
				document.body.innerHTML = document.body.innerHTML.replaceAll(accountNameVarName, name);
			};
			""");

		// Run functions for any users
		for (int i=0; i < userDatas.size(); i++) {
			String prefix = userDatas.get(i).type().toString();
			String info = userDatas.get(i).userData();
			js.append("load").append(prefix).append("AccountName(\"").append(info).append("\", \"").append(accountNameVars.get(i)).append("\");");
		}

		// Write to the stream
		stream.write(js.toString());
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
		if (object == null || getClass() != object.getClass()) return false;
		if (!super.equals(object)) return false;
		LazyLoadAccountNameScript that = (LazyLoadAccountNameScript) object;
		return Objects.equals(accountNameVars, that.accountNameVars) && Objects.equals(userDatas, that.userDatas);
	}

	@Override
	public int hashCode() {
		return Objects.hash(accountNameVars, userDatas);
	}
}
