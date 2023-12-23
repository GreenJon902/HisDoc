package com.greenjon902.hisdoc.pageBuilder.scripts;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;
import com.greenjon902.hisdoc.pageBuilder.PageVariable;
import com.greenjon902.hisdoc.sql.results.PersonData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This script provides the functionality to lazy load account names on the client side.
 * This works by replacing any instances of the page variable in the whole document with the correct value that was
 * loaded using the person info.<br>
 * We also cache personnames in localStorage under the key "mcAccountNameCache_{UUID}".
 */
public class LazyLoadAccountNameScript extends Script {
	private final List<PageVariable> accountNameVars = new ArrayList<>();  // Index of accountNameVars corresponds to index of personDatas
	private final List<PersonData> personDatas = new ArrayList<>();
	private final List<String> callbacks = new ArrayList<>();

	public LazyLoadAccountNameScript(PersonData personData, PageVariable accountNameVar) {  // Quick method for when only one
		add(personData, accountNameVar);
	}

	public LazyLoadAccountNameScript() {}

	/**
	 * Adds a personInfo to replace a {@link PageVariable} once it has been loaded on the client side.
	 * @param personData The person data to use
	 * @param accountNameVar The page variable
	 */
	public void add(PersonData personData, PageVariable accountNameVar) {
		personDatas.add(personData);
		accountNameVars.add(accountNameVar);
	}

	@Override
	protected void writeScriptContents(HtmlOutputStream stream) throws IOException {
		assert personDatas.size() == accountNameVars.size();

		// Declare all the loading functions
		// Name format is "load" + prefix + "AccountName"
		StringBuilder js = new StringBuilder("""
			async function loadMISCELLANEOUSAccountName(info, accountNameVarName) {
				const name = info;
				console.log("Name: " +  name);
				if (document.head.innerHTML.includes(accountNameVarName)) { 
					document.head.innerHTML = document.head.innerHTML.replaceAll(accountNameVarName, name);
				}
				document.body.innerHTML = document.body.innerHTML.replaceAll(accountNameVarName, name);
				lazyLoadCallbacks();
			};
			
			async function loadMINECRAFTAccountName(info, accountNameVarName) {
				var name = localStorage["mcAccountNameCache_" + info];
				
				if (name == null) {
					try {
						url = "https://playerdb.co/api/player/minecraft/" + info;
						console.log("Url: " + url);
						
						url = "https://corsproxy.io/?" + encodeURIComponent(url);
						console.log("Cors URL: " + url);
						const response = await fetch(url);
						
						console.log("Response: ");
						console.log(response);
						const responseJson = await (response.json());
						console.log("Response Json: ");
						console.log(responseJson);
						
						name = responseJson.data.player.username;
						console.log("Name: " +  name);
						
						localStorage["mcAccountNameCache_" + info] = name;
						
					} catch(error) {
						name = "Unknown";
						console.error(error);
					}
				}
				
				// FIXME: Find a better solution for https://github.com/GreenJon902/HisDoc/issues/11
				// We also replace head in the other lazyLoad functions
				if (document.head.innerHTML.includes(accountNameVarName)) { 
					document.head.innerHTML = document.head.innerHTML.replaceAll(accountNameVarName, name);
				}
				document.body.innerHTML = document.body.innerHTML.replaceAll(accountNameVarName, name);
				lazyLoadCallbacks();
			};
			""");

		js.append("function lazyLoadCallbacks() {\n");
		for (String callback : callbacks) {
			js.append(callback).append(";\n");
		}
		js.append("};\n");

		// Run functions for any persons
		for (int i=0; i < personDatas.size(); i++) {
			String prefix = personDatas.get(i).type().toString();
			String info = personDatas.get(i).personData();
			js.append("load").append(prefix).append("AccountName(\"").append(info).append("\", \"").append(accountNameVars.get(i)).append("\");\n");
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
		return Objects.equals(accountNameVars, that.accountNameVars) && Objects.equals(personDatas, that.personDatas);
	}

	@Override
	public int hashCode() {
		return Objects.hash(accountNameVars, personDatas);
	}

	/**
	 * Adds a callback. These will be run after each name has been loaded.
	 * @param js The js to run
	 */
	public void addCallback(String js) {
		callbacks.add(js);
	}
}
