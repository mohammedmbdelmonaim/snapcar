package com.intcore.snapcar.ui.nearby;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen;
import com.ethanhua.skeleton.Skeleton;
import com.google.firebase.auth.FirebaseAuth;
import com.intcore.snapcar.R;
import com.intcore.snapcar.di.UIHostComponentProvider;
import com.intcore.snapcar.di.activity.ActivityComponent;
import com.intcore.snapcar.di.fragment.FragmentModule;
import com.intcore.snapcar.store.model.brands.BrandsViewModel;
import com.intcore.snapcar.store.model.category.CategoryViewModel;
import com.intcore.snapcar.store.model.explore.ExploreItem;
import com.intcore.snapcar.store.model.model.ModelViewModel;
import com.intcore.snapcar.store.model.search.SearchRequestModel;
import com.intcore.snapcar.ui.addinterest.BrandAdapter;
import com.intcore.snapcar.ui.addinterest.CategoryAdapter;
import com.intcore.snapcar.ui.addinterest.ModelAdapter;
import com.intcore.snapcar.ui.advancedsearch.AdvancedSearchActivityArgs;
import com.intcore.snapcar.ui.home.HomeFragment;
import com.intcore.snapcar.ui.host.HostActivity;
import com.intcore.snapcar.ui.login.LoginActivity;
import com.intcore.snapcar.ui.search.SearchActivityArgs;
import com.intcore.snapcar.core.chat.model.constants.Gender;
import com.intcore.snapcar.core.scope.FragmentScope;
import com.intcore.snapcar.core.util.LocaleUtil;
import com.intcore.snapcar.core.util.Preconditions;
import com.intcore.snapcar.core.util.UiUtil;
import com.intcore.snapcar.core.widget.rxedittext.RxEditText;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.apptik.widget.MultiSlider;

/*
 * This class is responsible for Exploring all data in the app
 */
@FragmentScope
public class NearByFragment extends Fragment implements NearByScreen {

    private static String[] c = new String[]{" k", " m", " b", " t"};
    @Inject
    NearByPresenter presenter;
    @Inject
    NearByRecyclerAdapter adapter;
    @Inject
    UiUtil uiUtil;
    @BindView(R.id.tv_no_data)
    TextView noDataTextView;
    @BindView(R.id.rv_near_by)
    RecyclerView nearByRecyclerView;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @Gender
    int gender = Gender.SHOW_ALL;
    private MaterialDialog basicSearchDialog;
    private MaterialDialog materialDialog;
    private int minPrice;
    private int maxPrice;
    private RecyclerViewSkeletonScreen skeletonScreen;
    private HomeFragment homeFragment;
    Calendar limitCalender = Calendar.getInstance();
    private int fromYear = limitCalender.get(Calendar.YEAR) - 1;
    private int toYear = limitCalender.get(Calendar.YEAR) + 1;

    public NearByFragment() {
        // Required empty public constructor
    }

    private static String coolFormat(int n, int iteration) {
        int d = (n / 100) / 10;
        boolean isRound = (d * 10) % 10 == 0;//true if the decimal part is equal to 0 (then it's trimmed anyway)
        return (d < 1000 ? //this determines the class, i.e. 'k', 'm' etc
                ((d > 99.9 || isRound || (!isRound && d > 9.99) ? //this decides whether to trim the decimals
                        d * 10 / 10 : d + "" // (int) d * 10 / 10 drops the decimal
                ) + " " + c[iteration]) : coolFormat(d, iteration + 1));
    }

