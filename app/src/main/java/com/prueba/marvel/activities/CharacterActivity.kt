package com.prueba.marvel.activities

import android.os.Bundle
import android.webkit.WebView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.prueba.marvel.R
import com.prueba.marvel.model.CharacterResult
import com.prueba.marvel.model.Constants


class CharacterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character)

        if(intent!=null){
            loadCharacterDetail(intent.getParcelableExtra(Constants.EXTRA_CHARACTER))
        }
    }

    private fun loadCharacterDetail(characterData: CharacterResult){
        findViewById<TextView>(R.id.tv_name).text = characterData.name
        findViewById<TextView>(R.id.tv_description).text = characterData.description
        val url = characterData.thumbnail!!.path + "." + characterData.thumbnail.extension

        val image = findViewById<WebView>(R.id.wv_image)
        image.settings.loadWithOverviewMode = true
        image.settings.useWideViewPort = true
        image.loadUrl(url)
//        findViewById<WebView>(R.id.wv_image).loadUrl(url)
    }
}