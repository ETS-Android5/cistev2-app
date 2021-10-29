package com.bulrog59.ciste2dot0.editor

import android.app.Activity
import android.app.AlertDialog
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.bulrog59.ciste2dot0.R
import com.bulrog59.ciste2dot0.editor.GameOptionHelper.Companion.convertToJsonNode
import com.bulrog59.ciste2dot0.editor.GameOptionHelper.Companion.gamePreviousElement
import com.bulrog59.ciste2dot0.editor.GameOptionHelper.Companion.sceneDescriptions
import com.bulrog59.ciste2dot0.gamedata.GameData
import com.bulrog59.ciste2dot0.scenes.menu.MenuItem
import com.bulrog59.ciste2dot0.scenes.menu.MenuOptions
import com.fasterxml.jackson.databind.JsonNode

class MenuEditor(
    val activity: Activity,
    val gameData: GameData,
    scenePosition: Int,
    val done: (JsonNode) -> Unit
) {

    private var menuItems = gamePreviousElement<List<MenuItem>, MenuOptions>(
        gameData,
        scenePosition
    ) { it?.menuItems } ?: emptyList()


    private fun getMenuItemsText(gameData: GameData, menuItems: List<MenuItem>): List<String> {
        return menuItems.map { menuItem ->
            val nextScene = gameData.scenes.filter { it.sceneId == menuItem.nextScene }[0]
            "${menuItem.buttonText}->${nextScene.sceneId}:${nextScene.name}"
        }
    }

    private fun editMenuItem(existingItem: MenuItem?, done: (MenuItem) -> Unit) {
        var menuTitle: String?
        activity.setContentView(R.layout.editor_menu_title)
        val menuTitleField = activity.findViewById<EditText>(R.id.menu_title_input)
        existingItem?.apply {
            menuTitleField.setText(buttonText)
        }
        activity.findViewById<Button>(R.id.exit_button_menu_title).setOnClickListener {
            menuTitle = menuTitleField.text.toString()
            if (menuTitle.isNullOrEmpty()) {
                Toast.makeText(activity, R.string.empty_field_error, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            ItemPicker(activity).init(
                R.string.next_scene_title,
                sceneDescriptions(gameData.scenes, activity)
            ) {
                done(MenuItem(menuTitle!!, gameData.scenes[it].sceneId))
            }
        }
    }

    fun init() {
        ListEditor(
            activity,
            menuItems,
            { l -> getMenuItemsText(gameData, l) },
            this::editMenuItem,
            { i-> done(convertToJsonNode(MenuOptions(i))) }).init()

    }

}