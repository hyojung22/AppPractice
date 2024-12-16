package com.corporation8793.stopwatch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import com.corporation8793.stopwatch.databinding.ActivityStopWatchBinding
import com.corporation8793.stopwatch.databinding.DialogCountdownSettingBinding
import java.util.Timer
import kotlin.concurrent.timer

// 스톱워치 2번째 버전
// 클릭 이벤트 처리 인터페이스
class StopWatchActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding : ActivityStopWatchBinding
    var isRunning = false       // 실행 여부 확인용 변수 false로 초기화
    var timer: Timer? = null    // timer 변수 추가
    var time = 0                // time 변수 추가

    var countdownClick = false // 카운트다운 버튼 클릭 여부
    var countdownSecond = 5 // 카운트다운 변수 추가
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStopWatchBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // 버튼별 리스너 등록, 등록을해야 클릭이 가능
        binding.btnStart.setOnClickListener(this)
        binding.btnRefresh.setOnClickListener(this)
        binding.btnCountdown.setOnClickListener(this)
        binding.btnCheck.setOnClickListener(this)
    }

    // 클릭 이벤트시 수행할 기능 구현
    // setOnClickListener 인터페이스는 반드시 onClick() 함수를 오버라이드해야 한다
    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_start -> {  // 클릭 이벤트 시 뷰 id가 btn_start이며
                if (isRunning) { // 스톱워치가 동작 중이라면
                    pause()      // 일시정지 메서드를 실행하고
                } else {         // 동작 중이 아니라면
                    start()      // 시작 메서드를 실행한다
                }
            }
            R.id.btn_refresh -> { // 뷰 id가 btn_refresh이면
                refresh()       // 초기화 메서드를 실행한다
            }
            R.id.btn_countdown -> {
                countdownDialog()
            }
            R.id.btn_check -> {
                check()
            }
        }
    }

    private fun countdownDialog(){
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
                countdownClick = true
                countdownSecond = dialogBinding.countdownSecondPicker.value
                binding.tvSecond.text =
                    if (countdownSecond > 10) ":${countdownSecond}" else ":0${countdownSecond}"
            }
            setNegativeButton("취소") { _, _ ->
                countdownClick = false
            }
        }.show()
    }

    private fun start(){
        // 카운트다운 버튼 클릭 후 시작버튼 누르면 카운트다운되어야함
        if (countdownClick) {
            binding.btnStart.text = "일시정지"
            binding.btnStart.setBackgroundColor(getColor(R.color.red))
            isRunning = true

            // 초기 카운트다운 시간 설정
            time = countdownSecond * 100 // 1초 = 100 (밀리초 기준)

            // 타이머 시작
            timer = timer(period = 10) {
                runOnUiThread {
                    if (isRunning) {
                        if (time > 0) {
                            time--

                            // 시간 계산
                            val milli_second = time % 100
                            val second = (time % 6000) / 100
                            val minute = time / 6000

                            // UI 업데이트
                            binding.tvMillisecond.text =
                                if (milli_second < 10) ".0${milli_second}" else ".${milli_second}"
                            binding.tvSecond.text =
                                if (second < 10) ":0${second}" else ":${second}"
                            binding.tvMinute.text = "${minute}"
                        } else { // 카운트다운 완료
                            timer?.cancel()
                            val toast = Toast.makeText(this@StopWatchActivity, "카운트다운이 완료되었습니다!" , Toast.LENGTH_SHORT)
                            toast.setGravity(Gravity.CENTER, 0, 0)
                            toast.show()
                            isRunning = false
                            binding.btnStart.text = "시작"
                            binding.btnStart.setBackgroundColor(getColor(R.color.blue))
                        }
                    }
                }
            }
        } else {
            // 시작 버튼 클릭
            // 텍스트 일시정지로 변경 , 버튼 색상 레드, isRunning true로 변경
            binding.btnStart.text = "일시정지"
            binding.btnStart.setBackgroundColor(getColor(R.color.red))
            isRunning = true

            // 스톱워치를 시작하는 로직 (period : 일정한 주기로 동작 반복할 때 사용)
            timer = timer(period = 10){

                time++ // 0.01초마다 time에 1을 더함

                // 시간 계산
                val milli_second = time % 100
                val second = (time % 6000) / 100
                val minute = time / 6000

                runOnUiThread{      // UI 스레드 설정
                    /* runOnUiThread 사용 이유
                    사용자가 정지하는 시점과 UI 스레드에서 코드가 실행되는 시점이 다를 수 있기 때문에
                    runOnUiThread를 통해 UI 스레드에서 실행되도록 수정
                    또한 isRunning이 true일 경우만 UI 업데이트 되게 설정  */
                    if (isRunning) { // UI 업데이트 조건 설정
                        // 각 시간이 한 자리일 경우 0을 추가 -> 텍스트 길이 통일 위해
                        // ex) 0:1.8 -> 0:01.08
                        // 밀리초
                        binding.tvMillisecond.text = if (milli_second < 10)
                            ".0${milli_second}" else ".${milli_second}"
                        // 초
                        binding.tvSecond.text = if (second < 10)
                            ":0${second}" else ":${second}"
                        // 분
                        binding.tvMinute.text = "${minute}"
                    }
                }
            }
        }


    }

    private fun pause(){
        // 텍스트 속성 변경
        binding.btnStart.text = "시작"
        binding.btnStart.setBackgroundColor(getColor(R.color.blue))

        isRunning = false // 멈춤 상태로 전환
        timer?.cancel() // 타이머 멈추기
    }

    // 타이머 초기화
    private fun refresh(){
        timer?.cancel() // 백그라운드 타이머 멈추기

        binding.btnStart.text = "시작"
        binding.btnStart.setBackgroundColor(getColor(R.color.blue))
        isRunning = false // 멈춤 상태로 변경

        // 타이머 초기화
        time = 0
        binding.tvMillisecond.text = ".00"
        binding.tvSecond.text = ":00"
        binding.tvMinute.text = "00"

        binding.scrollCheck.isVisible = false
        binding.checkContainer.removeAllViews()

        countdownClick = false
    }

    private fun check(){
        binding.scrollCheck.isVisible = true

        // 현재 스톱워치의 시간 값 가져오기
        val milli_second = time % 100
        val second = (time % 6000) / 100
        val minute = time / 6000

        // 시간 포맷하여 기록
        val check_time = "${if (minute < 10) "0$minute" else "$minute"}:" +
                "${if (second < 10) "0$second" else "$second"}." +
                "${if (milli_second < 10) "0$milli_second" else "$milli_second"}"

        val container = binding.checkContainer
        TextView(this).apply {
            textSize = 20f
            gravity = Gravity.CENTER
            text = check_time // 포맷된 시간 텍스트로 설정
            setPadding(30)
        }.let { checkTextView ->
            container.addView(checkTextView, 0)
        }
    }
}