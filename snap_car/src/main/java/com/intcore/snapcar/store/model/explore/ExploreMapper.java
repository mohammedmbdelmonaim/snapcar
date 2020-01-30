package com.intcore.snapcar.store.model.explore;

import android.os.Build;
import androidx.annotation.RequiresApi;

import com.intcore.snapcar.store.model.ads.AdsViewModel;
import com.intcore.snapcar.store.model.car.CarViewModel;
import com.intcore.snapcar.store.model.defaultuser.DefaultUserModel;
import com.intcore.snapcar.store.model.hotzone.HotZoneViewModel;
import com.intcore.snapcar.core.scope.ApplicationScope;
import com.intcore.snapcar.core.util.ResourcesUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

/*
 *This class is for converting any type of object to ExploreItem to be able to show any type of object in one recycler view
 */
@ApplicationScope
public class ExploreMapper {

    private final ResourcesUtil resourcesUtil;

    @Inject
    ExploreMapper(ResourcesUtil resourcesUtil) {
        this.resourcesUtil = resourcesUtil;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<ExploreItem> toExploreItems(List<CarViewModel> featureList, List<HotZoneViewModel> hotZoneViewModels,
                                            List<DefaultUserModel> showRoomList, List<CarViewModel> carList,
                                            List<AdsViewModel> adsList) {
        List<ExploreItem> exploreItemList = new ArrayList<>();
        for (HotZoneViewModel viewModel : hotZoneViewModels) {
            exploreItemList.add(new HotZoneExploreItem(viewModel));
        }
        for (DefaultUserModel model : showRoomList) {
            exploreItemList.add(new ShowRoomExploreItem(model));
        }
        for (CarViewModel viewModel : carList) {
            exploreItemList.add(new CarExploreItem(viewModel));
        }
        /*
         * this is for sorting different objects based on newest
         */
        Collections.sort(exploreItemList, (o1, o2) -> {
            if (o1 instanceof HotZoneExploreItem && o2 instanceof HotZoneExploreItem) {
                return ((HotZoneExploreItem) o1).getHotZoneViewModel().getCreatedAt().compareTo(((HotZoneExploreItem) o2).getHotZoneViewModel().getCreatedAt());
            } else if (o1 instanceof HotZoneExploreItem && o2 instanceof ShowRoomExploreItem) {
                return ((HotZoneExploreItem) o1).getHotZoneViewModel().getCreatedAt().compareTo(((ShowRoomExploreItem) o2).getShowRoomModel().getCreatedAt());
            } else if (o1 instanceof HotZoneExploreItem && o2 instanceof CarExploreItem) {
                return ((HotZoneExploreItem) o1).getHotZoneViewModel().getCreatedAt().compareTo(((CarExploreItem) o2).getCarItem().getCreatedAt());
            } else if (o1 instanceof ShowRoomExploreItem && o2 instanceof ShowRoomExploreItem) {
                return ((ShowRoomExploreItem) o1).getShowRoomModel().getCreatedAt().compareTo(((ShowRoomExploreItem) o2).getShowRoomModel().getCreatedAt());
            } else if (o1 instanceof ShowRoomExploreItem && o2 instanceof HotZoneExploreItem) {
                return ((ShowRoomExploreItem) o1).getShowRoomModel().getCreatedAt().compareTo(((HotZoneExploreItem) o2).getHotZoneViewModel().getCreatedAt());
            } else if (o1 instanceof ShowRoomExploreItem && o2 instanceof CarExploreItem) {
                return ((ShowRoomExploreItem) o1).getShowRoomModel().getCreatedAt().compareTo(((CarExploreItem) o2).getCarItem().getCreatedAt());
            } else if (o1 instanceof CarExploreItem && o2 instanceof CarExploreItem) {
                return ((CarExploreItem) o1).getCarItem().getCreatedAt().compareTo(((CarExploreItem) o2).getCarItem().getCreatedAt());
            } else if (o1 instanceof CarExploreItem && o2 instanceof HotZoneExploreItem) {
                return ((CarExploreItem) o1).getCarItem().getCreatedAt().compareTo(((HotZoneExploreItem) o2).getHotZoneViewModel().getCreatedAt());
            } else if (o1 instanceof CarExploreItem && o2 instanceof ShowRoomExploreItem) {
                return ((CarExploreItem) o1).getCarItem().getCreatedAt().compareTo(((ShowRoomExploreItem) o2).getShowRoomModel().getCreatedAt());
            }
            return 0;
        });
        Collections.reverse(exploreItemList);
        if (featureList.size() != 0) {
            if (isEnglishLang()) {
                exploreItemList.add(0, new TitleExploreItem("Featured Ads"));
            } else {
                exploreItemList.add(0, new TitleExploreItem("إعلانات مميزة"));
            }
            exploreItemList.add(1, new FeaturedExploreItem(featureList));
            exploreItemList.add(2, new DividerExploreItem("divider"));
        }
        List<ExploreItem> finalExploreItemList = new ArrayList<>();
        if (featureList.size() != 0) {
            finalExploreItemList.add(exploreItemList.get(0));
            finalExploreItemList.add(exploreItemList.get(1));
            finalExploreItemList.add(exploreItemList.get(2));
        }
        /*
         * This for loop is for is for inserting Ads between the cars to be well distributed
         */
        int rowsNumber = 1;
        if (adsList.size() > 0) {
            rowsNumber = exploreItemList.size() / adsList.size();
            if (rowsNumber == 0) rowsNumber = 1;
        }
        int adsCounter = 0;
        for (int i = finalExploreItemList.size(); i < exploreItemList.size(); i++) {
            if ((i - 3) % rowsNumber == 0) {
                if (adsList.size() - 1 >= adsCounter) {
                    finalExploreItemList.add(new AdsExploreItem(adsList.get(adsCounter)));
                    finalExploreItemList.add(exploreItemList.get(i));
                    adsCounter++;
                } else {
                    finalExploreItemList.add(exploreItemList.get(i));
                }
            } else {
                finalExploreItemList.add(exploreItemList.get(i));
            }
        }
        return finalExploreItemList;
    }

    private boolean isEnglishLang() {
        return (Locale.getDefault().getLanguage().equals("en"));
    }
}