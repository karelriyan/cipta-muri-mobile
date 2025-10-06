package com.main.cipta_muri_mobile.ui.setor.riwayat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.main.cipta_muri_mobile.R

class RiwayatSetoranAdapter(
    private var items: List<RiwayatSetoran>, // ✅ ubah jadi var supaya bisa diupdate
    private val onItemClick: ((RiwayatSetoran) -> Unit)? = null
) : RecyclerView.Adapter<RiwayatSetoranAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTanggal: TextView = view.findViewById(R.id.tvTanggal)
        val tvJudul: TextView = view.findViewById(R.id.tvJudul)
        val tvSaldo: TextView = view.findViewById(R.id.tvSaldo)
//        val tvRincian: TextView = view.findViewById(R.id.tvRincian)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_riwayat_setoran, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        // 🗓️ Isi data sesuai model
        holder.tvTanggal.text = item.tanggal
        holder.tvJudul.text = "${item.jenisSetoran} (${item.beratFormatted})"
        holder.tvSaldo.text = item.totalSaldoFormatted

        // 📌 Jika ingin pakai klik item
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(item)
        }
    }

    override fun getItemCount(): Int = items.size

    // ✅ Fungsi untuk memperbarui data tanpa bikin adapter baru
    fun updateData(newItems: List<RiwayatSetoran>) {
        this.items = newItems
        notifyDataSetChanged()
    }
}
