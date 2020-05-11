package xcj.appsets.ui.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import xcj.appsets.Constant
import xcj.appsets.R
import xcj.appsets.SharedPreferencesTranslator
import xcj.appsets.adapter.FeaturedAppsAdapter
import xcj.appsets.ui.CategoryActivity
import xcj.appsets.ui.MainActivity
import xcj.appsets.util.*
import xcj.appsets.viewmodel.AppDetailsViewModel
import xcj.appsets.viewmodel.HomeAppsViewModel


class FragmentHome : Fragment() {

    private lateinit var spTranslator: SharedPreferencesTranslator
    private lateinit var topAppsAdapter: FeaturedAppsAdapter
    private lateinit var topGamesAdapter: FeaturedAppsAdapter
    private lateinit var topFamilyAdapter: FeaturedAppsAdapter
    private lateinit var homeAppViewModel: HomeAppsViewModel
    var FAB_CLICKED = false
    private val disposable = CompositeDisposable()
    private var appDetailsViewModel: AppDetailsViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        spTranslator = SharedPreferencesTranslator(getSharedPreferences(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("Home_onCreateView", "执行")
        return inflater.inflate(R.layout.fragment_home, container, false)

    }

    override fun onResume() {
        super.onResume()
        Log.d("Today_onResume", "执行")
        requireActivity().findViewById<AppCompatTextView>(R.id.search_edit_text).apply {
            text = getString(R.string.home)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(
            "Fragment栈的大小为", "[${
            requireActivity().supportFragmentManager.backStackEntryCount
            }]"
        )


        setupButtonsOnClickeListener()



    }
    private fun setupButtonsOnClickeListener() {
        btn_top_apps.setOnClickListener{
            openCategoryActivity(
                Constant.CATEGORY_APPS
            )
        }
        btn_top_games.setOnClickListener {
            openCategoryActivity(
                Constant.CATEGORY_GAME
            )
        }
        btn_top_family.setOnClickListener{
            openCategoryActivity(
                Constant.CATEGORY_FAMILY
            )
        }



    }
    private fun openCategoryActivity(categoryId: String) {
        val intent = Intent(requireContext(), CategoryActivity::class.java)
        intent.putExtra("CategoryId", categoryId)
        intent.putExtra("CategoryName", spTranslator.getString(categoryId))
        requireContext().startActivity(
            intent,
            ViewUtil.getEmptyActivityBundle(requireContext() as MainActivity)
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        homeAppsWhenNotLoginToGooglePlay.setImageResource(R.drawable.no_googleapi_tip)
        if (Accountant.isLoggedIn(requireContext()) != null && Accountant.isLoggedIn(requireContext()) == false) {
            homeAppsNestedScrollView.visibility = View.GONE
            homeAppsWhenNotLoginToGooglePlay.visibility = View.VISIBLE
        } else {
            val model by viewModels<HomeAppsViewModel>()
            homeAppViewModel = model
            setupRecyclers()
            fillAppContentToUI()
        }

    homeAppsNestedScrollView.isSmoothScrollingEnabled = true
    }
    private fun fillAppContentToUI(){
        homeAppViewModel.getTopApps().observe(viewLifecycleOwner) {
            recycler_top_apps.addVeiledItems(it.size)
            CoroutineScope(IO).launch {
                topAppsAdapter.apply {
                    addData(it)
                    if (isDataEmpty) {
                        MainScope().launch {
                            homeAppsNestedScrollView.visibility = View.GONE
                            homeAppsWhenNotLoginToGooglePlay.visibility = View.VISIBLE
                        }
                       /* homeAppsNestedScrollView.visibility = View.GONE
                        homeAppsWhenNotLoginToGooglePlay.visibility = View.VISIBLE*/
                    } else {
                        topAppsAdapter.notifyDataSetChanged()
                        MainScope().launch {
                            object : CountDownTimer(1500, 500) {
                                override fun onFinish() {

                                        veilLayout_top_apps?.unVeil()
                                        recycler_top_apps?.unVeil()
                                        veilLayout_top_apps_more?.unVeil()

                                    /* veilLayout_top_apps?.unVeil()
                                     recycler_top_apps?.unVeil()
                                     veilLayout_top_apps_more?.unVeil()*/
                                }

                                override fun onTick(millisUntilFinished: Long) {}
                            }.start()
                        }

                    }

                }
            }
           /* topAppsAdapter.apply {
                addData(it)
                if (isDataEmpty) {
                    homeAppsNestedScrollView.visibility = View.GONE
                    homeAppsWhenNotLoginToGooglePlay.visibility = View.VISIBLE
                } else {
                    topAppsAdapter.notifyDataSetChanged()
                    object : CountDownTimer(1500, 500) {
                        override fun onFinish() {
                            veilLayout_top_apps?.unVeil()
                            recycler_top_apps?.unVeil()
                            veilLayout_top_apps_more?.unVeil()
                        }

                        override fun onTick(millisUntilFinished: Long) {}
                    }.start()
                }

            }*/
        }


        homeAppViewModel.getTopGames().observe(viewLifecycleOwner) {

            recycler_top_games.addVeiledItems(it.size)
            CoroutineScope(IO).launch{
                topGamesAdapter.apply {
                    addData(it)
                    if (isDataEmpty) {
                        MainScope().launch {
                            homeAppsNestedScrollView.visibility = View.GONE
                            homeAppsWhenNotLoginToGooglePlay.visibility = View.VISIBLE
                        }
                       /* homeAppsNestedScrollView.visibility = View.GONE
                        homeAppsWhenNotLoginToGooglePlay.visibility = View.VISIBLE*/
                    } else {
                        notifyDataSetChanged()
                        //Looper.prepare()
                        MainScope().launch {
                            object : CountDownTimer(1500, 500) {
                                override fun onFinish() {

                                        veilLayout_top_games?.unVeil()
                                        recycler_top_games?.unVeil()
                                        veilLayout_top_games_more?.unVeil()

                                    /*veilLayout_top_games?.unVeil()
                                    recycler_top_games?.unVeil()
                                    veilLayout_top_games_more?.unVeil()*/
                                }

                                override fun onTick(millisUntilFinished: Long) {

                                }
                            }.start()
                        }

                    }
                }
            }
           /* topGamesAdapter.apply {
                addData(it)
                if (isDataEmpty) {
                    homeAppsNestedScrollView.visibility = View.GONE
                    homeAppsWhenNotLoginToGooglePlay.visibility = View.VISIBLE
                } else {
                    notifyDataSetChanged()
                    object : CountDownTimer(1500, 500) {
                        override fun onFinish() {
                            veilLayout_top_games?.unVeil()
                            recycler_top_games?.unVeil()
                            veilLayout_top_games_more?.unVeil()
                        }

                        override fun onTick(millisUntilFinished: Long) {

                        }
                    }.start()
                }
            }*/


        }

        homeAppViewModel.getTopFamilys().observe(viewLifecycleOwner) {
            recycler_top_family.addVeiledItems(it.size)
            CoroutineScope(IO).launch{
                topFamilyAdapter.apply {
                    addData(it)
                    if (isDataEmpty) {
                        MainScope().launch {
                            homeAppsNestedScrollView.visibility = View.GONE
                            homeAppsWhenNotLoginToGooglePlay.visibility = View.VISIBLE
                        }
                        /*homeAppsNestedScrollView.visibility = View.GONE
                        homeAppsWhenNotLoginToGooglePlay.visibility = View.VISIBLE*/
                    } else {
                        notifyDataSetChanged()
                        //Looper.prepare()
                        MainScope().launch {
                            object : CountDownTimer(    1500, 500) {
                                override fun onFinish() {

                                        veilLayout_top_family?.unVeil()
                                        recycler_top_family?.unVeil()
                                        veilLayout_top_family_more?.unVeil()

                                    /*  veilLayout_top_family?.unVeil()
                                      recycler_top_family?.unVeil()
                                      veilLayout_top_family_more?.unVeil()*/
                                }

                                override fun onTick(millisUntilFinished: Long) {

                                }
                            }.start()
                        }

                    }
                }
            }
   /*         topFamilyAdapter.apply {
                addData(it)
                if (isDataEmpty) {
                    homeAppsNestedScrollView.visibility = View.GONE
                    homeAppsWhenNotLoginToGooglePlay.visibility = View.VISIBLE
                } else {
                    notifyDataSetChanged()
                    object : CountDownTimer(1500, 500) {
                        override fun onFinish() {
                            veilLayout_top_family?.unVeil()
                            recycler_top_family?.unVeil()
                            veilLayout_top_family_more?.unVeil()
                        }

                        override fun onTick(millisUntilFinished: Long) {

                        }
                    }.start()
                }
            }*/


        }

        homeAppViewModel.getError().observe(viewLifecycleOwner) {}
    }
    private fun setupRecyclers() {

        topAppsAdapter = FeaturedAppsAdapter(requireContext(), requireActivity(), viewLifecycleOwner, disposable)
        topGamesAdapter = FeaturedAppsAdapter(requireContext(), requireActivity(), viewLifecycleOwner, disposable)
        topFamilyAdapter = FeaturedAppsAdapter(requireContext(), requireActivity(), viewLifecycleOwner, disposable)
        val mainActivityTopSearchBar = requireActivity().transformationLayout
        val decelerateInterpolator = DecelerateInterpolator()
        val flingListener = object : RecyclerView.OnFlingListener() {
            override fun onFling(velocityX: Int, velocityY: Int): Boolean {

                if (velocityY < 0) {
                  /*  open_category_action.apply {
                        animate().setDuration(200).translationY(
                            DensityUtil.dip2px(this@FragmentHome.requireContext(), 0f)
                        ).setInterpolator(decelerateInterpolator).start()
                    }*/
                    mainActivityTopSearchBar.animate().setDuration(200).translationY(
                        DensityUtil.dip2px(this@FragmentHome.requireContext(), 0f)
                    ).setInterpolator(decelerateInterpolator).start()

                } else if (velocityY > 0) {
                   /* open_category_action.apply {
                        animate().setDuration(400).translationY(
                            DensityUtil.dip2px(this@FragmentHome.requireContext(), 52f)
                        ).setInterpolator(decelerateInterpolator).start()
                    }*/
                    mainActivityTopSearchBar.animate().setDuration(400).translationY(
                        DensityUtil.dip2px(this@FragmentHome.requireContext(), -76f)
                    ).setInterpolator(decelerateInterpolator).start()
                }
                return false
            }

        }


        recycler_top_apps.apply {
            getRecyclerView().apply {
                overScrollMode = RecyclerView.OVER_SCROLL_NEVER
                onFlingListener = flingListener
            }
            setAdapter(topAppsAdapter)
            setLayoutManager(LinearLayoutManager(requireContext()))


        }
        recycler_top_games.apply {
            getRecyclerView().apply {
                overScrollMode = RecyclerView.OVER_SCROLL_NEVER
                onFlingListener = flingListener
            }
            setAdapter(topGamesAdapter)
            setLayoutManager(LinearLayoutManager(requireContext()))
        }
        recycler_top_family.apply {
            getRecyclerView().apply {
                overScrollMode = RecyclerView.OVER_SCROLL_NEVER
                onFlingListener = flingListener
            }
            setAdapter(topFamilyAdapter)
            setLayoutManager(LinearLayoutManager(requireContext()))
        }

        attachSnapPager(requireContext(), recycler_top_apps.getRecyclerView())
        attachSnapPager(requireContext(), recycler_top_games.getRecyclerView())
        attachSnapPager(requireContext(), recycler_top_family.getRecyclerView())
    }

    override fun onDestroy() {
        disposable.clear()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    companion object {

    }

}
