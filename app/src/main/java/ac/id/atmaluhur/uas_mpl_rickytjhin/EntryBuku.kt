package ac.id.atmaluhur.uas_mpl_rickytjhin

import androidx.appcompat.app.AppCompatActivity
import android.os.*
import android.widget.*
import com.squareup.picasso.Picasso
import androidx.activity.result.contract.ActivityResultContracts.*
import android.util.Base64
import java.io.ByteArrayOutputStream
import android.graphics.*
import androidx.annotation.RequiresApi
import java.util.*
import android.text.InputType
import com.android.volley.*
import com.android.volley.toolbox.*

class EntryBuku : AppCompatActivity() {
    private lateinit var url: String
    private lateinit var sr: StringRequest
    private lateinit var rq: RequestQueue

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry_buku)

        val modeEdit = intent.hasExtra("isbn")

        title = "${if(!modeEdit) "Tambah" else "Ubah"} Data Buku"

        val etIsbn = findViewById<EditText>(R.id.etIsbn)
        val etJdlBuku = findViewById<EditText>(R.id.etJdlBuku)
        val etPengarang = findViewById<EditText>(R.id.etPengarang)
        val etPenerbit = findViewById<EditText>(R.id.etPenerbit)
        val etThnTerbit = findViewById<EditText>(R.id.etThnTerbit)
        val etTmptTerbit = findViewById<EditText>(R.id.etTmptTerbit)
        val etCetakanKe = findViewById<EditText>(R.id.etCetakanKe)
        val etJmlhHal = findViewById<EditText>(R.id.etJmlhHal)
        val spnKlasifikasi = findViewById<Spinner>(R.id.spnKlasifikasi)
        val btnFoto = findViewById<Button>(R.id.btnFoto)
        val imgFoto = findViewById<ImageView>(R.id.imgFoto)
        val btnSimpan = findViewById<Button>(R.id.btnSimpan)

        val arrKlasifikasi = arrayOf(
            "Umum", "Filsafat", "Agama", "Sosial", "Bahasa", "Ilmu Murni/Sains",
            "Teknologi", "Seni", "Sastra" ,"Geografi/Sejarah"
        )
        spnKlasifikasi.adapter = ArrayAdapter(
            this@EntryBuku,
            android.R.layout.simple_spinner_dropdown_item,
            arrKlasifikasi
        )

        if(modeEdit) {
            etIsbn.inputType = InputType.TYPE_NULL
            with(intent) {
                etIsbn.setText(getStringExtra("isbn"))
                etJdlBuku.setText(getStringExtra("jdl_buku"))
                etPengarang.setText(getStringExtra("pengarang"))
                etPenerbit.setText(getStringExtra("penerbit"))
                etThnTerbit.setText("${getIntExtra("thn_terbit", 0)}")
                etTmptTerbit.setText(getStringExtra("tmpt_terbit"))
                etCetakanKe.setText("${getIntExtra("cetakan_ke", 0)}")
                etJmlhHal.setText(getStringExtra("jmlh_hal"))
                spnKlasifikasi.setSelection(arrKlasifikasi.indexOf(getStringExtra("klasifikasi")))
                Picasso.get().load(
                    "http://$ip/uas/foto/${getStringExtra("isbn")}.jpeg"
                ).into(imgFoto)
            }
            btnSimpan.text = "Ubah"
        } else {
            etIsbn.inputType = InputType.TYPE_CLASS_NUMBER
            btnSimpan.text = "Simpan"
        }
        var foto = ""
        val ambilFoto = registerForActivityResult(GetContent()) {
            if(it != null) {
                val source = ImageDecoder.createSource(contentResolver, it)
                foto = imgToString(ImageDecoder.decodeBitmap(source))
                imgFoto.setImageURI(it)
            }
        }
        btnFoto.setOnClickListener { ambilFoto.launch("image/*") }

        btnSimpan.setOnClickListener {
            val isbn = "${etIsbn.text}"
            val jdlBuku = "${etJdlBuku.text}"
            val pengarang = "${etPengarang.text}"
            val penerbit = "${etPenerbit.text}"
            val thnTerbit = "${etThnTerbit.text}"
            val tmptTerbit = "${etTmptTerbit.text}"
            val cetakanKe = "${etCetakanKe.text}"
            val jmlhHal = "${etJmlhHal.text}"
            val klasifikasi = "${spnKlasifikasi.selectedItem}"
            if(btnSimpan.text == "Simpan") {
                url = "http://$ip/uas/simpan.php"
                sr = object: StringRequest(Method.POST, url, {
                    Toast.makeText(
                        this@EntryBuku,
                        "Data buku $it disimpan",
                        Toast.LENGTH_SHORT
                    ).show()
                    if(it == "berhasil") finish()
                }, null) {
                    override fun getParams() = mutableMapOf(
                        "isbn" to isbn, "jdl_buku" to jdlBuku,
                        "pengarang" to pengarang, "penerbit" to penerbit,"thn_terbit" to thnTerbit,
                        "tmpt_terbit" to tmptTerbit, "cetakan_ke" to cetakanKe,
                        "jmlh_hal" to jmlhHal,"klasifikasi" to klasifikasi, "foto" to foto
                    )
                }
            } else {
                url = "http://$ip/uas/ubah.php?isbn=$isbn"
                sr = object: StringRequest(Method.POST, url, {
                    Toast.makeText(
                        this@EntryBuku,
                        "Data buku [$isbn] $it diubah",
                        Toast.LENGTH_SHORT
                    ).show()
                    if(it == "berhasil") finish()
                }, null) {
                    override fun getParams() = mutableMapOf(
                        "isbn" to isbn, "jdl_buku" to jdlBuku,
                        "pengarang" to pengarang, "penerbit" to penerbit,"thn_terbit" to thnTerbit,
                        "tmpt_terbit" to tmptTerbit, "cetakan_ke" to cetakanKe,
                        "jmlh_hal" to jmlhHal,"klasifikasi" to klasifikasi, "foto" to foto
                    )
                }
            }
            rq = Volley.newRequestQueue(this@EntryBuku)
            rq.add(sr)
        }
    }

    private fun imgToString(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imgbytes = baos.toByteArray()
        return Base64.encodeToString(imgbytes, Base64.DEFAULT)
    }
}