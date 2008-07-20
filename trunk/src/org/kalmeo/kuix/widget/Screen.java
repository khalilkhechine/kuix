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
 * Creation date : 5 dec. 07
 * Copyright (c) Kalmeo 2007-2008. All rights reserved.
 * http://www.kalmeo.org
 */

package org.kalmeo.kuix.widget;

import org.kalmeo.kuix.core.Kuix;
import org.kalmeo.kuix.core.KuixConstants;
import org.kalmeo.kuix.core.KuixMIDlet;
import org.kalmeo.kuix.core.focus.FocusManager;
import org.kalmeo.kuix.layout.BorderLayout;
import org.kalmeo.kuix.layout.BorderLayoutData;
import org.kalmeo.kuix.layout.GridLayout;
import org.kalmeo.kuix.layout.Layout;
import org.kalmeo.kuix.layout.LayoutData;
import org.kalmeo.kuix.layout.StaticLayout;
import org.kalmeo.kuix.layout.StaticLayoutData;
import org.kalmeo.kuix.transition.Transition;
import org.kalmeo.kuix.util.Alignment;
import org.kalmeo.kuix.util.Gap;
import org.kalmeo.kuix.util.Insets;
import org.kalmeo.util.BooleanUtil;
import org.kalmeo.util.MathFP;

/**
 * This class represents a Kuix screen. <br>
 * <br>
 * <strong>For further informations, visit the <a
 * href="http://www.kalmeo.org/files/kuix/widgetdoc/index.html"
 * target="new">Kuix widgets reference page</a></strong>.
 * 
 * @author bbeaulant
 */
public class Screen extends Widget {

	/**
	 * This class represents a screen top or bottom bar (used for title and/or menu)
	 */
	public class ScreenBar extends Widget {
		
		private final boolean isTop;
		private StaticLayoutData staticLayoutData;
		
		/**
		 * Construct a {@link ScreenBar}
		 *
		 * @param tag
		 * @param isTop
		 */
		public ScreenBar(String tag, boolean isTop) {
			super(tag);
			this.isTop = isTop;
		}

		/* (non-Javadoc)
		 * @see org.kalmeo.kuix.widget.Widget#getLayout()
		 */
		public Layout getLayout() {
			return StaticLayout.instance;
		}

		/* (non-Javadoc)
		 * @see org.kalmeo.kuix.widget.Widget#getLayoutData()
		 */
		public LayoutData getLayoutData() {
			if (barsOnTop) {
				if (staticLayoutData == null) {
					staticLayoutData = new StaticLayoutData(isTop ? Alignment.TOP_LEFT : Alignment.BOTTOM_LEFT, MathFP.ONE, -1);
				}
				return staticLayoutData;
			} else {
				return isTop ? BorderLayoutData.instanceNorth : BorderLayoutData.instanceSouth;
			}
		}
		
	}
	
	/**
	 * This class represents a screen menu
	 */
	public class ScreenMenu extends Menu {

		private StaticLayoutData layoutData;
		private boolean internal;

		/**
		 * Construct a {@link ScreenMenu}
		 *
		 * @param tag
		 * @param layoutData
		 * @param internal
		 */
		public ScreenMenu(String tag, StaticLayoutData layoutData, boolean internal) {
			super(tag, null);
			this.layoutData = layoutData;
			this.internal = internal;
		}
		
		/* (non-Javadoc)
		 * @see org.kalmeo.kuix.widget.Widget#getLayoutData()
		 */
		public LayoutData getLayoutData() {
			return layoutData;
		}

		/* (non-Javadoc)
		 * @see org.kalmeo.kuix.widget.AbstractFocusableWidget#isFocusable()
		 */
		public boolean isFocusable() {
			return false;
		}
		
		/* (non-Javadoc)
		 * @see org.kalmeo.kuix.widget.MenuItem#hideMenuTree()
		 */
		public boolean hideMenuTree() {
			switchToDefaultMenus();
			return super.hideMenuTree();
		}

		/* (non-Javadoc)
		 * @see org.kalmeo.kuix.widget.Menu#showPopup()
		 */
		public void showPopup() {
			showPopup(getDisplayX(), getDisplayY());
		}

		/* (non-Javadoc)
		 * @see org.kalmeo.kuix.widget.Menu#processActionEvent()
		 */
		public boolean processActionEvent() {
			if (!processMenuAction(this, internal, internal && this == firstInternalMenu || !internal && this == firstMenu)) {
				return super.processActionEvent();
			}
			return true;
		}
		
	}
	
	// FocusManager
	private final FocusManager focusManager;

	// The content's widgets
	private final Widget container;
	private ScreenBar topBar;
	private ScreenBar bottomBar;
	
