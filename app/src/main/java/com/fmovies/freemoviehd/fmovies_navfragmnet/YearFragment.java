package com.fmovies.freemoviehd.fmovies_navfragmnet;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.fmovies.freemoviehd.R;
import com.fmovies.freemoviehd.fmovies_adapter.CountryAdapter;
import com.fmovies.freemoviehd.fmovies_model.CommonModels;
import com.fmovies.freemoviehd.fmovies_utl.ApiResources;
import com.fmovies.freemoviehd.fmovies_utl.BannerAds;
import com.fmovies.freemoviehd.fmovies_utl.NetworkInst;
import com.fmovies.freemoviehd.fmovies_utl.SpacingItemDecoration;
import com.fmovies.freemoviehd.fmovies_utl.ToastMsg;
import com.fmovies.freemoviehd.fmovies_utl.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class YearFragment extends Fragment {

    ShimmerFrameLayout shimmerFrameLayout;
    private RecyclerView recyclerView;
    private List<CommonModels> list = new ArrayList<>();
    private CountryAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CoordinatorLayout coordinatorLayout;
    private TextView tvNoItem;

    private RelativeLayout adView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.layout_year,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getResources().getString(R.string.year));

        adView=view.findViewById(R.id.adView);
        coordinatorLayout=view.findViewById(R.id.coordinator_lyt);
        shimmerFrameLayout=view.findViewById(R.id.shimmer_view_container);
        swipeRefreshLayout=view.findViewById(R.id.swipe_layout);
        recyclerView=view.findViewById(R.id.recyclerView);
        tvNoItem=view.findViewById(R.id.tv_noitem);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(getActivity(), 10), true));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        mAdapter = new CountryAdapter(getContext(), list,"year");
        recyclerView.setAdapter(mAdapter);



        shimmerFrameLayout.startShimmer();

        getYear();

//
//        if (new NetworkInst(getContext()).isNetworkAvailable()){
//
//        }else {
//            tvNoItem.setText(getString(R.string.no_internet));
//            shimmerFrameLayout.stopShimmer();
//            shimmerFrameLayout.setVisibility(View.GONE);
//            coordinatorLayout.setVisibility(View.VISIBLE);
//        }


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                coordinatorLayout.setVisibility(View.GONE);

                recyclerView.removeAllViews();
                list.clear();
                mAdapter.notifyDataSetChanged();

                if (new NetworkInst(getContext()).isNetworkAvailable()){
                    getYear();
                }else {
                    tvNoItem.setText(getString(R.string.no_internet));
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    coordinatorLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        loadAd();

    }

    private void loadAd(){
        if (ApiResources.admobStatus.equals("1")){
            BannerAds.ShowBannerAds(getContext(), adView);
        }
    }


    private void getYear(){


        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        coordinatorLayout.setVisibility(View.GONE);

                for (int i=2020;i>1980;i--){

                    CommonModels models =new CommonModels();
                    models.setId(String.valueOf(i));
                    models.setTitle(String.valueOf(i));
                    list.add(models);
                }
                mAdapter.notifyDataSetChanged();




    }



}
