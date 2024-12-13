package com.corporation8793.emergencymedicalinformation

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import com.corporation8793.emergencymedicalinformation.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnEdit.setOnClickListener {
            val intent = Intent(this, EditActivity::class.java)
            startActivity(intent)
        }

        binding.btnDelete.setOnClickListener {
            deleteDate()
        }

        // 전화 실행하기
        binding.callLayer.setOnClickListener {
            with(Intent(Intent.ACTION_VIEW)) {
                val phoneNumber = binding.tvCall.text.toString()
                    .replace("-","")
                data = Uri.parse("tel:$phoneNumber")
                startActivity(this)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getDateUiUpdate()
    }

    // 데이터 불러오기
    private fun getDateUiUpdate(){
        with(getSharedPreferences(USER_INFO, Context.MODE_PRIVATE)) {
            binding.tvName.text = getString(NAME, "미정")
            binding.tvBirth.text = getString(BIRTH, "미정")
            binding.tvBloodType.text = getString(BLOOD_TYPE, "미정")
            binding.tvCall.text = getString(CALL, "미정")
            val warning = getString(WARNING, "")

            binding.warning.isVisible = warning.isNullOrEmpty().not()
            binding.tvWarning.isVisible = warning.isNullOrEmpty().not()

            if (!warning.isNullOrEmpty()) {
                binding.tvWarning.text = warning
            }
        }
    }

    // 데이터 삭제하기
    private fun deleteDate(){
        with(getSharedPreferences(USER_INFO, MODE_PRIVATE).edit()) {
            clear()
            apply()
            getDateUiUpdate()
        }
        Toast.makeText(this, "초기화를 완료했습니다.", Toast.LENGTH_SHORT).show()
    }


}