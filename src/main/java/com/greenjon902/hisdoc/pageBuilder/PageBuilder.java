package com.greenjon902.hisdoc.pageBuilder;

import com.greenjon902.hisdoc.pageBuilder.scripts.Script;
import com.greenjon902.hisdoc.pageBuilder.widgets.AbstractContainerWidgetBuilder;
import com.greenjon902.hisdoc.webDriver.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

public class PageBuilder extends AbstractContainerWidgetBuilder {
	private String title;
	private final Set<PageVariable> pageVariables = new HashSet<>();
	private final Set<Script> scripts = new HashSet<>();


	public void render(HtmlOutputStream stream, User user) throws IOException {
		stream.write("<!doctype html>");
		stream.write("<html>");
		renderHead(stream, user);
		renderBody(stream, user);
		renderScripts(stream);
		stream.write("</html>");
	}

	/**
	 * Writes the links for the different themes to the stream, also leaves only the correct theme enabled so page
	 * loading does not flicker.
	 * <p>
	 * Last theme name is the default.
	 */
	private void renderThemes(HtmlOutputStream stream, User user, String... names) throws IOException {
		boolean hadEnabled = false;
		for (int i=0; i < names.length; i++) {
			String name = names[i];

			stream.write("<link class=\"theme\" href=\"themes?name=");
			stream.write(name);
			stream.write("\" rel=\"stylesheet\"");

			if (user.theme().equals(name) || (!hadEnabled && i == names.length - 1)) {
				hadEnabled = true;
			} else {
				stream.write(" disabled");
			}

			stream.write(">");
		}
	}

	protected void renderHead(HtmlOutputStream stream, User user) throws IOException {
		stream.write("<head>");
		if (title != null) {
			stream.write("<title>");
			stream.writeSafe(title);
			stream.write("</title>");
		}
		stream.write("<link href=\"themes?name=general\" rel=\"stylesheet\">");
		renderThemes(stream, user, "dark", "light");

		stream.write("</head>");
	}

	protected void renderBody(HtmlOutputStream stream, User user) throws IOException {
		if (!childrenBuilders.isEmpty()) {
			stream.write("<body>");
			renderAllChildren(stream, user);
			stream.write("</body>");
		}
	}

	protected void renderScripts(HtmlOutputStream stream) throws IOException {
		for (Script script : scripts) {
			script.writeTo(stream);
		}
	}

	public void render(OutputStream stream, User user) throws IOException {
		render(new HtmlOutputStream(stream), user);
	}

	public String render(User user) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			render(stream, user);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return stream.toString();
	}

	public void title(String title) {
		this.title = title;
	}

	public PageVariable addVariable(String id) {
		PageVariable pageVariable;
		do {
			pageVariable = new PageVariable(id);
		} while (pageVariables.contains(pageVariable));  // So we don't accidentally have duplicates

		pageVariables.add(pageVariable);
		return pageVariable;
	}

	public void addScript(Script script) {
		this.scripts.add(script);
	}
}
