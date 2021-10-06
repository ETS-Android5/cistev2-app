package com.bulrog59.ciste2dot0

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bulrog59.ciste2dot0.editor.MenuSelectorAdapter
import com.bulrog59.ciste2dot0.game.management.GameDataLoader
import com.bulrog59.ciste2dot0.gamedata.GameData
import com.bulrog59.ciste2dot0.gamedata.SceneData
import com.bulrog59.ciste2dot0.gamedata.SceneType
import com.fasterxml.jackson.databind.ObjectMapper

class EditActivity : AppCompatActivity() {

    private lateinit var gameData: GameData

    private fun setEditorForScene(position: Int) {
        when (gameData.scenes[position].sceneType) {
            else -> Toast.makeText(this, getText(R.string.no_edit_mode), Toast.LENGTH_LONG).show()
        }
    }


    private fun sceneSelectionScreen() {
        val scenesDescription = gameData.scenes.map {
            "${it.sceneId}:${it.name ?: "none"} (${getText(it.sceneType.description)})"
        }

        setContentView(R.layout.editor_scene_selection)

        val recyclerView = findViewById<RecyclerView>(R.id.scene_selection_menu)
        recyclerView.adapter = MenuSelectorAdapter(scenesDescription) { p -> setEditorForScene(p) }
        recyclerView.layoutManager = LinearLayoutManager(this)
        findViewById<Button>(R.id.add_scene_button).setOnClickListener { sceneCreationScreen() }
    }

    private fun addNewSceneToGameData(sceneType:SceneType){
        val maxSceneId = gameData.scenes.map(SceneData::sceneId).maxOrNull() ?: 0
        val sceneData = mutableListOf<SceneData>().apply {
            addAll(gameData.scenes)
            add(
                SceneData(
                    maxSceneId + 1,
                    sceneType,
                    ObjectMapper().createObjectNode(),
                    //TODO: input verification
                    findViewById<TextView>(R.id.scene_title_input).text.toString()
                )
            )
        }
        gameData = GameData(
            gameData.starting,
            sceneData,
            gameData.backButtonScene,
            gameData.gameMetaData
        )
        sceneSelectionScreen()
    }

    private fun sceneCreationScreen() {
        setContentView(R.layout.editor_new_scene)
        val recyclerView = findViewById<RecyclerView>(R.id.scene_type_selection)
        val sceneTypeSelector = MenuSelectorAdapter(
            SceneType.values().map { v -> getText(v.description).toString() }) {}

        findViewById<Button>(R.id.create_scene_button).setOnClickListener {
            addNewSceneToGameData(SceneType.values()[sceneTypeSelector.positionSelected])
        }
        recyclerView.adapter=sceneTypeSelector
        recyclerView.layoutManager = LinearLayoutManager(this)

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler(this))
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        gameData = GameDataLoader(this).loadGameDataFromIntent()

        sceneSelectionScreen()

    }
}