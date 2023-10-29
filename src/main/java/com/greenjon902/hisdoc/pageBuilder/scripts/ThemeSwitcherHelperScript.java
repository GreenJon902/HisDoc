package com.greenjon902.hisdoc.pageBuilder.scripts;

import com.greenjon902.hisdoc.pageBuilder.HtmlOutputStream;
import com.greenjon902.hisdoc.pageBuilder.PageBuilder;

import java.io.IOException;

public class ThemeSwitcherHelperScript extends Script {
    public ThemeSwitcherHelperScript(PageBuilder pageBuilder) {
        pageBuilder.addScript(new CookieHelperScript());
    }

    @Override
    protected void writeScriptContents(HtmlOutputStream stream) throws IOException {
        stream.write("""
                function cycleThemes() {
                    var themeName = getComputedStyle(document.getElementById('theme-switcher-button')).getPropertyValue('--next-stylesheet');
                    switchTo(themeName);
                }
                                
                function switchTo(themeName) {
                    console.log("Switching theme to " + themeName);
                    
                    var themeLinks = document.getElementsByClassName('theme');
                    for (var i = 0; i < themeLinks.length; i++) {
                        var link = themeLinks[i];
                        if (link.href.endsWith(themeName)) {
                            link.disabled = false;
                        } else {
                            link.disabled = true;
                        }
                    }
                    correctThemeButton();
                    
                    setCookie("theme", themeName);  // Saves the theme for next page
                }
                                
                function correctThemeButton() {
                    var icon = document.getElementById('theme-switcher-icon');

                    if (icon != null) {
                        icon.className = getComputedStyle(document.getElementById('theme-switcher-button')).getPropertyValue('--icon-class');
                    } else {
                        document.onreadystatechange = () => {
                          if (document.readyState === 'complete') {
                            correctThemeButton();
                          }
                        };
                    }
                }
                
                // Ensure that correctThemeButton will be called upon any stylesheet load, as it may not be loaded yet  
                // when themes are cycled, so this will make sure it is called afterwards
                function fixThemeButtonIcon(themeName) {
                    var themeLinks = document.getElementsByClassName('theme');
                    for (var i = 0; i < themeLinks.length; i++) {
                        var link = themeLinks[i];
                        link.onload = correctThemeButton;
                    }
                    correctThemeButton();
                }
               
                fixThemeButtonIcon();  // Ensure correct theme button
                // Default theme is already here so nothing extra needs to be done!
                """);
    }
}
