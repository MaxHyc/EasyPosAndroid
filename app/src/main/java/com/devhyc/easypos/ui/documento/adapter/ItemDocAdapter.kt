package com.devhyc.jamesmobile.ui.documento.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devhyc.easypos.R
import com.devhyc.easypos.data.model.DTDocItem
import com.devhyc.easypos.databinding.ItemArticuloBinding
import com.devhyc.easypos.databinding.ItemDocumentoBinding

class ItemDocAdapter(var items:ArrayList<DTDocItem>): RecyclerView.Adapter<ItemDocAdapter.ItemDocViewHolder>() {

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
        return ItemDocViewHolder(layoutInflater.inflate(R.layout.item_articulo,parent,false),mListener)
    }

    override fun onBindViewHolder(holder: ItemDocViewHolder, position: Int) {
        val item: DTDocItem = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    class ItemDocViewHolder(view: View, listener: OnItemClickListener): RecyclerView.ViewHolder(view) {

        private val binding = ItemArticuloBinding.bind(view)

        @SuppressLint("ResourceAsColor", "SetTextI18n")
        fun bind(i: DTDocItem)
        {
            try
            {
                binding.tvNombreArticulo.text = i.nombre
                binding.tvCodBarras.text = "Cantidad x ${i.cantidad}"
                binding.tvPrecioArticulo.text = "$ ${i.precio.toString()}"
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