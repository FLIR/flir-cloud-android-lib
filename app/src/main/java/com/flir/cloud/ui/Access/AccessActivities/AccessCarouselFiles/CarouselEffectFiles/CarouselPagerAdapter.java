package com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.CarouselEffectFiles;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.flir.cloud.R;
import com.flir.cloud.ui.Access.AccessDevicesCarouselActivity;
import com.flir.sdk.models.Device.DeviceDetails;

import java.util.List;

public class CarouselPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {

    public final static float BIG_SCALE = 1.2f;
    public final static float SMALL_SCALE = 0.7f;
    public final static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;
    private AccessDevicesCarouselActivity context;
    private FragmentManager fragmentManager;
    private List<DeviceDetails> resourcesListDetails;
    private float scale;

    public CarouselPagerAdapter(AccessDevicesCarouselActivity context, FragmentManager fm, List<DeviceDetails> response) {
        super(fm);
        this.fragmentManager = fm;
        this.context = context;
        resourcesListDetails = response;
    }

    @Override
    public Fragment getItem(int position) {
        // make the first pager bigger than others
        try {
            if (position == (resourcesListDetails.size() / 2))
                scale = BIG_SCALE;
            else
                scale = SMALL_SCALE;

            position = position % AccessDevicesCarouselActivity.ItemCount;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return  CarouselItemFragment.newInstance(context, position, scale, resourcesListDetails.get(position));
    }

    @Override
    public int getCount() {
        return resourcesListDetails.size();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        try {
            if (positionOffset >= 0f && positionOffset <= 1f) {
                CarouselLinearLayout cur = getRootView(position);
                CarouselLinearLayout next = getRootView(position + 1);

                cur.setScaleBoth(BIG_SCALE - DIFF_SCALE * positionOffset);
                next.setScaleBoth(SMALL_SCALE + DIFF_SCALE * positionOffset);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @SuppressWarnings("ConstantConditions")
    private CarouselLinearLayout getRootView(int position) {
        return (CarouselLinearLayout) fragmentManager.findFragmentByTag(this.getFragmentTag(position))
                .getView().findViewById(R.id.root_container);
    }

    private String getFragmentTag(int position) {
        return "android:switcher:" + context.pager.getId() + ":" + position;
    }
}