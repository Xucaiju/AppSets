package xcj.appsets.ui.fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_community.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import xcj.appsets.R
import xcj.appsets.model.TodayApp
import xcj.appsets.ui.TimeLineAdapter
import xcj.appsets.viewmodel.TimeLineAppModel
import xyz.sangcomz.stickytimelineview.RecyclerSectionItemDecoration
import xyz.sangcomz.stickytimelineview.model.SectionInfo
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentCommunity.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentCommunity : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_community, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val linearLayoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        timeline_recycler_1.layoutManager = linearLayoutManager
        //val timeLineAppModel by lazy { ViewModelProvider.AndroidViewModelFactory.getInstance(this.application).create(TimeLineAppModel::class.java) }
        val timeLineAppModel by viewModels<TimeLineAppModel>()

        timeLineAppModel.apps?.observe(requireActivity()) {
            it?.let {
                timeline_recycler_1.addItemDecoration(getSectionCallback(it))

                timeline_recycler_1.adapter = TimeLineAdapter(requireContext(), layoutInflater, it, R.layout.include_today_app_card, requireActivity())
                (timeline_recycler_1.adapter as TimeLineAdapter).notifyDataSetChanged()
                //(timeline_recycler as AppSetsTimeLineRecyclerView).recyclerViewAttr?.sectionBackgroundColor = Color.WHITE
            }
        }
    }
    private fun getSectionCallback(appList: MutableList<TodayApp>): RecyclerSectionItemDecoration.SectionCallback {
        return object : RecyclerSectionItemDecoration.SectionCallback {
            //In your data, implement a method to determine if this is a section.
            override fun isSection(position: Int): Boolean {
                /*if (!bool) {
                    (timeline_recycler as AppSetsTimeLineRecyclerView).recyclerViewAttr?.sectionLineColor =
                        getRandomColor()
                }*/
                return appList[position].showedDate != appList[position - 1].showedDate
            }

            //Implement a method that returns a SectionHeader.
            override fun getSectionHeader(position: Int): SectionInfo? {
                val app = appList[position]
                var appicon: Drawable? = null
                CoroutineScope(Dispatchers.Default).launch{
                    appicon =  Drawable.createFromPath(Glide.with(requireContext()).asFile().load(app.appIcon).submit().get().path)
                }

                val calendar = Calendar.getInstance().apply {
                    app.showedDate?.let {
                        time = it
                    }
                }
                val year = calendar[Calendar.YEAR]
                val month = calendar[Calendar.MONTH] +1
                val day = calendar[Calendar.DATE]
                /* val dateArray = app.showedDate?.toString()?.split(" ")
                 val month = when(dateArray?.get(0)){
                         "Jan"->{getString(R.string.jan)}
                         "Feb"->{getString(R.string.feb)}
                         "Mar"->{getString(R.string.mar)}
                         "Apr"->{getString(R.string.apr)}
                     "May"->{getString(R.string.may)}
                     "Jun"->{getString(R.string.jun)}
                     "Jul"->{getString(R.string.jul)}
                     "Aug"->{getString(R.string.aug)}
                     "Sep"->{getString(R.string.sep)}
                     "Oct"->{getString(R.string.oct)}
                     "Nov"->{getString(R.string.nov)}
                     "Dec"->{getString(R.string.dec)}
                     else ->{getString(R.string.jan)}
                 }
                 val day = dateArray?.get(1)?.split(",")?.get(0)
                 val year = dateArray?.get(1)?.split(",")?.get(1)
                 val appShowDate = "%s-%s"*/
                return  SectionInfo("${year}-${month}-${day}", dotDrawable = appicon)
            }
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentCommunity.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentCommunity().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}