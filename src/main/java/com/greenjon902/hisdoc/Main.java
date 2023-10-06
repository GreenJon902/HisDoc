package com.greenjon902.hisdoc;

import com.greenjon902.hisdoc.pages.EventPage;
import com.greenjon902.hisdoc.sql.mysqlImpl.*;

import java.io.FileOutputStream;
import java.util.Map;

public class Main {


	public static void main(String[] args) throws Exception {
		FileOutputStream f = new FileOutputStream("./test.html");
		f.write(
				new EventPage(new SQLManager() {
					@Override
					public EventInfo getEvent(int id) {
						return new EventInfo("Omega Happens", "Omega starts a new base at (7777, -7777) causing the server to run out of storage.<br>The server runs out of storage. But world download problems delay the payed server.", "2022.02.05",
								new TagInfo[]{new TagInfo("Base", "green"), new TagInfo("Payed-Server", "red")},
								new LinkInfo[]{
										LinkInfo.event("Server switches to apex", 1),
										LinkInfo.event("Omega makes a new base", 2),
										LinkInfo.event("Omega makes a another new base", 2),
										LinkInfo.event("Omega makes a yet another new base", 3),
										LinkInfo.event("Omega's bases are getting old now", 4)},
								new LinkInfo[] {
										LinkInfo.event("Omegadestroy400", 2)
								}, new Changelog[]{
										new Changelog("2023/09/31", "GreenJon", "Linked PLayers"),
										new Changelog("2023/09/35", "YourMother", "Linked events")
						}, "2023/09/30", "GreenJon");


					}
				}).render(null, Map.of("id", "123"), null).getBytes()
		);
		f.close();
	}
}
