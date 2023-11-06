package com.example.myb10demo.adapter

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.myb10demo.databinding.RvItemsBinding
import com.minew.beaconplus.sdk.MTPeripheral


class MyAdapter: RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    inner class MyViewHolder(val binding: RvItemsBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding=RvItemsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
//        return differ.currentList.size
//        return bleList.size
        return bles.size
    }


    //lets implement the diff Util callback here
    private val differCallback = object : DiffUtil.ItemCallback<MTPeripheral>() {
        override fun areItemsTheSame(oldItem: MTPeripheral, newItem: MTPeripheral): Boolean {
            return oldItem.mMTFrameHandler.mac == newItem.mMTFrameHandler.mac
        }

        override fun areContentsTheSame(oldItem: MTPeripheral, newItem: MTPeripheral): Boolean {
            return oldItem.mMTFrameHandler.name == newItem.mMTFrameHandler.name
        }
    }

    val differ= AsyncListDiffer(this,differCallback)
    var bles:List<MTPeripheral>
        get()=differ.currentList
        set(value){differ.submitList(value)}
    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        val curr=differ.currentList[position]
//        val curr=bleList[position]
        val curr=bles[position]
        holder.binding.apply {
            tvDeviceName.text=curr.mMTFrameHandler.name
            if(tvDeviceName.text.isEmpty())tvDeviceName.text="UnKnown"
            tvAddress.text=curr.mMTFrameHandler.mac
            //later we will apply onclick listener here
            holder.itemView.setOnClickListener {
                onItemClickListener?.let {
                    it(curr)
                }
            }
        }
    }
//    private var onItemClickListener:((BluetoothDevice)->Unit)?=null
    private var onItemClickListener:((MTPeripheral)->Unit)?=null
    fun setOnItemClickListener(listener: (MTPeripheral)->Unit){
        onItemClickListener=listener
    }
}