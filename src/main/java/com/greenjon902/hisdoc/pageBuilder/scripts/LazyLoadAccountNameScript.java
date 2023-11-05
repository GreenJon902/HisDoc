package com.greenjon902.hisdoc.pageBuilder.scripts;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;
import com.greenjon902.hisdoc.pageBuilder.PageVariable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class LazyLoadAccountNameScript extends Script {
	private final List<PageVariable> accountNameVars = new ArrayList<>();
	private final List<String> userInfos = new ArrayList<>();

	public LazyLoadAccountNameScript(String userInfo, PageVariable accountNameVar) {  // Quick method for when only one
		add(userInfo, accountNameVar);
	}

	public LazyLoadAccountNameScript() {}

	public void add(String userInfo, PageVariable accountNameVar) {
		userInfos.add(userInfo);
		accountNameVars.add(accountNameVar);
	}

	@Override
	protected void writeScriptContents(HtmlOutputStream stream) throws IOException {
		assert userInfos.size() == accountNameVars.size();

		// Declare all the loading functions
		// Name format is "load" + prefix + "AccountName"
		StringBuilder js = new StringBuilder("""
			async function loadAccountName(info, accountNameVarName) {  // When has no prefix (not connected to anything)
				const name = info;
				console.log("Name: " +  name);
				document.body.innerHTML = document.body.innerHTML.replaceAll(accountNameVarName, name);
			};
			
			async function loadMCAccountName(info, accountNameVarName) {
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
		for (int i=0; i < userInfos.size(); i++) {
			String[] parts = userInfos.get(i).split("\\|", 2);
			String prefix;
			String info;
			if (parts.length == 1) {
				prefix = "";
				info = parts[0];
			} else if (parts.length == 2) {
				prefix = parts[0];
				info = parts[1];
			} else {
				throw new RuntimeException("Unknown prefix for user info \"" + userInfos.get(i) + "\" with parts " + Arrays.toString(parts));
			}

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
		return Objects.equals(accountNameVars, that.accountNameVars) && Objects.equals(userInfos, that.userInfos);
	}

	@Override
	public int hashCode() {
		return Objects.hash(accountNameVars, userInfos);
	}
}
