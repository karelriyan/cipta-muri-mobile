package com.main.cipta_muri_mobile.ui.mutasi

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.main.cipta_muri_mobile.data.MutasiSaldo
import com.main.cipta_muri_mobile.databinding.ItemMutasiSaldoBinding

class MutasiSaldoAdapter(
    private var items: List<MutasiSaldo>
) : RecyclerView.Adapter<MutasiSaldoAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemMutasiSaldoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MutasiSaldo) {
            binding.tvTanggal.text = item.tanggal
            binding.tvJudul.text= item.Judul
            binding.tvKeterangan.text = item.keterangan
            binding.tvNominal.text =
                if (item.tipe == "masuk") "+ Rp ${String.format("%,.0f", item.nominal)}"
                else "- Rp ${String.format("%,.0f", item.nominal)}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMutasiSaldoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<MutasiSaldo>) {
        items = newItems
        notifyDataSetChanged()
    }
}
