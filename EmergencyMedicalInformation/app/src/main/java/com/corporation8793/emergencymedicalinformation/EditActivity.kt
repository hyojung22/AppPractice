package com.corporation8793.emergencymedicalinformation

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.isVisible
import com.corporation8793.emergencymedicalinformation.databinding.ActivityEditBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditActivity : AppCompatActivity() {

    private lateinit var binding : ActivityEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // 생년월일 다이얼로그 띄우기
        binding.birthLayer.setOnClickListener{
            val listener = OnDateSetListener{ _, year, month, dayOfMonth ->
                binding.etBirth.text = "$year-${month.inc()}-$dayOfMonth"
            }
            DatePickerDialog(
                this,
                listener,
                2000,
                1,
                1
            ).show()
        }

        // 혈액형 리스트
        binding.spinnerBloodType.adapter = ArrayAdapter.createFromResource(
            this,
            R.array.blood_types,
            android.R.layout.simple_list_item_1
        )

        // 체크박스 상태 변경 리스너
        binding.checkWarning.setOnCheckedChangeListener { _, isChecked ->
            binding.etWarning.isVisible = isChecked
        }

        binding.etWarning.isVisible = binding.checkWarning.isChecked

        binding.btnSave.setOnClickListener {
            saveData()
            finish()
        }
    }

    // 데이터 저장
    private fun saveData(){
        with(getSharedPreferences(USER_INFO, Context.MODE_PRIVATE).edit()){
            putString(NAME, binding.etName.text.toString())
            putString(BIRTH, binding.etBirth.text.toString())
            putString(BLOOD_TYPE, getBloodType())
            putString(CALL, binding.etCall.text.toString())
            putString(WARNING,getWarning())
            apply()
        }

        Toast.makeText(this, "저장을 완료했습니다", Toast.LENGTH_SHORT).show()
    }

    // 혈액형 타입 반환
    private fun getBloodType(): String {
        val bloodAlphabet = binding.spinnerBloodType.selectedItem.toString()
        val bloodSign = if (binding.rhPlus.isChecked) "+" else "-"
        return "$bloodSign$bloodAlphabet"
    }

    // 주의사항 반환
    private fun getWarning(): String {
        return if (binding.checkWarning.isChecked) binding.etWarning.text.toString() else ""
    }

    /*
    fun dateDialog(){
        // 날짜를 선택하기 위해 사용하는 다이얼로그
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // 날짜를 선택하면 동작할 리스너
        val datePicker = object : DatePickerDialog.OnDateSetListener{
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                binding.etBirth.text = "$year-${month + 1}-$dayOfMonth"
            }

        }
        val pickerDialog = DatePickerDialog(this, datePicker, year, month, day)
        pickerDialog.show()
    }
     */
}