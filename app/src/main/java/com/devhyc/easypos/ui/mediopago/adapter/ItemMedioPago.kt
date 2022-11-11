package com.devhyc.easypos.ui.mediopago.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.devhyc.easypos.R
import com.devhyc.easypos.data.model.DTMedioPago
import com.devhyc.easypos.databinding.ItemDocumentoBinding
import com.devhyc.easypos.databinding.ItemMediopagoBinding

class ItemMedioPago(var mediosDepago:ArrayList<DTMedioPago>): RecyclerView.Adapter<ItemMedioPago.ItemMedioPagoViewHolder>() {
    private lateinit var mListener: onItemClickListener

    interface onItemClickListener
    {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener)
    {
        mListener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteItem(i: Int)
    {
        mediosDepago.removeAt(i)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemMedioPagoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ItemMedioPagoViewHolder(layoutInflater.inflate(R.layout.item_mediopago,parent,false),mListener)
    }

    override fun onBindViewHolder(holder: ItemMedioPagoViewHolder, position: Int) {
        val item: DTMedioPago = mediosDepago[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = mediosDepago.size

    class ItemMedioPagoViewHolder(view: View, listener: onItemClickListener): RecyclerView.ViewHolder(view) {

        private val binding = ItemMediopagoBinding.bind(view)

        @SuppressLint("ResourceAsColor", "SetTextI18n")
        fun bind(i: DTMedioPago)
        {
            try
            {
                binding.tvNombreMedioPago.text = i.Nombre
                binding.imgSeleccionado.isVisible = i.seleccionado
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