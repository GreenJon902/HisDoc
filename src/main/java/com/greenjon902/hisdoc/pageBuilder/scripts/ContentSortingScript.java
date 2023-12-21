package com.greenjon902.hisdoc.pageBuilder.scripts;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;

import java.io.IOException;

/**
 * Creates a function in js called "sortElements" which will sort the children of an element with a given
 * comparison function.
 * This is useful when we have to sort items that cannot be sorted on the server, e.g. the results of
 * {@link LazyLoadAccountNameScript}.
 */
public class ContentSortingScript extends Script {
	public final String id;
	public final String comparisonFunction;
	public final boolean runByDefault;

	/**
	 * Creates a new ContentSortingScript
	 * @param id The id of the container containing the items to sort
	 * @param comparisonFunction The js to use to do the comparison, this will be given variables a and b. e.g. `a.text.localeCompare(b.text)`
	 * @param runByDefault Should the sort function be run at the start
	 */
	public ContentSortingScript(String id, String comparisonFunction, boolean runByDefault) {
		this.id = id;
		this.comparisonFunction = comparisonFunction;
		this.runByDefault = runByDefault;
	}

	@Override
	protected void writeScriptContents(HtmlOutputStream stream) throws IOException {
		stream.write("function sortElements() {" +
				"	let div = document.getElementById(\"" + id + "\");\n" +
				"	\n" +
				"	children = Array.prototype.slice.call(div.children, 0); // Turn into array\n" +
				"	children.sort(function(a, b) {\n" +
				"	  return " + comparisonFunction + ";\n" +
				"	});\n" +
				"	\n" +
				"	div.innerHtml = \"\"; // Clear contents\n" +
				"	div.innerText = \"\"; // Clear newlines\n" +
				"	for(var i = 0; i < children.length; i++) { // Refill\n" +
				"	    div.appendChild(children[i]);\n" +
				"	}\n" +
				"}\n");
		if (runByDefault) {
			stream.write("sortElements();\n");
		}
	}
}
