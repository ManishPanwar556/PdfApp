package com.example.pdfapp

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView


class MyViewPagerAdapter(private val list:List<Bitmap>):RecyclerView.Adapter<MyViewPagerAdapter.MyViewHolder>() {
    inner class MyViewHolder(val view: View):RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.viewpager_item,parent,false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val pdfImage=holder.view.findViewById<ImageView>(R.id.pdfImage)
        pdfImage.setImageBitmap(list[position])
    }

    override fun getItemCount()=list.size

}