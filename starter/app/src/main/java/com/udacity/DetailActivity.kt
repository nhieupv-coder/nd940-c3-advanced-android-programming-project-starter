package com.udacity

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.udacity.databinding.ActivityDetailBinding
import com.udacity.model.DownloadContent
import com.udacity.model.DownloadStatus

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            val data = bundle.get(DATA_INFO_CONTENT) as? DownloadContent
            data?.let {
                binding.contentDetail.txtNameFile.text = it.title
                if(it.status == DownloadStatus.SUCCESS){
                    binding.contentDetail.txtStatus.setTextColor(getColor(R.color.colorPrimaryDark))
                }else{
                    binding.contentDetail.txtStatus.setTextColor(Color.RED)
                }
                binding.contentDetail.txtStatus.text = it.status.text
            }
        }
        binding.contentDetail.button.setOnClickListener{
            this.onBackPressed()
        }
    }
}
