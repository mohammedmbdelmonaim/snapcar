package com.intcore.snapcar.di.activity;

import androidx.lifecycle.Lifecycle;
import android.content.Context;
import androidx.fragment.app.FragmentManager;

import com.intcore.snapcar.ui.aboutus.AboutUsScreen;
import com.intcore.snapcar.ui.activation.ActivationScreen;
import com.intcore.snapcar.ui.addcar.AddCarScreen;
import com.intcore.snapcar.ui.addcarphotes.AddCarPhotosScreen;
import com.intcore.snapcar.ui.addinterest.AddInterestScreen;
import com.intcore.snapcar.ui.advancedsearch.AdvancedSearchScreen;
import com.intcore.snapcar.ui.blocklist.BlockListScreen;
import com.intcore.snapcar.ui.brushtool.BrushToolScreen;
import com.intcore.snapcar.ui.chatsearch.ChatSearchScreen;
import com.intcore.snapcar.ui.confirmcommision.ConfirmCommissionScreen;
import com.intcore.snapcar.ui.contactus.ContactUsScreen;
import com.intcore.snapcar.ui.coupon.CouponScreen;
import com.intcore.snapcar.ui.editPhone.EditPhoneScreen;
import com.intcore.snapcar.ui.editshowroom.EditShowRoomScreen;
import com.intcore.snapcar.ui.editshowroomlocation.EditShowRoomLocationScreen;
import com.intcore.snapcar.ui.editshowroomphone.EditShowRoomPhoneScreen;
import com.intcore.snapcar.ui.editUser.EditUserScreen;
import com.intcore.snapcar.ui.editcar.EditCarScreen;
import com.intcore.snapcar.ui.editinterest.EditInterestScreen;
import com.intcore.snapcar.ui.editphoneactivition.EditPhoneActivationScreen;
import com.intcore.snapcar.ui.feedback.FeedbackScreen;
import com.intcore.snapcar.ui.forgetpassword.ForgetPasswordScreen;
import com.intcore.snapcar.ui.host.HostScreen;
import com.intcore.snapcar.ui.login.LoginScreen;
import com.intcore.snapcar.ui.myinterstes.MyInterestScreen;
import com.intcore.snapcar.ui.mycars.MyCarsScreen;
import com.intcore.snapcar.ui.notificationsetting.NotificationSettingScreen;
import com.intcore.snapcar.ui.paymenactivity.PaymentScreen;
import com.intcore.snapcar.ui.paymenthistory.PaymentHistoryScreen;
import com.intcore.snapcar.ui.phaseoneregisteration.PhaseOneRegistrationScreen;
import com.intcore.snapcar.ui.phasetworegisteration.PhaseTwoRegistrationScreen;
import com.intcore.snapcar.ui.privacyactivtiy.PrivacyScreen;
import com.intcore.snapcar.ui.resetpassword.ResetPasswordScreen;
import com.intcore.snapcar.ui.search.SearchScreen;
import com.intcore.snapcar.ui.settings.SettingsScreen;
import com.intcore.snapcar.ui.survey.SurveyScreen;
import com.intcore.snapcar.ui.tutorial.TutorialScreen;
import com.intcore.snapcar.ui.updatepassword.UpdatePasswordScreen;
import com.intcore.snapcar.ui.verificationletter.VerificationLetterScreen;
import com.intcore.snapcar.ui.viewcar.ViewCarScreen;
import com.intcore.snapcar.ui.visitorprofile.VisitorProfileScreen;
import com.intcore.snapcar.core.base.BaseActivity;
import com.intcore.snapcar.core.qualifier.ForActivity;
import com.intcore.snapcar.core.scope.ActivityScope;
import com.intcore.snapcar.core.util.UiUtil;
import com.intcore.snapcar.core.util.permission.PermissionUtil;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

/**
 * This class is responsible for providing the requested objects to {@link ActivityScope} annotated classes
 */

