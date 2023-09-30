package com.greenjon902.hisdoc.pages;

import com.greenjon902.hisdoc.sql.*;
import com.greenjon902.hisdoc.webDriver.User;

import java.util.Map;

public class EventPage {
	private final SQLManager sqlManager;

	public EventPage(SQLManager sqlManager) {
		this.sqlManager = sqlManager;
	}

	public String render(User user, Map<String, String> query, String fragment) {
		int eventId = Integer.parseInt(query.get("id"));
		EventInfo eventInfo = sqlManager.getEvent(eventId);

		StringBuilder builder = new StringBuilder();
		builder.append("<html>");

		builder.append("<head>");
		builder.append("<link rel=\"stylesheet\" href=\"https://fonts.googleapis.com/css?family=Roboto\">");
		builder.append("<style>");

		builder.append("html {background:#f8f9fa;min-height:100%;}");
		builder.append("body {background:#ffffff;max-width:95.75em;min-width:31.25em;padding:1em;margin:0 auto;min-height:100vh;font-family: \"Roboto\", sans-serif;}");;
		builder.append(".date {color:#555555;margin-right:1em;float:right;}");
		builder.append("#circle {\n" +
				"      width: 15px;\n" +
				"      height: 15px;\n" +
				"      -webkit-border-radius: 25px;\n" +
				"      -moz-border-radius: 25px;\n" +
				"      background: white;\n" +
				"      float: left;\n" +
				"      margin-right: 2.5px;\n" +
				"    }");
		builder.append(".tag\n" +
				"{\n" +
				"  opacity:0.7;\n" +
				"  transition: opacity .2s ease-out;\n" +
				"  -moz-transition: opacity .2s ease-out;\n" +
				"  -webkit-transition: opacity .2s ease-out;\n" +
				"  -o-transition: opacity .2s ease-out;\n" +
				"}\n" +
				"\n" +
				".tag:hover\n" +
				"{\n" +
				"  opacity:1;\n" +
				"  transition: opacity .2s ease-out;\n" +
				"  -moz-transition: opacity .2s ease-out;\n" +
				"  -webkit-transition: opacity .2s ease-out;\n" +
				"  -o-transition: opacity .2s ease-out;\n" +
				"}");
		builder.append(".related\n" +
				"{\n" +
				"  text-decoration:none;\n" +
				"}\n" +
				"\n" +
				".related:hover\n" +
				"{\n" +
				"  text-decoration:underline;\n" +
				"}");

		builder.append("</style>");
		builder.append("</head>");


		builder.append("<body>");

		builder.append("<div style=\"justify-content: space-between;display: flex;\">");
		builder.append("<div>");
		builder.append("<span style=\"font-size:4em;font-family:serif;\"><b>").append(eventInfo.name()).append("</b></span>");

		builder.append("<br><span style=\"font-size:1.2em;font-family:serif;\"><b>").append("Description").append("</b></span>");
		builder.append("<hr>");
		builder.append("<p>").append(eventInfo.description()).append("</p>");

		builder.append("<br>");
		builder.append("<span style=\"font-size:1.2em;font-family:serif;\"><b>").append("Changelog").append("</b></span>");
		builder.append("<hr>");
		builder.append("<div>");

		int maxSpace = 0;
		for (Changelog changelog : eventInfo.changelogs()) {
			if (changelog.authorName().length() > maxSpace) {
				maxSpace = changelog.authorName().length();
			}
		}

		builder.append("<span>").append(eventInfo.createdDate()).append("&nbsp;&nbsp;&nbsp;");
		builder.append(String.format("%1$" + maxSpace + "s", eventInfo.postedBy()).replace(" ", "&nbsp;&nbsp;"));
		builder.append(": </span>");
		builder.append("<span class=\"date\" style=\"float:none\">").append("This event was created!").append("</span>");

		for (Changelog changelog : eventInfo.changelogs()) {
			builder.append("<br>");
			builder.append("<span>").append(changelog.dateString()).append("&nbsp;&nbsp;&nbsp;");
			builder.append(String.format("%1$" + maxSpace + "s", changelog.authorName()).replace(" ", "&nbsp;&nbsp;"));
			builder.append(": </span>");
			builder.append("<span class=\"date\" style=\"float:none\">").append(changelog.changeDescription()).append("</span>");
		}
		builder.append("</div>");

		builder.append("</div>");
		builder.append("<div>");

		builder.append("<span class=\"date\">").append(eventInfo.dateString()).append("</span>");
		builder.append("<br>");

		builder.append("<span><u>Related Events</u></span>");
		builder.append("<div style=\"overflow:auto;border-width:0.1em;border-color:#666666;\">");
		for (TagInfo tag : eventInfo.tags()) {
			builder.append(renderTag(tag));
		}
		builder.append("</div>");
		builder.append("<br>");

		builder.append("<span><u>Related Events</u></span>");
		for (LinkInfo link : eventInfo.relatedEvents()) {
			builder.append("<br>");
			builder.append("<a class=\"related\" href=\"event?id=").append(link.id()).append("\">").append(link.name()).append("</a>");
		}
		builder.append("<br>");
		builder.append("<br>");

		builder.append("<span><u>Related Players</u></span>");
		for (LinkInfo link : eventInfo.relatedPlayers()) {
			builder.append("<br>");
			builder.append("<a class=\"related\" href=\"player?id=").append(link.id()).append("\">").append(link.name()).append("</a>");
		}

		builder.append("</div>");
		builder.append("</div>");


		// TODO: Images from sql
		//builder.append("<div style=\"border-style:solid;padding:10px;overflow:auto;margin-top:10px;border-width:1px;border-color:#666666;\">")
		//		.append(renderImage("https://ishadeed.com/assets/spacing-css/spacing-1.png"))
		//		.append(renderImage("https://ishadeed.com/assets/spacing-css/negative-margin-1.png"))
		//		.append(renderImage("https://cdn.discordapp.com/attachments/902163346898423808/1152957142790316182/IMG_9455.jpg?ex=6518c943&is=651777c3&hm=d946b58e30af913110f609127376752576fd21e717e7ac3921f630051aa69919&"))
		//		.append(renderImage("https://cdn.discordapp.com/attachments/902163346898423808/1149975706118389820/image.png?ex=65192555&is=6517d3d5&hm=2923716cd3fbadb99797ac62fcdf733e0e03d8aff77865170fad99bd15a9fa57&"))
		//		.append("</div>");

		//builder.append("</body>");
		//builder.append("</html>");

		return builder.toString();
	}

	public String renderTag(TagInfo tag) {
		StringBuilder builder = new StringBuilder();
		builder.append("<div class=\"tag\" style=\"")
					.append("background-color:").append(tag.color()).append(";")
					.append("float:").append("left").append(";")
					.append("padding:").append("2px 5px").append(";")
					//.append("padding-left:").append("20px").append(";")
					.append("border-radius:").append("20px").append(";")
					.append("margin-right:").append("10px").append(";")
					.append("align-items:").append("center").append(";")
					.append("justify-items:").append("center").append(";")
					.append("display:").append("flex").append(";")
				.append("\">")
				.append("<div id=\"circle\">").append("</div>")
				.append("<a href=\"tag\" style=\"color:#ffffff;margin-right:5px;\"><b>").append(tag.text()).append("</b></a>")
		.append("</div>");
		return builder.toString();
	}

	public String renderImage(String url) {
		StringBuilder builder = new StringBuilder();
		builder.append("<div style=\"")
					.append("float:").append("left").append(";")
					.append("margin-right:").append("10px").append(";")
				.append("\">")
				.append("<img src=\"").append(url).append("\" width=\"100\" height=\"100\" style=\"border-radius:10px;\">")
				.append("</div>");
		return builder.toString();
	}
}
