package com.myproject.reauc.ui.showproduct;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.myproject.reauc.R;

public class ShowProductFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_show_myproduct, container, false);
        MyProductFragment productFragment = new MyProductFragment();
        MyBillFragment billFragment = new MyBillFragment();

        TabLayout tabs = root.findViewById(R.id.tab_layout);

        getActivity().getSupportFragmentManager().getPrimaryNavigationFragment()
                .getChildFragmentManager().beginTransaction().add(R.id.container, billFragment).commit();

        tabs.addTab(tabs.newTab().setText("입찰 내역"));
        tabs.addTab(tabs.newTab().setText("상품 등록 내역"));

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Fragment selected = null;
                if(position == 0) // 입찰 내역
                    selected = billFragment;
                else if(position == 1) // 물품 등록 내역
                    selected = productFragment;
                getActivity().getSupportFragmentManager().getPrimaryNavigationFragment()
                        .getChildFragmentManager().beginTransaction().replace(R.id.container, selected).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return root;
    }
}
