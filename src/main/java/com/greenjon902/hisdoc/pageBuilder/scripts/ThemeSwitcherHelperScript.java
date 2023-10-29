package com.greenjon902.hisdoc.pageBuilder.scripts;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;

import java.io.IOException;

public class ThemeSwitcherHelperScript extends Script {
	@Override
	protected void writeScriptContents(HtmlOutputStream stream) throws IOException {
		stream.write("""
    			function cycleThemes() {
    				const themeName = getComputedStyle(document.getElementById('theme-switcher-button')).getPropertyValue('--next-stylesheet');
    				console.log("Switching theme to " + themeName)
    			
					document.getElementById('theme').href = 'themes?name=' + themeName;
					correctThemeButton();
				}
				
				function correctThemeButton() {
					document.getElementById('theme-switcher-icon').className = getComputedStyle(document.getElementById('theme-switcher-button')).getPropertyValue('--icon-class');
				}
				
				correctThemeButton();
				""");
	}
}
