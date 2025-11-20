package com.main.cipta_muri_mobile.ui.setor.harga

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.main.cipta_muri_mobile.R

class HargaSampahAdapter(
    private var items: List<HargaSampahUi>
) : RecyclerView.Adapter<HargaSampahAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama: TextView = view.findViewById(R.id.tvNamaSampah)
        val tvHarga: TextView = view.findViewById(R.id.tvHargaPerKg)
        val tvTotal: TextView = view.findViewById(R.id.tvTotalSetoran)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_harga_sampah, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvNama.text = item.nama
        holder.tvHarga.text = item.hargaPerKg
        holder.tvTotal.text = item.totalSetoran
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<HargaSampahUi>) {
        items = newItems
        notifyDataSetChanged()
    }
}
