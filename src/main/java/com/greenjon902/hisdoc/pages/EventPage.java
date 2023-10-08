package com.greenjon902.hisdoc.pages;

import com.greenjon902.hisdoc.sql.Dispatcher;
import com.greenjon902.hisdoc.sql.results.*;
import com.greenjon902.hisdoc.webDriver.User;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class EventPage {

	private final Dispatcher dispatcher;

	public EventPage(Dispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	public String render(User user, Map<String, String> query, String fragment) throws SQLException {
		int eventId = Integer.parseInt(query.get("id"));
		EventInfo eventInfo = dispatcher.getEventInfo(eventId);

		if (eventInfo == null) {
			return "No event found :(";
		}

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

		String postedBy;
		if (eventInfo.postedBy() == null) {
			postedBy = "Unknown";
		} else {
			postedBy = eventInfo.postedBy().userInfo();
		}

		int maxSpace = postedBy.length();
		for (ChangeInfo changeInfo : eventInfo.changeInfos()) {
			int space;
			System.out.println(changeInfo.author().userInfo());
			if ((space = changeInfo.author().userInfo().length()) > maxSpace) {
				maxSpace = space;
			}
		}

		String postedDate = "Unknown";
		if (eventInfo.postedDate() != null) {
			postedDate = eventInfo.postedDate().toString();
		}

		builder.append("<span>").append(postedDate).append("&nbsp;&nbsp;&nbsp;");
		builder.append(String.format("%1$" + maxSpace + "s", postedBy).replace(" ", "&nbsp;&nbsp;"));
		builder.append(": </span>");
		builder.append("<span class=\"date\" style=\"float:none\">").append("This event was created!").append("</span>");

		for (ChangeInfo changeInfo : eventInfo.changeInfos()) {
			builder.append("<br>");
			builder.append("<span>").append(changeInfo.date()).append("&nbsp;&nbsp;&nbsp;");
			builder.append(String.format("%1$" + maxSpace + "s", changeInfo.author()).replace(" ", "&nbsp;&nbsp;"));
			builder.append(": </span>");
			builder.append("<span class=\"date\" style=\"float:none\">").append(changeInfo.description()).append("</span>");
		}
		builder.append("</div>");

		builder.append("</div>");
		builder.append("<div>");

		builder.append("<span class=\"date\">").append(formatDateString(eventInfo.eventDateInfo())).append("</span>");
		builder.append("<br>");

		builder.append("<span><u>Related Events</u></span>");
		builder.append("<div style=\"overflow:auto;border-width:0.1em;border-color:#666666;\">");
		System.out.println(eventInfo.tagInfos());
		for (TagInfo tag : eventInfo.tagInfos()) {
			builder.append(renderTag(tag));
		}
		builder.append("</div>");
		builder.append("<br>");

		/*builder.append("<span><u>Related Events</u></span>");
		for (LinkInfo link : eventInfo.relatedEvents()) {
			builder.append("<br>");
			builder.append("<a class=\"related\" href=\"event?id=").append(link.id()).append("\">").append(link.name()).append("</a>");
		}
		builder.append("<br>");
		builder.append("<br>");*/

		builder.append("<span><u>Related Players</u></span>");
		for (UserInfo userInfo : eventInfo.relatedPlayerInfos()) {
			builder.append("<br>");
			builder.append("<a class=\"related\" href=\"player?id=").append(userInfo.id()).append("\">").append(userInfo.userInfo()).append("</a>");
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

		builder.append("</body>");
		builder.append("</html>");

		return builder.toString();
	}

	private String formatDateString(DateInfo dateInfo) {
		if (Objects.equals(dateInfo.type(), "c")) {
			String pattern = "";
			switch (dateInfo.precision()) {
				case "d": pattern += "yyyy-MM-dd";
				case "h": pattern += " hh";
				case "m": pattern += ":mm";
			}
			String center = dateInfo.date1().toLocalDateTime().format(DateTimeFormatter.ofPattern(pattern));
			String diff = "";
			if (dateInfo.diff() != 0) {
				diff = " &plusmn;" + String.format("%02d", dateInfo.diff()) + dateInfo.diffType().toUpperCase(Locale.ROOT);
			}
			return center + diff;
		} else {
			return "Somewhere between " + dateInfo.date1().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) +
					" and " + dateInfo.date2().toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		}
	}

	public String renderTag(TagInfo tag) {
		StringBuilder builder = new StringBuilder();
		//.append("padding-left:").append("20px").append(";")
		builder.append("<div class=\"tag\" style=\"")
				.append("background-color:").append(String.format("%06x", tag.color())).append(";")
				.append("float:").append("left").append(";")
				.append("padding:").append("2px 5px").append(";")
				//.append("padding-left:").append("20px").append(";")
				.append("border-radius:").append("20px").append(";")
				.append("margin-right:").append("10px").append(";")
				.append("align-items:").append("center").append(";")
				.append("justify-items:").append("center").append(";")
				.append("display:").append("flex").append(";")
				.append("\">")
				.append("<div id=\"circle\"></div><a href=\"tag=").append(tag.id()).append("\" style=\"color:#ffffff;margin-right:5px;\"><b>").append(tag.name()).append("</b></a>")
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