@Module
public class
ActivityModule {

    private final BaseActivity activity;

    public ActivityModule(BaseActivity activity) {
        this.activity = activity;
    }

    @ActivityScope
    @Provides
    BaseActivity provideActivity() {
        return activity;
    }

    @ActivityScope
    @Provides
    FragmentManager provideFragmentManager() {
        return activity.getSupportFragmentManager();
    }

    @ActivityScope
    @Provides
    @ForActivity
    Context provideActivityContext() {
        return activity;
    }

    @ActivityScope
    @ForActivity
    @Provides
    Lifecycle provideLifCycle() {
        return activity.getLifecycle();
    }

    @ActivityScope
    @Provides
    UiUtil providesUiUtil() {
        return new UiUtil(activity);
    }

    @ActivityScope
    @ForActivity
    @Provides
    PermissionUtil providePermissionUtil() {
        return activity.getPermissionUtil();
    }

    @ActivityScope
    @ForActivity
    @Provides
    CompositeDisposable providesCompositeDisposable() {
        return new CompositeDisposable();
    }

    @ActivityScope
    @Provides
    PhaseOneRegistrationScreen providesPhaseOneRegistrationScreen() {
        return (PhaseOneRegistrationScreen) activity;
    }

    @ActivityScope
    @Provides
    PhaseTwoRegistrationScreen providesPhaseTwoRegistrationScreen() {
        return (PhaseTwoRegistrationScreen) activity;
    }

    @ActivityScope
    @Provides
    ActivationScreen providesActivationScreen() {
        return (ActivationScreen) activity;
    }

    @ActivityScope
    @Provides
    ForgetPasswordScreen providesForgetPasswordScreen() {
        return (ForgetPasswordScreen) activity;
    }

    @ActivityScope
    @Provides
    ResetPasswordScreen providesResetPasswordScreen() {
        return (ResetPasswordScreen) activity;
    }

    @ActivityScope
    @Provides
    LoginScreen providesLoginScreen() {
        return (LoginScreen) activity;
    }

    @ActivityScope
    @Provides
    HostScreen providesHostScreen() {
        return (HostScreen) activity;
    }

    @ActivityScope
    @Provides
    TutorialScreen providesTutorialScreen() {
        return (TutorialScreen) activity;
    }

    @ActivityScope
    @Provides
    VisitorProfileScreen providesVisitorProfileScreen() {
        return (VisitorProfileScreen) activity;
    }

    @ActivityScope
    @Provides
    EditUserScreen providesEditUserScreen() {
        return (EditUserScreen) activity;
    }

    @ActivityScope
    @Provides
    EditShowRoomScreen providesEditShowRoomScreen() {
        return (EditShowRoomScreen) activity;
    }

    @ActivityScope
    @Provides
    EditShowRoomLocationScreen providesEditShowRoomLocationScreen() {
        return (EditShowRoomLocationScreen) activity;
    }

    @ActivityScope
    @Provides
    PaymentScreen providesEditShowPaymentScreen() {
        return (PaymentScreen) activity;
    }

    @ActivityScope
    @Provides
    EditShowRoomPhoneScreen providesEditShowRoomPhoneScreen() {
        return (EditShowRoomPhoneScreen) activity;
    }

    @ActivityScope
    @Provides
    MyInterestScreen providesMyInterstesScreen() {
        return (MyInterestScreen) activity;
    }

    @ActivityScope
    @Provides
    EditPhoneScreen providesEditPhoneScreen() {
        return (EditPhoneScreen) activity;
    }

    @ActivityScope
    @Provides
    EditPhoneActivationScreen providesEditPhoneActivationScreen() {
        return (EditPhoneActivationScreen) activity;
    }

    @ActivityScope
    @Provides
    BlockListScreen providesBlockListScreen() {
        return (BlockListScreen) activity;
    }

    @ActivityScope
    @Provides
    AddInterestScreen providesAddInterestScreen() {
        return (AddInterestScreen) activity;
    }

    @ActivityScope
    @Provides
    AddCarPhotosScreen providesAddCarPhotosScreen() {
        return (AddCarPhotosScreen) activity;
    }

    @ActivityScope
    @Provides
    AddCarScreen providesAddCarScreen() {
        return (AddCarScreen) activity;
    }

    @ActivityScope
    @Provides
    SearchScreen providesSearchScreen() {
        return (SearchScreen) activity;
    }

    @ActivityScope
    @Provides
    AdvancedSearchScreen providesAdvancedSearchScreen() {
        return (AdvancedSearchScreen) activity;
    }

    @ActivityScope
    @Provides
    EditInterestScreen providesEditInterestScreen() {
        return (EditInterestScreen) activity;
    }

    @ActivityScope
    @Provides
    CouponScreen providesCouponScreen() {
        return (CouponScreen) activity;
    }

    @ActivityScope
    @Provides
    ViewCarScreen providesViewCarScreen() {
        return (ViewCarScreen) activity;
    }

    @ActivityScope
    @Provides
    MyCarsScreen providesMycarsScreen() {
        return (MyCarsScreen) activity;
    }

    @ActivityScope
    @Provides
    EditCarScreen providesEditCarScreen() {
        return (EditCarScreen) activity;
    }

    @ActivityScope
    @Provides
    BrushToolScreen providesBrushToolScreen() {
        return (BrushToolScreen) activity;
    }

    @ActivityScope
    @Provides
    PaymentHistoryScreen providesPaymentHistoryScreen() {
        return (PaymentHistoryScreen) activity;
    }

    @ActivityScope
    @Provides
    FeedbackScreen providesFeedbackScreen() {
        return (FeedbackScreen) activity;
    }

    @ActivityScope
    @Provides
    SurveyScreen providesSurveyScreen() {
        return (SurveyScreen) activity;
    }

    @ActivityScope
    @Provides
    VerificationLetterScreen providesVerificationLetterScreen() {
        return (VerificationLetterScreen) activity;
    }

    @ActivityScope
    @Provides
    UpdatePasswordScreen providesUpdatePasswordScreen() {
        return (UpdatePasswordScreen) activity;
    }

    @ActivityScope
    @Provides
    ConfirmCommissionScreen providesConfirmCommissionScreen() {
        return (ConfirmCommissionScreen) activity;
    }

    @ActivityScope
    @Provides
    AboutUsScreen providesAboutUsScreen() {
        return (AboutUsScreen) activity;
    }

    @ActivityScope
    @Provides
    PrivacyScreen providesPrivacyScreen() {
        return (PrivacyScreen) activity;
    }

    @ActivityScope
    @Provides
    ChatSearchScreen providesChatSearchScreen() {
        return (ChatSearchScreen) activity;
    }

    @ActivityScope
    @Provides
    NotificationSettingScreen providesNotificationSettingScreen() {
        return (NotificationSettingScreen) activity;
    }

    @ActivityScope
    @Provides
    ContactUsScreen providesContactUsScreen() {
        return (ContactUsScreen) activity;
    }

    @ActivityScope
    @Provides
    SettingsScreen providesSettingsScreen() {
        return (SettingsScreen) activity;
    }

}