package com.greenjon902.hisdoc;

import com.greenjon902.hisdoc.pages.EventPage;
import com.greenjon902.hisdoc.sql.EventInfo;
import com.greenjon902.hisdoc.sql.SQL;
import com.greenjon902.hisdoc.sql.TagInfo;

import java.io.FileOutputStream;
import java.util.Map;

public class Main {


	public static void main(String[] args) throws Exception {
		FileOutputStream f = new FileOutputStream("./test.html");
		f.write(
				new EventPage(new SQL() {
					@Override
					public EventInfo getEvent(int id) {
						return new EventInfo("Omega Happens", "Omega starts a new base at (7777, -7777) causing the server to run out of storage.<br>The server runs out of storage. But world download problems delay the payed server.", "2022.02.05",
								new TagInfo[]{new TagInfo("Base", "green"), new TagInfo("Payed-Server", "red")});


					}
				}).render(null, Map.of("id", "123"), null).getBytes()
		);
		f.close();
	}
}
