package ac.id.atmaluhur.uas_mpl_rickytjhin

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso

class AdapterBuku (val listBuku: ArrayList<Buku>, val context: Context):
    RecyclerView.Adapter<AdapterBuku.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.buku, parent,
            false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lb = listBuku[position]
        val isbn = lb.isbn
        val jdlBuku = lb.jdlBuku
        val pengarang = lb.pengarang
        val penerbit = lb.penerbit
        val thnTerbit = lb.thnTerbit
        val tmptTerbit = lb.tmptTerbit
        val cetakanKe = lb.cetakanKe
        val jmlhHal = lb.jmlhHal
        val klasifikasi = lb.klasifikasi
        val warnaKlasifikasi = Color.parseColor(when(klasifikasi) {
            "Umum" -> "#FF0000"
            "Filsafat" -> "#FF7F00"
            "Agama" -> "#FFFF00"
            "Sosial" -> "#00FF00"
            "Bahasa" -> "#0000FF"
            "Ilmu Murni/Sains" -> "#4B0082"
            "Teknologi" -> "#8F00FF"
            "Seni" -> "#F56FA1"
            "Sastra" -> "#FBEC5D"
            "Geografi/Sejarah" -> "#228B22"
            else -> "#800000"
        })
        val dataBuku = """
            ISBN: $isbn
            Judul Buku: $jdlBuku
            Pengarang: $pengarang
            Penerbit: $penerbit
            Tahun Terbit: $thnTerbit
            Tempat Terbit: $tmptTerbit
            Cetakan Ke: $cetakanKe
            Jumlah Halaman: $jmlhHal
            Klasifikasi: $klasifikasi
        """.trimIndent()
        val baseUrl = "http://$ip/uas/foto/"
        with(holder) {
            cvBuku.setCardBackgroundColor(warnaKlasifikasi)
            tvJdlBuku.text = jdlBuku
            tvJdlBuku.setTextColor(if(klasifikasi != "Bahasa") Color.BLACK else Color.WHITE)
            tvPengarang.text = "$pengarang"
            tvPengarang.setTextColor(if(klasifikasi != "Bahasa") Color.BLACK else Color.WHITE)
            Picasso.get().load("$baseUrl$isbn.jpeg").fit().into(imgFoto)
            itemView.setOnClickListener {
                val alb = AlertDialog.Builder(context)
                with(alb) {
                    setCancelable(false)
                    setTitle("Data Buku")
                    setMessage(dataBuku)
                    setPositiveButton("Ubah") { _, _ ->
                        val i = Intent(context, EntryBuku::class.java)
                        with(i) {
                            putExtra("isbn", isbn)
                            putExtra("jdl_buku", jdlBuku)
                            putExtra("pengarang", pengarang)
                            putExtra("penerbit", penerbit)
                            putExtra("thn_terbit", thnTerbit)
                            putExtra("tmpt_terbit", tmptTerbit)
                            putExtra("cetakan_ke", cetakanKe)
                            putExtra("jmlh_hal", jmlhHal)
                            putExtra("klasifikasi", klasifikasi)
                        }
                        context.startActivity(i)
                    }
                    setNegativeButton("Hapus") { _, _ ->
                        val url = "http://$ip/uas/hapus.php?isbn=$isbn"
                        val sr = StringRequest(Request.Method.GET, url, {
                            Toast.makeText(
                                context,
                                "Data buku [$isbn] $it dihapus",
                                Toast.LENGTH_SHORT
                            ).show()
                            if(it == "berhasil") {
                                listBuku.removeAt(position)
                                notifyItemRemoved(position)
                            }
                        }, null)
                        val rq = Volley.newRequestQueue(context)
                        rq.add(sr)
                    }
                    setNeutralButton("Tutup", null)
                    create().show()
                }
            }
        }
    }

    override fun getItemCount() = listBuku.size

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val cvBuku = itemView.findViewById<CardView>(R.id.cvBuku)
        val imgFoto = itemView.findViewById<ImageView>(R.id.imgFoto)
        val tvJdlBuku = itemView.findViewById<TextView>(R.id.tvJdlBuku)
        val tvPengarang = itemView.findViewById<TextView>(R.id.tvPengarang)
    }
}