	// Text widget for title
	private Text title;
	
	// Used to determine if this screen call its cleanUp method when removed from its parent
	public boolean cleanUpWhenRemoved = false;
	
	// Used to determine if firstMenu is on the left and then the secondMenu onthe right
	public boolean firstIsLeft = true;
	
	// Used to determine if topBar and bottomBar are displayed on top of the screen content
	public boolean barsOnTop = false;
	
	// Menus 
	private ScreenMenu firstMenu;
	private ScreenMenu secondMenu;
	private ScreenMenu firstInternalMenu;
	private ScreenMenu secondInternalMenu;
	
	/**
	 * Construct a {Screen}
	 */
	public Screen() {
		this(KuixConstants.SCREEN_WIDGET_TAG);
	}
	
	/**
	 * Construct a {Screen}
	 * 
	 * @param tag
	 */
	public Screen(String tag) {
		super(tag);
		
		// Init content's widgets
		container = new Widget() {

			/* (non-Javadoc)
			 * @see org.kalmeo.kuix.widget.Widget#getLayout()
			 */
			public Layout getLayout() {
				return (Layout) Screen.this.getStylePropertyValue(KuixConstants.LAYOUT_STYLE_PROPERTY, false);
			}
			
			/* (non-Javadoc)
			 * @see org.kalmeo.kuix.widget.Widget#getLayoutData()
			 */
			public LayoutData getLayoutData() {
				if (barsOnTop) {
					return StaticLayoutData.instanceFull;
				}
				return BorderLayoutData.instanceCenter;
			}
			
			/* (non-Javadoc)
			 * @see org.kalmeo.kuix.widget.Widget#getMargin()
			 */
			public Insets getMargin() {
				return (Insets) Screen.this.getStylePropertyValue(KuixConstants.MARGIN_STYLE_PROPERTY, false);
			}

			/* (non-Javadoc)
			 * @see org.kalmeo.kuix.widget.Widget#getBorder()
			 */
			public Insets getBorder() {
				return (Insets) Screen.this.getStylePropertyValue(KuixConstants.BORDER_STYLE_PROPERTY, false);
			}

			/* (non-Javadoc)
			 * @see org.kalmeo.kuix.widget.Widget#getPadding()
			 */
			public Insets getPadding() {
				return (Insets) Screen.this.getStylePropertyValue(KuixConstants.PADDING_STYLE_PROPERTY, false);
			}

			/* (non-Javadoc)
			 * @see org.kalmeo.kuix.widget.Widget#getAlign()
			 */
			public Alignment getAlign() {
				return (Alignment) Screen.this.getStylePropertyValue(KuixConstants.ALIGN_STYLE_PROPERTY, false);
			}

			/* (non-Javadoc)
			 * @see org.kalmeo.kuix.widget.Widget#getGap()
			 */
			public Gap getGap() {
				return (Gap) Screen.this.getStylePropertyValue(KuixConstants.GAP_STYLE_PROPERTY, false);
			}
			
		};
		super.add(container);
		
		// Init focusManagers
		focusManager = new FocusManager(this, false) {
			
			/* (non-Javadoc)
			 * @see org.kalmeo.kuix.core.focus.FocusManager#processKeyEvent(byte, int)
			 */
			public boolean processKeyEvent(byte type, int kuixKeyCode) {
				if (!super.processKeyEvent(type, kuixKeyCode)) {
					return processSoftKeyEvent(type, kuixKeyCode);
				}
				return true;	
			}

		};
		
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#getInternalChildInstance(java.lang.String)
	 */
	public Widget getInternalChildInstance(String tag) {
		if (KuixConstants.SCREEN_TOP_BAR_WIDGET_TAG.equals(tag)) {
			return getTopBar();
		}
		if (KuixConstants.SCREEN_BOTTOM_BAR_WIDGET_TAG.equals(tag)) {
			return getBottomBar();
		}
		if (KuixConstants.SCREEN_FIRST_MENU_WIDGET_TAG.equals(tag)) {
			return getFirstMenu();
		}
		if (KuixConstants.SCREEN_SECOND_MENU_WIDGET_TAG.equals(tag)) {
			return getSecondMenu();
		}
		return super.getInternalChildInstance(tag);
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#setAttribute(java.lang.String, java.lang.String)
	 */
	public boolean setAttribute(String name, String value) {
		if (KuixConstants.TITLE_ATTRIBUTE.equals(name)) {
			setTitle(value);
			return true;
		}
		if (KuixConstants.FOCUS_LOOP_ATTRIBUTE.equals(name)) {
			focusManager.setLoop(BooleanUtil.parseBoolean(value));
			return true;
		}
		if (KuixConstants.FIRST_IS_LEFT_ATTRIBUTE.equals(name)) {
			firstIsLeft = BooleanUtil.parseBoolean(value);
			return true;
		}
		if (KuixConstants.CLEAN_UP_WHEN_REMOVED_ATTRIBUTE.equals(name)) {
			cleanUpWhenRemoved = BooleanUtil.parseBoolean(value);
			return true;
		}
		if (KuixConstants.BARS_ON_TOP_ATTRIBUTE.equals(name)) {
			barsOnTop = BooleanUtil.parseBoolean(value);
			return true;
		}
		return super.setAttribute(name, value);
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#getFocusManager()
	 */
	public FocusManager getFocusManager() {
		return focusManager;
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#getLayout()
	 */
	public Layout getLayout() {
		if (barsOnTop) {
			return StaticLayout.instance;
		}
		return BorderLayout.instance;
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#getMargin()
	 */
	public Insets getMargin() {
		return Widget.DEFAULT_MARGIN;
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#getBorder()
	 */
	public Insets getBorder() {
		return Widget.DEFAULT_BORDER;
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#getPadding()
	 */
	public Insets getPadding() {
		return Widget.DEFAULT_PADDING;
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#getAlign()
	 */
	public Alignment getAlign() {
		return Widget.DEFAULT_ALIGN;
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#getGap()
	 */
	public Gap getGap() {
		return Widget.DEFAULT_GAP;
	}

	/**
	 * @return the transition
	 */
	public Transition getTransition() {
		Object transition = getStylePropertyValue(KuixConstants.TRANSITION_STYLE_PROPERTY, false);
		if (transition != null) {
			return (Transition) transition;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#getDefaultStylePropertyValue(java.lang.String)
	 */
	protected Object getDefaultStylePropertyValue(String name) {
		if (KuixConstants.LAYOUT_STYLE_PROPERTY.equals(name)) {
			return GridLayout.instanceOneByOne;
		}
		return super.getDefaultStylePropertyValue(name);
	}
	
	/**
	 * Define the desktop title
	 * 
	 * @param text
	 */
	public void setTitle(String text) {
		if (text == null) {
			if (title != null) {
				title.remove();
				title = null;
			}
		} else {
			if (title == null) {
				title = new Text();
				getTopBar().add(title);
			}
			title.setText(text);
		}
	}
	
	/**
	 * Create the internal topBar instance if it doesn't exist and return it.
	 * 
	 * @return the internal topBar instance
	 */
	public ScreenBar getTopBar() {
		if (topBar == null) {
			topBar = new ScreenBar(KuixConstants.SCREEN_TOP_BAR_WIDGET_TAG, true);
			super.add(topBar);
		}
		return topBar;
	}
	
	/**
	 * Create the internal bottomBar instance if it doesn't exist and return it.
	 * 
	 * @return the internal bottomBar instance
	 */
	public ScreenBar getBottomBar() {
		if (bottomBar == null) {
			bottomBar = new ScreenBar(KuixConstants.SCREEN_BOTTOM_BAR_WIDGET_TAG, false);
			super.add(bottomBar);
		}
		return bottomBar;
	}
	
	/**
	 * Returns the {@link ScreenMenu} that correspond to the given
	 * <code>kuixKeyCode</code>.
	 * 
	 * @param kuixKeyCode
	 * @return the {@link ScreenMenu} that correspond to the given
	 *         <code>kuixKeyCode</code>
	 */
	public Menu getScreenMenu(int kuixKeyCode) {
		if (firstIsLeft && kuixKeyCode == KuixConstants.KUIX_KEY_SOFT_LEFT || !firstIsLeft && kuixKeyCode == KuixConstants.KUIX_KEY_SOFT_RIGHT) {
			if (firstMenu != null && firstMenu.isVisible()) {
				return firstMenu;
			}
			return firstInternalMenu;
		} else {
			if (secondMenu != null && secondMenu.isVisible()) {
				return secondMenu;
			}
			return secondInternalMenu;
		}
	}
	
	/**
	 * Create the firstMenu instance if it doesn't exist and return it.
	 * 
	 * @return the internal firstMenu instance
	 */
	public Menu getFirstMenu() {
		if (firstMenu == null) {
			firstMenu = new ScreenMenu(KuixConstants.SCREEN_FIRST_MENU_WIDGET_TAG, new StaticLayoutData(firstIsLeft ? Alignment.LEFT : Alignment.RIGHT), false);
			getBottomBar().add(firstMenu);
		}
		return firstMenu;
	}
	
	/**
	 * Create the secondMenu instance if it doesn't exist and return it.
	 * 
	 * @return the internal secondMenu instance
	 */
	public Menu getSecondMenu() {
		if (secondMenu == null) {
			secondMenu = new ScreenMenu(KuixConstants.SCREEN_SECOND_MENU_WIDGET_TAG, new StaticLayoutData(firstIsLeft ? Alignment.RIGHT : Alignment.LEFT), false);
			getBottomBar().add(secondMenu);
		}
		return secondMenu;
	}
	
	/**
	 * Create the internal firstMenu instance if it doesn't exist and return it.
	 * 
	 * @return the internal firstMenu instance
	 */
	protected ScreenMenu getFirstInternalMenu() {
		if (firstInternalMenu == null) {
			firstInternalMenu = new ScreenMenu(KuixConstants.SCREEN_FIRST_MENU_WIDGET_TAG, new StaticLayoutData(firstIsLeft ? Alignment.LEFT : Alignment.RIGHT), true);
			firstInternalMenu.add(new Text().setText(Kuix.getMessage(KuixConstants.SELECT_I18N_KEY)));
			getBottomBar().add(firstInternalMenu);
		}
		return firstInternalMenu;
	}
	
	/**
	 * Create the internal secondMenu instance if it doesn't exist and return it.
	 * 
	 * @return the internal secondMenu instance
	 */
	protected ScreenMenu getSecondInternalMenu() {
		if (secondInternalMenu == null) {
			secondInternalMenu = new ScreenMenu(KuixConstants.SCREEN_SECOND_MENU_WIDGET_TAG, new StaticLayoutData(firstIsLeft ? Alignment.RIGHT : Alignment.LEFT), true);
			secondInternalMenu.add(new Text().setText(Kuix.getMessage(KuixConstants.CANCEL_I18N_KEY)));
			getBottomBar().add(secondInternalMenu);
		}
		return secondInternalMenu;
	}
	
	/**
	 * Switch menu display from defaults menus to internal menus
	 */
	protected void switchToInternalMenus() {
		if (firstMenu != null) {
			firstMenu.setVisible(false);
		}
		if (secondMenu != null) {
			secondMenu.setVisible(false);
		}
		getFirstInternalMenu().setVisible(true);
		getSecondInternalMenu().setVisible(true);
	}
	
	/**
	 * Switch menu display from internal menus to default menus
	 */
	protected void switchToDefaultMenus() {
		if (firstInternalMenu != null) {
			firstInternalMenu.setVisible(false);
		}
		if (secondInternalMenu != null) {
			secondInternalMenu.setVisible(false);
		}
		if (firstMenu != null) {
			firstMenu.setVisible(true);
		}
		if (secondMenu != null) {
			secondMenu.setVisible(true);
		}
	}
	
	/**
	 * Set this {@link Screen} has current.
	 */
	public void setCurrent() {
		try {
			KuixMIDlet.getDefault().getCanvas().getDesktop().setCurrentScreen(this);
		} catch (Exception e) {
		}
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#add(org.kalmeo.kuix.widget.Widget)
	 */
	public Widget add(Widget widget) {
		return container.add(widget);
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#removeAll()
	 */
	public void removeAll() {
		container.removeAll();
	}
	
	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.AbstractActionWidget#processPointerEvent(byte, int, int)
	 */
	public boolean processPointerEvent(byte type, int x, int y) {
		// Does nothing on pointer events
		return false;
	}
	
	/**
	 * Process an internal or default menu action.
	 * 
	 * @param menu
	 * @param internal
	 * @param isFirst
	 * @return <code>true</code> if the event is treated by the widget
	 */
	protected boolean processMenuAction(Menu menu, boolean internal, boolean isFirst) {
		if (internal) {
			boolean switchToDefaultMenu = true;
			if (isFirst) {
				FocusManager tmpFocusManager = getDesktop().getCurrentFocusManager();
				if (tmpFocusManager != null) {
					switchToDefaultMenu = tmpFocusManager.processKeyEvent(KuixConstants.KEY_PRESSED_EVENT_TYPE, KuixConstants.KUIX_KEY_FIRE) && !(tmpFocusManager.getFocusedWidget() instanceof Menu);
				}
			} else {
				Menu.hideAllPopupMenu();
			}
			if (switchToDefaultMenu) {
				switchToDefaultMenus();
			}
			return true;
		} else if (menu.popup != null) {
			switchToInternalMenus();
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.kalmeo.kuix.widget.Widget#onRemoved(org.kalmeo.kuix.widget.Widget)
	 */
	protected void onRemoved(Widget parent) {
		if (cleanUpWhenRemoved) {
			cleanUp();
		}
	}
	
}
