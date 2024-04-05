package com.example.alfacalculator

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.alfacalculator.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.yandex.mobile.ads.banner.BannerAdEventListener
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import net.objecthunter.exp4j.ExpressionBuilder
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity()
{
    //Биндинг
    private lateinit var binding: ActivityMainBinding
    private var exp: String? = ""
    //Сохранение всех настроек
    private lateinit var sharedPreferences: SharedPreferences
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setTheme()
    {
        val allCV: ArrayList<CardView> = arrayListOf(binding.btnMultiplier, binding.btnDivider, binding.btnPercent, binding.btnRank, binding.btnRadical, binding.btnPoint, binding.btnPi,
            binding.btnExp, binding.btnLeftBr, binding.btnRightBr, binding.btnMinus, binding.btnPlus, binding.btn0, binding.btn1, binding.btn2, binding.btn3, binding.btn4,
            binding.btn5, binding.btn6, binding.btn7, binding.btn8, binding.btn9)
        val allIV: ArrayList<ImageView> = arrayListOf(binding.btnPercentIv, binding.btn0Iv, binding.btn1Iv, binding.btn2Iv, binding.btn3Iv, binding.btn4Iv, binding.btn5Iv,
            binding.btn6Iv, binding.btn7Iv, binding.btn8Iv, binding.btn9Iv, binding.btnPlusIv, binding.btnRadicalIv, binding.btnMinusIv, binding.btnMultiplierIv, binding.btnDividerIv)
        val allTV: ArrayList<TextView> = arrayListOf(binding.btnLeftBrTv, binding.btnPiTv, binding.btnExpTv, binding.btnPointTv, binding.btnRankTv, binding.btnRightBrTv, binding.history, binding.operationTextView, binding.resultTextView)
        if (sharedPreferences.getString("theme", "light") == "light")
        {
            for(item in allCV)
            {
                item.setCardBackgroundColor(resources.getColor(R.color.main, null))
            }
            val colorStateList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black))
            for(item in allIV)
            {
                item.foregroundTintList = colorStateList
            }
            for(item in allTV)
            {
                item.setTextColor(resources.getColor(R.color.black, null))
            }
            binding.cl.setBackgroundColor(resources.getColor(R.color.white, null))
            binding.theme.foreground = resources.getDrawable(R.drawable.sun, null)
            binding.theme.foregroundTintList = colorStateList
        }
        else
        {
            for(item in allCV)
            {
                item.setCardBackgroundColor(resources.getColor(R.color.black, null))
            }
            val colorStateList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.whiteDark))
            for(item in allIV)
            {
                item.foregroundTintList = colorStateList
            }
            for(item in allTV)
            {
                item.setTextColor(resources.getColor(R.color.whiteDark, null))
            }
            binding.cl.setBackgroundColor(resources.getColor(R.color.grayBlack, null))
            binding.theme.foreground = resources.getDrawable(R.drawable.moon, null)
            binding.theme.foregroundTintList = colorStateList
        }
    }
    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        //Создание самого проекта и привязки!
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Весь остальной код, выше не трогать!!!
        sharedPreferences = getSharedPreferences(getString(R.string.shared_pref), MODE_PRIVATE)
        if (!sharedPreferences.contains("theme"))
        {
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK)
            {
                Configuration.UI_MODE_NIGHT_YES -> sharedPreferences.edit().putString("theme", "dark").apply()
                Configuration.UI_MODE_NIGHT_NO -> sharedPreferences.edit().putString("theme", "light").apply()
            }
        }
        setTheme()
        binding.adContainerView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener
        {
            override fun onGlobalLayout()
            {
                binding.adContainerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                mBannerAd = loadBannerAd(getAdSize())
            }
        })
        binding.theme.setOnClickListener {
            if (sharedPreferences.getString("theme", "light") == "light")
            {
                sharedPreferences.edit().putString("theme", "dark").apply()
            }
            else
            {
                sharedPreferences.edit().putString("theme", "light").apply()
            }
            setTheme()
        }
        binding.operationTextView.showSoftInputOnFocus = false
        binding.btn0.setOnClickListener { isCorrect("0") }
        binding.btn1.setOnClickListener { isCorrect("1") }
        binding.btn2.setOnClickListener { isCorrect("2") }
        binding.btn3.setOnClickListener { isCorrect("3") }
        binding.btn4.setOnClickListener { isCorrect("4") }
        binding.btn5.setOnClickListener { isCorrect("5") }
        binding.btn6.setOnClickListener { isCorrect("6") }
        binding.btn7.setOnClickListener { isCorrect("7") }
        binding.btn8.setOnClickListener { isCorrect("8") }
        binding.btn9.setOnClickListener { isCorrect("9") }
        binding.btnPlus.setOnClickListener { isCorrect("+") }
        binding.btnMinus.setOnClickListener { isCorrect("-") }
        binding.btnDivider.setOnClickListener { isCorrect("/") }
        binding.btnMultiplier.setOnClickListener { isCorrect("*") }
        binding.btnPercent.setOnClickListener { isCorrect("%") }
        binding.btnLeftBr.setOnClickListener { isCorrect("(") }
        binding.btnRightBr.setOnClickListener { isCorrect(")") }
        binding.btnPoint.setOnClickListener { isCorrect(".") }
        binding.btnRadical.setOnClickListener { isCorrect("√(") }
        binding.btnRank.setOnClickListener { isCorrect("^") }
        binding.btnPi.setOnClickListener { isCorrect("π") }
        binding.btnExp.setOnClickListener { isCorrect("e") }
        enabled()
        binding.btnEqual.setOnClickListener {
            val optext = exp //Выражение в формате строки
            if (optext != "")
            {
                try
                {
                    val expr = ExpressionBuilder(exp).build() //строим выражение
                    val res = expr.evaluate() //Находим ответ (число, может быть нецелое)
                    val longres = res.toLong() //longres - число в формате long (целочисленное)
                    if (longres.toDouble() == res)
                    { //Если число целое,
                        binding.resultTextView.text = longres.toString() //То: Отбрасываем ноль после запятой
                    }
                    else
                    {
                        binding.resultTextView.text = res.toString() //Иначе: Сохраняем числа после запятой
                    }
                    binding.history.text = binding.operationTextView.text.toString() + " = " + binding.resultTextView.text + "\n" + binding.history.text
                    binding.operationTextView.setText(binding.resultTextView.text)
                    exp =  binding.operationTextView.text.toString()
                    binding.resultTextView.text = ""
                }
                catch (e: Exception)
                { //Если выражение записано некорректно
                    binding.resultTextView.text = "Ошибка" //В поле ответа пишем 'Error'\
                    Snackbar.make(binding.operationTextView,"Введите верное выражение!", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
        binding.btnRemove.setOnClickListener {
            try
            {
                val s = binding.operationTextView.text.toString()
                if (s != "")
                {
                    binding.operationTextView.setText(s.substring(0,s.length-1))
                    exp = if (exp!![exp!!.length-1] == 't')
                    {
                        exp!!.substring(0, exp!!.length-4)
                    }
                    else
                    {
                        exp!!.substring(0, exp!!.length-1)
                    }
                    binding.resultTextView.text = ""
                }
                calc()
            }
            catch(_:Exception){}
        }
        binding.btnClear.setOnClickListener {
            val s = binding.operationTextView.text.toString()
            if (s != "")
            {
                binding.operationTextView.setText("")
                binding.resultTextView.text = ""
                exp =  binding.operationTextView.text.toString()
            }
            enabled()
        }
    }
    private fun isCorrect(string: String)
    {
        if (binding.operationTextView.text.isEmpty())
        {
            if (string[0] !in '0' .. '9' && string[0] != '(' && string[0] != ')' && string[0] != 'e' && string[0] != 'π' && string[0] != '√' )
            {
//                binding.operationTextView.append("0")
//                exp += "0"
                binding.operationTextView.append(string)
                exp += string
            }
            else if (string[0] == ')')
            {
                binding.operationTextView.append("(")
                exp += "("
            }
            else
            {
                binding.operationTextView.append(string)
                exp += if (string == "√(")
                {
                    "sqrt("
                }
                else
                {
                    string
                }
            }
        }
        else
        {
            binding.operationTextView.append(string)
            exp += if (string == "√(")
            {
                "sqrt("
            }
            else
            {
                string
            }
        }
        calc()
    }
    private fun calc()
    {
        val optext = exp
        if (optext != "")
        {
            try
            {
                val expr = ExpressionBuilder(exp).build() //строим выражение
                val res = expr.evaluate() //Находим ответ (число, может быть нецелое)
                val longres = res.toLong() //longres - число в формате long (целочисленное)
                if (longres.toDouble() == res)
                { //Если число целое,
                    binding.resultTextView.text = longres.toString() //То: Отбрасываем ноль после запятой
                }
                else
                {
                    binding.resultTextView.text = res.toString() //Иначе: Сохраняем числа после запятой
                }
            }
            catch (_: Exception)
            {
                binding.resultTextView.text = "Ошибка"
            }
        }
        enabled()
        scrollToRight()
    }
    private fun scrollToRight()
    {
        binding.horSv.post { binding.horSv.smoothScrollTo(binding.operationTextView.right, 0) }
    }
    private fun enabled()
    {
        Log.e("1", "зашел")
        val array: ArrayList<View> = arrayListOf( binding.btnMultiplier, binding.btnDivider, binding.btnPercent, binding.btnRank, binding.btnRadical, binding.btnPoint)
        val arrayChar: ArrayList<Char> = arrayListOf('(', '%', '*', '/', '-', '+', '.', '^', '√')
        if (exp!!.isEmpty())
        {
            for(item1 in array)
            {
                item1.isEnabled = false
                item1.alpha = 0.5f
                binding.btnRightBr.isEnabled = false
                binding.btnRightBr.alpha = 0.5f
            }
        }
        else
        {
            for(item in arrayChar)
            {
                if (exp!![exp!!.length - 1] == item)
                {
                    for(item1 in array)
                    {
                        item1.isEnabled = false
                        item1.alpha = 0.5f
                        binding.btnRightBr.isEnabled = false
                        binding.btnRightBr.alpha = 0.5f
                    }
                    return
                }
                else
                {
                    for(item1 in array)
                    {
                        item1.isEnabled = true
                        item1.alpha = 1f
                        binding.btnRightBr.isEnabled = true
                        binding.btnRightBr.alpha = 1f
                    }
                }
            }
            var countRight = 0
            var countLeft = 0
            for(item2 in exp!!)
            {
                if (item2 == '(')
                {
                    countLeft++
                }
                else if (item2 == ')')
                {
                    countRight++
                }
            }
            if (countLeft > countRight)
            {
                binding.btnRightBr.isEnabled = true
                binding.btnRightBr.alpha = 1f
            }
            else
            {
                binding.btnRightBr.isEnabled = false
                binding.btnRightBr.alpha = 0.5f
            }
        }

    }
    //Реклама
    private var mBannerAd: BannerAdView? = null
    private fun getAdSize(): BannerAdSize
    {
        val displayMetrics = resources.displayMetrics
        var adWidthPixels: Int = binding.adContainerView.width
        if (adWidthPixels == 0)
        {
            adWidthPixels = displayMetrics.widthPixels
        }
        val adWidth = (adWidthPixels / displayMetrics.density).roundToInt()
        return BannerAdSize.stickySize(this, adWidth)
    }
    private fun loadBannerAd(adSize: BannerAdSize): BannerAdView
    {
        val bannerAd: BannerAdView = binding.adContainerView
        bannerAd.setAdSize(adSize)
        bannerAd.setAdUnitId("R-M-6693798-1")
        //bannerAd.setAdUnitId("demo-banner-yandex")
        bannerAd.setBannerAdEventListener(object : BannerAdEventListener
        {
            override fun onAdLoaded()
            {
                if (isDestroyed && mBannerAd != null)
                {
                    mBannerAd!!.destroy()
                }
            }
            override fun onAdFailedToLoad(adRequestError: AdRequestError) {}
            override fun onAdClicked() { }
            override fun onLeftApplication(){}
            override fun onReturnedToApplication(){}
            override fun onImpression(impressionData: ImpressionData?){}
        })
        val adRequest = AdRequest.Builder().build()
        bannerAd.loadAd(adRequest)
        return bannerAd
    }
}