package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;
import com.greenjon902.hisdoc.webDriver.Session;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

/**
 * Creates radio buttons.
 * The name of the group is supplied, the IDs of the buttons created are {groupName}-{valueName}.
 */
public class RadioButton extends AbstractContainerWidgetBuilder {
	private final String groupName;
	private final String defaultValue;
	private final String updateFunc;
	private final List<String> values;

	public RadioButton(@NotNull String groupName, @NotNull String defaultValue, @NotNull List<String> values, @Nullable String updateFunc) {
		this.groupName = groupName;
		this.defaultValue = defaultValue;
		this.values = values;
		this.updateFunc = updateFunc;
		assert values.contains(defaultValue);
	}

	@Override
	public void render(HtmlOutputStream stream, Session session) throws IOException {
		stream.write("<div class=\"radio-button-holder\">");
		for (String value : values) {
			renderButton(stream, value, defaultValue.equals(value));
		}
		stream.write("</div>");
	}

	private void renderButton(HtmlOutputStream stream, String name, boolean defaultActive) throws IOException {
		// defaultActive is whether it is the default thing to be selected
		stream.write("<input type=\"radio\" class=\"radio-button\" " +
				"id=\"" + id(name) + "\"" +
				"value=\"" + name + "\" " +
				"name=\"" + groupName + "\"" + (defaultActive ? " checked" : "") + " " +
				(updateFunc != null ? "onchange=\"" + updateFunc + "\"" : "") + ">" +
				"<label class=\"radio-button\" for=\"" + id(name) + "\">" + name + "</label>");
	}

	/**
	 * Returns the id of a specific button with the name given, the name is one of the different buttons.
	 */
	public String id(String name) {
		return id() + "-" + name;
	}

	/**
	 * Returns the group name.
	 */
	public String id() {
		return groupName;
	}
}
