package com.devhyc.easypos.ui.pagoTarjeta.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devhyc.easypos.R
import com.devhyc.easypos.data.model.DTDocItem
import com.devhyc.easypos.data.model.DTMedioPago
import com.devhyc.easypos.data.model.DTMedioPagoAceptado
import com.devhyc.easypos.databinding.ItemArticuloBinding
import com.devhyc.easypos.databinding.ItemPagoAceptadoBinding

class ItemPagoAceptadoAdapter (var items:ArrayList<DTMedioPagoAceptado>): RecyclerView.Adapter<ItemPagoAceptadoAdapter.ItemDocViewHolder>() {

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
        items.removeAt(i)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemDocViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ItemDocViewHolder(layoutInflater.inflate(R.layout.item_pago_aceptado,parent,false),mListener)
    }

    override fun onBindViewHolder(holder: ItemDocViewHolder, position: Int) {
        val item: DTMedioPagoAceptado = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    class ItemDocViewHolder(view: View, listener: OnItemClickListener): RecyclerView.ViewHolder(view) {

        private val binding = ItemPagoAceptadoBinding.bind(view)

        @SuppressLint("ResourceAsColor", "SetTextI18n")
        fun bind(i: DTMedioPagoAceptado)
        {
            try
            {
                binding.tvMontoPago.text = i.Pago.toString()
                binding.tvNombrePagoAceptado.text = i.Nombre
                when(i.Tipo)
                {
                    "1" ->
                    {
                        binding.imgPago.setImageResource(R.drawable.dollar)
                    }
                    "3" ->
                    {
                        binding.imgPago.setImageResource(R.drawable.ic_tarjeta)
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