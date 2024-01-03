package com.devhyc.easypos.ui.clientes.listado.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devhyc.easymanagementmobile.ui.articulos.adapter.ItemArticuloAdapter
import com.devhyc.easypos.R
import com.devhyc.easypos.data.model.DTCliente
import com.devhyc.easypos.databinding.ItemArticuloBinding
import com.devhyc.easypos.databinding.ItemClienteBinding
import com.devhyc.easypos.utilidades.Globales
import com.integration.easyposkotlin.data.model.DTArticulo

class ItemClienteAdapter(var clientes:ArrayList<DTCliente>): RecyclerView.Adapter<ItemClienteAdapter.ItemClienteViewHolder>() {

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
        clientes.removeAt(i)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemClienteViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ItemClienteViewHolder(layoutInflater.inflate(R.layout.item_cliente,parent,false),mListener)
    }

    override fun onBindViewHolder(holder: ItemClienteViewHolder, position: Int) {
        val item: DTCliente = clientes[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = clientes.size

    class ItemClienteViewHolder(view: View, listener: OnItemClickListener): RecyclerView.ViewHolder(view) {

        private val binding = ItemClienteBinding.bind(view)

        @SuppressLint("ResourceAsColor", "SetTextI18n")
        fun bind(i: DTCliente)
        {
            try
            {
                binding.tvNombC.text = i.nombre
                binding.tvRazonC.text = i.razonSocial
                binding.tvRutC.text = i.documento
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