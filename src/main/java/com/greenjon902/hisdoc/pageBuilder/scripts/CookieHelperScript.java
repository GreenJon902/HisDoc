package com.greenjon902.hisdoc.pageBuilder.scripts;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;

import java.io.IOException;

public class CookieHelperScript extends Script {
	@Override
	protected void writeScriptContents(HtmlOutputStream stream) throws IOException {
		// Taken from w3 schools
		stream.write("function setCookie(cname, cvalue) {\n" +
				"  document.cookie = cname + \"=\" + cvalue + \";SameSite=Strict\";\n" +
				"}\n" +
				"\n" +
				"function getCookie(cname) {\n" +
				"  let name = cname + \"=\";\n" +
				"  let ca = document.cookie.split(';');\n" +
				"  for(let i = 0; i < ca.length; i++) {\n" +
				"    let c = ca[i];\n" +
				"    while (c.charAt(0) == ' ') {\n" +
				"      c = c.substring(1);\n" +
				"    }\n" +
				"    if (c.indexOf(name) == 0) {\n" +
				"      return c.substring(name.length, c.length);\n" +
				"    }\n" +
				"  }\n" +
				"  return \"\";\n" +
				"}");
	}
}
