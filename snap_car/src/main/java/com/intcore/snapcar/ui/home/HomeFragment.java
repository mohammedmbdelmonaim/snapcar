package com.intcore.snapcar.ui.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.SwitchCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.duolingo.open.rtlviewpager.RtlViewPager;
import com.google.firebase.auth.FirebaseAuth;
import com.intcore.snapcar.R;
import com.intcore.snapcar.di.UIHostComponentProvider;
import com.intcore.snapcar.di.activity.ActivityComponent;
import com.intcore.snapcar.di.fragment.FragmentModule;
import com.intcore.snapcar.events.OperationListener;
import com.intcore.snapcar.store.model.brands.BrandsViewModel;
import com.intcore.snapcar.store.model.car.CarApiResponse;
import com.intcore.snapcar.store.model.category.CategoryViewModel;
import com.intcore.snapcar.store.model.constant.HomeItem;
import com.intcore.snapcar.store.model.explore.ExploreItem;
import com.intcore.snapcar.store.model.filter.FilterApiResponse;
import com.intcore.snapcar.store.model.model.ModelViewModel;
import com.intcore.snapcar.store.model.settings.SettingsModel;
import com.intcore.snapcar.store.model.survey.SurveyApiResponse;
import com.intcore.snapcar.ui.addcar.AddCarActivity;
import com.intcore.snapcar.ui.addinterest.AddInterestActivity;
import com.intcore.snapcar.ui.confirmcommision.ConfirmCommissionActivity;
import com.intcore.snapcar.ui.host.HostActivity;
import com.intcore.snapcar.ui.login.LoginActivity;
import com.intcore.snapcar.ui.paymenactivity.PaymentActivityForAddcar;
import com.intcore.snapcar.ui.privacyactivtiy.PrivacyActivity;
import com.intcore.snapcar.ui.survey.SurveyActivity;
import com.intcore.snapcar.core.scope.FragmentScope;
import com.intcore.snapcar.core.util.LocaleUtil;
import com.intcore.snapcar.core.util.Preconditions;
import com.intcore.snapcar.core.util.PreferencesUtil;
import com.intcore.snapcar.core.util.UiUtil;
import com.intcore.snapcar.core.widget.rxedittext.RxEditText;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Context.MODE_PRIVATE;

@FragmentScope
public class HomeFragment extends Fragment implements HomeScreen {

    @Inject
    HomePresenter presenter;
    @Inject
    UiUtil uiUtil;
    @Inject
    PreferencesUtil preferencesUtil;

