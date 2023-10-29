package com.greenjon902.hisdoc.pageBuilder;

import com.greenjon902.hisdoc.pageBuilder.scripts.Script;
import com.greenjon902.hisdoc.pageBuilder.widgets.AbstractContainerWidgetBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

public class PageBuilder extends AbstractContainerWidgetBuilder {
	private String title;
	private final Set<PageVariable> pageVariables = new HashSet<>();
	private final Set<Script> scripts = new HashSet<>();


	public void render(HtmlOutputStream stream) throws IOException {
		stream.write("<!doctype html>");
		stream.write("<html>");
		renderHead(stream);
		renderBody(stream);
		renderScripts(stream);
		stream.write("</html>");
	}

	protected void renderHead(HtmlOutputStream stream) throws IOException {
		stream.write("<head>");
		if (title != null) {
			stream.write("<title>");
			stream.writeSafe(title);
			stream.write("</title>");
		}
		stream.write("<link rel=\"stylesheet\" href=\"https://fonts.googleapis.com/css2?family=Great+Vibes&display=swap\">");
		stream.write("<link rel=\"stylesheet\" href=\"https://fonts.googleapis.com/css?family=Roboto\">");
		stream.write("<link href=\"themes?name=general\" rel=\"stylesheet\">");
		stream.write("<link href=\"themes?name=light\" rel=\"stylesheet\">");
		stream.write("</head>");
	}

	protected void renderBody(HtmlOutputStream stream) throws IOException {
		if (!childrenBuilders.isEmpty()) {
			stream.write("<body>");
			renderAllChildren(stream);
			stream.write("</body>");
		}
	}

	protected void renderScripts(HtmlOutputStream stream) throws IOException {
		for (Script script : scripts) {
			script.writeTo(stream);
		}
	}

	public void render(OutputStream stream) throws IOException {
		render(new HtmlOutputStream(stream));
	}

	public String render() {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			render(stream);
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
