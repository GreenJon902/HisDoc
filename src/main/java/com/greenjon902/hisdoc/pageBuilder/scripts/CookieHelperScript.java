package com.greenjon902.hisdoc.pageBuilder.scripts;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;

import java.io.IOException;

/**
 * A script that creates the utility functions `setCookie`, and `getCookie`. <br>
 * <br>
 * `setCookie` takes the arguments `cname` - the name of the cookie -
 * and `cvalue` - the value to set for that cookie. Note that these cookies have no expiration date, and have `SameSite`
 * set to `Strict`. They also set path to "/" so I don't have to deal with duplicate cookies issues with testing between
 * the large and refined datasets.<br>
 * <br>
 * `getCookie` takes the argument `cname` - the name of the cookie.
 */
public class CookieHelperScript extends Script {
	@Override
	protected void writeScriptContents(HtmlOutputStream stream) throws IOException {
		// Taken from w3 schools
		stream.write("""
				function setCookie(cname, cvalue) {
				  document.cookie = cname + "=" + cvalue + ";SameSite=Strict;path=/; ";
				}

				function getCookie(cname) {
				  let name = cname + "=";
				  let ca = document.cookie.split(';');
				  for(let i = 0; i < ca.length; i++) {
				    let c = ca[i];
				    while (c.charAt(0) == ' ') {
				      c = c.substring(1);
				    }
				    if (c.indexOf(name) == 0) {
				      return c.substring(name.length, c.length);
				    }
				  }
				}""");
		stream.write("""
				function eraseCookie(name) {  \s
				    document.cookie = name+'=; Max-Age=-99999999;SameSite=Strict;'; \s
				}""");
	}
}
