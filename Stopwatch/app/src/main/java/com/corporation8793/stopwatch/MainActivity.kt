package com.corporation8793.stopwatch

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import com.corporation8793.stopwatch.databinding.ActivityMainBinding
import com.corporation8793.stopwatch.databinding.DialogCountdownSettingBinding
import java.util.Timer
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private var countdownSecond = 5 // 카운트다운 시간 초 단위로 설정 (기본값 : 5초)
    private var currentCountdownDeciSecond = countdownSecond * 10 // 카운트 다운 시간 0.1초 단위로 변환한 값
    private var currentDeciSecond = 0 // 스톱워치 실행된 시간 0.1초 단위로 저장
    private var timer: Timer? = null // 일정 주기로 작업을 실행하는 타이머
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.tvCountdown.setOnClickListener {
            showCountdownSettingDialog()
        }

        // 버튼 시작 -> 시작, 정지 버튼 사라지게 / 일시정지, 체크 버튼 보이게
        binding.btnStart.setOnClickListener {
            start()
            binding.btnStart.isVisible = false
            binding.btnStop.isVisible = false
            binding.btnPause.isVisible = true
            binding.btnLap.isVisible = true
        }

        binding.btnStop.setOnClickListener {
            showAlertDialog()
        }

        binding.btnPause.setOnClickListener {
            pause()
            binding.btnStart.isVisible = true
            binding.btnStop.isVisible = true
            binding.btnPause.isVisible = false
            binding.btnLap.isVisible = false
        }

        // 현재 시간 기록 버튼
        binding.btnLap.setOnClickListener {
            lap()
        }
        // 초기 카운트다운 관련 뷰 초기화
        initCountdownViews()
    }

    private fun initCountdownViews() {
        binding.tvCountdown.text = String.format("%02d", countdownSecond)
        binding.progressCountdown.progress = 100
    }

    // 타이머 시작
    private fun start(){
        timer = timer(initialDelay = 0, period = 100) { // 0.1초마다 작업 실행
            if (currentCountdownDeciSecond == 0) {
                // 카운트다운 끝난 경우 스톱워치 시간 0.1초씩 증가시킴
                currentDeciSecond += 1
                Log.d("currentDeciSecond", currentDeciSecond.toString())

                val minutes = currentDeciSecond.div(10) / 60
                val seconds = currentDeciSecond.div(10) % 60
                val deciSeconds = currentDeciSecond % 10

                runOnUiThread {
                    binding.tvTime.text =
                        String.format("%02d:%02d", minutes, seconds)
                    binding.tvTick.text = deciSeconds.toString()

                    binding.groupCountdown.isVisible = false
                }
            } else {
                currentCountdownDeciSecond -= 1
                val seconds = currentCountdownDeciSecond / 10
                val progress = (currentCountdownDeciSecond / (countdownSecond * 10f)) * 100

                binding.root.post {
                    binding.tvCountdown.text = String.format("%02d", seconds)
                    binding.progressCountdown.progress = progress.toInt()
                }
            }
            if (currentDeciSecond == 0 && currentCountdownDeciSecond < 31 && currentCountdownDeciSecond % 10 == 0) {
                val toneType= if(currentCountdownDeciSecond == 0) ToneGenerator.TONE_CDMA_HIGH_L else ToneGenerator.TONE_CDMA_ANSWER
                ToneGenerator(AudioManager.STREAM_ALARM, ToneGenerator.MAX_VOLUME)
                    .startTone(toneType, 100)
            }
        }
    }

    private fun pause(){
        timer?.cancel()
        timer = null
    }
    private fun stop(){
        binding.btnStart.isVisible = true
        binding.btnStop.isVisible = true
        binding.btnPause.isVisible = false
        binding.btnLap.isVisible = false

        currentDeciSecond = 0
        // stop 한 뒤 다시 start 하면, 그때도 카운트다운 실행되도록
        currentCountdownDeciSecond = countdownSecond * 10

        binding.tvTime.text = "00:00"
        binding.tvTick.text = "0"

        binding.groupCountdown.isVisible = true
        initCountdownViews()
        binding.lapContainer.removeAllViews()
    }

    private fun lap(){
        if (currentDeciSecond == 0) return
        val container = binding.lapContainer
        TextView(this).apply {
            textSize = 20f
            gravity = Gravity.CENTER
            val minutes = currentDeciSecond.div(10) / 60
            val seconds = currentDeciSecond.div(10) % 60
            val deciSeconds = currentDeciSecond % 10
            text = "${container.childCount.inc()}. " + String.format(
                "%02d:%02d %01d",
                minutes,
                seconds,
                deciSeconds
            )
            setPadding(30)
        }.let { labTextView ->
            container.addView(labTextView, 0)
        }
    }

    // 카운트다운 숫자 정하는 다이얼로그
    private fun showCountdownSettingDialog() {
        AlertDialog.Builder(this).apply {
            val dialogBinding = DialogCountdownSettingBinding.inflate(layoutInflater)
            with(dialogBinding.countdownSecondPicker) {
                maxValue = 20
                minValue = 0
                value = countdownSecond
            }
            setTitle("카운트다운 설정")
            setView(dialogBinding.root)
            setPositiveButton("확인") { _, _ ->
                countdownSecond = dialogBinding.countdownSecondPicker.value
                currentCountdownDeciSecond = countdownSecond * 10
                binding.tvCountdown.text = String.format("%02d", countdownSecond)
            }
            setNegativeButton("취소", null)
        }.show()
    }

    // 정지 버튼 눌렀을 때 초기화하는 다이얼로그
    private fun showAlertDialog() {
        AlertDialog.Builder(this).apply {
            setMessage("종료하시겠습니까?")
            // 네 버튼 누르면 시간 초기화됨
            setPositiveButton("네") { _, _ ->
                stop()
            }
            setNegativeButton("아니요", null)
        }.show()
    }
}