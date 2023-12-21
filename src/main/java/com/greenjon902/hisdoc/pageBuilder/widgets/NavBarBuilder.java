package com.greenjon902.hisdoc.pageBuilder.widgets;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;
import com.greenjon902.hisdoc.pageBuilder.PageBuilder;
import com.greenjon902.hisdoc.webDriver.User;

import java.io.IOException;

import static com.greenjon902.hisdoc.pageBuilder.widgets.IconBuilder.IconType.*;

public class NavBarBuilder implements WidgetBuilder {
	private final ContainerWidgetBuilder navBar = new ContainerWidgetBuilder();

	public NavBarBuilder(PageBuilder pageBuilder) {
		ColumnLayoutBuilder columnLayoutBuilder1 = new ColumnLayoutBuilder();
		ColumnLayoutBuilder columnLayoutBuilder2 = new ColumnLayoutBuilder();

		columnLayoutBuilder2.add(new LogoBuilder());
		columnLayoutBuilder2.add(new TextBuilder(TextType.NORMAL) {{
			add(new IconBuilder(G_DEM), "..", false, "G-Dem Home Page");
		}});
		columnLayoutBuilder2.add(new TextBuilder(TextType.NORMAL) {{
			add(new IconBuilder(TIMELINE), "timeline", false, "Timeline");
		}});
		columnLayoutBuilder2.add(new TextBuilder(TextType.NORMAL) {{
			add(new IconBuilder(PERSONS), "persons", false, "People");
		}});
		columnLayoutBuilder2.add(new TextBuilder(TextType.NORMAL) {{
			add(new IconBuilder(TAGS), "tags", false, "Tags");
		}});
		columnLayoutBuilder2.add(new TextBuilder(TextType.NORMAL) {{
			add(new IconBuilder(ADD), "add", false, "Add Event");
		}});

		columnLayoutBuilder1.add(columnLayoutBuilder2);
		columnLayoutBuilder1.add(new ThemeSwitcherBuilder(pageBuilder));

		navBar.add(columnLayoutBuilder1);
		navBar.add(new SeparatorBuilder(0.3));
	}

	@Override
	public void render(HtmlOutputStream stream, User user) throws IOException {
		navBar.render(stream, user);
	}
}
