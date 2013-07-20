package com.titan;

import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;

@SuppressWarnings("deprecation")
public class MacAboutBoxHandler extends ApplicationAdapter {

	public void handleAbout(ApplicationEvent event) {
		event.setHandled(true);
		//		new AboutUsDialog(null).setVisible(true);
	}

	public void handleQuit(ApplicationEvent event) {
		TitanSetting.getInstance().save();
		System.exit(0);
	}
}