    @BindView(R.id.btn_explorer)
    TextView explorerTextView;
    @BindView(R.id.btn_near_by)
    TextView nearByTextView;
    @BindView(R.id.iv_filter)
    ImageView filterImageView;
    @BindView(R.id.view_pager)
    RtlViewPager viewPager;
    private NotifyFilterNearByDataReady filterNearByDataReady;
    private NotifyFilterExploreDataReady filterExploreDataReady;
    private MaterialDialog filterDialog;
    private MaterialDialog materialDialog;
    private boolean isNearByFragmentVisible = true;
    private boolean applyToAll = true;
    private int carId;
    private HomePagerAdapter adapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onDestroy() {
        //presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        adapter.onDestroy();
        super.onDestroyView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UIHostComponentProvider) {
            UIHostComponentProvider provider = ((UIHostComponentProvider) context);
            if (provider.getComponent() instanceof ActivityComponent) {
                ((ActivityComponent) provider.getComponent())
                        .plus(new FragmentModule(this))
                        .inject(this);
            } else {
                throw new IllegalStateException("Component mus_t be " + ActivityComponent.class.getName());
            }
        } else {
            throw new AssertionError("host context must implement " + UIHostComponentProvider.class.getName());
        }
    }

    @Override
    public void onResume() {
        changeStatusBarColor();
        if (getActivity() instanceof HostActivity) {
            ((HostActivity) getActivity()).setHighlightedItem(HomeItem.HOME);
        }
        super.onResume();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        adapter = new HomePagerAdapter(getChildFragmentManager());
        presenter.onViewCreated();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if ("ar".equals(LocaleUtil.getLanguage(getActivity()))) {
            getActivity().getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            getActivity().getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void showErrorMessage(String message) {
        uiUtil.getErrorSnackBar(((HostActivity) Preconditions.requireNonNull(getActivity())).getSnackBarContainer(), message).show();
    }

    @Override
    public void showSuccessMessage(String message) {
        uiUtil.getSuccessSnackBar(((HostActivity) Preconditions.requireNonNull(getActivity())).getSnackBarContainer(), message).show();
    }

    @Override
    public void showWarningMessage(String message) {
        uiUtil.getWarningSnackBar(((HostActivity) Preconditions.requireNonNull(getActivity())).getSnackBarContainer(), message).show();
    }

    @Override
    public void showLoadingAnimation() {
        uiUtil.getProgressDialog(getString(R.string.please_wait_dotted))
                .show();
    }

    @Override
    public void initializeFragment() {
        explorerTextView.setSelected(true);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    explorerTextView.setSelected(true);
                    nearByTextView.setSelected(false);
                } else if (position == 1) {
                    explorerTextView.setSelected(false);
                    nearByTextView.setSelected(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void hideLoadingAnimation() {
        uiUtil.getProgressDialog()
                .hide();
    }

    @Override
    public void updateUi(FilterApiResponse filterViewModel) {
        filterNearByDataReady.onDataReady(filterViewModel);
        filterDialog.dismiss();
    }

    private void updateExploreUi(List<ExploreItem> exploreItems) {
        filterExploreDataReady.onDataReady(exploreItems);
        filterDialog.dismiss();
    }

    public void setFragment(Fragment fragment, String tag) {
        if (getChildFragmentManager().findFragmentByTag(tag) != null)
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, getChildFragmentManager().findFragmentByTag(tag), tag)
                    .addToBackStack(tag)
                    .commit();
        else
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment, tag)
                    .addToBackStack(tag)
                    .commit();
    }

    @OnClick(R.id.btn_explorer)
    void onExplorerClicked() {
        isNearByFragmentVisible = true;
        viewPager.setCurrentItem(0);
    }

    @OnClick(R.id.btn_near_by)
    void onNearByClicked() {
        isNearByFragmentVisible = false;
        viewPager.setCurrentItem(1);
    }

    private void updateBrand(BrandsViewModel viewModel,
                             RxEditText brandEditText,
                             RxEditText modelEditText,
                             RxEditText categoryEditText) {
        brandEditText.setText(viewModel.getName());
        if (viewModel.getBrandsModels().size() > 0) {
            updateModel(viewModel.getBrandsModels().get(0), modelEditText, categoryEditText);
            presenter.setSelectedModelModel(viewModel.getBrandsModels().get(0));
        } else {
            updateModel(ModelViewModel.createDefault(), modelEditText, categoryEditText);
            presenter.setSelectedModelModel(ModelViewModel.createDefault());
        }
    }

    private void updateModel(ModelViewModel viewModel, RxEditText modelEditText, RxEditText categoryEditText) {
        modelEditText.setText(viewModel.getName());
        if (viewModel.getCategoryViewModels().size() > 0) {
            updateCategory(viewModel.getCategoryViewModels().get(0), categoryEditText);
            presenter.setSelectedCategoryModel(viewModel.getCategoryViewModels().get(0));
        } else {
            updateCategory(CategoryViewModel.createDefault(), categoryEditText);
            presenter.setSelectedCategoryModel(CategoryViewModel.createDefault());
        }
    }

    private void updateCategory(CategoryViewModel categoryModel, RxEditText categoryEditText) {
        categoryEditText.setText(categoryModel.getName());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @OnClick(R.id.iv_filter)
    void onFilterClicked() {
        if (getActivity() instanceof HostActivity) {
            filterDialog = new MaterialDialog.Builder(getActivity())
                    .customView(R.layout.filter_dialog_layout, false)
                    .backgroundColor(getResources().getColor(R.color.transparent))
                    .build();
            if ("ar".equals(LocaleUtil.getLanguage(getActivity()))) {
                filterDialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            } else {
                filterDialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }
            View inflater = filterDialog.getCustomView();
            RxEditText brandEditText = inflater.findViewById(R.id.et_brand);
            RxEditText modelEditText = inflater.findViewById(R.id.et_model);
            RxEditText categoryEditText = inflater.findViewById(R.id.et_category);
            SwitchCompat showAllSwitch = inflater.findViewById(R.id.show_all_switch);
            SwitchCompat menSwitch = inflater.findViewById(R.id.men_switch);
            SwitchCompat womenSwitch = inflater.findViewById(R.id.women_switch);
            SwitchCompat damagedSwitch = inflater.findViewById(R.id.damage_switch);
            SwitchCompat vipShowRoomSwitch = inflater.findViewById(R.id.vip_showroom_switch);
            SwitchCompat showRoomSwitch = inflater.findViewById(R.id.showroom_switch);
            SwitchCompat vipHotZoneSwitch = inflater.findViewById(R.id.vip_hotzone_switch);
            SwitchCompat hotZoneSwitch = inflater.findViewById(R.id.hotzone_switch);
            TextView applyTextView = inflater.findViewById(R.id.tv_apply);
            FrameLayout progressBar = inflater.findViewById(R.id.progress_bar);
            showAllSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    applyToAll = true;
                    vipShowRoomSwitch.setChecked(true);
                    vipHotZoneSwitch.setChecked(true);
                    showRoomSwitch.setChecked(true);
                    damagedSwitch.setChecked(true);
                    hotZoneSwitch.setChecked(true);
                    womenSwitch.setChecked(true);
                    menSwitch.setChecked(true);
                } else {
                    if (!applyToAll) {
                        applyToAll = true;
                        return;
                    }
                    vipShowRoomSwitch.setChecked(false);
                    vipHotZoneSwitch.setChecked(false);
                    showRoomSwitch.setChecked(false);
                    hotZoneSwitch.setChecked(false);
                    damagedSwitch.setChecked(false);
                    womenSwitch.setChecked(false);
                    menSwitch.setChecked(false);
                }
            });
            vipShowRoomSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (!isChecked) {
                    applyToAll = false;
                    showAllSwitch.setChecked(false);
                }
            });
            vipHotZoneSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (!isChecked) {
                    applyToAll = false;
                    showAllSwitch.setChecked(false);
                }
            });
            showRoomSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (!isChecked) {
                    applyToAll = false;
                    showAllSwitch.setChecked(false);
                }
            });
            hotZoneSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (!isChecked) {
                    applyToAll = false;
                    showAllSwitch.setChecked(false);
                }
            });
            damagedSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (!isChecked) {
                    applyToAll = false;
                    showAllSwitch.setChecked(false);
                }
            });
            womenSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (!isChecked) {
                    applyToAll = false;
                    showAllSwitch.setChecked(false);
                }
            });
            menSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (!isChecked) {
                    applyToAll = false;
                    showAllSwitch.setChecked(false);
                }
            });
            if (!isNearByFragmentVisible) {
                showAllSwitch.setChecked(preferencesUtil.getInt("showAllNearBy", 1) == 1);
                vipShowRoomSwitch.setChecked(preferencesUtil.getInt("vipShowRoomNearBy", 1) == 1);
                vipHotZoneSwitch.setChecked(preferencesUtil.getInt("vipHotZoneNearBy", 1) == 1);
                damagedSwitch.setChecked(preferencesUtil.getInt("damagedCarNearBy", 1) == 1);
                showRoomSwitch.setChecked(preferencesUtil.getInt("showRoomNearBy", 1) == 1);
                hotZoneSwitch.setChecked(preferencesUtil.getInt("hotZoneNearBy", 1) == 1);
                womenSwitch.setChecked(preferencesUtil.getInt("womenCarNearBy", 1) == 1);
                menSwitch.setChecked(preferencesUtil.getInt("menCarNearBy", 1) == 1);
            } else {
                showAllSwitch.setChecked(preferencesUtil.getInt("showAll", 1) == 1);
                vipShowRoomSwitch.setChecked(preferencesUtil.getInt("vipShowRoom", 1) == 1);
                vipHotZoneSwitch.setChecked(preferencesUtil.getInt("vipHotZone", 1) == 1);
                damagedSwitch.setChecked(preferencesUtil.getInt("damagedCar", 1) == 1);
                showRoomSwitch.setChecked(preferencesUtil.getInt("showRoom", 1) == 1);
                hotZoneSwitch.setChecked(preferencesUtil.getInt("hotZone", 1) == 1);
                womenSwitch.setChecked(preferencesUtil.getInt("womenCar", 1) == 1);
                menSwitch.setChecked(preferencesUtil.getInt("menCar", 1) == 1);
            }

            filterDialog.setOnDismissListener(dialog -> {
                presenter.setSelectedModelModel(ModelViewModel.createDefault());
                presenter.setSelectedCategoryModel(CategoryViewModel.createDefault());
                presenter.setSelectedBrandModel(BrandsViewModel.createDefault());
            });
            brandEditText.setOnClickListener(v -> {
                if (materialDialog != null) {
                    materialDialog.dismiss();
                    materialDialog = null;
                }
                materialDialog = new MaterialDialog.Builder(getActivity())
                        .adapter(new BrandAdapter(getActivity(), viewModel -> {
                                    presenter.setSelectedBrandModel(viewModel);
                                    updateBrand(viewModel, brandEditText, modelEditText, categoryEditText);
                                    materialDialog.dismiss();
                                }, presenter.getBrandsList()),
                                new LinearLayoutManager(getActivity()))
                        .build();
                if ("ar".equals(LocaleUtil.getLanguage(getActivity()))) {
                    materialDialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                } else {
                    materialDialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                }
                materialDialog.show();
            });
            modelEditText.setOnClickListener(v -> {
                if (materialDialog != null) {
                    materialDialog.dismiss();
                    materialDialog = null;
                }
                if (presenter.getSelectedBrandModel() != null) {
                    List<ModelViewModel> modelModelList = new ArrayList<>();
                    modelModelList.add(ModelViewModel.createDefault());
                    modelModelList.addAll(presenter.getSelectedBrandModel().getBrandsModels());
                    materialDialog = new MaterialDialog.Builder(getActivity())
                            .adapter(new ModelAdapter(getActivity(), viewModel -> {
                                        presenter.setSelectedModelModel(viewModel);
                                        updateModel(viewModel, modelEditText, categoryEditText);
                                        materialDialog.dismiss();
                                    }, modelModelList),
                                    new LinearLayoutManager(getActivity()))
                            .build();
                    if ("ar".equals(LocaleUtil.getLanguage(getActivity()))) {
                        materialDialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                    } else {
                        materialDialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                    }
                    materialDialog.show();
                } else {
                    showWarningMessage(getString(R.string.you_should_select_brand_first));
                }
            });
            categoryEditText.setOnClickListener(v -> {
                if (materialDialog != null) {
                    materialDialog.dismiss();
                    materialDialog = null;
                }
                if (presenter.getSelectedModelModel() != null) {
                    List<CategoryViewModel> categoryModels = new ArrayList<>();
                    categoryModels.add(CategoryViewModel.createDefault());
                    categoryModels.addAll(presenter.getSelectedModelModel().getCategoryViewModels());
                    materialDialog = new MaterialDialog.Builder(getActivity())
                            .adapter(new CategoryAdapter(getActivity(), viewModel -> {
                                        presenter.setSelectedCategoryModel(viewModel);
                                        updateCategory(viewModel, categoryEditText);
                                        materialDialog.dismiss();
                                    }, categoryModels),
                                    new LinearLayoutManager(getActivity()))
                            .build();
                    if ("ar".equals(LocaleUtil.getLanguage(getActivity()))) {
                        materialDialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                    } else {
                        materialDialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                    }
                    materialDialog.show();
                } else {
                    showWarningMessage(getString(R.string.you_should_select_model_first));
                }
            });
            applyTextView.setOnClickListener(v -> {
                if (isNearByFragmentVisible) {
                    presenter.onNearByFilterClicked(
                            showAllSwitch.isChecked() ? 1 : 0,
                            menSwitch.isChecked() ? 1 : 0,
                            womenSwitch.isChecked() ? 1 : 0,
                            damagedSwitch.isChecked() ? 1 : 0,
                            vipShowRoomSwitch.isChecked() ? 1 : 0,
                            showRoomSwitch.isChecked() ? 1 : 0,
                            vipHotZoneSwitch.isChecked() ? 1 : 0,
                            hotZoneSwitch.isChecked() ? 1 : 0,
                            new OperationListener<FilterApiResponse>() {
                                @Override
                                public void onPreOperation() {
                                    progressBar.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onPostOperation() {
                                    progressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onSuccess(FilterApiResponse element) {
                                    updateUi(element);
                                }

                                @Override
                                public void onError(Throwable t) {
                                    presenter.processError(t);
                                }
                            });
                } else {
                    presenter.onExploreFilterClicked(showAllSwitch.isChecked() ? 1 : 0,
                            menSwitch.isChecked() ? 1 : 0,
                            womenSwitch.isChecked() ? 1 : 0,
                            damagedSwitch.isChecked() ? 1 : 0,
                            vipShowRoomSwitch.isChecked() ? 1 : 0,
                            showRoomSwitch.isChecked() ? 1 : 0,
                            vipHotZoneSwitch.isChecked() ? 1 : 0,
                            hotZoneSwitch.isChecked() ? 1 : 0,
                            new OperationListener<List<ExploreItem>>() {
                                @Override
                                public void onPreOperation() {
                                    progressBar.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onPostOperation() {
                                    progressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onSuccess(List<ExploreItem> element) {
                                    updateExploreUi(element);
                                }

                                @Override
                                public void onError(Throwable t) {
                                    presenter.processError(t);
                                }
                            });
                }
            });
            filterDialog.show();
        }
    }

    @Override
    public void showSwearDialog(SettingsModel settingsModel, int carId) {
        uiUtil.getDialogBuilder(getActivity(), R.layout.dialog_sewar)
                .text(R.id.tv_sewar, settingsModel.getSwearText())
                .clickListener(R.id.btn_reject, (dialog, view) -> {
                    dialog.dismiss();
                })
                .clickListener(R.id.btn_accept, (dialog, view) -> {
                    Intent i = new Intent(getActivity(), PaymentActivityForAddcar.class);
                    i.putExtra("commission", settingsModel.getCommissionPercentage());
                    i.putExtra("carId", carId);
                    startActivityForResult(i, 100);
                    dialog.dismiss();
                })
                .background(R.color.colorwhete)
                .build()
                .show();
    }

    @Override
    public void showTermsDialog() {
        startActivity(new Intent(getContext(), PrivacyActivity.class));
    }

    @Override
    public void huaweiPermission() {
        final SharedPreferences settings = getActivity().getSharedPreferences("ProtectedApps", MODE_PRIVATE);
        final String saveIfSkip = "skipProtectedAppsMessage";
        boolean skipMessage = settings.getBoolean(saveIfSkip, false);
        if (!skipMessage) {
            final SharedPreferences.Editor editor = settings.edit();
            Intent intent = new Intent();
            intent.setClassName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity");
            if (isCallable(intent)) {
                final AppCompatCheckBox dontShowAgain = new AppCompatCheckBox(getActivity());
                dontShowAgain.setText("Do not show again");
                dontShowAgain.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    editor.putBoolean(saveIfSkip, isChecked);
                    editor.apply();
                });

                new AlertDialog.Builder(getActivity())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Huawei Protected Apps")
                        .setMessage(String.format("%s requires to be enabled in 'Protected Apps' to function properly.%n", getString(R.string.app_name)))
                        .setView(dontShowAgain)
                        .setPositiveButton("Protected Apps", (dialog, which) -> huaweiProtectedApps())
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            } else {
                editor.putBoolean(saveIfSkip, true);
                editor.apply();
            }
        }
    }

    @Override
    public void showInterestPopup() {
        uiUtil.getDialogBuilder(getActivity(), R.layout.layout_interest_dialog)
                .text(R.id.tv_message, getString(R.string.do_you_want_add_interest))
                .textGravity(R.id.tv_message, Gravity.CENTER)
                .clickListener(R.id.no, (dialog, view) -> dialog.dismiss())
                .clickListener(R.id.yes, (dialog, view) -> {
                    startActivity(new Intent(getActivity(), AddInterestActivity.class));
                    dialog.dismiss();
                })
                .background(R.drawable.inset_bottomsheet_background)
                .gravity(Gravity.CENTER)
                .build()
                .show();
    }

    private boolean isCallable(Intent intent) {
        List<ResolveInfo> list = getActivity().getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private void huaweiProtectedApps() {
        try {
            String cmd = "am start -n com.huawei.systemmanager/.optimize.process.ProtectActivity";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                cmd += " --user " + getUserSerial();
            }
            Runtime.getRuntime().exec(cmd);
        } catch (IOException ignored) {
        }
    }

    private String getUserSerial() {
        //noinspection ResourceType
        Object userManager = getActivity().getSystemService("user");
        if (null == userManager) return "";

        try {
            Method myUserHandleMethod = android.os.Process.class.getMethod("myUserHandle", (Class<?>[]) null);
            Object myUserHandle = myUserHandleMethod.invoke(android.os.Process.class, (Object[]) null);
            Method getSerialNumberForUser = userManager.getClass().getMethod("getSerialNumberForUser", myUserHandle.getClass());
            Long userSerial = (Long) getSerialNumberForUser.invoke(userManager, myUserHandle);
            if (userSerial != null) {
                return String.valueOf(userSerial);
            } else {
                return "";
            }
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException | IllegalAccessException ignored) {
        }
        return "";
    }

    private void changeStatusBarColor() {
        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getResources().getColor(R.color.colorLayoutBackground));
    }

    @OnClick(R.id.iv_add)
    void onAddClicked() {
        startActivity(new Intent(getContext(), AddCarActivity.class));
    }

    public void setFilterNearByDataReady(NotifyFilterNearByDataReady filterNearByDataReady) {
        this.filterNearByDataReady = filterNearByDataReady;
    }

    public void setFilterExploreDataReady(NotifyFilterExploreDataReady filterExploreDataReady) {
        this.filterExploreDataReady = filterExploreDataReady;
    }

    public void setLocation(Location location) {
        presenter.setLastLocation(location);
    }

    public Location getCurrentLocation() {
        return presenter.getLastLocation();
    }

    @Override
    public void showCarExpiredDialog(CarApiResponse carApiResponse) {
        this.carId = carApiResponse.getId();
        String carName = carApiResponse.getCarName();
        if (isEnglishLang()) {
            carName = carApiResponse.getCarNameEn();
        }
        uiUtil.getDialogBuilder(getActivity(), R.layout.dialog_car_expired)
                .text(R.id.tv_sewar, getString(R.string.your_car) + " \" " + carName + " \" " + getString(R.string.is_expired))
                .clickListener(R.id.btn_reject, (dialog, view) ->
                {
                    presenter.markCarSold(carApiResponse.getId());
                    dialog.dismiss();
                })
                .clickListener(R.id.btn_accept, (dialog, view) -> {
                    presenter.renewCar(carApiResponse.getId());
                    showSuccessMessage(getString(R.string.is_renewed_successfuly));
                    dialog.dismiss();
                })
                .background(getResources().getDrawable(R.drawable.shape_rounded_white))
                .gravity(Gravity.CENTER)
                .cancelable(false)
                .build()
                .show();
    }

    @Override
    public void setSwearData(SettingsModel settingsApiResponse) {
        uiUtil.getDialogBuilder(getActivity(), R.layout.dialog_sewar)
                .text(R.id.tv_sewar, settingsApiResponse.getSwearText())
                .clickListener(R.id.btn_reject, (dialog, view) -> {
//                    presenter.renewCar(carId);
                    dialog.dismiss();
                })
                .clickListener(R.id.btn_accept, (dialog, view) -> {
                    Intent i = new Intent(getContext(), ConfirmCommissionActivity.class);
                    i.putExtra("commission", settingsApiResponse.getCommissionPercentage());
                    i.putExtra("carId", carId);
                    startActivityForResult(i, 100);
                    dialog.dismiss();
                }).background(R.color.colorwhete)
                .background(getResources().getDrawable(R.drawable.shape_rounded_white))
                .gravity(Gravity.CENTER)
                .cancelable(false)
                .build()
                .show();
    }

    boolean isEnglishLang() {
        return Locale.getDefault().getLanguage().equals("en");
    }

    @Override
    public void showSurveyDialog(SurveyApiResponse.Surveies surveies) {
        String surveyName = surveies.getNameAr();
        if (isEnglishLang()) {
            surveyName = surveies.getNameEn();
        }
        uiUtil.getDialogBuilder(getActivity(), R.layout.dialog_survey)
                .text(R.id.tv_title, surveyName)
                .text(R.id.tv_sewar, getString(R.string.you_have_survey))
                .clickListener(R.id.btn_reject, (dialog, view) -> {
                    presenter.postCanceledSurvey(surveies.getId());
                    dialog.dismiss();
                })
                .clickListener(R.id.btn_accept, (dialog, view) -> {
                    startActivity(new Intent(getActivity(), SurveyActivity.class));
                    dialog.dismiss();
                })
                .background(getResources().getDrawable(R.drawable.shape_rounded_white))
                .gravity(Gravity.CENTER)
                .cancelable(false)
                .build()
                .show();
    }

    @Override
    public void processLogout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }

}