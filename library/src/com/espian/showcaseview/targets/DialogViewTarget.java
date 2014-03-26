package com.espian.showcaseview.targets;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Point;
import android.view.View;

public class DialogViewTarget implements Target {

	private final View target;
	
	public DialogViewTarget(int viewId, AlertDialog dialog) {
		target = dialog.findViewById(viewId);
	}

	@Override
	public Point getPoint() {
        int[] location = new int[2];
        target.getLocationInWindow(location);
        int x = location[0] + target.getWidth() / 2;
        int y = location[1] + target.getHeight() / 2;
        return new Point(x, y);
	}

}
