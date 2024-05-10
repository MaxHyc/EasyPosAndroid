package com.devhyc.easypos.ui.transacciones.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.devhyc.easypos.databinding.ItemTransaccionBinding
import com.devhyc.easypos.fiserv.model.ITDEstadoTransaccion
import com.devhyc.easypos.fiserv.model.ITDTransaccionLista
import com.devhyc.easypos.utilidades.Globales

class ItemDevolucionAdapter(var items:ArrayList<ITDTransaccionLista>,var mostrarBotonSeleccionar:Boolean): RecyclerView.Adapter<ItemDevolucionAdapter.ItemDevolucionViewHolder>() {
    private lateinit var mListener: OnItemClickListener

    fun updateList(newList:ArrayList<ITDTransaccionLista>)
    {
        val listdiff = ItemDevolucionDiffUtil(items,newList)
        val result = DiffUtil.calculateDiff(listdiff)
        items = newList
        result.dispatchUpdatesTo(this)
    }

    interface OnItemClickListener
    {
        fun onConsultarButtonClick(position: Int)
        fun onAnularButtonClick(position: Int)
        fun onSeleccionarPagoButtonClick(position: Int)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemDevolucionViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemTransaccionBinding.inflate(layoutInflater, parent, false)
        return ItemDevolucionViewHolder(binding, mListener)
    }

    override fun onBindViewHolder(holder: ItemDevolucionViewHolder, position: Int) {
        val item: ITDTransaccionLista = items[position]
        holder.bind(item,mostrarBotonSeleccionar)
    }

    override fun getItemCount(): Int = items.size

    class ItemDevolucionViewHolder(
        private val binding: ItemTransaccionBinding,
        private val listener: ItemDevolucionAdapter.OnItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        interface OnItemClickListener {
            fun onItemClick(position: Int)
        }

        fun bind(d: ITDTransaccionLista,mostrarBotonSeleccionar:Boolean) {
            binding.tvDocumentoDev.text = "Documento: ${d.Documento}"
            binding.tvMontoDev.text = "Monto: ${d.Monto}"
            binding.tvNroTransaccionDev.text = "Transacción Nro: ${d.TransaccionId}"
            binding.btnSeleccionarPago.isVisible = mostrarBotonSeleccionar

            when (d.Estado) {
                ITDEstadoTransaccion.CONERROR.value -> {
                    binding.tvEstadoDev.text = "Estado: ERROR"
                    binding.tvEstadoDev.setTextColor(Color.RED)
                }
                ITDEstadoTransaccion.APROBADA.value -> {
                    binding.tvEstadoDev.text = "Estado: APROBADA"
                    binding.tvEstadoDev.setTextColor(Color.GREEN)
                }
                ITDEstadoTransaccion.REVERSADA.value -> {
                    binding.tvEstadoDev.text = "Estado: REVERSADA"
                    binding.tvEstadoDev.setTextColor(Color.YELLOW)
                }
                ITDEstadoTransaccion.ANULADA.value, ITDEstadoTransaccion.CANCELADA.value -> {
                    binding.tvEstadoDev.text = "Estado: ANULADA / CANCELADA"
                    binding.tvEstadoDev.setTextColor(Color.GRAY)
                }
                else -> {
                    binding.tvEstadoDev.text = "Estado: PENDIENTE"
                    binding.tvEstadoDev.setTextColor(Color.BLACK)
                }
            }

            binding.tvTipoDev.text = d.Tipo
            binding.tvFechaHoraDev.text = "Fecha Hora: ${Globales.Herramientas.TransformarFecha(d.FechaHora,Globales.FechaJson,Globales.Fecha_dd_MM_yyyy_HH_mm_ss)}"

            binding.btnConsultarTransaccion.setOnClickListener {
                listener.onConsultarButtonClick(adapterPosition)
            }
            binding.btnAnularTransaccion.setOnClickListener {
                listener.onAnularButtonClick(adapterPosition)
            }
            binding.btnSeleccionarPago.setOnClickListener {
                listener.onSeleccionarPagoButtonClick(adapterPosition)
            }
        }
    }

}