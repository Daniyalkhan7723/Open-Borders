package com.open.borders.thirdPartyModules.slidingrootnav.transform;

import android.view.View;

import java.util.List;

/**
 * Created by ismaiel sharif on 14.12.2021.
 */

public class CompositeTransformation implements RootTransformation {

    private List<RootTransformation> transformations;

    public CompositeTransformation(List<RootTransformation> transformations) {
        this.transformations = transformations;
    }

    @Override
    public void transform(float dragProgress, View rootView) {
        for (RootTransformation t : transformations) {
            t.transform(dragProgress, rootView);
        }
    }
}
