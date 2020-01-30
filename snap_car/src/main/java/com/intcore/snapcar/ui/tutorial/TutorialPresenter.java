package com.intcore.snapcar.ui.tutorial;

import com.intcore.snapcar.core.base.BaseActivityPresenter;
import com.intcore.snapcar.core.scope.ActivityScope;

import javax.inject.Inject;

@ActivityScope
class TutorialPresenter extends BaseActivityPresenter {

    private final TutorialScreen tutorialScreen;

    @Inject
    TutorialPresenter(TutorialScreen tutorialScreen) {
        super(tutorialScreen);
        this.tutorialScreen = tutorialScreen;
    }

    @Override
    protected void onCreate() {
        tutorialScreen.setupViewPager();
    }
}