package com.example.todolist.fragment.list.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.data.models.Priority
import com.example.todolist.data.models.ToDoData
import com.example.todolist.fragment.list.ListFragmentDirections

class ListAdapter : RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    var dataList = emptyList<ToDoData>()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         val title_txt: TextView = itemView.findViewById(R.id.title_txt)
         val description_txt: TextView = itemView.findViewById(R.id.description_txt)
         val priority_indicator: CardView = itemView.findViewById(R.id.priority_indicator)
        val row_background: ConstraintLayout = itemView.findViewById(R.id.row_background)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_layout,parent,false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.title_txt.text = dataList[position].title
        holder.description_txt.text = dataList[position].description

        holder.row_background.startAnimation(AnimationUtils.loadAnimation(holder.itemView.context,R.anim.anim_four))

        holder.row_background.setOnClickListener {

            val action =
                ListFragmentDirections.actionListFragmentToUpdateFragment(dataList[position])

        holder.itemView.findNavController().navigate(action)

        }

        holder.priority_indicator.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context,R.color.red))

        when(dataList[position].priority) {

            Priority.HIGH -> holder.priority_indicator.setBackgroundColor(ContextCompat.getColor(holder.itemView.context,R.color.red))
            Priority.MEDIUM -> holder.priority_indicator.setBackgroundColor(ContextCompat.getColor(holder.itemView.context,R.color.yellow))
            Priority.LOW -> holder.priority_indicator.setBackgroundColor(ContextCompat.getColor(holder.itemView.context,R.color.green))
        }

    }

    fun setData(toDoData: List<ToDoData>){
        this.dataList = toDoData
        notifyDataSetChanged()
    }
}