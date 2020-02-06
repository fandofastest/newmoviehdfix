package com.fmovies.freemoviehd.fmovies_fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.ixidev.gdpr.GDPRChecker;
import com.fmovies.freemoviehd.DetailsActivity;
import com.fmovies.freemoviehd.ItemMovieActivity;
import com.fmovies.freemoviehd.ItemTVActivity;
import com.fmovies.freemoviehd.MainActivity;
import com.fmovies.freemoviehd.R;
import com.fmovies.freemoviehd.fmovies_adapter.GenreAdapter;
import com.fmovies.freemoviehd.fmovies_adapter.GenreHomeAdapter;
import com.fmovies.freemoviehd.fmovies_adapter.HomePageAdapter;
import com.fmovies.freemoviehd.fmovies_adapter.LiveTvHomeAdapter;
import com.fmovies.freemoviehd.fmovies_model.CommonModels;
import com.fmovies.freemoviehd.fmovies_model.GenreModel;
import com.fmovies.freemoviehd.fmovies_utl.ApiResources;
import com.fmovies.freemoviehd.fmovies_utl.BannerAds;
import com.fmovies.freemoviehd.fmovies_utl.Constants;
import com.fmovies.freemoviehd.fmovies_utl.NetworkInst;
import com.fmovies.freemoviehd.fmovies_utl.ToastMsg;
import com.fmovies.freemoviehd.fmovies_utl.VolleySingleton;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.fmovies.freemoviehd.Config;

public class HomeFragment extends Fragment {

    ViewPager viewPager;
    CirclePageIndicator indicator;

    private List<CommonModels> listSlider = new ArrayList<>();

    private Timer timer;

    private ShimmerFrameLayout shimmerFrameLayout;
    private RecyclerView recyclerViewMovie2020,recyclerViewMovie2019,recyclerViewMovie2018, recyclerViewTv, recyclerViewTvSeries, recyclerViewGenre,recyclerViewPopular,rvlatestmoviews,rvpopularmovies;
    private RecyclerView genreRv;
    private RecyclerView countryRv;
    private GenreAdapter genreAdapter;
    private GenreAdapter countryAdapter;
    private RelativeLayout genreLayout, countryLayout;
    private HomePageAdapter adapterMovie2020,adapterMovie2018,adapterMovie2019, adapterSeries,adapterpopular,adapterlatestmovies;
    private LiveTvHomeAdapter adapterTv;
    private List<CommonModels> listlatestMovie = new ArrayList<>();
    private List <CommonModels> list2020 =new ArrayList<>();
    private List <CommonModels> list2019 =new ArrayList<>();
    private List <CommonModels> list2018 =new ArrayList<>();

    private List<CommonModels> listTv = new ArrayList<>();
    private List<CommonModels> listpopular = new ArrayList<>();
    private List<CommonModels> listSeries = new ArrayList<>();
    private List<CommonModels> genreList = new ArrayList<>();
    private List<CommonModels> countryList = new ArrayList<>();
    private ApiResources apiResources;
    private Button btnMoreMovie, btnMoreTv, btnmore2020,btnmore2019,btnmore2018,btnMorePopularTvseries,btnmorepopularmovies,btnmorelatestmovies;

    private int checkPass = 0;

    private SliderAdapter sliderAdapter;

    private VolleySingleton singleton;
    private TextView tvNoItem;
    private CoordinatorLayout coordinatorLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NestedScrollView scrollView;

    private RelativeLayout adView, adView1;


    private List<GenreModel> listGenre = new ArrayList<>();

    private GenreHomeAdapter genreHomeAdapter;
    private View sliderLayout;

    private MainActivity activity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        activity = (MainActivity) getActivity();

