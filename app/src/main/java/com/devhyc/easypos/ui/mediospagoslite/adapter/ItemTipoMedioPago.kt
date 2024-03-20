package com.devhyc.easypos.ui.mediospagoslite.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devhyc.easypos.R
import com.devhyc.easypos.data.model.DTDocPago
import com.devhyc.easypos.data.model.DTMedioPago
import com.devhyc.easypos.databinding.ItemMediopagoBinding
import com.devhyc.easypos.databinding.ItemTipoMedioPagoBinding

class ItemTipoMedioPago(var mediosDepago: ArrayList<DTMedioPago>): RecyclerView.Adapter<ItemTipoMedioPago.ItemTipoMedioPagoViewHolder>() {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemTipoMedioPagoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ItemTipoMedioPagoViewHolder(layoutInflater.inflate(R.layout.item_tipo_medio_pago,parent,false),mListener)
    }

    override fun onBindViewHolder(holder: ItemTipoMedioPagoViewHolder, position: Int) {
        val item: DTMedioPago = mediosDepago[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = mediosDepago.size

    class ItemTipoMedioPagoViewHolder(view: View, listener: onItemClickListener): RecyclerView.ViewHolder(view) {

        private val binding = ItemTipoMedioPagoBinding.bind(view)

        @SuppressLint("ResourceAsColor", "SetTextI18n")
        fun bind(i: DTMedioPago)
        {
            try
            {
                binding.tvTextoMedioPago.text = i.Nombre
                if (i.Proveedor == "GEOCOM")
                {
                    binding.imgLogoFiserv.visibility = View.VISIBLE
                }
                else
                {
                    binding.imgLogoFiserv.visibility = View.GONE
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