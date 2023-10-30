package com.greenjon902.hisdoc.pageBuilder;

import com.greenjon902.hisdoc.pageBuilder.scripts.Script;
import com.greenjon902.hisdoc.pageBuilder.widgets.AbstractContainerWidgetBuilder;
import com.greenjon902.hisdoc.webDriver.Session;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

public class PageBuilder extends AbstractContainerWidgetBuilder {
	private String title;
	private final Set<PageVariable> pageVariables = new HashSet<>();
	private final Set<Script> scripts = new HashSet<>();


	public void render(HtmlOutputStream stream, Session session) throws IOException {
		stream.write("<!doctype html>");
		stream.write("<html>");
		renderHead(stream, session);
		renderBody(stream, session);
		renderScripts(stream);
		stream.write("</html>");
	}

	/**
	 * Writes the links for the different themes to the stream, also leaves only the correct theme enabled so page
	 * loading does not flicker.
	 * <p>
	 * Last theme name is the default.
	 */
	private void renderThemes(HtmlOutputStream stream, Session session, String... names) throws IOException {
		boolean hadEnabled = false;
		for (int i=0; i < names.length; i++) {
			String name = names[i];

			stream.write("<link class=\"theme\" href=\"themes?name=");
			stream.write(name);
			stream.write("\" rel=\"stylesheet\"");

			if (session.theme().equals(name) || (!hadEnabled && i == names.length - 1)) {
				hadEnabled = true;
			} else {
				stream.write(" disabled");
			}

			stream.write(">");
		}
	}

	protected void renderHead(HtmlOutputStream stream, Session session) throws IOException {
		stream.write("<head>");
		if (title != null) {
			stream.write("<title>");
			stream.writeSafe(title);
			stream.write("</title>");
		}
		stream.write("<link rel=\"stylesheet\" href=\"https://fonts.googleapis.com/css2?family=Great+Vibes&display=swap\">");
		stream.write("<link rel=\"stylesheet\" href=\"https://fonts.googleapis.com/css?family=Roboto\">");
		stream.write("<link rel=\"stylesheet\" href=\"https://unpkg.com/css.gg@2.0.0/icons/css/sun.css\" >");
		stream.write("<link rel=\"stylesheet\" href=\"https://unpkg.com/css.gg@2.0.0/icons/css/moon.css\" >");
		stream.write("<link href=\"themes?name=general\" rel=\"stylesheet\">");
		renderThemes(stream, session, "dark", "light");

		stream.write("</head>");
	}

	protected void renderBody(HtmlOutputStream stream, Session session) throws IOException {
		if (!childrenBuilders.isEmpty()) {
			stream.write("<body>");
			renderAllChildren(stream, session);
			stream.write("</body>");
		}
	}

	protected void renderScripts(HtmlOutputStream stream) throws IOException {
		for (Script script : scripts) {
			script.writeTo(stream);
		}
	}

	public void render(OutputStream stream, Session session) throws IOException {
		render(new HtmlOutputStream(stream), session);
	}

	public String render(Session session) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			render(stream, session);
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