        return inflater.inflate(R.layout.fragment_home, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle(getResources().getString(R.string.app_title));
        apiResources = new ApiResources();

        singleton = new VolleySingleton(getActivity());

        adView = view.findViewById(R.id.adView);
        adView1 = view.findViewById(R.id.adView1);
        btnmorelatestmovies = view.findViewById(R.id.btn_more_latest_movie);
        btnmore2020=view.findViewById(R.id.btn2020);
        btnmore2019=view.findViewById(R.id.btn2019);
        btnmore2018=view.findViewById(R.id.btn2018);
        btnMoreTv = view.findViewById(R.id.btn_more_tv);
        btnMoreMovie = view.findViewById(R.id.btn_more_movie);


        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container);
        viewPager = view.findViewById(R.id.viewPager);
        indicator = view.findViewById(R.id.indicator);
        tvNoItem = view.findViewById(R.id.tv_noitem);
        coordinatorLayout = view.findViewById(R.id.coordinator_lyt);
        swipeRefreshLayout = view.findViewById(R.id.swipe_layout);
        scrollView = view.findViewById(R.id.scrollView);
        sliderLayout = view.findViewById(R.id.slider_layout);
        genreRv = view.findViewById(R.id.genre_rv);
        countryRv = view.findViewById(R.id.country_rv);
        genreLayout = view.findViewById(R.id.genre_layout);
        countryLayout = view.findViewById(R.id.country_layout);

        if (!Constants.IS_GENRE_SHOW) {
            genreLayout.setVisibility(View.GONE);
        }
        if (!Constants.IS_COUNTRY_SHOW) {
            countryLayout.setVisibility(View.GONE);
        }

        sliderAdapter = new SliderAdapter(getActivity(), listSlider);
        viewPager.setAdapter(sliderAdapter);
        indicator.setViewPager(viewPager);

        //----init timer slider--------------------
        timer = new Timer();


        //----btn click-------------
        btnClick();
        sliderLayout.setVisibility(View.GONE);

        // --- genre recycler view ---------
        genreRv.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
        genreRv.setHasFixedSize(true);
        genreRv.setNestedScrollingEnabled(false);
        genreAdapter = new GenreAdapter(getActivity(), genreList, "genre");
        genreRv.setAdapter(genreAdapter);

        // --- genre recycler view ---------
        countryRv.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
        countryRv.setHasFixedSize(true);
        countryRv.setNestedScrollingEnabled(false);
        countryAdapter = new GenreAdapter(getActivity(), countryList, "country");
        countryRv.setAdapter(countryAdapter);

        //----featured tv recycler view-----------------
        recyclerViewTv = view.findViewById(R.id.recyclerViewTv);
        recyclerViewTv.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewTv.setHasFixedSize(true);
        recyclerViewTv.setNestedScrollingEnabled(false);
        adapterTv = new LiveTvHomeAdapter(getContext(), listTv, "MainActivity");
        recyclerViewTv.setAdapter(adapterTv);


        //----featured tv recycler view-----------------
        recyclerViewMovie2020 = view.findViewById(R.id.rv2020);
        recyclerViewMovie2020.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewMovie2020.setHasFixedSize(true);
        recyclerViewMovie2020.setNestedScrollingEnabled(false);
        adapterMovie2020 = new HomePageAdapter(getActivity(), list2020);
        recyclerViewMovie2020.setAdapter(adapterMovie2020);
        //----featured tv recycler view-----------------
        recyclerViewMovie2018 = view.findViewById(R.id.rv2018);
        recyclerViewMovie2018.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewMovie2018.setHasFixedSize(true);
        recyclerViewMovie2018.setNestedScrollingEnabled(false);
        adapterMovie2018 = new HomePageAdapter(getActivity(), list2018);
        recyclerViewMovie2018.setAdapter(adapterMovie2018);

        //----featured tv recycler view-----------------
        recyclerViewMovie2019 = view.findViewById(R.id.rv2019);
        recyclerViewMovie2019.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewMovie2019.setHasFixedSize(true);
        recyclerViewMovie2019.setNestedScrollingEnabled(false);
        adapterMovie2019 = new HomePageAdapter(getActivity(), list2019);
        recyclerViewMovie2019.setAdapter(adapterMovie2019);

        //----movie's recycler view-----------------
        rvlatestmoviews = view.findViewById(R.id.recyclerLatestmovies);
        rvlatestmoviews.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        rvlatestmoviews.setHasFixedSize(true);
        rvlatestmoviews.setNestedScrollingEnabled(false);
        adapterlatestmovies = new HomePageAdapter(getContext(), listlatestMovie);
        rvlatestmoviews.setAdapter(adapterlatestmovies);

//        //----series's recycler view-----------------
//        recyclerViewTvSeries = view.findViewById(R.id.recyclerLatestmovies);
//        recyclerViewTvSeries.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
//        recyclerViewTvSeries.setHasFixedSize(true);
//        recyclerViewTvSeries.setNestedScrollingEnabled(false);
//        adapterSeries = new HomePageAdapter(getActivity(), listSeries);
//        recyclerViewTvSeries.setAdapter(adapterSeries);

