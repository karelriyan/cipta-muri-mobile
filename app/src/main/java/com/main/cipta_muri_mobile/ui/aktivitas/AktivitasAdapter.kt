package com.main.cipta_muri_mobile.ui.aktivitas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.main.cipta_muri_mobile.R
import com.main.cipta_muri_mobile.data.Aktivitas

class AktivitasAdapter(private var listAktivitas: List<Aktivitas>) :
    RecyclerView.Adapter<AktivitasAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTanggal: TextView = view.findViewById(R.id.tvTanggal)
        val tvJenis: TextView = view.findViewById(R.id.tvJenisTransaksi)
        val tvKeterangan: TextView = view.findViewById(R.id.tvKeterangan)
        val tvJumlah: TextView = view.findViewById(R.id.tvJumlah)
        val ivIcon: ImageView = view.findViewById(R.id.ivIcon)
        val tvWaktu: TextView = view.findViewById(R.id.tvWaktu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_aktivitas, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val aktivitas = listAktivitas[position]

        // Tampilkan header tanggal hanya untuk item pertama dari tanggal tsb
        val showHeader = position == 0 || listAktivitas[position - 1].tanggal != aktivitas.tanggal
        holder.tvTanggal.visibility = if (showHeader) View.VISIBLE else View.GONE
        if (showHeader) holder.tvTanggal.text = aktivitas.tanggal

        holder.tvJenis.text = aktivitas.jenis
        holder.tvKeterangan.text = aktivitas.keterangan
        holder.tvJumlah.text = aktivitas.jumlah
        holder.tvWaktu.text = aktivitas.waktu

        if (aktivitas.isMasuk) {
            holder.tvJumlah.setTextColor(holder.itemView.context.getColor(R.color.green_500))
            holder.ivIcon.setImageResource(R.drawable.ic_money_in)
            holder.ivIcon.rotation = 0f
        } else {
            holder.tvJumlah.setTextColor(holder.itemView.context.getColor(R.color.red_500))
            holder.ivIcon.setImageResource(R.drawable.ic_money_in)
            holder.ivIcon.rotation = 180f
        }
    }

    override fun getItemCount(): Int = listAktivitas.size

    fun updateData(newList: List<Aktivitas>) {
        listAktivitas = newList
        notifyDataSetChanged()
    }
}
