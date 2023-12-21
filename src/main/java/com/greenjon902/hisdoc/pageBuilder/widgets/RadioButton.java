package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;
import com.greenjon902.hisdoc.webDriver.User;
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
	private final List<String> tooltips;

	/**
	 * Creates a new radioButtonBuilder
	 * @param groupName The group name to use
	 * @param defaultValue The default value to be active, this should be contained in values
	 * @param values The list of values that can be selected
	 * @param tooltips The tooltips for those values
	 * @param updateFunc The js to run when the tooltip is changed, this may run multiple times? idk
	 */
	public RadioButton(@NotNull String groupName, @NotNull String defaultValue, @NotNull List<String> values, @NotNull List<String> tooltips, @Nullable String updateFunc) {
		this.groupName = groupName;
		this.defaultValue = defaultValue;
		this.values = values;
		this.updateFunc = updateFunc;
		this.tooltips = tooltips;

		assert values.contains(defaultValue);
		assert values.size() == tooltips.size();
	}

	@Override
	public void render(HtmlOutputStream stream, User user) throws IOException {
		stream.write("<div class=\"radio-button-holder\">");
		for (int i=0; i<values.size(); i++) {
			String value = values.get(i);
			String tooltip = tooltips.get(i);
			renderButton(stream, value, tooltip, defaultValue.equals(value));
		}
		stream.write("</div>");
	}

	private void renderButton(HtmlOutputStream stream, String name, String tooltip, boolean defaultActive) throws IOException {
		// defaultActive is whether it is the default thing to be selected
		stream.write("<input type=\"radio\" class=\"radio-button\" " +
				"id=\"" + id(name) + "\"" +
				"value=\"" + name + "\" " +
				"name=\"" + groupName + "\"" + (defaultActive ? " checked" : "") + " " +
				(updateFunc != null ? "onchange=\"" + updateFunc + "\"" : "") + ">" +
				"<label title=\"" + tooltip + "\" class=\"radio-button\" for=\"" + id(name) + "\">" + name + "</label>");
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