    @Override
    public void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
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
                throw new IllegalStateException("Component must be " + ActivityComponent.class.getName());
            }
        } else {
            throw new AssertionError("host context must implement " + UIHostComponentProvider.class.getName());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        presenter.onViewCreated();
        noDataTextView.setVisibility(View.GONE);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if ("ar".equals(LocaleUtil.getLanguage(getActivity()))) {
            getActivity().getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            getActivity().getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        return inflater.inflate(R.layout.fragment_near_by, container, false);
    }

    @Override
    public void updateUi(List<ExploreItem> paginateexploreItemsRelay) {
        noDataTextView.setVisibility(View.GONE);
        nearByRecyclerView.postDelayed(() -> skeletonScreen.hide(), 3000);
//        nearByRecyclerView.getRecycledViewPool().setMaxRecycledViews(1,0);
        adapter.setdata(paginateexploreItemsRelay);
        if (!isEnglishLang()) {
            c = new String[]{" الف", " مليون", " بليون", " t"};
        }
    }

    @Override
    public void setupRecyclerView() {
        nearByRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        skeletonScreen =  Skeleton.bind(nearByRecyclerView)
                .adapter(adapter)
                .duration(1200)
                .count(10)
                .shimmer(false)
                .load(R.layout.item_skeleton_news)
                .show();
        adapter.setOnBottomReachedListener(presenter::fetchPaginateExploreItems);
    }

    @Override
    public void setupRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(presenter::fetchData);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResume() {
        homeFragment = (HomeFragment) getParentFragment();
        presenter.onResume();
        noDataTextView.setVisibility(View.GONE);
        if (homeFragment != null) {
            homeFragment.setFilterExploreDataReady(exploreItems -> {
                noDataTextView.setVisibility(View.GONE);
                presenter.setExploreItems(exploreItems);
                if (exploreItems.size() > 0) {
                    noDataTextView.setVisibility(View.GONE);
                } else {
                    noDataTextView.setVisibility(View.VISIBLE);
                }
            });
        }
        super.onResume();
    }

    @Override
    public void showLoadingAnimation() {
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideLoadingAnimation() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showErrorMessage(String message) {
        uiUtil.getErrorSnackBar(((HostActivity) Preconditions.requireNonNull(getActivity())).getSnackBarContainer(), message).show();
    }

    public void showSuccessMessage(String message) {
        uiUtil.getSuccessSnackBar(((HostActivity) Preconditions.requireNonNull(getActivity())).getSnackBarContainer(), message).show();
    }

    public void showWarningMessage(String message) {
        uiUtil.getWarningSnackBar(((HostActivity) Preconditions.requireNonNull(getActivity())).getSnackBarContainer(), message).show();
    }

    @Override
    public void showNoDataIndicator() {
        noDataTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void processLogout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void notifyAdapter(int counter, int size) {
        adapter.notifyItemRangeInserted(counter, size);
    }

    @Override
    public void appendData(List<ExploreItem> exploreItems) {
        nearByRecyclerView.post(() -> adapter.appendData(exploreItems));
    }

    @OnClick(R.id.et_search)
    void onSearchClicked()  {
        if (getActivity() instanceof HostActivity) {
            basicSearchDialog = new MaterialDialog.Builder(getActivity())
                    .customView(R.layout.basic_search_dialog_layout, false)
                    .backgroundColor(getResources().getColor(R.color.transparent))
                    .cancelable(false)
                    .build();
            if ("ar".equals(LocaleUtil.getLanguage(getActivity()))) {
                basicSearchDialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            } else {
                basicSearchDialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }
            basicSearchDialog.setOnDismissListener(dialog -> {
                presenter.setSelectedModelModel(ModelViewModel.createDefault());
                presenter.setSelectedCategoryModel(CategoryViewModel.createDefault());
                presenter.setSelectedBrandModel(BrandsViewModel.createDefault());
            });
            Calendar currentCalender = Calendar.getInstance();
            View inflater = basicSearchDialog.getCustomView();
            MultiSlider yearMultiSlider = inflater.findViewById(R.id.year_slider);
            MultiSlider priceMultiSlider = inflater.findViewById(R.id.price_slider);
            TextView minYearTextView = inflater.findViewById(R.id.tv_year_min);
            TextView maxYearTextView = inflater.findViewById(R.id.tv_year_max);
            TextView minPriceTextView = inflater.findViewById(R.id.tv_price_min);
            TextView maxPriceTextView = inflater.findViewById(R.id.tv_price_max);
            RxEditText categoryEditText = inflater.findViewById(R.id.et_category);
            RxEditText brandEditText = inflater.findViewById(R.id.et_brand);
            RxEditText modelEditText = inflater.findViewById(R.id.et_model);
            EditText  priceFromEditText = inflater.findViewById(R.id.et_price_from);
            EditText  priceToEditText = inflater.findViewById(R.id.et_price_to);
            EditText  yearToEditText = inflater.findViewById(R.id.et_year_to);
            EditText  yearFromEditText = inflater.findViewById(R.id.et_year_from);

            disableLongPress(categoryEditText);
            disableLongPress(brandEditText);
            disableLongPress(modelEditText);
            TextView searchTextView = inflater.findViewById(R.id.tv_search);
            TextView cancelTextView = inflater.findViewById(R.id.tv_cancel);
            TextView advancedSearchTextView = inflater.findViewById(R.id.tv_advanced_search);
            RxEditText typeEditText = inflater.findViewById(R.id.et_type);
            SwitchCompat nearBySwitch = inflater.findViewById(R.id.switch_nearby);
            yearMultiSlider.setMax(currentCalender.get(Calendar.YEAR) + 1);
            yearMultiSlider.setMin(1950);
            minYearTextView.setText(String.valueOf(yearMultiSlider.getMin()));
            maxYearTextView.setText(String.valueOf(yearMultiSlider.getMax()));
            yearMultiSlider.getThumb(0).setValue(yearMultiSlider.getMin());
            yearMultiSlider.getThumb(1).setValue(yearMultiSlider.getMax());
            if (presenter.getMaxPrice() != null && presenter.getMinPrice() != null) {

                // init year editText
                yearFromEditText.setText("1950");
                yearToEditText.setText(String.valueOf(toYear));
                yearFromEditText.setOnClickListener(v->{
                    if (fromYear == limitCalender.get(Calendar.YEAR) - 1) {
                        MonthPickerDialog.Builder builder =
                                new MonthPickerDialog.Builder(getContext(), (selectedMonth, selectedYear) ->
                                        yearFromEditText.setText(String.valueOf(selectedYear)),
                                        limitCalender.get(Calendar.YEAR),
                                        limitCalender.get(Calendar.MONTH));
                        builder.setActivatedMonth(Calendar.JULY)
                                .setMinYear(1950)
                                .setActivatedYear(limitCalender.get(Calendar.YEAR) - 1)
                                .setMaxYear(limitCalender.get(Calendar.YEAR) - 1)
                                .showYearOnly()
                                .setOnYearChangedListener(year -> {
                                    yearFromEditText.setText(String.valueOf(year));
                                    fromYear = year;
                                })
                                .build()
                                .show();
                    } else {
                        MonthPickerDialog.Builder builder =
                                new MonthPickerDialog.Builder(getContext(), (selectedMonth, selectedYear) ->
                                        yearFromEditText.setText(String.valueOf(selectedYear)),
                                        limitCalender.get(Calendar.YEAR),
                                        limitCalender.get(Calendar.MONTH));
                        builder.setActivatedMonth(Calendar.JULY)
                                .setMinYear(1950)
                                .setActivatedYear(fromYear)
                                .setMaxYear(limitCalender.get(Calendar.YEAR) - 1)
                                .showYearOnly()
                                .setOnYearChangedListener(year -> {
                                    yearFromEditText.setText(String.valueOf(year));
                                    fromYear = year;
                                })
                                .build()
                                .show();
                    }
                });
                yearToEditText.setOnClickListener(v->{
                    if (toYear == limitCalender.get(Calendar.YEAR) + 2) {
                        MonthPickerDialog.Builder builder =
                                new MonthPickerDialog.Builder(getContext(), (selectedMonth, selectedYear) ->
                                        yearToEditText.setText(String.valueOf(selectedYear)),
                                        limitCalender.get(Calendar.YEAR) + 2,
                                        limitCalender.get(Calendar.MONTH));
                        builder.setActivatedMonth(Calendar.JULY)
                                .setMinYear(1950)
                                .setActivatedYear(limitCalender.get(Calendar.YEAR))
                                .setMaxYear(limitCalender.get(Calendar.YEAR) + 1)
                                .showYearOnly()
                                .setOnYearChangedListener(year -> {
                                    yearToEditText.setText(String.valueOf(year));
                                    toYear = year;
                                })
                                .build()
                                .show();
                    } else {
                        MonthPickerDialog.Builder builder = new
                                MonthPickerDialog.Builder(getContext(), (selectedMonth, selectedYear) ->
                                yearToEditText.setText(String.valueOf(selectedYear)),
                                limitCalender.get(Calendar.YEAR) + 2,
                                limitCalender.get(Calendar.MONTH));
                        builder.setActivatedMonth(Calendar.JULY)
                                .setMinYear(1950)
                                .setActivatedYear(toYear)
                                .setMaxYear(limitCalender.get(Calendar.YEAR) + 1)
                                .showYearOnly()
                                .setOnYearChangedListener(year -> {
                                    yearToEditText.setText(String.valueOf(year));
                                    toYear = year;
                                })
                                .build()
                                .show();
                    }
                });

                // init price editText
                priceToEditText.setText(presenter.getMaxPrice() == null ? "1000" : presenter.getMaxPrice());
                priceFromEditText.setText(presenter.getMinPrice());

                priceMultiSlider.setOnThumbValueChangeListener((multiSlider, thumb, thumbIndex, value) -> {
                    minPrice = multiSlider.getThumb(0).getValue();
                    maxPrice = multiSlider.getThumb(1).getValue();
                    minPriceTextView.setText(String.valueOf(coolFormat((multiSlider.getThumb(0).getValue()), 0)));
                    maxPriceTextView.setText(String.valueOf(coolFormat((multiSlider.getThumb(1).getValue()), 0)));
                });
                priceMultiSlider.setMax(Integer.parseInt(presenter.getMaxPrice() == null ? "1000" : presenter.getMaxPrice()));
                priceMultiSlider.setMin(Integer.parseInt(presenter.getMinPrice()));
                maxPriceTextView.setText(String.valueOf(coolFormat((Integer.parseInt(presenter.getMaxPrice())), 0)));
                minPriceTextView.setText(presenter.getMinPrice());
                priceMultiSlider.getThumb(0).setValue(Integer.parseInt(presenter.getMinPrice()));
                priceMultiSlider.getThumb(1).setValue(Integer.parseInt(presenter.getMaxPrice()));
                if (!isEnglishLang()) {
                    minPriceTextView.setGravity(Gravity.RIGHT);
                    maxPriceTextView.setGravity(Gravity.LEFT);
                }
            }
            yearMultiSlider.setOnThumbValueChangeListener((multiSlider, thumb, thumbIndex, value) -> {
                minYearTextView.setText(String.valueOf(multiSlider.getThumb(0).getValue()));
                maxYearTextView.setText(String.valueOf(multiSlider.getThumb(1).getValue()));
            });
            brandEditText.setOnClickListener(v -> {
                materialDialog = new MaterialDialog.Builder(getActivity())
                        .adapter(new BrandAdapter(getActivity(), viewModel -> {
                                    presenter.setSelectedBrandModel(viewModel);
                                    updateBrand(viewModel, brandEditText, modelEditText, categoryEditText);
                                    materialDialog.dismiss();
                                }, presenter.getBrandsList()),
                                new LinearLayoutManager(getActivity()))
                        .build();
                materialDialog.show();
            });
            modelEditText.setOnClickListener(v -> {
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
                    materialDialog.show();
                } else {
                    showWarningMessage(getString(R.string.you_should_select_brand_first));
                }
            });
            categoryEditText.setOnClickListener(v -> {
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
                    materialDialog.show();
                } else {
                    showWarningMessage(getString(R.string.you_should_select_model_first));
                }
            });
            typeEditText.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(getActivity(), v);
                getActivity().getMenuInflater().inflate(R.menu.menu_gender_show_all, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(item -> onSellerFlowMenuItemClicked(item, typeEditText));
                popupMenu.show();
            });
            searchTextView.setOnClickListener(v -> {
                int brandId = 0, modelId = 0, categoryId = 0;
                if (presenter.getSelectedBrandModel() != null) {
                    brandId = presenter.getSelectedBrandModel().getId();
                }
                if (presenter.getSelectedModelModel() != null) {
                    modelId = presenter.getSelectedModelModel().getId();
                }
                if (presenter.getSelectedCategoryModel() != null) {
                    categoryId = presenter.getSelectedCategoryModel().getId();
                }

                minPrice = 0;
                maxPrice = Integer.parseInt(presenter.getMaxPrice()==null?"100000":presenter.getMaxPrice());
                if(!TextUtils.isEmpty(priceFromEditText.getText().toString())){
                    minPrice = Integer.parseInt(priceFromEditText.getText().toString());
                }
                if(!TextUtils.isEmpty(priceToEditText.getText().toString())){
                    maxPrice = Integer.parseInt(priceToEditText.getText().toString());
                }
                SearchRequestModel.Builder builder = new SearchRequestModel.Builder()
                        .categoryId(categoryId)
                        .brandId(brandId)
                        .modelId(modelId)
                        .priceFrom(String.valueOf(minPrice))
                        .price_to(String.valueOf(maxPrice))
                        .yearFrom(yearFromEditText.getText().toString())
                        .yearTo(yearToEditText.getText().toString())
                        .gender(gender);
                if (nearBySwitch.isChecked()) {
                    if (homeFragment != null) {
                        if (homeFragment.getCurrentLocation() != null)
                            builder.longitude(String.valueOf(homeFragment.getCurrentLocation().getLongitude()))
                                    .latitude(String.valueOf(homeFragment.getCurrentLocation().getLatitude()));
                    }
                }
                SearchRequestModel searchRequestModel = builder.build();
                basicSearchDialog.dismiss();
                new SearchActivityArgs(searchRequestModel, true)
                        .launch(getActivity());
            });
            advancedSearchTextView.setOnClickListener(v -> {
                if (presenter.getMinPrice() != null) {
                    new AdvancedSearchActivityArgs(presenter.getMinPrice(), presenter.getMaxPrice())
                            .launch(getActivity());
                }
                basicSearchDialog.dismiss();
            });
            cancelTextView.setOnClickListener(v -> basicSearchDialog.dismiss());
            basicSearchDialog.show();
        }
    }

    private boolean onSellerFlowMenuItemClicked(MenuItem menuItem, RxEditText typeEditText) {
        if (menuItem.getItemId() == R.id.male) {
            typeEditText.setText(getString(R.string.male));
            gender = Gender.MALE;
            return true;
        } else if (menuItem.getItemId() == R.id.female) {
            typeEditText.setText(getString(R.string.female));
            gender = Gender.FEMALE;
            return true;
        } else if (menuItem.getItemId() == R.id.item_all) {
            typeEditText.setText(getString(R.string.show_all));
            gender = Gender.SHOW_ALL;
            return true;
        }
        return true;
    }

    private void updateBrand(BrandsViewModel viewModel,
                             RxEditText brandEditText,
                             RxEditText modelEditText,
                             RxEditText categoryEditText) {
        brandEditText.setText(viewModel.getName());
        if (viewModel.getBrandsModels().size() > 0) {
            updateModel(viewModel.getBrandsModels().get(0), modelEditText, categoryEditText);
            presenter.setSelectedModelModel(viewModel.getBrandsModels().get(0));
        }
    }

    private void updateModel(ModelViewModel viewModel,
                             RxEditText modelEditText,
                             RxEditText categoryEditText) {
        modelEditText.setText(viewModel.getName());
        if (viewModel.getCategoryViewModels().size() > 0) {
            updateCategory(viewModel.getCategoryViewModels().get(0), categoryEditText);
            presenter.setSelectedCategoryModel(viewModel.getCategoryViewModels().get(0));
        }
    }

    private void updateCategory(CategoryViewModel viewModel,
                                RxEditText categoryEditText) {
        categoryEditText.setText(viewModel.getName());
    }

    private boolean isEnglishLang() {
        return (LocaleUtil.getLanguage(getActivity()).equals("en"));
    }

    void disableLongPress(RxEditText editText) {
        editText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });
    }

    @Override
    public void onDestroyView() {
        nearByRecyclerView.setAdapter(null);
        super.onDestroyView();
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        homeFragment = ((HomeFragment) fragmentManager.findFragmentByTag(HomeFragment.class.getSimpleName()));
    }

}