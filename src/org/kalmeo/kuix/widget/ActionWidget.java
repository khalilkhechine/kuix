/*
 * This file is part of org.kalmeo.kuix.
 * 
 * org.kalmeo.kuix is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * org.kalmeo.kuix is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with org.kalmeo.kuix.  If not, see <http://www.gnu.org/licenses/>.
 *  
 * Creation date : 21 nov. 2007
 * Copyright (c) Kalmeo 2007-2008. All rights reserved.
 * http://www.kalmeo.org
 */

package org.kalmeo.kuix.widget;

import org.kalmeo.kuix.core.Kuix;
import org.kalmeo.kuix.core.KuixConstants;

/**
 * This class is base for all action widgets. <br>
 * <br>
 * <strong>For further informations, visit the <a
 * href="http://www.kalmeo.org/files/kuix/widgetdoc/index.html"
 * target="new">Kuix widgets reference page</a></strong>.
 * 
 * @author bbeaulant
 */
public class ActionWidget extends FocusableWidget {
	
	// The action method
	private String onAction;

	/**
	 * Construct a {@link ActionWidget}
	 *
	 * @param tag
	 */
	public ActionWidget(String tag) {
		super(tag);
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#setAttribute(java.lang.String, java.lang.String)
	 */
	public boolean setAttribute(String name, String value) {
		if (KuixConstants.ON_ACTION_ATTRIBUTE.equals(name)) {
			setOnAction(value);
			return true;
		}
		return super.setAttribute(name, value);
	}
	
	/**
	 * @return the onAction
	 */
	public String getOnAction() {
		return onAction;
	}

	/**
	 * @param onAction
	 */
	public void setOnAction(String onAction) {
		this.onAction = onAction;
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#processKeyEvent(byte, intt)
	 */
	public boolean processKeyEvent(byte type, int kuixKeyCode) {
		if (isEnabled()
				&& kuixKeyCode == KuixConstants.KUIX_KEY_FIRE
				&& type == KuixConstants.KEY_PRESSED_EVENT_TYPE) {
			return processActionEvent();
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#processShortcutKeyEvent(byte, int)
	 */
	public boolean processShortcutKeyEvent(byte type, int kuixKeyCode) {
		if (isEnabled()) {
			if (!super.processShortcutKeyEvent(type, kuixKeyCode)) {
				return processActionEvent();
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#processPointerEvent(byte, int, int)
	 */
	public boolean processPointerEvent(byte type, int x, int y) {
		if (isEnabled() && type == KuixConstants.POINTER_RELEASED_EVENT_TYPE) {
			boolean superProcess = super.processPointerEvent(type, x, y);
			return processActionEvent() || superProcess;
		}
		return super.processPointerEvent(type, x, y);
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#processActionEvent()
	 */
	public boolean processActionEvent() {
		if (onAction != null) {
			Kuix.callActionMethod(Kuix.parseMethod(onAction, this));
			return true;
		}
		return false;
	}
	
}
