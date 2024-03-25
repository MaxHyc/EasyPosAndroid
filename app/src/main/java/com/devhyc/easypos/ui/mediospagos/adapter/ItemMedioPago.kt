package com.devhyc.easypos.ui.mediospagos.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devhyc.easypos.R
import com.devhyc.easypos.data.model.DTDocPago
import com.devhyc.easypos.data.model.DTMedioPago
import com.devhyc.easypos.data.model.DTMoneda
import com.devhyc.easypos.databinding.ItemMediopagoBinding

class ItemMedioPago(var mediosDepago:ArrayList<DTDocPago>, var lmoneda:ArrayList<DTMoneda>,var ltipoMedioPago:ArrayList<DTMedioPago>): RecyclerView.Adapter<ItemMedioPago.ItemMedioPagoViewHolder>() {
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
        val item: DTDocPago = mediosDepago[position]
        holder.bind(item,lmoneda,ltipoMedioPago)
    }

    override fun getItemCount(): Int = mediosDepago.size

    class ItemMedioPagoViewHolder(view: View, listener: onItemClickListener): RecyclerView.ViewHolder(view) {

        private val binding = ItemMediopagoBinding.bind(view)

        @SuppressLint("ResourceAsColor", "SetTextI18n")
        fun bind(i: DTDocPago, m:ArrayList<DTMoneda>,mp:ArrayList<DTMedioPago>)
        {
            try
            {
                binding.tvMonedaMedioPago.text = m.find {
                    it.codigo == i.monedaCodigo
                }!!.signo
                binding.tvNombreMedioPago.text = mp.find {
                    it.Id == i.medioPagoCodigo.toString()
                }!!.Nombre
                binding.tvMontoMedioPago.text = i.importe.toString()
                //
                if (i.numero!!.isNotEmpty())
                {
                    binding.tvNroTarjetaMedioPago.visibility = View.VISIBLE
                    binding.tvNroTarjetaMedioPago.text = "NRO: ${i.numero}"
                }
                else
                {
                    binding.tvNroTarjetaMedioPago.visibility = View.GONE
                }
                if (i.cuotas > 0)
                {
                    binding.tvCuotasMedioPago.visibility = View.VISIBLE
                    binding.tvCuotasMedioPago.text = "CUOTAS: ${i.cuotas.toString()}"
                }
                if (i.autorizacion!!.isNotEmpty())
                {
                    binding.tvAutorizacionMedioPago.visibility = View.VISIBLE
                    binding.tvAutorizacionMedioPago.text = "AUTORIZACIÓN: ${i.autorizacion}"
                }
                if (i.fechaVto!!.isNotEmpty())
                {
                    binding.tvFechaVtoMedioPago.visibility = View.VISIBLE
                    binding.tvFechaVtoMedioPago.text = "FECHA VTO: ${i.fechaVto}"
                }
                //
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