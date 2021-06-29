package com.example.wwubricks

import android.content.res.AssetManager
import android.util.Log
import org.json.JSONArray
import java.io.InputStream



class ParseJSON {
    var assets: AssetManager
    var fileString: String
    var json: String = ""
    constructor(assets: AssetManager, fileString: String) {
        this.assets = assets
        this.fileString = fileString
        readFromFile()
    }

    private fun readFromFile() {
        try {
            val  inputStream: InputStream = assets.open(fileString)
            json = inputStream.bufferedReader().use{ it.readText() }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun addToDatabase(dbman: DatabaseManager) {
        var name: String
        var url: String
        var score: Int
        var jsonArray = JSONArray(json)
        Log.d("DEBUG", jsonArray.toString())
        for(i in 0 until jsonArray.length()) {
            Log.d("DEBUG", i.toString())
            name = jsonArray.getJSONObject(i).getString("name")
            url = jsonArray.getJSONObject(i).getString("url") //TODO handle null case
            score = jsonArray.getJSONObject(i).getInt("score")
            dbman.insert(name, url, score)
        }
    }
}