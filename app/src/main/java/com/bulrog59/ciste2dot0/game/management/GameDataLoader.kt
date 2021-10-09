package com.bulrog59.ciste2dot0.game.management

import android.app.Activity
import android.net.Uri
import com.bulrog59.ciste2dot0.ResourceManager
import com.bulrog59.ciste2dot0.ResourceManager.Companion.GAME_RESOURCE_NAME
import com.bulrog59.ciste2dot0.gamedata.GameData
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.InputStream

class GameDataLoader(activity: Activity) {
    val resourceFinder=ResourceManager(activity)
    val mapper=ObjectMapper().apply { registerModule(KotlinModule()) }

    fun loadGameDataFromIntent():GameData{
        val ios = resourceFinder.getStreamFromUri(GAME_RESOURCE_NAME)

        return mapper.readValue(
            ios,
            GameData::class.java
        )

    }

    fun getStreamFromUri(resourceName: String) : InputStream {
        return resourceFinder.getStreamFromUri(resourceName)
    }

    fun getUri(resourceName: String): Uri {
        return resourceFinder.getUri(resourceName)
    }

}