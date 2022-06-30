package com.devhyc.easypos.ui.impresora.adapter

import android.annotation.SuppressLint
import android.bluetooth.BluetoothClass
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devhyc.easypos.R
import com.devhyc.easypos.data.model.DTImpresora
import com.devhyc.easypos.databinding.ItemImpresoraBinding

class ItemImpresoraAdapter(var impresoras:ArrayList<DTImpresora>): RecyclerView.Adapter<ItemImpresoraAdapter.ItemImpresoraViewHolder>() {
    private lateinit var mListener: OnItemClickListener

    interface OnItemClickListener
    {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener)
    {
        mListener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteItem(i: Int)
    {
        impresoras.removeAt(i)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemImpresoraViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ItemImpresoraViewHolder(layoutInflater.inflate(R.layout.item_impresora,parent,false),mListener)
    }

    override fun onBindViewHolder(holder: ItemImpresoraViewHolder, position: Int) {
        val item: DTImpresora = impresoras[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = impresoras.size

    class ItemImpresoraViewHolder(view: View, listener: OnItemClickListener): RecyclerView.ViewHolder(view) {

        private val binding = ItemImpresoraBinding.bind(view)

        @SuppressLint("ResourceAsColor", "SetTextI18n")
        fun bind(i: DTImpresora)
        {
            try
            {
                binding.tvPrinterName.text = i.nombre
                binding.tvPrinterName.text = i.nombre
                binding.tvMacDir.text = i.mac
                if (i.linkeada)
                {
                    binding.tvLink.text = "Vinculada"
                }
                else
                {
                    binding.tvLink.text = ""
                }
                if (i.seleccionada)
                {
                    binding.imgSelected.visibility = View.VISIBLE
                }
                else
                {
                    binding.imgSelected.visibility = View.GONE
                }
                when(i.tipo)
                {
                    BluetoothClass.Device.Major.AUDIO_VIDEO -> {
                        binding.imageView4.setImageResource(R.drawable.ic_baseline_tv_24)
                    }
                    BluetoothClass.Device.Major.PHONE -> {
                        binding.imageView4.setImageResource(R.drawable.ic_baseline_phone_android_24)
                    }
                    BluetoothClass.Device.Major.PERIPHERAL -> {
                        binding.imageView4.setImageResource(R.drawable.ic_baseline_headphones_24)
                    }
                    BluetoothClass.Device.Major.HEALTH -> {
                        binding.imageView4.setImageResource(R.drawable.ic_baseline_health_and_safety_24)
                    }
                    BluetoothClass.Device.Major.COMPUTER -> {
                        binding.imageView4.setImageResource(R.drawable.ic_baseline_computer_24)
                    }
                    BluetoothClass.Device.Major.IMAGING -> {
                        binding.imageView4.setImageResource(R.drawable.ic_baseline_print_24)
                    }
                    BluetoothClass.Device.Major.MISC -> {
                        binding.imageView4.setImageResource(R.drawable.ic_baseline_miscellaneous_services_24)
                    }
                    BluetoothClass.Device.Major.NETWORKING -> {
                        binding.imageView4.setImageResource(R.drawable.ic_baseline_router_24)
                    }
                    BluetoothClass.Device.Major.TOY -> {
                        binding.imageView4.setImageResource(R.drawable.ic_baseline_toys_24)
                    }
                    BluetoothClass.Device.Major.UNCATEGORIZED -> {
                        binding.imageView4.setImageResource(R.drawable.ic_baseline_question_mark_24)
                    }
                    BluetoothClass.Device.Major.WEARABLE -> {
                        binding.imageView4.setImageResource(R.drawable.ic_baseline_watch_24)
                    }
                }
            }
            catch (e: Exception)
            {
                throw IllegalArgumentException(e.message)
            }
        }

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }
}