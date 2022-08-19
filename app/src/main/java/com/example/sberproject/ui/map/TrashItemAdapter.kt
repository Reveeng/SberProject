package com.example.sberproject.ui.map

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ListAdapter
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.sberproject.R
import com.example.sberproject.TrashType
import com.example.sberproject.Util
import com.example.sberproject.databinding.GridViewLayoutItemBinding

class TrashItemAdapter(
    var context: Context,
    var arrayList: List<TrashType>,
    private val onClick: (GridViewLayoutItemBinding, TrashType) -> Unit
) :
    BaseAdapter() {
    override fun getCount(): Int = arrayList.size

    override fun getItem(p0: Int): Any? = null

    override fun getItemId(p0: Int): Long = 0

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding = GridViewLayoutItemBinding.inflate(LayoutInflater.from(context), parent, false)
        val trashType = arrayList[position]
        binding.root.setOnClickListener {
            onClick(binding, trashType)
        }
        binding.trashName.text = trashType.toStringUI()
        binding.trashImage.setImageDrawable(
            ContextCompat.getDrawable(
                binding.root.context,
                Util.trashTypeToIcon[trashType]!!
            )
        )
        return binding.root
    }
}