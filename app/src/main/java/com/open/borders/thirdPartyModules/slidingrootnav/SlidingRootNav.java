package com.open.borders.thirdPartyModules.slidingrootnav;

/**
 * Created by ismaiel sharif on 14.12.2021.
 */

public interface SlidingRootNav {

    boolean isMenuClosed();

    boolean isMenuOpened();

    boolean isMenuLocked();

    void setMenuLocked(boolean locked);

    void closeMenu();

    void closeMenu(boolean animated);

    void openMenu();

    void openMenu(boolean animated);

    SlidingRootNavLayout getLayout();

}
