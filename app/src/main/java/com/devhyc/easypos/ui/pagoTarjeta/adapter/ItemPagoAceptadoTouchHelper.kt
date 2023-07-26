package com.devhyc.easypos.ui.pagoTarjeta.adapter

import android.content.Context
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.devhyc.easypos.R
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

abstract class ItemPagoAceptadoTouchHelper (context: Context) : ItemTouchHelper.SimpleCallback(0,
    ItemTouchHelper.LEFT) {

    var eliminarColor = ContextCompat.getColor(context, R.color.red)
    var eliminarIcon = R.drawable.ic_baseline_delete_24
    var ColorIconoFuente = ContextCompat.getColor(context, R.color.white)

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            //Izquierda
            .addSwipeLeftActionIcon(eliminarIcon).setSwipeLeftActionIconTint(ColorIconoFuente)
            .addSwipeLeftLabel("Eliminar").setSwipeLeftLabelColor(ColorIconoFuente)
            .addSwipeLeftBackgroundColor(eliminarColor)
            .create()
            .decorate()
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}