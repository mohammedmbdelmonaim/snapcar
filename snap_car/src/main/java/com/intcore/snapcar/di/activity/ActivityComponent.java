package com.intcore.snapcar.di.activity;

import com.intcore.snapcar.backgroundServices.ScheduledJobService;
import com.intcore.snapcar.core.base.BaseActivity;
import com.intcore.snapcar.di.fragment.FragmentComponent;
import com.intcore.snapcar.di.fragment.FragmentModule;
import com.intcore.snapcar.ui.aboutus.AboutUsActivity;
import com.intcore.snapcar.ui.activation.ActivationActivity;
import com.intcore.snapcar.ui.addcar.AddCarActivity;
import com.intcore.snapcar.ui.addcarphotes.AddCarPhotosActivity;
import com.intcore.snapcar.ui.addinterest.AddInterestActivity;
import com.intcore.snapcar.ui.advancedsearch.AdvancedSearchActivity;
import com.intcore.snapcar.ui.blocklist.BlockListActivity;
import com.intcore.snapcar.ui.brushtool.BrushToolActivity;
import com.intcore.snapcar.ui.chatsearch.ChatSearchActivity;
import com.intcore.snapcar.ui.confirmcommision.ConfirmCommissionActivity;
import com.intcore.snapcar.ui.chatthread.ChatThreadActivity;
import com.intcore.snapcar.ui.contactus.ContactUsActivity;
import com.intcore.snapcar.ui.coupon.CouponActivity;
import com.intcore.snapcar.ui.editPhone.EditPhoneActivity;
import com.intcore.snapcar.ui.editshowroom.EditShowRoomActivity;
import com.intcore.snapcar.ui.editshowroomlocation.EditShowRoomLocationActivity;
import com.intcore.snapcar.ui.editshowroomphone.EditShowroomPhoneActivity;
import com.intcore.snapcar.ui.editUser.EditUserActivity;
import com.intcore.snapcar.ui.editcar.EditCarActivity;
import com.intcore.snapcar.ui.editinterest.EditInterestActivity;
import com.intcore.snapcar.ui.editphoneactivition.EditPhoneActivationActivity;
import com.intcore.snapcar.ui.feedback.FeedbackActivity;
import com.intcore.snapcar.ui.forgetpassword.ForgetPasswordActivity;
import com.intcore.snapcar.ui.host.HostActivity;
import com.intcore.snapcar.ui.login.LoginActivity;
import com.intcore.snapcar.ui.myinterstes.MyInterestActivity;
import com.intcore.snapcar.ui.mycars.MyCarsActivity;
import com.intcore.snapcar.ui.notificationsetting.NotificationSettingActivity;
import com.intcore.snapcar.ui.paymenactivity.PaymentActivity;
import com.intcore.snapcar.ui.paymenactivity.PaymentActivityForAddcar;
import com.intcore.snapcar.ui.paymenthistory.PaymentHistoryActivity;
import com.intcore.snapcar.ui.phaseoneregisteration.PhaseOneRegistrationActivity;
import com.intcore.snapcar.ui.phasetworegisteration.PhaseTwoRegistrationActivity;
import com.intcore.snapcar.ui.privacyactivtiy.PrivacyActivity;
import com.intcore.snapcar.ui.resetpassword.ResetPasswordActivity;
import com.intcore.snapcar.ui.search.SearchActivity;
import com.intcore.snapcar.ui.settings.SettingsActivity;
import com.intcore.snapcar.ui.splash.SplashActivity;
import com.intcore.snapcar.ui.survey.SurveyActivity;
import com.intcore.snapcar.ui.tutorial.TutorialActivity;
import com.intcore.snapcar.ui.verificationletter.VerificationLetterActivity;
import com.intcore.snapcar.ui.viewcar.ViewCarActivity;
import com.intcore.snapcar.ui.visitorprofile.VisitorProfileActivity;
import com.intcore.snapcar.ui.updatepassword.UpdatePasswordActivity;
import com.intcore.snapcar.core.scope.ActivityScope;

import dagger.Subcomponent;

/**
 * This interface is used by dagger to generate the code that defines the connection between the provider of objects
 * (i.e. {@link ActivityModule}), and the object which expresses a dependency.
 */

@ActivityScope
@Subcomponent(modules = {ActivityModule.class})
public interface ActivityComponent {

    FragmentComponent plus(FragmentModule fragmentModule);

    void inject(BaseActivity baseActivity);

    void inject(PhaseOneRegistrationActivity phaseOneRegistrationActivity);

    void inject(PhaseTwoRegistrationActivity phaseTwoRegistrationActivity);

    void inject(ForgetPasswordActivity forgetPasswordActivity);

    void inject(ResetPasswordActivity resetPasswordActivity);

    void inject(ActivationActivity activationActivity);

    void inject(TutorialActivity tutorialActivity);

    void inject(SplashActivity splashActivity);

    void inject(LoginActivity loginActivity);

    void inject(HostActivity hostActivity);

    void inject(VisitorProfileActivity visitorProfileActivity);

    void inject(EditUserActivity editUserActivity);

    void inject(EditShowRoomActivity editShowRoomActivity);

    void inject(EditShowRoomLocationActivity editShowRoomLocationActivity);

    void inject(EditPhoneActivity editPhoneActivity);

    void inject(EditShowroomPhoneActivity editShowroomPhoneActivity);

    void inject(MyInterestActivity myinterstesActivity);

    void inject(EditPhoneActivationActivity editPhoneActivationActivity);

    void inject(BlockListActivity blockListActivity);

    void inject(AddInterestActivity addInterestActivity);

    void inject(AddCarActivity addInterestActivity);

    void inject(AddCarPhotosActivity addInterestActivity);

    void inject(ViewCarActivity addInterestActivity);

    void inject(MyCarsActivity addInterestActivity);

    void inject(SearchActivity searchActivity);

    void inject(AdvancedSearchActivity advancedSearchActivity);

    void inject(EditInterestActivity editInterestActivity);

    void inject(CouponActivity couponActivity);

    void inject(EditCarActivity addInterestActivity);

    void inject(BrushToolActivity addInterestActivity);

    void inject(PaymentActivity addInterestActivity);

    void inject(ConfirmCommissionActivity addInterestActivity);

    void inject(ScheduledJobService addInterestActivity);

    void inject(PaymentActivityForAddcar addInterestActivity);

    void inject(PaymentHistoryActivity addInterestActivity);

    void inject(FeedbackActivity addInterestActivity);

    void inject(SurveyActivity surveyActivity);

    void inject(VerificationLetterActivity activity);

    void inject(UpdatePasswordActivity updatePasswordActivity);

    void inject(SettingsActivity settingsActivity);

    void inject(ChatThreadActivity chatThreadActivity);

    void inject(AboutUsActivity aboutUsActivity);

    void inject(PrivacyActivity privacyActivity);

    void inject(ChatSearchActivity chatSearchActivity);

    void inject(NotificationSettingActivity notificationSettingActivity);

    void inject(ContactUsActivity notificationSettingActivity);
}
