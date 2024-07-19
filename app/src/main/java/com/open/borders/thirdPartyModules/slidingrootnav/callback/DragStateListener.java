package com.open.borders.thirdPartyModules.slidingrootnav.callback;

/**
 * Created by ismaiel sharif on 14.12.2021.
 */

public interface DragStateListener {

    void onDragStart();

    void onDragEnd(boolean isMenuOpened);
}
