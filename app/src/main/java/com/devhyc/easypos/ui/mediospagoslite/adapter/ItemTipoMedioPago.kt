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
import com.devhyc.easypos.utilidades.Globales

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
                if (i.Proveedor == Globales.TProveedorTarjeta.FISERV.valor)
                {
                    //binding.imgLogoFiserv.visibility = View.VISIBLE
                }
                else
                {
                    //binding.imgLogoFiserv.visibility = View.GONE
                }
                when(i.Tipo)
                {
                    Globales.TMedioPago.TARJETA.codigo.toString() -> {
                        when(i.Proveedor)
                        {
                            Globales.TProveedorTarjeta.FISERV.valor -> binding.imageView7.setImageResource(R.drawable.fiservlogo)
                            Globales.TProveedorTarjeta.HANDY.valor -> binding.imageView7.setImageResource(R.drawable.handylogo2)
                            Globales.TProveedorTarjeta.GETNET.valor -> binding.imageView7.setImageResource(R.drawable.logogetnet)
                            Globales.TProveedorTarjeta.OCA.valor -> binding.imageView7.setImageResource(R.drawable.logooca)
                            else -> binding.imageView7.setImageResource(R.drawable.ic_baseline_payment_24)
                        }
                    }
                    Globales.TMedioPago.EFECTIVO.codigo.toString() -> {
                        binding.imageView7.setImageResource(R.drawable.ic_baseline_money_24)
                    }
                    Globales.TMedioPago.CHEQUE.codigo.toString() -> {
                        binding.imageView7.setImageResource(R.drawable.ic_cheque)
                    }
                    Globales.TMedioPago.GIFTCARD .codigo.toString() -> {
                        binding.imageView7.setImageResource(R.drawable.ic_baseline_card_giftcard_24)
                    }
                    Globales.TMedioPago.MERCADOP.codigo.toString() -> {
                        binding.imageView7.setImageResource(R.drawable.mercadopagologo)
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