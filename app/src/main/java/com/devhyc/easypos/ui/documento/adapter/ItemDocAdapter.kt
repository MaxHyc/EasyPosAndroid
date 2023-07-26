package com.devhyc.easypos.ui.documento.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.devhyc.easypos.R
import com.devhyc.easypos.data.model.DTDocDetalle
import com.devhyc.easypos.databinding.ItemArticuloBinding
import com.devhyc.easypos.utilidades.Globales

class ItemDocAdapter (var items:ArrayList<DTDocDetalle>): RecyclerView.Adapter<ItemDocAdapter.ItemViewHolder>() {

    private lateinit var mListener: OnItemClickListener

    private lateinit var lListener: AdapterView.OnItemLongClickListener

    fun updateList(newList:ArrayList<DTDocDetalle>)
    {
        val listdiff = ItemDocDiffUtil(items,newList)
        val result = DiffUtil.calculateDiff(listdiff)
        items = newList
        result.dispatchUpdatesTo(this)
    }

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(layoutInflater.inflate(R.layout.item_articulo,parent,false),mListener)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item: DTDocDetalle = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    class ItemViewHolder(view: View, listener: OnItemClickListener): RecyclerView.ViewHolder(view) {

        private val binding = ItemArticuloBinding.bind(view)

        @SuppressLint("ResourceAsColor", "SetTextI18n")
        fun bind(i: DTDocDetalle)
        {
            try
            {
                binding.tvNombreArt.text = i.descripcion
                binding.tvTipoDocDoc.text = "Cantidad: ${i.cantidad}"
                if (Globales.ParametrosDocumento != null)
                {
                    if(Globales.ParametrosDocumento.Validaciones.Valorizado)
                    {
                        when(Globales.MonedaSeleccionada)
                        {
                            Globales.TMoneda.PESOS.codigo -> binding.tvTotalDoc.text = "$ ${i.precioUnitario}"
                            Globales.TMoneda.DOLARES.codigo -> binding.tvTotalDoc.text = "U$" + "S" + "${i.precioUnitario}"
                        }
                        if(i.descuentoPorc > 0)
                        {
                            binding.tvFechaDoc.text = "Descuento: ${i.descuentoPorc}%"
                            R.color.red
                            binding.tvFechaDoc.setTextColor(Color.parseColor("#F44336"))
                        }
                        else
                        {
                            binding.tvFechaDoc.text = ""
                        }
                    }
                    else
                    {
                        binding.tvTotalDoc.text = ""
                    }
                }
                else
                {
                    binding.tvTotalDoc.text = ""
                }

                if (i.serie.isEmpty())
                {
                    binding.tvSerie.text = ""
                }
                else
                {
                    binding.tvSerie.text = "Serie: ${i.serie}"
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