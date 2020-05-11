package xcj.appsets.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.item_app_permission.view.*
import xcj.appsets.R
import xcj.appsets.ui.fragment.FragmentToday.Companion.getRandomColorStateList

class PermissionAdapter(val context: Context?, val permissList:MutableSet<String>?): RecyclerView.Adapter<PermissionAdapter.ViewHolder>() {

    private val tempPermissionList:List<String>? = permissList?.toList()
    override fun getItemCount(): Int = tempPermissionList?.size?:0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

       holder.chip?.apply {
           text = tempPermissionList?.get(position)
           chipStrokeColor = getRandomColorStateList(context)
           chipBackgroundColor = ColorStateList.valueOf(android.R.attr.colorBackground)
       }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val view =  LayoutInflater.from(parent.context).inflate(R.layout.item_app_permission, parent,false)
        return ViewHolder(view)
    }

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val chip:Chip? = itemView.permission_chip
    }
}