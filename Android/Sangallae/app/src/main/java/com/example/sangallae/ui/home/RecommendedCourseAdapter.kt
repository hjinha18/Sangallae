package com.example.sangallae.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sangallae.GlobalApplication

import com.example.sangallae.R
import com.example.sangallae.databinding.HomeItemBinding
import com.example.sangallae.retrofit.models.Home

class RecommendedCourseAdapter : RecyclerView.Adapter<RecommendedCourseAdapter.CourseItemViewHolder>() {

    private var recCourseList = ArrayList<Home>()

    inner class CourseItemViewHolder(private val itemBinding: HomeItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bindWithView(courseItem: Home) {
            itemBinding.courseName.text = courseItem.name
            itemBinding.courseInfo.text = courseItem.distance + " / " + courseItem.moving_time + " / " + courseItem.difficulty

            Glide.with(GlobalApplication.instance).load(courseItem.thumbnail)
                .placeholder(R.drawable.ic_baseline_photo_24).into(itemBinding.courseImageView)

            val pos = adapterPosition
            if(pos!= RecyclerView.NO_POSITION)
            {
                itemView.setOnClickListener {
                    listener?.onItemClick(itemView, courseItem.id, pos)
                }
            }
        }
    }

    interface OnItemClickListener{
        fun onItemClick(v:View, data: Int, pos : Int)
    }
    private var listener : OnItemClickListener? = null
    fun setOnItemClickListener(listener : OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseItemViewHolder {
        val itemBinding = HomeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CourseItemViewHolder(itemBinding)
    }

    override fun onBindViewHolder(
        holder: CourseItemViewHolder,
        position: Int
    ) {
        holder.bindWithView(this.recCourseList[position])
    }

    override fun getItemCount(): Int {
        return this.recCourseList.size
    }

    fun submitList(courseList: ArrayList<Home>){
        this.recCourseList = courseList
    }
}