        //----genre's recycler view--------------------
        recyclerViewGenre = view.findViewById(R.id.recyclerView_by_genre);
        recyclerViewGenre.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewGenre.setHasFixedSize(true);
        recyclerViewGenre.setNestedScrollingEnabled(false);
        genreHomeAdapter = new GenreHomeAdapter(getContext(), listGenre);
        recyclerViewGenre.setAdapter(genreHomeAdapter);


        shimmerFrameLayout.startShimmer();


        if (new NetworkInst(getContext()).isNetworkAvailable()) {

            if (Constants.IS_GENRE_SHOW) {
                getAllGenre();
            }
            if (Constants.IS_COUNTRY_SHOW) {
                getAllCountry();
            }
            getmovegdrive();
            getmovieyear2019();
            getmovieyear2018();
            getmovieyear2020();

//            getLatestSeries();




        } else {
            tvNoItem.setText(getString(R.string.no_internet));
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
            coordinatorLayout.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

//                recyclerViewTv.removeAllViews();
//                recyclerViewTvSeries.removeAllViews();
//                recyclerViewGenre.removeAllViews();
                rvlatestmoviews.removeAllViews();
                recyclerViewMovie2019.removeAllViews();
                recyclerViewMovie2020.removeAllViews();
                recyclerViewMovie2018.removeAllViews();

                genreList.clear();
                countryList.clear();
                listlatestMovie.clear();



                if (new NetworkInst(getContext()).isNetworkAvailable()) {
                    if (Constants.IS_GENRE_SHOW) {
                        getAllGenre();
                    }
                    if (Constants.IS_COUNTRY_SHOW) {
                        getAllCountry();
                    }
                    getmovegdrive();
                    getmovieyear2020();
                    getmovieyear2019();
                    getmovieyear2018();


                } else {
                    tvNoItem.setText(getString(R.string.no_internet));
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    coordinatorLayout.setVisibility(View.VISIBLE);
                    scrollView.setVisibility(View.GONE);
                }
            }
        });


        getAdDetails(new ApiResources().getAdDetails());


    }

    private void loadAd() {
        if (ApiResources.admobStatus.equals("1")) {
            BannerAds.ShowBannerAds(activity, adView);
            BannerAds.ShowBannerAds(activity, adView1);
        }
    }

    private void btnClick() {

        btnmore2019.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ItemMovieActivity.class);
                intent.putExtra("url", apiResources.getGdrivesearch()+"year="+"2019");
                intent.putExtra("title", "Movies");
                intent.putExtra("type","");
                getActivity().startActivity(intent);
            }
        });


        btnmore2018.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ItemMovieActivity.class);
                intent.putExtra("url", apiResources.getGdrivesearch()+"year="+"2018");
                intent.putExtra("title", "Movies");
                intent.putExtra("type","");

                getActivity().startActivity(intent);
            }
        });
        btnmore2020.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ItemMovieActivity.class);
                intent.putExtra("url", apiResources.getGdrivesearch()+"year="+"2020");
                intent.putExtra("title", "Movies");
                intent.putExtra("type","");

                getActivity().startActivity(intent);
            }
        });




        btnmorelatestmovies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ItemMovieActivity.class);
                intent.putExtra("url", apiResources.getNewgdriveplayerus());
                intent.putExtra("title", "Movies");
                intent.putExtra("type","");

                getActivity().startActivity(intent);
            }
        });
        btnMoreTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ItemTVActivity.class);
                intent.putExtra("url", apiResources.getGet_live_tv());
                intent.putExtra("title", "Live TV");
                intent.putExtra("type","");

                getActivity().startActivity(intent);
            }
        });



    }


    private void getAdDetails(String url) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject jsonObject = response.getJSONObject("admob");

                    ApiResources.admobStatus = jsonObject.getString("status");
                    ApiResources.adMobBannerId = jsonObject.getString("admob_banner_ads_id");
                    ApiResources.adMobInterstitialId = jsonObject.getString("admob_interstitial_ads_id");
                    ApiResources.adMobPublisherId = jsonObject.getString("admob_publisher_id");


                    new GDPRChecker()
                            .withContext(activity)
                            .withPrivacyUrl(Config.TERMS_URL) // your privacy url
                            .withPublisherIds(ApiResources.adMobPublisherId) // your admob account Publisher id
                            .withTestMode("9424DF76F06983D1392E609FC074596C") // remove this on real project
                            .check();

                    loadAd();


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        Volley.newRequestQueue(getContext()).add(jsonObjectRequest);


    }




    private void getmovegdrive() {

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, apiResources.getNewgdriveplayerus(), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                swipeRefreshLayout.setRefreshing(false);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        CommonModels models = new CommonModels();
                        models.setImageUrl(jsonObject.getString("poster"));
                        models.setGenre(jsonObject.getString("genre"));
                        models.setDirector(jsonObject.getString("director"));
                        if ( jsonObject.getString("poster").equals("")){
                            models.setImageUrl("https://fando.id/movienodb/uploads/default.jpg");
                        }

                        models.setTitle(jsonObject.getString("title"));




                        models.setVideoType("movie");
                        models.setReleaseDate(jsonObject.getString("year"));
                        models.setQuality(jsonObject.getString("quality"));
                        models.setId(jsonObject.getString("imdb"));

                        if (ApiResources.statussistem.equals("berbahaya")){
                            models.setId("0");
                            models.setTitle("Title Movie");
                            models.setImageUrl("https://fando.id/movienodb/uploads/default.jpg");


                        }

                        listlatestMovie.add(models);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapterlatestmovies.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        singleton.addToRequestQueue(jsonArrayRequest);

    }



    private void getmovieyear2020() {



        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, apiResources.getGdrivesearch()+"year="+"2020", null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                swipeRefreshLayout.setRefreshing(false);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        CommonModels models = new CommonModels();
                        models.setImageUrl(jsonObject.getString("poster"));
                        models.setGenre(jsonObject.getString("genre"));
                        models.setDirector(jsonObject.getString("director"));
                        if ( jsonObject.getString("poster").equals("")){
                            models.setImageUrl("https://fando.id/movienodb/uploads/default.jpg");
                        }

                        models.setTitle(jsonObject.getString("title"));




                        models.setVideoType("movie");
                        models.setReleaseDate(jsonObject.getString("year"));
                        models.setQuality(jsonObject.getString("quality"));
                        models.setId(jsonObject.getString("imdb"));

                        if (ApiResources.statussistem.equals("berbahaya")){
                            models.setId("0");
                            models.setTitle("Title Movie");
                            models.setImageUrl("https://fando.id/movienodb/uploads/default.jpg");


                        }

                        list2020.add(models);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapterMovie2020.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        singleton.addToRequestQueue(jsonArrayRequest);

    }
    private void getmovieyear2018() {



        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, apiResources.getGdrivesearch()+"year="+"2018", null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                swipeRefreshLayout.setRefreshing(false);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        CommonModels models = new CommonModels();
                        models.setImageUrl(jsonObject.getString("poster"));
                        models.setGenre(jsonObject.getString("genre"));
                        models.setDirector(jsonObject.getString("director"));
                        if ( jsonObject.getString("poster").equals("")){
                            models.setImageUrl("https://fando.id/movienodb/uploads/default.jpg");
                        }

                        models.setTitle(jsonObject.getString("title"));




                        models.setVideoType("movie");
                        models.setReleaseDate(jsonObject.getString("year"));
                        models.setQuality(jsonObject.getString("quality"));
                        models.setId(jsonObject.getString("imdb"));

                        if (ApiResources.statussistem.equals("berbahaya")){
                            models.setId("0");
                            models.setTitle("Title Movie");
                            models.setImageUrl("https://fando.id/movienodb/uploads/default.jpg");


                        }

                        list2018.add(models);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapterMovie2018.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        singleton.addToRequestQueue(jsonArrayRequest);

    }
    private void getmovieyear2019() {



        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, apiResources.getGdrivesearch()+"year="+"2019", null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                swipeRefreshLayout.setRefreshing(false);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        CommonModels models = new CommonModels();
                        models.setImageUrl(jsonObject.getString("poster"));
                        models.setGenre(jsonObject.getString("genre"));
                        models.setDirector(jsonObject.getString("director"));
                        models.setTitle(jsonObject.getString("title"));
                        if ( jsonObject.getString("poster").equals("")){
                            models.setImageUrl("https://fando.id/movienodb/uploads/default.jpg");
                        }


                        models.setVideoType("movie");
                        models.setReleaseDate(jsonObject.getString("year"));
                        models.setQuality(jsonObject.getString("quality"));
                        models.setId(jsonObject.getString("imdb"));

                        if (ApiResources.statussistem.equals("berbahaya")){
                            models.setId("0");
                            models.setTitle("Title Movie");
                            models.setImageUrl("https://fando.id/movienodb/uploads/default.jpg");


                        }







                        list2019.add(models);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapterMovie2019.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        singleton.addToRequestQueue(jsonArrayRequest);

    }



    @Override
    public void onStart() {
        super.onStart();
        shimmerFrameLayout.startShimmer();
    }

    @Override
    public void onPause() {
        super.onPause();
        shimmerFrameLayout.stopShimmer();
        timer.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();
        timer = new Timer();
        timer.scheduleAtFixedRate(new SliderTimer(), 5000, 5000);
    }

    //----timer for auto slide------------------
    private class SliderTimer extends TimerTask {

        @Override
        public void run() {

            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (viewPager.getCurrentItem() < listSlider.size() - 1) {
                            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                        } else {
                            viewPager.setCurrentItem(0);
                        }
                    }
                });
            }

        }
    }

    //----adapter for slider-------------
    public class SliderAdapter extends PagerAdapter {

        private Context context;
        private List<CommonModels> list;

        public SliderAdapter(Context context, List<CommonModels> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.item_slider, null);

            View lyt_parent = view.findViewById(R.id.lyt_parent);

            final CommonModels models = list.get(position);

            TextView textView = view.findViewById(R.id.textView);

            textView.setText(models.getTitle());

            ImageView imageView = view.findViewById(R.id.imageview);

            Picasso.get().load(models.getImageUrl()).into(imageView);


            ViewPager viewPager = (ViewPager) container;
            viewPager.addView(view, 0);

            lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (models.getVideoType().equals("movie")) {

                        Intent intent = new Intent(getContext(), DetailsActivity.class);
                        intent.putExtra("vType","movie");
                        intent.putExtra("id", models.getImdb());
                        startActivity(intent);

                    } else {

                        Intent intent = new Intent(getContext(), DetailsActivity.class);
                        intent.putExtra("vType", models.getVideoType());
                        intent.putExtra("id", models.getId());
                        startActivity(intent);

                    }
                }
            });
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ViewPager viewPager = (ViewPager) container;
            View view = (View) object;
            viewPager.removeView(view);
        }
    }

    private void getAllGenre() {

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, apiResources.getAllGenre(), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

                if (String.valueOf(response).length() < 10) {
                    coordinatorLayout.setVisibility(View.VISIBLE);
                } else {
                    coordinatorLayout.setVisibility(View.GONE);
                }

                for (int i = 0; i < response.length(); i++) {

                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        CommonModels models = new CommonModels();
                        models.setId(jsonObject.getString("genre_id"));
                        models.setTitle(jsonObject.getString("name"));
                        models.setImageUrl(jsonObject.getString("image_url"));
                        genreList.add(models);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                //Toast.makeText(activity, "size:" + genreList.size(), Toast.LENGTH_SHORT).show();
                genreAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipeRefreshLayout.setRefreshing(false);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                new ToastMsg(getActivity()).toastIconError(getString(R.string.fetch_error));

                coordinatorLayout.setVisibility(View.VISIBLE);
            }
        });
        Volley.newRequestQueue(getContext()).add(jsonArrayRequest);


    }

    private void getAllCountry() {

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, apiResources.getAllCountry(), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);


                if (String.valueOf(response).length() < 10) {
                    coordinatorLayout.setVisibility(View.VISIBLE);
                } else {
                    coordinatorLayout.setVisibility(View.GONE);
                }

                for (int i = 0; i < response.length(); i++) {

                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        CommonModels models = new CommonModels();
                        models.setTitle(jsonObject.getString("name"));
                        models.setId(jsonObject.getString("country_id"));
                        models.setImageUrl(jsonObject.getString("image_url"));
                        countryList.add(models);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                countryAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipeRefreshLayout.setRefreshing(false);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                new ToastMsg(getActivity()).toastIconError(getString(R.string.fetch_error));
                coordinatorLayout.setVisibility(View.VISIBLE);

            }
        });
        Volley.newRequestQueue(getContext()).add(jsonArrayRequest);

    }


}
