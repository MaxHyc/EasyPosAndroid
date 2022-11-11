package com.devhyc.easypos.ui.reportecaja.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devhyc.easypos.R
import com.devhyc.easypos.data.model.DTCajaNroDocumentos
import com.devhyc.easypos.databinding.ItemNrosdocumentosBinding

class ItemNrosDocumentos(var nrosDocumentos:ArrayList<DTCajaNroDocumentos>): RecyclerView.Adapter<ItemNrosDocumentos.ItemNrosDocumentosViewHolder>() {
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
        nrosDocumentos.removeAt(i)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemNrosDocumentosViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ItemNrosDocumentosViewHolder(layoutInflater.inflate(R.layout.item_nrosdocumentos,parent,false),mListener)
    }

    override fun onBindViewHolder(holder: ItemNrosDocumentosViewHolder, position: Int) {
        val item: DTCajaNroDocumentos = nrosDocumentos[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = nrosDocumentos.size

    class ItemNrosDocumentosViewHolder(view: View, listener: onItemClickListener): RecyclerView.ViewHolder(view) {

        private val binding = ItemNrosdocumentosBinding.bind(view)

        @SuppressLint("ResourceAsColor", "SetTextI18n")
        fun bind(i: DTCajaNroDocumentos)
        {
            try
            {
                binding.tvCajaNombreDocumento.text = i.Nombre
                binding.tvCajaNroDesde.text = i.NroDesde.toString()
                binding.tvCajaNroHasta.text = i.NroHasta.toString()
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