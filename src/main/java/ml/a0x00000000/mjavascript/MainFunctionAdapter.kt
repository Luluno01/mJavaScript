package ml.a0x00000000.mjavascript

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

class MainFunctionAdapter constructor(val context: Context, val items: Array<FunctionItem>): RecyclerView.Adapter<MainFunctionAdapter.ViewHolder>() {
    interface FunctionItem {
        var id: Any
        var icon: Int
        var title: String
        var content: String
    }
    interface OnItemClickListener {
        fun onItemClick(index: Any)
    }

    class ViewHolder constructor(val view: View, adapter: MainFunctionAdapter): RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.imageView)
        val title: TextView = view.findViewById(R.id.title)
        val content: TextView = view.findViewById(R.id.content)
        val item: CardView = view.findViewById(R.id.item)
        init {
            item.setOnClickListener { _ -> adapter.listener?.onItemClick(view.tag) }
        }
    }

    var listener: OnItemClickListener? = null
    private lateinit var view: View
    private lateinit var viewHolder: ViewHolder

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        view = LayoutInflater.from(context).inflate(R.layout.activity_main_item, parent, false)
        viewHolder = ViewHolder(view, this)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.tag = items[position].id
        holder.image.setImageResource(items[position].icon)
        holder.title.text = items[position].title
        holder.content.text = items[position].content
    }

    override fun getItemCount(): Int {
        return items.size
    }
}