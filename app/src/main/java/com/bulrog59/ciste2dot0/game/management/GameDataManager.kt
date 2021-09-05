package com.bulrog59.ciste2dot0.game.management

import android.content.Context
import android.widget.Toast
import com.bulrog59.ciste2dot0.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.nio.file.Files
import java.util.*

class GameDataManager(context: Context) {
    private val folderGame = context.filesDir.absolutePath + FOLDER_FOR_GAME_DATA
    private val storage = Firebase.storage


    fun gameIsAvailable(gameID: UUID): Boolean {
        val folderForGameData = File(folderGame + gameID)
        return folderForGameData.exists() && folderForGameData.isDirectory
    }

    fun loadGame(
        id: UUID?,
        callOnProgress: (transferBytes: Long, totalBytes: Long) -> Unit,
        callOnFailure: (e: Exception) -> Unit,
        onSuccessAction: () -> Unit
    ) {
        if (id == null || gameIsAvailable(id)) {
            return
        }
        if (!File(folderGame).exists()) {
            Files.createDirectory(File(folderGame).toPath())
        }
        loadFileFireStore(id, callOnProgress, callOnFailure ,onSuccessAction)

    }



    private fun deleteRecursive(fileOrDirectory: File) {
        if (fileOrDirectory.isDirectory) for (child in fileOrDirectory.listFiles()) deleteRecursive(
            child
        )
        fileOrDirectory.delete()
    }

    fun eraseLocalGame(id: UUID?) {
        if (id == null || !gameIsAvailable(id)) {
            return
        }

        deleteRecursive(File("$folderGame$id"))

    }

    private fun loadFileFireStore(
        id: UUID,
        callOnProgress: (transferBytes: Long, totalBytes: Long) -> Unit,
        callOnFailure: (e: Exception) -> Unit,
        callOnSuccess: () -> Unit
    ) {
        val localZipFile = File("$folderGame$id.zip")
        val referenceData =
            storage.getReferenceFromUrl("$URL_FIRESTORE$id.zip")
        referenceData.getFile(localZipFile)
            .addOnSuccessListener {
                UnzipUtils.unzip(localZipFile, "$folderGame$id")
                localZipFile.delete()
                callOnSuccess()
            }
            .addOnProgressListener {
                callOnProgress(it.bytesTransferred, it.totalByteCount)
            }
            .addOnFailureListener {
                callOnFailure(it)
            }
    }

    companion object {
        val FOLDER_FOR_GAME_DATA = "/gameData/"
        private val URL_FIRESTORE =
            "https://firebasestorage.googleapis.com/v0/b/cistes2dot0.appspot.com/o/"
    }
}