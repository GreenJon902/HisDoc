package com.greenjon902.hisdoc.pageBuilder;

import com.greenjon902.hisdoc.pageBuilder.widgets.ContainerWidgetBuilder;
import com.greenjon902.hisdoc.pageBuilder.widgets.LogoBuilder;
import com.greenjon902.hisdoc.pageBuilder.widgets.WidgetBuilder;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class PageBuilder extends ContainerWidgetBuilder {
	private String title;


	public void render(HtmlOutputStream stream) throws IOException {
		stream.write("<html>");
		renderHead(stream);
		renderBody(stream);
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
}
