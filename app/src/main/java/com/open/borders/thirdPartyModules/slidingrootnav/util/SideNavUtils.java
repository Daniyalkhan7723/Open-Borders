package com.open.borders.thirdPartyModules.slidingrootnav.util;

/**
 * Created by ismaiel sharif on 14.12.2021.
 */

public abstract class SideNavUtils {

    public static float evaluate(float fraction, float startValue, float endValue) {
        return startValue + fraction * (endValue - startValue);
    }
}
